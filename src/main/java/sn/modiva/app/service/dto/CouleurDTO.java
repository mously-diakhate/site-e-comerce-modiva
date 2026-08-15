package sn.modiva.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link sn.modiva.app.domain.Couleur} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CouleurDTO implements Serializable {

    private Long id;

    @NotNull
    private String libelle;

    private String codeHex;

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

    public String getCodeHex() {
        return codeHex;
    }

    public void setCodeHex(String codeHex) {
        this.codeHex = codeHex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CouleurDTO)) {
            return false;
        }

        CouleurDTO couleurDTO = (CouleurDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, couleurDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CouleurDTO{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", codeHex='" + getCodeHex() + "'" +
            "}";
    }
}
