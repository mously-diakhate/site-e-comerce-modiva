package sn.modiva.app.web.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Protection contre les attaques par force brute sur POST /api/authenticate.
 *
 * - 5 échecs sur le même identifiant  => blocage de cet identifiant pendant 15 minutes.
 * - 30 échecs depuis la même adresse IP => blocage de cette adresse pendant 15 minutes
 *   (seuil plus haut : plusieurs personnes peuvent partager une même IP, par exemple
 *   en réseau mobile).
 * - Une connexion réussie remet à zéro le compteur de l'identifiant.
 *
 * Pendant un blocage, le serveur répond 429 sans même vérifier le mot de passe.
 * Les compteurs sont gardés en mémoire : ils repartent de zéro au redémarrage et
 * ne sont pas partagés entre plusieurs instances du serveur.
 */
@Component
public class LoginAttemptFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoginAttemptFilter.class);

    private static final String AUTHENTICATE_PATH = "/api/authenticate";
    private static final int MAX_FAILURES_PER_USER = 5;
    private static final int MAX_FAILURES_PER_IP = 30;
    private static final Duration WINDOW = Duration.ofMinutes(15);
    private static final int MAX_BODY_BYTES = 4096;
    private static final int CLEANUP_THRESHOLD = 5000;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Deque<Instant>> failures = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod()) || !request.getRequestURI().endsWith(AUTHENTICATE_PATH);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        byte[] body = request.getInputStream().readNBytes(MAX_BODY_BYTES + 1);
        if (body.length > MAX_BODY_BYTES) {
            response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
            return;
        }

        String username = extractUsername(body);
        String userKey = "u:" + username;
        String ipKey = "ip:" + request.getRemoteAddr();

        long wait = Math.max(
            retryAfterSeconds(ipKey, MAX_FAILURES_PER_IP),
            username.isEmpty() ? 0 : retryAfterSeconds(userKey, MAX_FAILURES_PER_USER)
        );
        if (wait > 0) {
            LOG.warn("Connexion bloquée (trop d'échecs) pour '{}' depuis {}", username, request.getRemoteAddr());
            reject(response, wait);
            return;
        }

        chain.doFilter(new CachedBodyRequest(request, body), response);

        int status = response.getStatus();
        if (status == HttpServletResponse.SC_UNAUTHORIZED) {
            recordFailure(ipKey);
            if (!username.isEmpty()) {
                recordFailure(userKey);
            }
        } else if (status == HttpServletResponse.SC_OK && !username.isEmpty()) {
            failures.remove(userKey);
        }
    }

    private String extractUsername(byte[] body) {
        try {
            JsonNode node = objectMapper.readTree(body);
            String username = node.path("username").asText("").trim().toLowerCase(Locale.ROOT);
            return username.length() > 100 ? username.substring(0, 100) : username;
        } catch (IOException e) {
            return "";
        }
    }

    /** Nombre de secondes restantes avant la fin du blocage (0 = pas bloqué). */
    private long retryAfterSeconds(String key, int maxFailures) {
        Deque<Instant> attempts = failures.get(key);
        if (attempts == null) {
            return 0;
        }
        synchronized (attempts) {
            purgeExpired(attempts);
            if (attempts.size() < maxFailures) {
                return 0;
            }
            Instant unlockAt = attempts.getLast().plus(WINDOW);
            return Math.max(1, Duration.between(Instant.now(), unlockAt).getSeconds());
        }
    }

    private void recordFailure(String key) {
        if (failures.size() > CLEANUP_THRESHOLD) {
            cleanup();
        }
        Deque<Instant> attempts = failures.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (attempts) {
            purgeExpired(attempts);
            attempts.addLast(Instant.now());
        }
    }

    private void purgeExpired(Deque<Instant> attempts) {
        Instant limit = Instant.now().minus(WINDOW);
        while (!attempts.isEmpty() && attempts.getFirst().isBefore(limit)) {
            attempts.removeFirst();
        }
    }

    private void cleanup() {
        failures
            .entrySet()
            .removeIf(entry -> {
                synchronized (entry.getValue()) {
                    purgeExpired(entry.getValue());
                    return entry.getValue().isEmpty();
                }
            });
    }

    private void reject(HttpServletResponse response, long retryAfterSeconds) throws IOException {
        long minutes = Math.max(1, (retryAfterSeconds + 59) / 60);
        response.setStatus(429);
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response
            .getWriter()
            .write(
                "{\"title\":\"Too Many Requests\",\"status\":429,\"message\":\"error.http.429\",\"detail\":\"Trop de tentatives de connexion. Réessayez dans " +
                    minutes +
                    " minute(s).\"}"
            );
    }

    /** Permet de relire le corps de la requête (déjà consommé pour lire l'identifiant) dans le contrôleur. */
    private static class CachedBodyRequest extends HttpServletRequestWrapper {

        private final byte[] body;

        CachedBodyRequest(HttpServletRequest request, byte[] body) {
            super(request);
            this.body = body;
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream in = new ByteArrayInputStream(body);
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return in.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int read() {
                    return in.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
    }
}
