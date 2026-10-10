package sn.modiva.app.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sn.modiva.app.security.SecurityUtils;
import sn.modiva.app.service.MonPanierService;
import sn.modiva.app.service.MonPanierService.LigneResult;

/**
 * Lignes du panier de la cliente connectée. Aucun id de panier ni de cliente
 * n'est accepté du client : tout est résolu depuis le login du JWT.
 * Remplace l'usage de POST/PATCH/DELETE /api/ligne-paniers par le frontend
 * (ces endpoints génériques sont désormais réservés à l'admin).
 */
@RestController
@RequestMapping("/api/mon-panier")
public class MonPanierResource {

    private final MonPanierService monPanierService;

    public MonPanierResource(MonPanierService monPanierService) {
        this.monPanierService = monPanierService;
    }

    public record AjoutLigneRequest(@NotNull Long varianteId, @Min(1) @Max(MonPanierService.QUANTITE_MAX) int quantite) {}

    public record QuantiteRequest(@Min(1) @Max(MonPanierService.QUANTITE_MAX) int quantite) {}

    @PostMapping("/lignes")
    public ResponseEntity<LigneResult> ajouter(@Valid @RequestBody AjoutLigneRequest request) {
        LigneResult result = monPanierService.addOrSetLine(login(), request.varianteId(), request.quantite());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping("/lignes/{id}")
    public ResponseEntity<LigneResult> modifierQuantite(@PathVariable("id") Long id, @Valid @RequestBody QuantiteRequest request) {
        return ResponseEntity.ok(monPanierService.updateLine(login(), id, request.quantite()));
    }

    @DeleteMapping("/lignes/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable("id") Long id) {
        monPanierService.deleteLine(login(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lignes")
    public ResponseEntity<Void> vider() {
        monPanierService.clearLines(login());
        return ResponseEntity.noContent().build();
    }

    private String login() {
        return SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
