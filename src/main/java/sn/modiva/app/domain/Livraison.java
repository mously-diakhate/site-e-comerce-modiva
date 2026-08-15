package sn.modiva.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import sn.modiva.app.domain.enumeration.StatutLivraison;

/**
 * A Livraison.
 */
@Entity
@Table(name = "livraison")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Livraison implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "adresse", nullable = false)
    private String adresse;

    @NotNull
    @Column(name = "ville", nullable = false)
    private String ville;

    @Column(name = "quartier")
    private String quartier;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "frais_livraison", precision = 21, scale = 2)
    private BigDecimal fraisLivraison;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_livraison", nullable = false)
    private StatutLivraison statutLivraison;

    @JsonIgnoreProperties(value = { "paiement", "livraison", "lignes", "clienteProfile" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "livraison")
    private Commande commande;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Livraison id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdresse() {
        return this.adresse;
    }

    public Livraison adresse(String adresse) {
        this.setAdresse(adresse);
        return this;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return this.ville;
    }

    public Livraison ville(String ville) {
        this.setVille(ville);
        return this;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getQuartier() {
        return this.quartier;
    }

    public Livraison quartier(String quartier) {
        this.setQuartier(quartier);
        return this;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public Livraison telephone(String telephone) {
        this.setTelephone(telephone);
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public BigDecimal getFraisLivraison() {
        return this.fraisLivraison;
    }

    public Livraison fraisLivraison(BigDecimal fraisLivraison) {
        this.setFraisLivraison(fraisLivraison);
        return this;
    }

    public void setFraisLivraison(BigDecimal fraisLivraison) {
        this.fraisLivraison = fraisLivraison;
    }

    public StatutLivraison getStatutLivraison() {
        return this.statutLivraison;
    }

    public Livraison statutLivraison(StatutLivraison statutLivraison) {
        this.setStatutLivraison(statutLivraison);
        return this;
    }

    public void setStatutLivraison(StatutLivraison statutLivraison) {
        this.statutLivraison = statutLivraison;
    }

    public Commande getCommande() {
        return this.commande;
    }

    public void setCommande(Commande commande) {
        if (this.commande != null) {
            this.commande.setLivraison(null);
        }
        if (commande != null) {
            commande.setLivraison(this);
        }
        this.commande = commande;
    }

    public Livraison commande(Commande commande) {
        this.setCommande(commande);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Livraison)) {
            return false;
        }
        return getId() != null && getId().equals(((Livraison) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Livraison{" +
            "id=" + getId() +
            ", adresse='" + getAdresse() + "'" +
            ", ville='" + getVille() + "'" +
            ", quartier='" + getQuartier() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", fraisLivraison=" + getFraisLivraison() +
            ", statutLivraison='" + getStatutLivraison() + "'" +
            "}";
    }
}
