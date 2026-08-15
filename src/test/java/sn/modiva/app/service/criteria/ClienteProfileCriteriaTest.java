package sn.modiva.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ClienteProfileCriteriaTest {

    @Test
    void newClienteProfileCriteriaHasAllFiltersNullTest() {
        var clienteProfileCriteria = new ClienteProfileCriteria();
        assertThat(clienteProfileCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void clienteProfileCriteriaFluentMethodsCreatesFiltersTest() {
        var clienteProfileCriteria = new ClienteProfileCriteria();

        setAllFilters(clienteProfileCriteria);

        assertThat(clienteProfileCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void clienteProfileCriteriaCopyCreatesNullFilterTest() {
        var clienteProfileCriteria = new ClienteProfileCriteria();
        var copy = clienteProfileCriteria.copy();

        assertThat(clienteProfileCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(clienteProfileCriteria)
        );
    }

    @Test
    void clienteProfileCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var clienteProfileCriteria = new ClienteProfileCriteria();
        setAllFilters(clienteProfileCriteria);

        var copy = clienteProfileCriteria.copy();

        assertThat(clienteProfileCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(clienteProfileCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var clienteProfileCriteria = new ClienteProfileCriteria();

        assertThat(clienteProfileCriteria).hasToString("ClienteProfileCriteria{}");
    }

    private static void setAllFilters(ClienteProfileCriteria clienteProfileCriteria) {
        clienteProfileCriteria.id();
        clienteProfileCriteria.telephone();
        clienteProfileCriteria.actif();
        clienteProfileCriteria.userId();
        clienteProfileCriteria.panierId();
        clienteProfileCriteria.adresseId();
        clienteProfileCriteria.commandeId();
        clienteProfileCriteria.distinct();
    }

    private static Condition<ClienteProfileCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTelephone()) &&
                condition.apply(criteria.getActif()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getPanierId()) &&
                condition.apply(criteria.getAdresseId()) &&
                condition.apply(criteria.getCommandeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ClienteProfileCriteria> copyFiltersAre(
        ClienteProfileCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTelephone(), copy.getTelephone()) &&
                condition.apply(criteria.getActif(), copy.getActif()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getPanierId(), copy.getPanierId()) &&
                condition.apply(criteria.getAdresseId(), copy.getAdresseId()) &&
                condition.apply(criteria.getCommandeId(), copy.getCommandeId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
