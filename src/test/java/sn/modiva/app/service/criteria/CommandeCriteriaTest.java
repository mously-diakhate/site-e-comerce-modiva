package sn.modiva.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CommandeCriteriaTest {

    @Test
    void newCommandeCriteriaHasAllFiltersNullTest() {
        var commandeCriteria = new CommandeCriteria();
        assertThat(commandeCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void commandeCriteriaFluentMethodsCreatesFiltersTest() {
        var commandeCriteria = new CommandeCriteria();

        setAllFilters(commandeCriteria);

        assertThat(commandeCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void commandeCriteriaCopyCreatesNullFilterTest() {
        var commandeCriteria = new CommandeCriteria();
        var copy = commandeCriteria.copy();

        assertThat(commandeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(commandeCriteria)
        );
    }

    @Test
    void commandeCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var commandeCriteria = new CommandeCriteria();
        setAllFilters(commandeCriteria);

        var copy = commandeCriteria.copy();

        assertThat(commandeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(commandeCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var commandeCriteria = new CommandeCriteria();

        assertThat(commandeCriteria).hasToString("CommandeCriteria{}");
    }

    private static void setAllFilters(CommandeCriteria commandeCriteria) {
        commandeCriteria.id();
        commandeCriteria.numero();
        commandeCriteria.dateCommande();
        commandeCriteria.montantTotal();
        commandeCriteria.modePaiement();
        commandeCriteria.statutCommande();
        commandeCriteria.paiementId();
        commandeCriteria.livraisonId();
        commandeCriteria.ligneId();
        commandeCriteria.clienteProfileId();
        commandeCriteria.distinct();
    }

    private static Condition<CommandeCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNumero()) &&
                condition.apply(criteria.getDateCommande()) &&
                condition.apply(criteria.getMontantTotal()) &&
                condition.apply(criteria.getModePaiement()) &&
                condition.apply(criteria.getStatutCommande()) &&
                condition.apply(criteria.getPaiementId()) &&
                condition.apply(criteria.getLivraisonId()) &&
                condition.apply(criteria.getLigneId()) &&
                condition.apply(criteria.getClienteProfileId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CommandeCriteria> copyFiltersAre(CommandeCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNumero(), copy.getNumero()) &&
                condition.apply(criteria.getDateCommande(), copy.getDateCommande()) &&
                condition.apply(criteria.getMontantTotal(), copy.getMontantTotal()) &&
                condition.apply(criteria.getModePaiement(), copy.getModePaiement()) &&
                condition.apply(criteria.getStatutCommande(), copy.getStatutCommande()) &&
                condition.apply(criteria.getPaiementId(), copy.getPaiementId()) &&
                condition.apply(criteria.getLivraisonId(), copy.getLivraisonId()) &&
                condition.apply(criteria.getLigneId(), copy.getLigneId()) &&
                condition.apply(criteria.getClienteProfileId(), copy.getClienteProfileId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
