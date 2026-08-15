package sn.modiva.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link sn.modiva.app.domain.Adresse} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AdresseDTO implements Serializable {

    private Long id;

    private String libelle;

    @NotNull
    private String adresse;

    @NotNull
    private String ville;

    private String quartier;

    private String telephone;

    private Boolean parDefaut;

    @NotNull
    private ClienteProfileDTO clienteProfile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
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

    public Boolean getParDefaut() {
        return parDefaut;
    }

    public void setParDefaut(Boolean parDefaut) {
        this.parDefaut = parDefaut;
    }

    public ClienteProfileDTO getClienteProfile() {
        return clienteProfile;
    }

    public void setClienteProfile(ClienteProfileDTO clienteProfile) {
        this.clienteProfile = clienteProfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AdresseDTO)) {
            return false;
        }

        AdresseDTO adresseDTO = (AdresseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, adresseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AdresseDTO{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", adresse='" + getAdresse() + "'" +
            ", ville='" + getVille() + "'" +
            ", quartier='" + getQuartier() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", parDefaut='" + getParDefaut() + "'" +
            ", clienteProfile=" + getClienteProfile() +
            "}";
    }
}
