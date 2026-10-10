package sn.modiva.app.web.rest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.modiva.app.domain.Couleur;
import sn.modiva.app.domain.ImageProduit;
import sn.modiva.app.domain.Produit;
import sn.modiva.app.domain.Promotion;
import sn.modiva.app.domain.Taille;
import sn.modiva.app.domain.VarianteProduit;
import sn.modiva.app.repository.ImageProduitRepository;
import sn.modiva.app.repository.ProduitRepository;
import sn.modiva.app.repository.VarianteProduitRepository;
import sn.modiva.app.service.dto.CouleurSummaryDTO;
import sn.modiva.app.service.dto.ProduitListExtraDTO;
import sn.modiva.app.service.dto.PromotionSummaryDTO;
import sn.modiva.app.service.dto.TailleSummaryDTO;

@RestController
@RequestMapping("/api/produits")
public class ProduitExtrasResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProduitExtrasResource.class);

    private final ImageProduitRepository imageProduitRepository;
    private final VarianteProduitRepository varianteProduitRepository;
    private final ProduitRepository produitRepository;

    public ProduitExtrasResource(
        ImageProduitRepository imageProduitRepository,
        VarianteProduitRepository varianteProduitRepository,
        ProduitRepository produitRepository
    ) {
        this.imageProduitRepository = imageProduitRepository;
        this.varianteProduitRepository = varianteProduitRepository;
        this.produitRepository = produitRepository;
    }

    @GetMapping("/list-extras")
    public List<ProduitListExtraDTO> getListExtras(@RequestParam List<Long> ids) {
        LOG.debug("REST request to get list extras for produits : {}", ids);

        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        Map<Long, String> firstImageByProduit = imageProduitRepository
            .findByProduitIdInOrderByOrdreAsc(ids)
            .stream()
            .collect(Collectors.toMap(img -> img.getProduit().getId(), ImageProduit::getUrl, (first, second) -> first));

        List<VarianteProduit> variantes = varianteProduitRepository.findByProduitIdInWithCouleurAndTaille(ids);

        Map<Long, List<VarianteProduit>> variantesByProduit = variantes
            .stream()
            .collect(Collectors.groupingBy(v -> v.getProduit().getId()));

        // NEW : produits avec leurs promotions déjà chargées
        Map<Long, Produit> produitsById = produitRepository
            .findAllByIdInWithPromotions(ids)
            .stream()
            .collect(Collectors.toMap(Produit::getId, p -> p));

        return ids
            .stream()
            .map(id -> {
                List<VarianteProduit> vs = variantesByProduit.getOrDefault(id, List.of());

                Map<Long, CouleurSummaryDTO> couleursById = new LinkedHashMap<>();
                Map<Long, TailleSummaryDTO> taillesById = new LinkedHashMap<>();

                for (VarianteProduit v : vs) {
                    Couleur c = v.getCouleur();
                    if (c != null && !couleursById.containsKey(c.getId())) {
                        couleursById.put(c.getId(), new CouleurSummaryDTO(c.getId(), c.getLibelle(), c.getCodeHex()));
                    }
                    Taille t = v.getTaille();
                    if (t != null && !taillesById.containsKey(t.getId())) {
                        taillesById.put(t.getId(), new TailleSummaryDTO(t.getId(), t.getLibelle()));
                    }
                }

                // NEW : promotions de ce produit
                List<PromotionSummaryDTO> promotionSummaries = List.of();
                Produit produit = produitsById.get(id);
                if (produit != null) {
                    promotionSummaries = produit
                        .getPromotions()
                        .stream()
                        .map(promo -> new PromotionSummaryDTO(promo.getId(), promo.getNom()))
                        .toList();
                }

                return new ProduitListExtraDTO(
                    id,
                    firstImageByProduit.get(id),
                    List.copyOf(couleursById.values()),
                    List.copyOf(taillesById.values()),
                    promotionSummaries
                );
            })
            .toList();
    }
}
