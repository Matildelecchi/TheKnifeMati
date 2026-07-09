package com.example.theknife.common;

import java.io.Serializable;

/**
 * Rappresenta una recensione associata a un ristorante.
 *
 * <p>
 * Ogni recensione contiene informazioni relative all'esperienza di un utente
 * presso un ristorante, includendo valutazione, testo, data e un eventuale
 * riscontro da parte del ristorante.
 * </p>
 *
 * <p>
 * La classe è serializzabile per permettere il trasferimento tramite RMI
 * e la persistenza dei dati tra client e server.
 * </p>
 *
 * <p><strong>Informazioni contenute:</strong></p>
 * <ul>
 *     <li>Numero di stelle (1–5)</li>
 *     <li>Titolo della recensione</li>
 *     <li>Testo della recensione</li>
 *     <li>Identificativo del ristorante (telefono)</li>
 *     <li>Username dell'utente autore</li>
 *     <li>Data e ora di pubblicazione</li>
 *     <li>Risposta del ristoratore (opzionale)</li>
 * </ul>
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
     * Costruisce una nuova istanza di {@code Recensione}.
     *
     * @param id_rec identificativo univoco della recensione
     * @param titolo titolo della recensione
     * @param testo testo della recensione
     * @param stelle numero di stelle assegnate (1–5)
     * @param data_rec data di pubblicazione della recensione
     * @param ora ora di pubblicazione della recensione
     * @param num_tel numero di telefono del ristorante recensito
     * @param username username dell’utente autore della recensione
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

   /** @return numero di stelle assegnate alla recensione */
    public int getStelle() { return stelle; }

    /** @return ora di pubblicazione della recensione */
    public String getOra() { return ora; }

     /** @return identificativo della recensione */
    public int getIdRec() { return idRec; }

    /** @return titolo della recensione */
    public String getTitolo() { return titolo; }

    /** @return testo della recensione */
    public String getTesto() { return testo; }

    /** @return numero di telefono del ristorante recensito */
    public String getRistoranteTel() { return ristoranteTel; }

    /** @return username dell’autore della recensione */
    public String getUsername() { return username; }

    /** @return data di pubblicazione della recensione */
    public String getData() { return data; }

    /** @return risposta del ristoratore (può essere vuota) */
    public String getRisposta() { 
        return risposta; 
    }

    // ------------------ Value Setters ------------------

    /** @param value imposta il numero di stelle */    
    public void setStelle(int value) { stelle = value; }

    /** @param value imposta il testo della recensione */
    public void setTesto(String value) { testo = value; }

    /** @param value imposta la data della recensione */
    public void setData(String value) { data = value; }

    /** @param value imposta la risposta del ristoratore */
    public void setRisposta(String value) { risposta = value; }

    /** @param value imposta il numero di telefono del ristorante recensito */
    public void setOra(String value) {ora = value; }
    
    /** @param value imposta l’identificativo della recensione */
    public void setIdRec(int value) {idRec = value; }
    
    /** @param value imposta il titolo della recensione */
    public void setTitolo(String value) {titolo = value; }

    /**
     * Restituisce una rappresentazione testuale della recensione.
     *
     * @return stringa formattata contenente i principali attributi
     */
    @Override
    public String toString() {
        return String.format("Recensione{stelle=%d, testo='%s', ristorante='%s', utente='%s', data='%s'}",
                getStelle(), getTesto(), getRistoranteTel(), getUsername(), getData());
    }
}