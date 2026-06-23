package com.example.theknife.common;

import java.io.Serializable;

/**
 * Rappresenta una recensione per un ristorante.
 * <p>
 * Ogni recensione contiene:
 * <ul>
 *     <li>Numero di stelle (1-5)</li>
 *     <li>Testo della recensione</li>
 *     <li>ID del ristorante recensito</li>
 *     <li>Username dell'utente che ha scritto la recensione</li>
 *     <li>Data di creazione della recensione</li>
 *     <li>Risposta del ristorante (opzionale)</li>
 * </ul>
 * </p>
 *
 * Le proprietà utilizzano le classi di binding di JavaFX per supportare
 * l'aggiornamento dinamico dell'interfaccia utente.
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */
public class Recensione  implements Serializable {

    private static final long serialVersionUID = 1L;
    private int stelle;
    private String testo;
    private String ristoranteTel;
    private String username;
    private String data;
    private String ora;
    private String risposta;
    private int idRec;
    private String titolo;

    /**
     * Costruttore per creare una nuova recensione.
     *
     * @param stelle numero di stelle della recensione (da 1 a 5)
     * @param testo testo della recensione
     * @param ristoranteId identificativo del ristorante recensito
     * @param username username dell'utente che scrive la recensione
     */
    public Recensione(int id_rec,String titolo, String testo,int stelle, String data_rec, String ora, String num_tel, String username) {
        this.stelle = stelle;
        this.testo = testo;
        this.ristoranteTel = num_tel;
        this.username = username;
        /*this.data = new SimpleStringProperty(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));*/
        this.data =  data_rec;
        this.ora =  ora;

        this.risposta = "";
        this.idRec = id_rec;
        this.titolo = titolo;
    }

    // ------------------ Value Getters ------------------

    /** Restituisce il numero di stelle della recensione */
    public int getStelle() { return stelle; }

    public String getOra() { return ora; }
    public int getIdRec() { return idRec; }
    public String getTitolo() { return titolo; }

    /** Restituisce il testo della recensione */
    public String getTesto() { return testo; }

    /** Restituisce l'ID del ristorante recensito */
    public String getRistoranteId() { return ristoranteTel; }

    /** Restituisce l'username dell'utente che ha scritto la recensione */
    public String getUsername() { return username; }

    /** Restituisce la data di creazione della recensione */
    public String getData() { return data; }

    /** Restituisce la risposta del ristorante */
    public String getRisposta() { 
        return risposta; 
    }

    // ------------------ Value Setters ------------------

    /** Imposta il numero di stelle della recensione */
    public void setStelle(int value) { stelle = value; }

    /** Imposta il testo della recensione */
    public void setTesto(String value) { testo = value; }

    /** Imposta la data della recensione */
    public void setData(String value) { data = value; }

    /** Imposta la risposta del ristorante */
    public void setRisposta(String value) { risposta = value; }

    public void setOra(String value) {ora = value; }
    public void setIdRec(int value) {idRec = value; }
    public void setTitolo(String value) {titolo = value; }

    /**
     * Rappresentazione testuale della recensione.
     *
     * @return stringa contenente stelle, testo, ristorante, utente e data
     */
    @Override
    public String toString() {
        return String.format("Recensione{stelle=%d, testo='%s', ristorante='%s', utente='%s', data='%s'}",
                getStelle(), getTesto(), getRistoranteId(), getUsername(), getData());
    }
}