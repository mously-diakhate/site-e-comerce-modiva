package sn.modiva.app.service.dto;

public class PromotionSummaryDTO {

    private Long id;
    private String nom;

    public PromotionSummaryDTO() {}

    public PromotionSummaryDTO(Long id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
