package sn.modiva.app.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.modiva.app.domain.VarianteProduit} entity. This class is used
 * in {@link sn.modiva.app.web.rest.VarianteProduitResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /variante-produits?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VarianteProduitCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter stock;

    private LongFilter tailleId;

    private LongFilter couleurId;

    private LongFilter produitId;

    private Boolean distinct;

    public VarianteProduitCriteria() {}

    public VarianteProduitCriteria(VarianteProduitCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.stock = other.optionalStock().map(IntegerFilter::copy).orElse(null);
        this.tailleId = other.optionalTailleId().map(LongFilter::copy).orElse(null);
        this.couleurId = other.optionalCouleurId().map(LongFilter::copy).orElse(null);
        this.produitId = other.optionalProduitId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VarianteProduitCriteria copy() {
        return new VarianteProduitCriteria(this);
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

    public IntegerFilter getStock() {
        return stock;
    }

    public Optional<IntegerFilter> optionalStock() {
        return Optional.ofNullable(stock);
    }

    public IntegerFilter stock() {
        if (stock == null) {
            setStock(new IntegerFilter());
        }
        return stock;
    }

    public void setStock(IntegerFilter stock) {
        this.stock = stock;
    }

    public LongFilter getTailleId() {
        return tailleId;
    }

    public Optional<LongFilter> optionalTailleId() {
        return Optional.ofNullable(tailleId);
    }

    public LongFilter tailleId() {
        if (tailleId == null) {
            setTailleId(new LongFilter());
        }
        return tailleId;
    }

    public void setTailleId(LongFilter tailleId) {
        this.tailleId = tailleId;
    }

    public LongFilter getCouleurId() {
        return couleurId;
    }

    public Optional<LongFilter> optionalCouleurId() {
        return Optional.ofNullable(couleurId);
    }

    public LongFilter couleurId() {
        if (couleurId == null) {
            setCouleurId(new LongFilter());
        }
        return couleurId;
    }

    public void setCouleurId(LongFilter couleurId) {
        this.couleurId = couleurId;
    }

    public LongFilter getProduitId() {
        return produitId;
    }

    public Optional<LongFilter> optionalProduitId() {
        return Optional.ofNullable(produitId);
    }

    public LongFilter produitId() {
        if (produitId == null) {
            setProduitId(new LongFilter());
        }
        return produitId;
    }

    public void setProduitId(LongFilter produitId) {
        this.produitId = produitId;
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
        final VarianteProduitCriteria that = (VarianteProduitCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(stock, that.stock) &&
            Objects.equals(tailleId, that.tailleId) &&
            Objects.equals(couleurId, that.couleurId) &&
            Objects.equals(produitId, that.produitId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stock, tailleId, couleurId, produitId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VarianteProduitCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStock().map(f -> "stock=" + f + ", ").orElse("") +
            optionalTailleId().map(f -> "tailleId=" + f + ", ").orElse("") +
            optionalCouleurId().map(f -> "couleurId=" + f + ", ").orElse("") +
            optionalProduitId().map(f -> "produitId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
