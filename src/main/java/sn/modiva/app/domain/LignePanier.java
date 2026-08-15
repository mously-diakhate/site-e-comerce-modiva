package sn.modiva.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LignePanier.
 */
@Entity
@Table(name = "ligne_panier")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LignePanier implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "taille", "couleur", "produit" }, allowSetters = true)
    private VarianteProduit variante;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "lignes", "clienteProfile" }, allowSetters = true)
    private Panier panier;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LignePanier id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantite() {
        return this.quantite;
    }

    public LignePanier quantite(Integer quantite) {
        this.setQuantite(quantite);
        return this;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public VarianteProduit getVariante() {
        return this.variante;
    }

    public void setVariante(VarianteProduit varianteProduit) {
        this.variante = varianteProduit;
    }

    public LignePanier variante(VarianteProduit varianteProduit) {
        this.setVariante(varianteProduit);
        return this;
    }

    public Panier getPanier() {
        return this.panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    public LignePanier panier(Panier panier) {
        this.setPanier(panier);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LignePanier)) {
            return false;
        }
        return getId() != null && getId().equals(((LignePanier) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LignePanier{" +
            "id=" + getId() +
            ", quantite=" + getQuantite() +
            "}";
    }
}
