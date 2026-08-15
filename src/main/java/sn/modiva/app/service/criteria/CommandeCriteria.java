package sn.modiva.app.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import sn.modiva.app.domain.enumeration.StatutCommande;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.modiva.app.domain.Commande} entity. This class is used
 * in {@link sn.modiva.app.web.rest.CommandeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /commandes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommandeCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatutCommande
     */
    public static class StatutCommandeFilter extends Filter<StatutCommande> {

        public StatutCommandeFilter() {}

        public StatutCommandeFilter(StatutCommandeFilter filter) {
            super(filter);
        }

        @Override
        public StatutCommandeFilter copy() {
            return new StatutCommandeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter numero;

    private InstantFilter dateCommande;

    private BigDecimalFilter montantTotal;

    private StringFilter modePaiement;

    private StatutCommandeFilter statutCommande;

    private LongFilter paiementId;

    private LongFilter livraisonId;

    private LongFilter ligneId;

    private LongFilter clienteProfileId;

    private Boolean distinct;

    public CommandeCriteria() {}

    public CommandeCriteria(CommandeCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.numero = other.optionalNumero().map(StringFilter::copy).orElse(null);
        this.dateCommande = other.optionalDateCommande().map(InstantFilter::copy).orElse(null);
        this.montantTotal = other.optionalMontantTotal().map(BigDecimalFilter::copy).orElse(null);
        this.modePaiement = other.optionalModePaiement().map(StringFilter::copy).orElse(null);
        this.statutCommande = other.optionalStatutCommande().map(StatutCommandeFilter::copy).orElse(null);
        this.paiementId = other.optionalPaiementId().map(LongFilter::copy).orElse(null);
        this.livraisonId = other.optionalLivraisonId().map(LongFilter::copy).orElse(null);
        this.ligneId = other.optionalLigneId().map(LongFilter::copy).orElse(null);
        this.clienteProfileId = other.optionalClienteProfileId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CommandeCriteria copy() {
        return new CommandeCriteria(this);
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

    public StringFilter getNumero() {
        return numero;
    }

    public Optional<StringFilter> optionalNumero() {
        return Optional.ofNullable(numero);
    }

    public StringFilter numero() {
        if (numero == null) {
            setNumero(new StringFilter());
        }
        return numero;
    }

    public void setNumero(StringFilter numero) {
        this.numero = numero;
    }

    public InstantFilter getDateCommande() {
        return dateCommande;
    }

    public Optional<InstantFilter> optionalDateCommande() {
        return Optional.ofNullable(dateCommande);
    }

    public InstantFilter dateCommande() {
        if (dateCommande == null) {
            setDateCommande(new InstantFilter());
        }
        return dateCommande;
    }

    public void setDateCommande(InstantFilter dateCommande) {
        this.dateCommande = dateCommande;
    }

    public BigDecimalFilter getMontantTotal() {
        return montantTotal;
    }

    public Optional<BigDecimalFilter> optionalMontantTotal() {
        return Optional.ofNullable(montantTotal);
    }

    public BigDecimalFilter montantTotal() {
        if (montantTotal == null) {
            setMontantTotal(new BigDecimalFilter());
        }
        return montantTotal;
    }

    public void setMontantTotal(BigDecimalFilter montantTotal) {
        this.montantTotal = montantTotal;
    }

    public StringFilter getModePaiement() {
        return modePaiement;
    }

    public Optional<StringFilter> optionalModePaiement() {
        return Optional.ofNullable(modePaiement);
    }

    public StringFilter modePaiement() {
        if (modePaiement == null) {
            setModePaiement(new StringFilter());
        }
        return modePaiement;
    }

    public void setModePaiement(StringFilter modePaiement) {
        this.modePaiement = modePaiement;
    }

    public StatutCommandeFilter getStatutCommande() {
        return statutCommande;
    }

    public Optional<StatutCommandeFilter> optionalStatutCommande() {
        return Optional.ofNullable(statutCommande);
    }

    public StatutCommandeFilter statutCommande() {
        if (statutCommande == null) {
            setStatutCommande(new StatutCommandeFilter());
        }
        return statutCommande;
    }

    public void setStatutCommande(StatutCommandeFilter statutCommande) {
        this.statutCommande = statutCommande;
    }

    public LongFilter getPaiementId() {
        return paiementId;
    }

    public Optional<LongFilter> optionalPaiementId() {
        return Optional.ofNullable(paiementId);
    }

    public LongFilter paiementId() {
        if (paiementId == null) {
            setPaiementId(new LongFilter());
        }
        return paiementId;
    }

    public void setPaiementId(LongFilter paiementId) {
        this.paiementId = paiementId;
    }

    public LongFilter getLivraisonId() {
        return livraisonId;
    }

    public Optional<LongFilter> optionalLivraisonId() {
        return Optional.ofNullable(livraisonId);
    }

    public LongFilter livraisonId() {
        if (livraisonId == null) {
            setLivraisonId(new LongFilter());
        }
        return livraisonId;
    }

    public void setLivraisonId(LongFilter livraisonId) {
        this.livraisonId = livraisonId;
    }

    public LongFilter getLigneId() {
        return ligneId;
    }

    public Optional<LongFilter> optionalLigneId() {
        return Optional.ofNullable(ligneId);
    }

    public LongFilter ligneId() {
        if (ligneId == null) {
            setLigneId(new LongFilter());
        }
        return ligneId;
    }

    public void setLigneId(LongFilter ligneId) {
        this.ligneId = ligneId;
    }

    public LongFilter getClienteProfileId() {
        return clienteProfileId;
    }

    public Optional<LongFilter> optionalClienteProfileId() {
        return Optional.ofNullable(clienteProfileId);
    }

    public LongFilter clienteProfileId() {
        if (clienteProfileId == null) {
            setClienteProfileId(new LongFilter());
        }
        return clienteProfileId;
    }

    public void setClienteProfileId(LongFilter clienteProfileId) {
        this.clienteProfileId = clienteProfileId;
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
        final CommandeCriteria that = (CommandeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(numero, that.numero) &&
            Objects.equals(dateCommande, that.dateCommande) &&
            Objects.equals(montantTotal, that.montantTotal) &&
            Objects.equals(modePaiement, that.modePaiement) &&
            Objects.equals(statutCommande, that.statutCommande) &&
            Objects.equals(paiementId, that.paiementId) &&
            Objects.equals(livraisonId, that.livraisonId) &&
            Objects.equals(ligneId, that.ligneId) &&
            Objects.equals(clienteProfileId, that.clienteProfileId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            numero,
            dateCommande,
            montantTotal,
            modePaiement,
            statutCommande,
            paiementId,
            livraisonId,
            ligneId,
            clienteProfileId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommandeCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNumero().map(f -> "numero=" + f + ", ").orElse("") +
            optionalDateCommande().map(f -> "dateCommande=" + f + ", ").orElse("") +
            optionalMontantTotal().map(f -> "montantTotal=" + f + ", ").orElse("") +
            optionalModePaiement().map(f -> "modePaiement=" + f + ", ").orElse("") +
            optionalStatutCommande().map(f -> "statutCommande=" + f + ", ").orElse("") +
            optionalPaiementId().map(f -> "paiementId=" + f + ", ").orElse("") +
            optionalLivraisonId().map(f -> "livraisonId=" + f + ", ").orElse("") +
            optionalLigneId().map(f -> "ligneId=" + f + ", ").orElse("") +
            optionalClienteProfileId().map(f -> "clienteProfileId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
