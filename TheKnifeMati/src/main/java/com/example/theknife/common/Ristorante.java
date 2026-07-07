package com.example.theknife.common;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import com.example.theknife.client.ClientMain;

/**
 * Rappresenta un ristorante dell'applicazione TheKnife.
 *
 * <p>
 * Questa classe contiene tutte le informazioni identificative e descrittive
 * di un ristorante, comprese le informazioni di contatto, la localizzazione,
 * la tipologia di cucina, i servizi offerti, gli eventuali riconoscimenti
 * Michelin e il proprietario del locale.
 * </p>
 *
 * <p>
 * La classe implementa {@link Serializable} per consentire il trasferimento
 * degli oggetti tramite Java RMI tra client e server.
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

    /** Identificatore per la serializzazione della classe. */
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
    private String localita;

    /**
     * Il prezzo medio espresso come stringa.
     */
    private String prezzo;

    /**
     * Il tipo di cucina offerto dal ristorante.
     */
    private String cucina;

    /**
     * Commentata: La longitudine della posizione del ristorante.
     * private double longitudine;
     */

    /**
     * Commentata: La latitudine della posizione del ristorante.
     * private double latitudine;
     */

    /**
     * Il numero di telefono del ristorante.
     */
    private String numeroTelefono;

    /**
     * L'URL associato al ristorante.
     */
    private String url;

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

    /**
     * La città in cui si trova il ristorante.
     */
    private String citta;

    /**
     * Lo stato in cui si trova il ristorante.
     */
    private String stato;

    /**
     * Indica se il ristorante offre il servizio di consegna.
     */
    private boolean consegna;

    /**
     * Indica se il ristorante offre il servizio di prenotazione.
     */
    private boolean prenotazione;

    /**
     * Il proprietario del ristorante.
     */
    private String proprietario;

    /**
     * Costruisce un nuovo oggetto {@code Ristorante}.
     *
     * @param numeroTelefono numero di telefono del ristorante
     * @param nome nome del ristorante
     * @param indirizzo indirizzo del ristorante
     * @param stato stato in cui è situato
     * @param citta città in cui è situato
     * @param servizi servizi offerti
     * @param sitoWeb sito web del ristorante
     * @param premio premio Michelin assegnato
     * @param cucina tipologia di cucina
     * @param stelle valore della Stella Verde Michelin
     * @param prezzo fascia di prezzo
     * @param prenotazione indica se il ristorante accetta prenotazioni
     * @param consegna indica se il ristorante effettua consegne
     * @param descrizione descrizione del ristorante
     * @param proprietario username del proprietario
     */
     
    public Ristorante(String numeroTelefono, String nome, String indirizzo, String stato, String citta, String servizi, 
                      String sitoWeb, String premio, String cucina,double stelle, String prezzo, 
                      boolean prenotazione,boolean consegna, String descrizione, String proprietario) {
        this.nome = nome;
        this.indirizzo = indirizzo;
        //this.localita = citta;
        this.citta = citta;
        this.prezzo = prezzo;
        this.cucina = cucina;
        this.prenotazione = prenotazione;
        this.consegna = consegna;
        this.numeroTelefono = numeroTelefono;
        this.stato = stato;
        this.sitoWeb = sitoWeb;
        this.premio = premio;
        this.stelle = stelle;
        this.servizi = servizi;
        this.descrizione = descrizione;
        this.proprietario = proprietario;
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
    public String getLocalita() {
        return localita;
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
    /*public double getLongitudine() {
        return longitudine;
    }*/

    /**
     * Restituisce la latitudine della posizione del ristorante.
     *
     * @return la latitudine.
     */
    /*public double getLatitudine() {
        return latitudine;
    }*/

    /**
     * Restituisce il numero di telefono del ristorante.
     *
     * @return il numero di telefono.
     */
    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    /**
     * Restituisce l'URL associato al ristorante.
     *
     * @return l'URL del ristorante.
     */
    public String getUrl() {
        return url;
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
     * Restituisce il valore associato alla Stella Verde.
     *
     * @return il valore della Stella Verde
     */
    public double getStelle() {
        return stelle;
    }

    /**
     * Recupera dal server la media delle stelle assegnate al ristorante.
     *
     * <p>
     * Il metodo effettua una chiamata remota al server RMI per ottenere
     * la valutazione media del ristorante identificato dal numero di telefono.
     * </p>
     *
     * @return la media delle stelle oppure {@code 0} in caso di errore
     */
    public double getStelleByTel() {
        DBService server;
        double val = 0;
        try {
            server = ClientMain.getServer();
            System.out.println("server = "+server);
            val = server.getStelleByTel(this.numeroTelefono);
        } catch(RemoteException | SQLException | NotBoundException e) {
            System.out.println("errore con il server");
            e.printStackTrace();
        }
        return val;
    }

    /**
     * Restituisce lo stato del ristorante.
     *
     * @return lo stato
     */
    public String getStato() {
        return stato;
    }

    /**
     * Restituisce la città del ristorante.
     *
     * @return la città
     */
    public String getCitta() {
        return citta;
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
     * Restituisce una rappresentazione testuale dell'oggetto.
     *
     * @return una stringa contenente tutte le informazioni principali del ristorante
     */
    @Override
    public String toString() {
        return "Ristorante{" +
                "nome='" + nome + '\'' +
                ", indirizzo='" + indirizzo + '\'' +
                ", localita='" + localita + '\'' +
                ", prezzo='" + prezzo + '\'' +
                ", cucina='" + cucina + '\'' +
                //", longitudine=" + longitudine + 
                //", latitudine=" + latitudine + 
                ", numeroTelefono='" + numeroTelefono + '\'' +
                ", url='" + url + '\'' +
                ", sitoWeb='" + sitoWeb + '\'' +
                ", premio='" + premio + '\'' +
                ", stellaVerde='" + stelle + '\'' +
                ", servizi='" + servizi + '\'' +
                ", descrizione='" + descrizione + '\'' +
                '}';
    }
}
