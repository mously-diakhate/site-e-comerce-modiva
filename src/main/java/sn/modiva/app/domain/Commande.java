package sn.modiva.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import sn.modiva.app.domain.enumeration.StatutCommande;

/**
 * A Commande.
 */
@Entity
@Table(name = "commande")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Commande implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "numero", nullable = false, unique = true)
    private String numero;

    @NotNull
    @Column(name = "date_commande", nullable = false)
    private Instant dateCommande;

    @NotNull
    @Column(name = "montant_total", precision = 21, scale = 2, nullable = false)
    private BigDecimal montantTotal;

    @Column(name = "mode_paiement")
    private String modePaiement;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_commande", nullable = false)
    private StatutCommande statutCommande;

    @JsonIgnoreProperties(value = { "commande" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Paiement paiement;

    @JsonIgnoreProperties(value = { "commande" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Livraison livraison;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "commande")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "variante", "commande" }, allowSetters = true)
    private Set<LigneCommande> lignes = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "panier", "adresses", "commandes" }, allowSetters = true)
    private ClienteProfile clienteProfile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Commande id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return this.numero;
    }

    public Commande numero(String numero) {
        this.setNumero(numero);
        return this;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Instant getDateCommande() {
        return this.dateCommande;
    }

    public Commande dateCommande(Instant dateCommande) {
        this.setDateCommande(dateCommande);
        return this;
    }

    public void setDateCommande(Instant dateCommande) {
        this.dateCommande = dateCommande;
    }

    public BigDecimal getMontantTotal() {
        return this.montantTotal;
    }

    public Commande montantTotal(BigDecimal montantTotal) {
        this.setMontantTotal(montantTotal);
        return this;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getModePaiement() {
        return this.modePaiement;
    }

    public Commande modePaiement(String modePaiement) {
        this.setModePaiement(modePaiement);
        return this;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public StatutCommande getStatutCommande() {
        return this.statutCommande;
    }

    public Commande statutCommande(StatutCommande statutCommande) {
        this.setStatutCommande(statutCommande);
        return this;
    }

    public void setStatutCommande(StatutCommande statutCommande) {
        this.statutCommande = statutCommande;
    }

    public Paiement getPaiement() {
        return this.paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }

    public Commande paiement(Paiement paiement) {
        this.setPaiement(paiement);
        return this;
    }

    public Livraison getLivraison() {
        return this.livraison;
    }

    public void setLivraison(Livraison livraison) {
        this.livraison = livraison;
    }

    public Commande livraison(Livraison livraison) {
        this.setLivraison(livraison);
        return this;
    }

    public Set<LigneCommande> getLignes() {
        return this.lignes;
    }

    public void setLignes(Set<LigneCommande> ligneCommandes) {
        if (this.lignes != null) {
            this.lignes.forEach(i -> i.setCommande(null));
        }
        if (ligneCommandes != null) {
            ligneCommandes.forEach(i -> i.setCommande(this));
        }
        this.lignes = ligneCommandes;
    }

    public Commande lignes(Set<LigneCommande> ligneCommandes) {
        this.setLignes(ligneCommandes);
        return this;
    }

    public Commande addLigne(LigneCommande ligneCommande) {
        this.lignes.add(ligneCommande);
        ligneCommande.setCommande(this);
        return this;
    }

    public Commande removeLigne(LigneCommande ligneCommande) {
        this.lignes.remove(ligneCommande);
        ligneCommande.setCommande(null);
        return this;
    }

    public ClienteProfile getClienteProfile() {
        return this.clienteProfile;
    }

    public void setClienteProfile(ClienteProfile clienteProfile) {
        this.clienteProfile = clienteProfile;
    }

    public Commande clienteProfile(ClienteProfile clienteProfile) {
        this.setClienteProfile(clienteProfile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Commande)) {
            return false;
        }
        return getId() != null && getId().equals(((Commande) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Commande{" +
            "id=" + getId() +
            ", numero='" + getNumero() + "'" +
            ", dateCommande='" + getDateCommande() + "'" +
            ", montantTotal=" + getMontantTotal() +
            ", modePaiement='" + getModePaiement() + "'" +
            ", statutCommande='" + getStatutCommande() + "'" +
            "}";
    }
}
