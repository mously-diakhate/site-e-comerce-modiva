package sn.modiva.app.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link sn.modiva.app.domain.ClienteProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClienteProfileDTO implements Serializable {

    private Long id;

    private String telephone;

    private Boolean actif;

    // NB : @NotNull retiré pour permettre un ClienteProfile "invité" (commande
    // sans compte utilisateur) — voir la migration Liquibase associée qui
    // retire la contrainte NOT NULL sur la colonne user_id.
    private UserDTO user;

    private PanierDTO panier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
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
        if (!(o instanceof ClienteProfileDTO)) {
            return false;
        }

        ClienteProfileDTO clienteProfileDTO = (ClienteProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, clienteProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClienteProfileDTO{" +
            "id=" + getId() +
            ", telephone='" + getTelephone() + "'" +
            ", actif='" + getActif() + "'" +
            ", user=" + getUser() +
            ", panier=" + getPanier() +
            "}";
    }
}
