package sn.modiva.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class VarianteProduitCriteriaTest {

    @Test
    void newVarianteProduitCriteriaHasAllFiltersNullTest() {
        var varianteProduitCriteria = new VarianteProduitCriteria();
        assertThat(varianteProduitCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void varianteProduitCriteriaFluentMethodsCreatesFiltersTest() {
        var varianteProduitCriteria = new VarianteProduitCriteria();

        setAllFilters(varianteProduitCriteria);

        assertThat(varianteProduitCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void varianteProduitCriteriaCopyCreatesNullFilterTest() {
        var varianteProduitCriteria = new VarianteProduitCriteria();
        var copy = varianteProduitCriteria.copy();

        assertThat(varianteProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(varianteProduitCriteria)
        );
    }

    @Test
    void varianteProduitCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var varianteProduitCriteria = new VarianteProduitCriteria();
        setAllFilters(varianteProduitCriteria);

        var copy = varianteProduitCriteria.copy();

        assertThat(varianteProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(varianteProduitCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var varianteProduitCriteria = new VarianteProduitCriteria();

        assertThat(varianteProduitCriteria).hasToString("VarianteProduitCriteria{}");
    }

    private static void setAllFilters(VarianteProduitCriteria varianteProduitCriteria) {
        varianteProduitCriteria.id();
        varianteProduitCriteria.stock();
        varianteProduitCriteria.tailleId();
        varianteProduitCriteria.couleurId();
        varianteProduitCriteria.produitId();
        varianteProduitCriteria.distinct();
    }

    private static Condition<VarianteProduitCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStock()) &&
                condition.apply(criteria.getTailleId()) &&
                condition.apply(criteria.getCouleurId()) &&
                condition.apply(criteria.getProduitId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<VarianteProduitCriteria> copyFiltersAre(
        VarianteProduitCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStock(), copy.getStock()) &&
                condition.apply(criteria.getTailleId(), copy.getTailleId()) &&
                condition.apply(criteria.getCouleurId(), copy.getCouleurId()) &&
                condition.apply(criteria.getProduitId(), copy.getProduitId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
