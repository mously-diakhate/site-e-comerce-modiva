package sn.modiva.app.service.dto;

public class CouleurSummaryDTO {

    private Long id;
    private String libelle;
    private String codeHex;

    public CouleurSummaryDTO() {}

    public CouleurSummaryDTO(Long id, String libelle, String codeHex) {
        this.id = id;
        this.libelle = libelle;
        this.codeHex = codeHex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getCodeHex() {
        return codeHex;
    }

    public void setCodeHex(String codeHex) {
        this.codeHex = codeHex;
    }
}
