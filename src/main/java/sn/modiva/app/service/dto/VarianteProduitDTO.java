package sn.modiva.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link sn.modiva.app.domain.VarianteProduit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VarianteProduitDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 0)
    private Integer stock;

    @NotNull
    private TailleDTO taille;

    @NotNull
    private CouleurDTO couleur;

    @NotNull
    private ProduitDTO produit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public TailleDTO getTaille() {
        return taille;
    }

    public void setTaille(TailleDTO taille) {
        this.taille = taille;
    }

    public CouleurDTO getCouleur() {
        return couleur;
    }

    public void setCouleur(CouleurDTO couleur) {
        this.couleur = couleur;
    }

    public ProduitDTO getProduit() {
        return produit;
    }

    public void setProduit(ProduitDTO produit) {
        this.produit = produit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VarianteProduitDTO)) {
            return false;
        }

        VarianteProduitDTO varianteProduitDTO = (VarianteProduitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, varianteProduitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VarianteProduitDTO{" +
            "id=" + getId() +
            ", stock=" + getStock() +
            ", taille=" + getTaille() +
            ", couleur=" + getCouleur() +
            ", produit=" + getProduit() +
            "}";
    }
}
