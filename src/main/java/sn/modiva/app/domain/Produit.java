package sn.modiva.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import sn.modiva.app.domain.enumeration.StatutProduit;

/**
 * A Produit.
 */
@Entity
@Table(name = "produit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Produit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @Lob
    @Column(name = "description")
    private String description;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "prix", precision = 21, scale = 2, nullable = false)
    private BigDecimal prix;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutProduit statut;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "produit")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "produit" }, allowSetters = true)
    private Set<ImageProduit> images = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "produit")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "taille", "couleur", "produit" }, allowSetters = true)
    private Set<VarianteProduit> variantes = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_produit__promotion",
        joinColumns = @JoinColumn(name = "produit_id"),
        inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "produits" }, allowSetters = true)
    private Set<Promotion> promotions = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "produits" }, allowSetters = true)
    private Categorie categorie;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Produit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Produit nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return this.description;
    }

    public Produit description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrix() {
        return this.prix;
    }

    public Produit prix(BigDecimal prix) {
        this.setPrix(prix);
        return this;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public StatutProduit getStatut() {
        return this.statut;
    }

    public Produit statut(StatutProduit statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutProduit statut) {
        this.statut = statut;
    }

    public Set<ImageProduit> getImages() {
        return this.images;
    }

    public void setImages(Set<ImageProduit> imageProduits) {
        if (this.images != null) {
            this.images.forEach(i -> i.setProduit(null));
        }
        if (imageProduits != null) {
            imageProduits.forEach(i -> i.setProduit(this));
        }
        this.images = imageProduits;
    }

    public Produit images(Set<ImageProduit> imageProduits) {
        this.setImages(imageProduits);
        return this;
    }

    public Produit addImage(ImageProduit imageProduit) {
        this.images.add(imageProduit);
        imageProduit.setProduit(this);
        return this;
    }

    public Produit removeImage(ImageProduit imageProduit) {
        this.images.remove(imageProduit);
        imageProduit.setProduit(null);
        return this;
    }

    public Set<VarianteProduit> getVariantes() {
        return this.variantes;
    }

    public void setVariantes(Set<VarianteProduit> varianteProduits) {
        if (this.variantes != null) {
            this.variantes.forEach(i -> i.setProduit(null));
        }
        if (varianteProduits != null) {
            varianteProduits.forEach(i -> i.setProduit(this));
        }
        this.variantes = varianteProduits;
    }

    public Produit variantes(Set<VarianteProduit> varianteProduits) {
        this.setVariantes(varianteProduits);
        return this;
    }

    public Produit addVariante(VarianteProduit varianteProduit) {
        this.variantes.add(varianteProduit);
        varianteProduit.setProduit(this);
        return this;
    }

    public Produit removeVariante(VarianteProduit varianteProduit) {
        this.variantes.remove(varianteProduit);
        varianteProduit.setProduit(null);
        return this;
    }

    public Set<Promotion> getPromotions() {
        return this.promotions;
    }

    public void setPromotions(Set<Promotion> promotions) {
        this.promotions = promotions;
    }

    public Produit promotions(Set<Promotion> promotions) {
        this.setPromotions(promotions);
        return this;
    }

    public Produit addPromotion(Promotion promotion) {
        this.promotions.add(promotion);
        return this;
    }

    public Produit removePromotion(Promotion promotion) {
        this.promotions.remove(promotion);
        return this;
    }

    public Categorie getCategorie() {
        return this.categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Produit categorie(Categorie categorie) {
        this.setCategorie(categorie);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Produit)) {
            return false;
        }
        return getId() != null && getId().equals(((Produit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Produit{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", description='" + getDescription() + "'" +
            ", prix=" + getPrix() +
            ", statut='" + getStatut() + "'" +
            "}";
    }
}
