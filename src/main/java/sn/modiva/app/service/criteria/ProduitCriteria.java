package sn.modiva.app.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import sn.modiva.app.domain.enumeration.StatutProduit;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.modiva.app.domain.Produit} entity. This class is used
 * in {@link sn.modiva.app.web.rest.ProduitResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /produits?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProduitCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatutProduit
     */
    public static class StatutProduitFilter extends Filter<StatutProduit> {

        public StatutProduitFilter() {}

        public StatutProduitFilter(StatutProduitFilter filter) {
            super(filter);
        }

        @Override
        public StatutProduitFilter copy() {
            return new StatutProduitFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nom;

    private BigDecimalFilter prix;

    private StatutProduitFilter statut;

    private LongFilter imageId;

    private LongFilter varianteId;

    private LongFilter promotionId;

    private LongFilter categorieId;

    private Boolean distinct;

    public ProduitCriteria() {}

    public ProduitCriteria(ProduitCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nom = other.optionalNom().map(StringFilter::copy).orElse(null);
        this.prix = other.optionalPrix().map(BigDecimalFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutProduitFilter::copy).orElse(null);
        this.imageId = other.optionalImageId().map(LongFilter::copy).orElse(null);
        this.varianteId = other.optionalVarianteId().map(LongFilter::copy).orElse(null);
        this.promotionId = other.optionalPromotionId().map(LongFilter::copy).orElse(null);
        this.categorieId = other.optionalCategorieId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProduitCriteria copy() {
        return new ProduitCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNom() {
        return nom;
    }

    public Optional<StringFilter> optionalNom() {
        return Optional.ofNullable(nom);
    }

    public StringFilter nom() {
        if (nom == null) {
            setNom(new StringFilter());
        }
        return nom;
    }

    public void setNom(StringFilter nom) {
        this.nom = nom;
    }

    public BigDecimalFilter getPrix() {
        return prix;
    }

    public Optional<BigDecimalFilter> optionalPrix() {
        return Optional.ofNullable(prix);
    }

    public BigDecimalFilter prix() {
        if (prix == null) {
            setPrix(new BigDecimalFilter());
        }
        return prix;
    }

    public void setPrix(BigDecimalFilter prix) {
        this.prix = prix;
    }

    public StatutProduitFilter getStatut() {
        return statut;
    }

    public Optional<StatutProduitFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutProduitFilter statut() {
        if (statut == null) {
            setStatut(new StatutProduitFilter());
        }
        return statut;
    }

    public void setStatut(StatutProduitFilter statut) {
        this.statut = statut;
    }

    public LongFilter getImageId() {
        return imageId;
    }

    public Optional<LongFilter> optionalImageId() {
        return Optional.ofNullable(imageId);
    }

    public LongFilter imageId() {
        if (imageId == null) {
            setImageId(new LongFilter());
        }
        return imageId;
    }

    public void setImageId(LongFilter imageId) {
        this.imageId = imageId;
    }

    public LongFilter getVarianteId() {
        return varianteId;
    }

    public Optional<LongFilter> optionalVarianteId() {
        return Optional.ofNullable(varianteId);
    }

    public LongFilter varianteId() {
        if (varianteId == null) {
            setVarianteId(new LongFilter());
        }
        return varianteId;
    }

    public void setVarianteId(LongFilter varianteId) {
        this.varianteId = varianteId;
    }

    public LongFilter getPromotionId() {
        return promotionId;
    }

    public Optional<LongFilter> optionalPromotionId() {
        return Optional.ofNullable(promotionId);
    }

    public LongFilter promotionId() {
        if (promotionId == null) {
            setPromotionId(new LongFilter());
        }
        return promotionId;
    }

    public void setPromotionId(LongFilter promotionId) {
        this.promotionId = promotionId;
    }

    public LongFilter getCategorieId() {
        return categorieId;
    }

    public Optional<LongFilter> optionalCategorieId() {
        return Optional.ofNullable(categorieId);
    }

    public LongFilter categorieId() {
        if (categorieId == null) {
            setCategorieId(new LongFilter());
        }
        return categorieId;
    }

    public void setCategorieId(LongFilter categorieId) {
        this.categorieId = categorieId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProduitCriteria that = (ProduitCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(prix, that.prix) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(imageId, that.imageId) &&
            Objects.equals(varianteId, that.varianteId) &&
            Objects.equals(promotionId, that.promotionId) &&
            Objects.equals(categorieId, that.categorieId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, prix, statut, imageId, varianteId, promotionId, categorieId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProduitCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNom().map(f -> "nom=" + f + ", ").orElse("") +
            optionalPrix().map(f -> "prix=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalImageId().map(f -> "imageId=" + f + ", ").orElse("") +
            optionalVarianteId().map(f -> "varianteId=" + f + ", ").orElse("") +
            optionalPromotionId().map(f -> "promotionId=" + f + ", ").orElse("") +
            optionalCategorieId().map(f -> "categorieId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
