package sn.modiva.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ClienteProfile.
 */
@Entity
@Table(name = "cliente_profile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClienteProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "actif")
    private Boolean actif;

    // NB : rendu optionnel pour permettre un ClienteProfile "invité" (commande
    // sans compte utilisateur) — voir la migration Liquibase associée qui
    // retire la contrainte NOT NULL sur la colonne user_id.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    @JsonIgnoreProperties(value = { "lignes", "clienteProfile" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Panier panier;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clienteProfile")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "clienteProfile" }, allowSetters = true)
    private Set<Adresse> adresses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clienteProfile")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "paiement", "livraison", "lignes", "clienteProfile" }, allowSetters = true)
    private Set<Commande> commandes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClienteProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public ClienteProfile telephone(String telephone) {
        this.setTelephone(telephone);
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public ClienteProfile actif(Boolean actif) {
        this.setActif(actif);
        return this;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ClienteProfile user(User user) {
        this.setUser(user);
        return this;
    }

    public Panier getPanier() {
        return this.panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    public ClienteProfile panier(Panier panier) {
        this.setPanier(panier);
        return this;
    }

    public Set<Adresse> getAdresses() {
        return this.adresses;
    }

    public void setAdresses(Set<Adresse> adresses) {
        if (this.adresses != null) {
            this.adresses.forEach(i -> i.setClienteProfile(null));
        }
        if (adresses != null) {
            adresses.forEach(i -> i.setClienteProfile(this));
        }
        this.adresses = adresses;
    }

    public ClienteProfile adresses(Set<Adresse> adresses) {
        this.setAdresses(adresses);
        return this;
    }

    public ClienteProfile addAdresse(Adresse adresse) {
        this.adresses.add(adresse);
        adresse.setClienteProfile(this);
        return this;
    }

    public ClienteProfile removeAdresse(Adresse adresse) {
        this.adresses.remove(adresse);
        adresse.setClienteProfile(null);
        return this;
    }

    public Set<Commande> getCommandes() {
        return this.commandes;
    }

    public void setCommandes(Set<Commande> commandes) {
        if (this.commandes != null) {
            this.commandes.forEach(i -> i.setClienteProfile(null));
        }
        if (commandes != null) {
            commandes.forEach(i -> i.setClienteProfile(this));
        }
        this.commandes = commandes;
    }

    public ClienteProfile commandes(Set<Commande> commandes) {
        this.setCommandes(commandes);
        return this;
    }

    public ClienteProfile addCommande(Commande commande) {
        this.commandes.add(commande);
        commande.setClienteProfile(this);
        return this;
    }

    public ClienteProfile removeCommande(Commande commande) {
        this.commandes.remove(commande);
        commande.setClienteProfile(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClienteProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((ClienteProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClienteProfile{" +
            "id=" + getId() +
            ", telephone='" + getTelephone() + "'" +
            ", actif='" + getActif() + "'" +
            "}";
    }
}
