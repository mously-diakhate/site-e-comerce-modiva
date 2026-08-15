package sn.modiva.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import sn.modiva.app.domain.enumeration.StatutCommande;

/**
 * A DTO for the {@link sn.modiva.app.domain.Commande} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommandeDTO implements Serializable {

    private Long id;

    @NotNull
    private String numero;

    @NotNull
    private Instant dateCommande;

    @NotNull
    private BigDecimal montantTotal;

    private String modePaiement;

    @NotNull
    private StatutCommande statutCommande;

    private PaiementDTO paiement;

    @NotNull
    private LivraisonDTO livraison;

    @NotNull
    private ClienteProfileDTO clienteProfile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Instant getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Instant dateCommande) {
        this.dateCommande = dateCommande;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public StatutCommande getStatutCommande() {
        return statutCommande;
    }

    public void setStatutCommande(StatutCommande statutCommande) {
        this.statutCommande = statutCommande;
    }

    public PaiementDTO getPaiement() {
        return paiement;
    }

    public void setPaiement(PaiementDTO paiement) {
        this.paiement = paiement;
    }

    public LivraisonDTO getLivraison() {
        return livraison;
    }

    public void setLivraison(LivraisonDTO livraison) {
        this.livraison = livraison;
    }

    public ClienteProfileDTO getClienteProfile() {
        return clienteProfile;
    }

    public void setClienteProfile(ClienteProfileDTO clienteProfile) {
        this.clienteProfile = clienteProfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommandeDTO)) {
            return false;
        }

        CommandeDTO commandeDTO = (CommandeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, commandeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommandeDTO{" +
            "id=" + getId() +
            ", numero='" + getNumero() + "'" +
            ", dateCommande='" + getDateCommande() + "'" +
            ", montantTotal=" + getMontantTotal() +
            ", modePaiement='" + getModePaiement() + "'" +
            ", statutCommande='" + getStatutCommande() + "'" +
            ", paiement=" + getPaiement() +
            ", livraison=" + getLivraison() +
            ", clienteProfile=" + getClienteProfile() +
            "}";
    }
}
