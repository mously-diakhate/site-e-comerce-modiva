package sn.modiva.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link sn.modiva.app.domain.LigneCommande} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneCommandeDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer quantite;

    @NotNull
    private BigDecimal prixUnitaire;

    @NotNull
    private VarianteProduitDTO variante;

    @NotNull
    private CommandeDTO commande;

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

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public VarianteProduitDTO getVariante() {
        return variante;
    }

    public void setVariante(VarianteProduitDTO variante) {
        this.variante = variante;
    }

    public CommandeDTO getCommande() {
        return commande;
    }

    public void setCommande(CommandeDTO commande) {
        this.commande = commande;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LigneCommandeDTO)) {
            return false;
        }

        LigneCommandeDTO ligneCommandeDTO = (LigneCommandeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ligneCommandeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneCommandeDTO{" +
            "id=" + getId() +
            ", quantite=" + getQuantite() +
            ", prixUnitaire=" + getPrixUnitaire() +
            ", variante=" + getVariante() +
            ", commande=" + getCommande() +
            "}";
    }
}
