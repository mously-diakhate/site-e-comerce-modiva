package sn.modiva.app.service.dto;

import java.util.List;

public class ProduitListExtraDTO {

    private Long produitId;
    private String imageUrl;
    private List<CouleurSummaryDTO> couleurs;
    private List<TailleSummaryDTO> tailles;
    private List<PromotionSummaryDTO> promotions;

    public ProduitListExtraDTO() {}

    public ProduitListExtraDTO(
        Long produitId,
        String imageUrl,
        List<CouleurSummaryDTO> couleurs,
        List<TailleSummaryDTO> tailles,
        List<PromotionSummaryDTO> promotions
    ) {
        this.produitId = produitId;
        this.imageUrl = imageUrl;
        this.couleurs = couleurs;
        this.tailles = tailles;
        this.promotions = promotions;
    }

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<CouleurSummaryDTO> getCouleurs() {
        return couleurs;
    }

    public void setCouleurs(List<CouleurSummaryDTO> couleurs) {
        this.couleurs = couleurs;
    }

    public List<TailleSummaryDTO> getTailles() {
        return tailles;
    }

    public void setTailles(List<TailleSummaryDTO> tailles) {
        this.tailles = tailles;
    }

    public List<PromotionSummaryDTO> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<PromotionSummaryDTO> promotions) {
        this.promotions = promotions;
    }
}
