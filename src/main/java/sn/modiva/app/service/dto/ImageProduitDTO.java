package sn.modiva.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link sn.modiva.app.domain.ImageProduit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImageProduitDTO implements Serializable {

    private Long id;

    @NotNull
    private String url;

    private Integer ordre;

    @NotNull
    private ProduitDTO produit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
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
        if (!(o instanceof ImageProduitDTO)) {
            return false;
        }

        ImageProduitDTO imageProduitDTO = (ImageProduitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, imageProduitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImageProduitDTO{" +
            "id=" + getId() +
            ", url='" + getUrl() + "'" +
            ", ordre=" + getOrdre() +
            ", produit=" + getProduit() +
            "}";
    }
}
