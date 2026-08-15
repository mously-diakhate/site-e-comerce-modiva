package sn.modiva.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import sn.modiva.app.domain.enumeration.StatutPaiement;

/**
 * A Paiement.
 */
@Entity
@Table(name = "paiement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Paiement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "montant", precision = 21, scale = 2, nullable = false)
    private BigDecimal montant;

    @Column(name = "date_paiement")
    private Instant datePaiement;

    @Column(name = "moyen_paiement")
    private String moyenPaiement;

    @Column(name = "reference_transaction")
    private String referenceTransaction;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement", nullable = false)
    private StatutPaiement statutPaiement;

    @JsonIgnoreProperties(value = { "paiement", "livraison", "lignes", "clienteProfile" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "paiement")
    private Commande commande;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Paiement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return this.montant;
    }

    public Paiement montant(BigDecimal montant) {
        this.setMontant(montant);
        return this;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Instant getDatePaiement() {
        return this.datePaiement;
    }

    public Paiement datePaiement(Instant datePaiement) {
        this.setDatePaiement(datePaiement);
        return this;
    }

    public void setDatePaiement(Instant datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getMoyenPaiement() {
        return this.moyenPaiement;
    }

    public Paiement moyenPaiement(String moyenPaiement) {
        this.setMoyenPaiement(moyenPaiement);
        return this;
    }

    public void setMoyenPaiement(String moyenPaiement) {
        this.moyenPaiement = moyenPaiement;
    }

    public String getReferenceTransaction() {
        return this.referenceTransaction;
    }

    public Paiement referenceTransaction(String referenceTransaction) {
        this.setReferenceTransaction(referenceTransaction);
        return this;
    }

    public void setReferenceTransaction(String referenceTransaction) {
        this.referenceTransaction = referenceTransaction;
    }

    public StatutPaiement getStatutPaiement() {
        return this.statutPaiement;
    }

    public Paiement statutPaiement(StatutPaiement statutPaiement) {
        this.setStatutPaiement(statutPaiement);
        return this;
    }

    public void setStatutPaiement(StatutPaiement statutPaiement) {
        this.statutPaiement = statutPaiement;
    }

    public Commande getCommande() {
        return this.commande;
    }

    public void setCommande(Commande commande) {
        if (this.commande != null) {
            this.commande.setPaiement(null);
        }
        if (commande != null) {
            commande.setPaiement(this);
        }
        this.commande = commande;
    }

    public Paiement commande(Commande commande) {
        this.setCommande(commande);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Paiement)) {
            return false;
        }
        return getId() != null && getId().equals(((Paiement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Paiement{" +
            "id=" + getId() +
            ", montant=" + getMontant() +
            ", datePaiement='" + getDatePaiement() + "'" +
            ", moyenPaiement='" + getMoyenPaiement() + "'" +
            ", referenceTransaction='" + getReferenceTransaction() + "'" +
            ", statutPaiement='" + getStatutPaiement() + "'" +
            "}";
    }
}
