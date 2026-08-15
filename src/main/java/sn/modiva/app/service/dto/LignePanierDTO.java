package sn.modiva.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link sn.modiva.app.domain.LignePanier} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LignePanierDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer quantite;

    @NotNull
    private VarianteProduitDTO variante;

    @NotNull
    private PanierDTO panier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public VarianteProduitDTO getVariante() {
        return variante;
    }

    public void setVariante(VarianteProduitDTO variante) {
        this.variante = variante;
    }

    public PanierDTO getPanier() {
        return panier;
    }

    public void setPanier(PanierDTO panier) {
        this.panier = panier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LignePanierDTO)) {
            return false;
        }

        LignePanierDTO lignePanierDTO = (LignePanierDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, lignePanierDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LignePanierDTO{" +
            "id=" + getId() +
            ", quantite=" + getQuantite() +
            ", variante=" + getVariante() +
            ", panier=" + getPanier() +
            "}";
    }
}
