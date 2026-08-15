package sn.modiva.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import sn.modiva.app.domain.enumeration.StatutLivraison;

/**
 * A DTO for the {@link sn.modiva.app.domain.Livraison} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LivraisonDTO implements Serializable {

    private Long id;

    @NotNull
    private String adresse;

    @NotNull
    private String ville;

    private String quartier;

    private String telephone;

    private BigDecimal fraisLivraison;

    @NotNull
    private StatutLivraison statutLivraison;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getQuartier() {
        return quartier;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public BigDecimal getFraisLivraison() {
        return fraisLivraison;
    }

    public void setFraisLivraison(BigDecimal fraisLivraison) {
        this.fraisLivraison = fraisLivraison;
    }

    public StatutLivraison getStatutLivraison() {
        return statutLivraison;
    }

    public void setStatutLivraison(StatutLivraison statutLivraison) {
        this.statutLivraison = statutLivraison;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LivraisonDTO)) {
            return false;
        }

        LivraisonDTO livraisonDTO = (LivraisonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, livraisonDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LivraisonDTO{" +
            "id=" + getId() +
            ", adresse='" + getAdresse() + "'" +
            ", ville='" + getVille() + "'" +
            ", quartier='" + getQuartier() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", fraisLivraison=" + getFraisLivraison() +
            ", statutLivraison='" + getStatutLivraison() + "'" +
            "}";
    }
}
