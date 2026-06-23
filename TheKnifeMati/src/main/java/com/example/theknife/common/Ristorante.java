package com.example.theknife.common;

import java.io.Serializable;

/**
 * La classe {@code Ristorante} rappresenta un'entità che contiene tutte le informazioni relative
 * ad un ristorante, quali il nome, l'indirizzo, la località, il prezzo, la tipologia di cucina, le coordinate
 * geografiche e altre informazioni utili. <br>
 * Viene utilizzata per mappare i dati caricati da un file CSV e per popolare la {@code TableView} nell'interfaccia
 * grafica dell'applicazione "TheKnife".
 *
 * <p>
 * La classe fornisce un costruttore per inizializzare tutti gli attributi e una serie di metodi getter per
 * accedere ai dati. Inoltre, il metodo {@link #toString()} restituisce una rappresentazione testuale dell'oggetto,
 * utile per scopi di debug e logging.
 * </p>
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */
public class Ristorante implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Il nome del ristorante.
     */
    private String nome;

    /**
     * L'indirizzo del ristorante.
     */
    private String indirizzo;

    /**
     * La località in cui si trova il ristorante.
     */
    private String citta;

    /**
     * Il prezzo medio espresso come stringa.
     */
    private String prezzo;

    /**
     * Il tipo di cucina offerto dal ristorante.
     */
    private String cucina;



    /**
     * Il numero di telefono del ristorante.
     */
    private String numeroTelefono;

    /**
     * L'URL associato al ristorante.
     */
    //private String url;

    /**
     * Il sito web del ristorante.
     */
    private String sitoWeb;

    /**
     * Il premio assegnato al ristorante.
     */
    private String premio;

    /**
     * Il riconoscimento "stella verde" del ristorante.
     */
    private double stelle;

    /**
     * I servizi offerti dal ristorante.
     */
    private String servizi;

    /**
     * Una descrizione del ristorante.
     */
    private String descrizione;
    
    private String stato;
    private String proprietario;

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getProprietario() {
        return proprietario;
    }

    public void setProprietario(String proprietario) {
        this.proprietario = proprietario;
    }

    public boolean isConsegna() {
        return consegna;
    }

    public void setConsegna(boolean consegna) {
        this.consegna = consegna;
    }

    public boolean isPrenotazione() {
        return prenotazione;
    }

    public void setPrenotazione(boolean prenotazione) {
        this.prenotazione = prenotazione;
    }

    private boolean consegna;
    private boolean prenotazione;

    /**
     * Crea un nuovo oggetto {@code Ristorante} con i dettagli specificati.
     *
     * @param nome           il nome del ristorante.
     * @param indirizzo      l'indirizzo del ristorante.
     * @param citta       la località in cui si trova il ristorante.
     * @param prezzo         il prezzo medio espresso come stringa.
     * @param longitudine    la longitudine della posizione del ristorante.
     * @param latitudine     la latitudine della posizione del ristorante.
     * @param numeroTelefono il numero di telefono del ristorante.
     * @param url            l'URL associato al ristorante.
     * @param sitoWeb        il sito web del ristorante.
     * @param premio         il premio inglobato nel riconoscimento.
     * @param stellaVerde    il riconoscimento "stella verde" assegnato.
     * @param servizi        i servizi offerti dal ristorante.
     * @param descrizione    una descrizione del ristorante.
     * @param string 
     */
    public Ristorante(String numeroTelefono, String nome, String indirizzo, String stato, String citta, String servizi,
                       String sitoWeb, String premio,String cucina, double stelle, String prezzo,
                      boolean prenotazione, boolean consegna, String descrizione, String proprietario) {
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.citta = citta;
        this.prezzo = prezzo;
        this.cucina = cucina;
        this.prenotazione = prenotazione;
        this.consegna = consegna;
        this.numeroTelefono = numeroTelefono;
        this.stato = stato;
        //this.url = url;
        this.sitoWeb = sitoWeb;
        this.premio = premio;
        this.stelle = stelle;
        this.servizi = servizi;
        this.descrizione = descrizione;
        this.proprietario = proprietario;
    }

    

    /*public Ristorante(Object trim, String nome2, Object trim2, String string, Object trim3, String servizi2,
            Object trim4, Object value, String cucine, Object object, Object value2, boolean b, boolean c, Object trim5,
            String usernameUtente) {
        //TODO Auto-generated constructor stub
    }

    /**
     * Restituisce il nome del ristorante.
     *
     * @return il nome del ristorante.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce l'indirizzo del ristorante.
     *
     * @return l'indirizzo del ristorante.
     */
    public String getIndirizzo() {
        return indirizzo;
    }

    /**
     * Restituisce la località del ristorante.
     *
     * @return la località del ristorante.
     */
    public String getcitta() {
        return citta;
    }

    /**
     * Restituisce la località del ristorante.
     * Metodo aggiuntivo con nome standard per JavaFX PropertyValueFactory.
     *
     * @return la località del ristorante.
     */
    public String getCitta() {
        return citta;
    }

    /**
     * Restituisce il prezzo del ristorante.
     *
     * @return il prezzo come stringa.
     */
    public String getPrezzo() {
        return prezzo;
    }

    /**
     * Restituisce il tipo di cucina offerto dal ristorante.
     *
     * @return il tipo di cucina.
     */
    public String getCucina() {
        return cucina;
    }

    /**
     * Restituisce la longitudine della posizione del ristorante.
     *
     * @return la longitudine.
     */
    

    /**
     * Restituisce la latitudine della posizione del ristorante.
     *
     * @return la latitudine.
     */
    

    /**
     * Restituisce il numero di telefono del ristorante.
     *
     * @return il numero di telefono.
     */
    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    

    /**
     * Restituisce il sito web del ristorante.
     *
     * @return il sito web del ristorante.
     */
    public String getSitoWeb() {
        return sitoWeb;
    }

    /**
     * Restituisce il premio assegnato al ristorante.
     *
     * @return il premio.
     */
    public String getPremio() {
        return premio;
    }

    /**
     * Restituisce lo status "stella verde" del ristorante.
     *
     * @return lo status "stella verde".
     */
    public double getStellaVerde() {
        return stelle;
    }

    /**
     * Restituisce i servizi offerti dal ristorante.
     *
     * @return i servizi.
     */
    public String getServizi() {
        return servizi;
    }

    /**
     * Restituisce una descrizione del ristorante.
     *
     * @return la descrizione.
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Restituisce una rappresentazione testuale dell'oggetto {@code Ristorante}, utile per scopi di debug.
     *
     * @return una stringa che descrive il ristorante e le sue proprietà.
     */
    @Override
    public String toString() {
        return "Ristorante{" +
                "nome='" + nome + '\'' +
                ", indirizzo='" + indirizzo + '\'' +
                ", citta='" + citta + '\'' +
                ", prezzo='" + prezzo + '\'' +
                ", cucina='" + cucina + '\'' +
                
                ", numeroTelefono='" + numeroTelefono + '\'' +
                //", url='" + url + '\'' +
                ", sitoWeb='" + sitoWeb + '\'' +
                ", premio='" + premio + '\'' +
                ", stellaVerde='" + stelle + '\'' +
                ", servizi='" + servizi + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", proprietario='" + proprietario + '\'' +
                '}';
    }
}
