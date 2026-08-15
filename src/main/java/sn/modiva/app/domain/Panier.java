package sn.modiva.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Panier.
 */
@Entity
@Table(name = "panier")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Panier implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date_creation")
    private Instant dateCreation;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "panier")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "variante", "panier" }, allowSetters = true)
    private Set<LignePanier> lignes = new HashSet<>();

    @JsonIgnoreProperties(value = { "user", "panier", "adresses", "commandes" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "panier")
    private ClienteProfile clienteProfile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Panier id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDateCreation() {
        return this.dateCreation;
    }

    public Panier dateCreation(Instant dateCreation) {
        this.setDateCreation(dateCreation);
        return this;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Set<LignePanier> getLignes() {
        return this.lignes;
    }

    public void setLignes(Set<LignePanier> lignePaniers) {
        if (this.lignes != null) {
            this.lignes.forEach(i -> i.setPanier(null));
        }
        if (lignePaniers != null) {
            lignePaniers.forEach(i -> i.setPanier(this));
        }
        this.lignes = lignePaniers;
    }

    public Panier lignes(Set<LignePanier> lignePaniers) {
        this.setLignes(lignePaniers);
        return this;
    }

    public Panier addLigne(LignePanier lignePanier) {
        this.lignes.add(lignePanier);
        lignePanier.setPanier(this);
        return this;
    }

    public Panier removeLigne(LignePanier lignePanier) {
        this.lignes.remove(lignePanier);
        lignePanier.setPanier(null);
        return this;
    }

    public ClienteProfile getClienteProfile() {
        return this.clienteProfile;
    }

    public void setClienteProfile(ClienteProfile clienteProfile) {
        if (this.clienteProfile != null) {
            this.clienteProfile.setPanier(null);
        }
        if (clienteProfile != null) {
            clienteProfile.setPanier(this);
        }
        this.clienteProfile = clienteProfile;
    }

    public Panier clienteProfile(ClienteProfile clienteProfile) {
        this.setClienteProfile(clienteProfile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Panier)) {
            return false;
        }
        return getId() != null && getId().equals(((Panier) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Panier{" +
            "id=" + getId() +
            ", dateCreation='" + getDateCreation() + "'" +
            "}";
    }
}
