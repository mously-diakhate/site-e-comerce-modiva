package sn.modiva.app.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import sn.modiva.app.domain.enumeration.StatutProduit;

/**
 * A DTO for the {@link sn.modiva.app.domain.Produit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProduitDTO implements Serializable {

    private Long id;

    @NotNull
    private String nom;

    @Lob
    private String description;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal prix;

    @NotNull
    private StatutProduit statut;

    private Set<PromotionDTO> promotions = new HashSet<>();

    @NotNull
    private CategorieDTO categorie;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public StatutProduit getStatut() {
        return statut;
    }

    public void setStatut(StatutProduit statut) {
        this.statut = statut;
    }

    public Set<PromotionDTO> getPromotions() {
        return promotions;
    }

    public void setPromotions(Set<PromotionDTO> promotions) {
        this.promotions = promotions;
    }

    public CategorieDTO getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieDTO categorie) {
        this.categorie = categorie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProduitDTO)) {
            return false;
        }

        ProduitDTO produitDTO = (ProduitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, produitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProduitDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", description='" + getDescription() + "'" +
            ", prix=" + getPrix() +
            ", statut='" + getStatut() + "'" +
            ", promotions=" + getPromotions() +
            ", categorie=" + getCategorie() +
            "}";
    }
}
