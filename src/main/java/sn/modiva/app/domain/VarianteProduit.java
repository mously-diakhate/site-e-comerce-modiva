package sn.modiva.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A VarianteProduit.
 */
@Entity
@Table(name = "variante_produit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VarianteProduit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 0)
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @ManyToOne(optional = false)
    @NotNull
    private Taille taille;

    @ManyToOne(optional = false)
    @NotNull
    private Couleur couleur;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "images", "variantes", "promotions", "categorie" }, allowSetters = true)
    private Produit produit;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VarianteProduit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStock() {
        return this.stock;
    }

    public VarianteProduit stock(Integer stock) {
        this.setStock(stock);
        return this;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Taille getTaille() {
        return this.taille;
    }

    public void setTaille(Taille taille) {
        this.taille = taille;
    }

    public VarianteProduit taille(Taille taille) {
        this.setTaille(taille);
        return this;
    }

    public Couleur getCouleur() {
        return this.couleur;
    }

    public void setCouleur(Couleur couleur) {
        this.couleur = couleur;
    }

    public VarianteProduit couleur(Couleur couleur) {
        this.setCouleur(couleur);
        return this;
    }

    public Produit getProduit() {
        return this.produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public VarianteProduit produit(Produit produit) {
        this.setProduit(produit);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VarianteProduit)) {
            return false;
        }
        return getId() != null && getId().equals(((VarianteProduit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VarianteProduit{" +
            "id=" + getId() +
            ", stock=" + getStock() +
            "}";
    }
}
