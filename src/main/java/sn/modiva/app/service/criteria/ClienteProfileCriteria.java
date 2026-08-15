package sn.modiva.app.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.modiva.app.domain.ClienteProfile} entity. This class is used
 * in {@link sn.modiva.app.web.rest.ClienteProfileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cliente-profiles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClienteProfileCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter telephone;

    private BooleanFilter actif;

    private LongFilter userId;

    private LongFilter panierId;

    private LongFilter adresseId;

    private LongFilter commandeId;

    private Boolean distinct;

    public ClienteProfileCriteria() {}

    public ClienteProfileCriteria(ClienteProfileCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.telephone = other.optionalTelephone().map(StringFilter::copy).orElse(null);
        this.actif = other.optionalActif().map(BooleanFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.panierId = other.optionalPanierId().map(LongFilter::copy).orElse(null);
        this.adresseId = other.optionalAdresseId().map(LongFilter::copy).orElse(null);
        this.commandeId = other.optionalCommandeId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ClienteProfileCriteria copy() {
        return new ClienteProfileCriteria(this);
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

    public StringFilter getTelephone() {
        return telephone;
    }

    public Optional<StringFilter> optionalTelephone() {
        return Optional.ofNullable(telephone);
    }

    public StringFilter telephone() {
        if (telephone == null) {
            setTelephone(new StringFilter());
        }
        return telephone;
    }

    public void setTelephone(StringFilter telephone) {
        this.telephone = telephone;
    }

    public BooleanFilter getActif() {
        return actif;
    }

    public Optional<BooleanFilter> optionalActif() {
        return Optional.ofNullable(actif);
    }

    public BooleanFilter actif() {
        if (actif == null) {
            setActif(new BooleanFilter());
        }
        return actif;
    }

    public void setActif(BooleanFilter actif) {
        this.actif = actif;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getPanierId() {
        return panierId;
    }

    public Optional<LongFilter> optionalPanierId() {
        return Optional.ofNullable(panierId);
    }

    public LongFilter panierId() {
        if (panierId == null) {
            setPanierId(new LongFilter());
        }
        return panierId;
    }

    public void setPanierId(LongFilter panierId) {
        this.panierId = panierId;
    }

    public LongFilter getAdresseId() {
        return adresseId;
    }

    public Optional<LongFilter> optionalAdresseId() {
        return Optional.ofNullable(adresseId);
    }

    public LongFilter adresseId() {
        if (adresseId == null) {
            setAdresseId(new LongFilter());
        }
        return adresseId;
    }

    public void setAdresseId(LongFilter adresseId) {
        this.adresseId = adresseId;
    }

    public LongFilter getCommandeId() {
        return commandeId;
    }

    public Optional<LongFilter> optionalCommandeId() {
        return Optional.ofNullable(commandeId);
    }

    public LongFilter commandeId() {
        if (commandeId == null) {
            setCommandeId(new LongFilter());
        }
        return commandeId;
    }

    public void setCommandeId(LongFilter commandeId) {
        this.commandeId = commandeId;
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
        final ClienteProfileCriteria that = (ClienteProfileCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(telephone, that.telephone) &&
            Objects.equals(actif, that.actif) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(panierId, that.panierId) &&
            Objects.equals(adresseId, that.adresseId) &&
            Objects.equals(commandeId, that.commandeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, telephone, actif, userId, panierId, adresseId, commandeId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClienteProfileCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTelephone().map(f -> "telephone=" + f + ", ").orElse("") +
            optionalActif().map(f -> "actif=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalPanierId().map(f -> "panierId=" + f + ", ").orElse("") +
            optionalAdresseId().map(f -> "adresseId=" + f + ", ").orElse("") +
            optionalCommandeId().map(f -> "commandeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
