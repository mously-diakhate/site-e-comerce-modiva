package sn.modiva.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link sn.modiva.app.domain.Promotion} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PromotionDTO implements Serializable {

    private Long id;

    @NotNull
    private String nom;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal reduction;

    @NotNull
    private Instant dateDebut;

    @NotNull
    private Instant dateFin;

    private Set<ProduitDTO> produits = new HashSet<>();

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

    public BigDecimal getReduction() {
        return reduction;
    }

    public void setReduction(BigDecimal reduction) {
        this.reduction = reduction;
    }

    public Instant getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Instant dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Instant getDateFin() {
        return dateFin;
    }

    public void setDateFin(Instant dateFin) {
        this.dateFin = dateFin;
    }

    public Set<ProduitDTO> getProduits() {
        return produits;
    }

    public void setProduits(Set<ProduitDTO> produits) {
        this.produits = produits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PromotionDTO)) {
            return false;
        }

        PromotionDTO promotionDTO = (PromotionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, promotionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PromotionDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", reduction=" + getReduction() +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            ", produits=" + getProduits() +
            "}";
    }
}
