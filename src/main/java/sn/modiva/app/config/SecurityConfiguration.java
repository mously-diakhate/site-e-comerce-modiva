package sn.modiva.app.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import sn.modiva.app.security.*;
import sn.modiva.app.web.filter.SpaWebFilter;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final JHipsterProperties jHipsterProperties;

    public SecurityConfiguration(JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class)
            .headers(headers ->
                headers
                    .contentSecurityPolicy(csp -> csp.policyDirectives(jHipsterProperties.getSecurity().getContentSecurityPolicy()))
                    .frameOptions(FrameOptionsConfig::sameOrigin)
                    .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .permissionsPolicyHeader(permissions ->
                        permissions.policy(
                            "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                        )
                    )
            )
            .authorizeHttpRequests(authz ->
                // prettier-ignore
                authz
                    .requestMatchers("/index.html", "/*.js", "/*.txt", "/*.json", "/*.map", "/*.css").permitAll()
                    .requestMatchers("/*.ico", "/*.png", "/*.svg", "/*.webapp").permitAll()
                    .requestMatchers("/content/**").permitAll()
                    .requestMatchers("/resources/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/authenticate").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/authenticate").permitAll()
                    .requestMatchers("/api/register").permitAll()
                    .requestMatchers("/api/activate").permitAll()
                    .requestMatchers("/api/account/reset-password/init").permitAll()
                    .requestMatchers("/api/account/reset-password/finish").permitAll()
                    .requestMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)

                    // --- Compte de l'utilisatrice connectée (profil, mot de passe...) ---
                    .requestMatchers("/api/account", "/api/account/**").authenticated()

                    // --- Lecture publique pour la boutique (page d'accueil, catalogue) ---
                    // Uniquement du GET : créer/modifier/supprimer reste réservé à l'admin (plus bas).
                    .requestMatchers(HttpMethod.GET, "/api/produits").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/produits/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/image-produits/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/variante-produits/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/couleurs/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/tailles/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/promotions/**").permitAll()
                    // -----------------------------------------------------------------------

                    // --- Commande (visiteur OU cliente) : UN SEUL point d'entrée public ---
                    // POST /api/checkout recalcule les prix depuis la base, vérifie et déduit le
                    // stock, puis crée Livraison / Paiement / Commande / LigneCommande dans une
                    // seule transaction. Les endpoints génériques POST /api/commandes,
                    // /api/paiements, /api/livraisons, /api/ligne-commandes et
                    // /api/cliente-profiles ne sont donc PLUS publics (ils sont réservés à
                    // l'admin plus bas) : sinon n'importe qui pouvait y envoyer ses propres prix,
                    // un statut de paiement "REUSSI" ou rattacher une commande à une autre cliente.
                    .requestMatchers(HttpMethod.POST, "/api/checkout").permitAll()
                    // -----------------------------------------------------------------------

                    // --- Self-service pour les clientes connectées ---
                    // Ces endpoints ne prennent AUCUN identifiant venant du client pour savoir
                    // "qui je suis" : tout est résolu côté serveur via le login du token JWT, et
                    // chaque ligne manipulée est vérifiée comme appartenant au panier de l'appelante.
                    // IMPORTANT : doivent rester AVANT les règles ADMIN génériques ci-dessous
                    // (Spring Security applique la première règle qui correspond).
                    .requestMatchers(HttpMethod.GET, "/api/cliente-profiles/me").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/commandes/mes-commandes").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/paniers/me").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/ligne-paniers/mine").authenticated()
                    .requestMatchers("/api/mon-panier/**").authenticated()
                    // -----------------------------------------------------------------------

                    // --- Réservé à l'administratrice (espace back-office) ---
                    .requestMatchers("/api/produits", "/api/produits/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/api/categories", "/api/categories/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/api/image-produits", "/api/image-produits/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/api/variante-produits", "/api/variante-produits/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/api/couleurs", "/api/couleurs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/api/tailles", "/api/tailles/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/api/promotions", "/api/promotions/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/api/commandes", "/api/commandes/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/api/ligne-commandes", "/api/ligne-commandes/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/api/livraisons", "/api/livraisons/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/api/paiements", "/api/paiements/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/api/cliente-profiles", "/api/cliente-profiles/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/api/paniers", "/api/paniers/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    // CORRECTION : ces lignes n'étaient protégées par aucune règle (donc ouvertes à
                    // toute cliente connectée). Les clientes passent désormais par /api/mon-panier/**.
                    .requestMatchers("/api/ligne-paniers", "/api/ligne-paniers/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    // -----------------------------------------------------------------------

                    // CORRECTION : refus par défaut. Tout endpoint /api/** non listé plus haut est
                    // réservé à l'admin (avant : ouvert à toute personne connectée). Si un écran
                    // cliente renvoie 403, ajoutez sa route dans le bloc "self-service".
                    .requestMatchers("/api/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/v3/api-docs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/management/health").permitAll()
                    .requestMatchers("/management/health/**").permitAll()
                    .requestMatchers("/management/info").permitAll()
                    .requestMatchers("/management/prometheus").permitAll()
                    .requestMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions ->
                exceptions
                    .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                    .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }
}
