package sn.modiva.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProduitCriteriaTest {

    @Test
    void newProduitCriteriaHasAllFiltersNullTest() {
        var produitCriteria = new ProduitCriteria();
        assertThat(produitCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void produitCriteriaFluentMethodsCreatesFiltersTest() {
        var produitCriteria = new ProduitCriteria();

        setAllFilters(produitCriteria);

        assertThat(produitCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void produitCriteriaCopyCreatesNullFilterTest() {
        var produitCriteria = new ProduitCriteria();
        var copy = produitCriteria.copy();

        assertThat(produitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(produitCriteria)
        );
    }

    @Test
    void produitCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var produitCriteria = new ProduitCriteria();
        setAllFilters(produitCriteria);

        var copy = produitCriteria.copy();

        assertThat(produitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(produitCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var produitCriteria = new ProduitCriteria();

        assertThat(produitCriteria).hasToString("ProduitCriteria{}");
    }

    private static void setAllFilters(ProduitCriteria produitCriteria) {
        produitCriteria.id();
        produitCriteria.nom();
        produitCriteria.prix();
        produitCriteria.statut();
        produitCriteria.imageId();
        produitCriteria.varianteId();
        produitCriteria.promotionId();
        produitCriteria.categorieId();
        produitCriteria.distinct();
    }

    private static Condition<ProduitCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNom()) &&
                condition.apply(criteria.getPrix()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getImageId()) &&
                condition.apply(criteria.getVarianteId()) &&
                condition.apply(criteria.getPromotionId()) &&
                condition.apply(criteria.getCategorieId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProduitCriteria> copyFiltersAre(ProduitCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNom(), copy.getNom()) &&
                condition.apply(criteria.getPrix(), copy.getPrix()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getImageId(), copy.getImageId()) &&
                condition.apply(criteria.getVarianteId(), copy.getVarianteId()) &&
                condition.apply(criteria.getPromotionId(), copy.getPromotionId()) &&
                condition.apply(criteria.getCategorieId(), copy.getCategorieId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
