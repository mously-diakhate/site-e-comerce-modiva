package sn.modiva.app.web.rest;

import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.modiva.app.security.SecurityUtils;
import sn.modiva.app.service.CheckoutService;
import sn.modiva.app.service.CheckoutService.CheckoutRequest;
import sn.modiva.app.service.CheckoutService.CheckoutResult;

/**
 * POST /api/checkout — public (visiteur ou cliente). Voir SecurityConfiguration.
 */
@RestController
@RequestMapping("/api")
public class CheckoutResource {

    private final CheckoutService checkoutService;

    public CheckoutResource(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResult> checkout(@Valid @RequestBody CheckoutRequest request) {
        // Attention : pour un visiteur, Spring place une authentification "anonymousUser" ;
        // isAuthenticated() l'exclut, sinon on chercherait un compte nommé "anonymousUser".
        Optional<String> login = SecurityUtils.isAuthenticated() ? SecurityUtils.getCurrentUserLogin() : Optional.empty();
        CheckoutResult result = checkoutService.passerCommande(request, login);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
