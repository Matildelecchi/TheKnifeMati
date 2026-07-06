package com.example.theknife.client;

/**
 * La classe {@code SessioneUtente} gestisce lo stato dell'utente attualmente
 * loggato all'interno dell'applicazione.
 *
 * <p>
 * Implementa il pattern **Singleton** per garantire che esista una sola istanza
 * della classe durante l’esecuzione dell’applicazione, fornendo un punto di accesso
 * globale alle informazioni dell’utente corrente.
 * </p>
 *
 * <p>
 * La classe mantiene i dati principali dell’utente autenticato, tra cui:
 * nome, cognome, username, email, città, stato, indirizzo e ruolo.
 * Fornisce inoltre metodi di utilità per verificare lo stato di login e il tipo
 * di utente (cliente, ristoratore o ospite).
 * </p>
 *
 * <p>
 * Il logout e la gestione della sessione vengono centralizzati tramite metodi statici.
 * </p>
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */
public class SessioneUtente {

    /**
     * L'unica istanza della classe SessioneUtente (Singleton).
     */
    private static SessioneUtente istanza;

    /**
     * Il nome dell'utente corrente.
     */
    private String nome;
    /**
     * Il cognome dell'utente corrente.
     */
    private String cognome;
    /**
     * Il citta dell'utente corrente.
     */
    private String citta;
    /**
     * Lo username dell'utente corrente.
     */
    private String username;
    /**
     * Il ruolo dell'utente corrente ( true = ristoratore, false = cliente)
     */
    private boolean ruolo;

    /**
     * Stato geografico dell’utente.
     */
    private String stato;
   
    /**
     * Flag che indica se un utente è attualmente loggato.
     */
    private boolean isLoggato;

     /**
     * Email dell’utente corrente.
     */
    private String email;

    /**
     * Data di nascita o registrazione dell’utente.
     */
    private String data;

    /**
     * Indirizzo dell’utente.
     */
    private String indirizzo;

    /**
     * Costruttore privato per impedire istanziazione diretta.
     * <p>
     * Necessario per l’implementazione del pattern Singleton.
     * </p>
     */
    private SessioneUtente() {
        this.isLoggato = false;
    }

    /**
     * Restituisce l’istanza singleton della classe.
     *
     * <p>
     * Se non esiste ancora, viene creata; altrimenti viene restituita quella esistente.
     * </p>
     *
     * @return istanza unica di {@code SessioneUtente}
     */
    public static synchronized SessioneUtente getIstanza() {
        if (istanza == null) {
            istanza = new SessioneUtente();
        }
        return istanza;
    }

    /**
     * Imposta i dati dell’utente corrente e attiva la sessione.
     *
     * @param nome      nome dell’utente
     * @param cognome   cognome dell’utente
     * @param username  username univoco
     * @param ruolo     ruolo (true = ristoratore, false = cliente)
     * @param citta     città dell’utente
     * @param stato     stato dell’utente
     * @param email     email dell’utente
     * @param data      data associata all’utente
     * @param indirizzo indirizzo dell’utente
     */
    public static void impostaUtenteCorrente(String nome, String cognome, String username, boolean ruolo, String citta,
            String stato, String email, String data, String indirizzo) {
        SessioneUtente sessione = getIstanza();
        sessione.nome = nome;
        sessione.cognome = cognome;
        sessione.username = username;
        sessione.ruolo = ruolo;
        sessione.citta = citta;
        sessione.isLoggato = true;
        sessione.stato = stato;
        sessione.email = email;
        sessione.data = data;
        sessione.indirizzo = indirizzo;
        System.out.println("DEBUG: Sessione utente impostata - " + nome + " " + cognome + " (" + ruolo + ") " + citta
                + " " + stato);
    }

    /**
     * Verifica se un utente è attualmente loggato.
     *
     * @return {@code true} se la sessione è attiva, altrimenti {@code false}
     */
    public static boolean isUtenteLoggato() {
        SessioneUtente sessione = getIstanza();
        return sessione.isLoggato && sessione.username != null && !sessione.username.isEmpty();
    }

    /**
     * Restituisce lo username dell’utente loggato.
     *
     * @return username oppure {@code null} se non loggato
     */
    public static String getUsernameUtente() {
        SessioneUtente sessione = getIstanza();
        return sessione.isLoggato ? sessione.username : null;
    }

    /**
     * Restituisce lo stato dell’utente.
     *
     * @return stato dell’utente
     */
    public static String getStato() {
        return getIstanza().stato;
    }

    /**
     * Restituisce l’email dell’utente.
     *
     * @return email dell’utente
     */
    public static String getEmail() {
        return getIstanza().email;
    }

    /**
     * Restituisce l’indirizzo dell’utente.
     *
     * @return indirizzo dell’utente
     */
    public static String getIndirizzo() {
        return getIstanza().indirizzo;
    }

    /**
     * Imposta il nome dell’utente.
     *
     * @param nome nome da impostare
     */
    public static void setNome(String nome) {
        getIstanza().nome = nome;
    }

    /**
     * Imposta il cognome dell’utente.
     *
     * @param cognome cognome da impostare
     */
    public static void setCognome(String cognome) {
        getIstanza().cognome = cognome;
    }

    /**
     * Imposta l’indirizzo dell’utente.
     *
     * @param indirizzo indirizzo da impostare
     */
    public static void setIndirizzo(String indirizzo) {
        getIstanza().indirizzo = indirizzo;
    }

    /**
     * Imposta la città dell’utente.
     *
     * @param citta città da impostare
     */
    public static void setCitta(String citta) {
        getIstanza().citta = citta;
    }

    /**
     * Imposta lo stato dell’utente.
     *
     * @param stato stato da impostare
     */
    public static void setStato(String stato) {
        getIstanza().stato = stato;
    }

    /**
     * Restituisce il nome completo dell’utente.
     *
     * @return nome completo oppure "Ospite" o stringa vuota se non loggato
     */
    public static String getNomeCompleto() {
        SessioneUtente sessione = getIstanza();
        if (!sessione.isLoggato)
            return "";
        if (isOspite())
            return "Ospite";
        return sessione.nome + " " + sessione.cognome;
    }

    /**
     * Restituisce il nome dell’utente.
     *
     * @return nome
     */
    public static String getNome() {
        SessioneUtente sessione = getIstanza();
        return sessione.nome;
    }

     /**
     * Restituisce il cognome dell’utente.
     *
     * @return cognome
     */
    public static String getCognome() {
        SessioneUtente sessione = getIstanza();
        return sessione.cognome;
    }

    /**
     * Verifica se l’utente è un ristoratore.
     *
     * @return true se ristoratore
     */
    public static boolean isRuolo() {
        SessioneUtente sessione = getIstanza();
        return sessione.ruolo;
    }

    /**
     * Restituisce la data associata all’utente.
     *
     * @return data
     */
    public static String getData() {
        SessioneUtente sessione = getIstanza();
        return sessione.data;
    }

    /**
     * Restituisce la città dell’utente.
     *
     * @return città
     */
    public static String getCitta() {
        SessioneUtente sessione = getIstanza();
        return sessione.citta; // Correzione: uso sessione.citta invece di this.citta
    }

    /**
     * Verifica se l’utente è un cliente.
     *
     * @return true se cliente
     */
    public static boolean isCliente() {
        return !getIstanza().ruolo;
    }

    /**
     * Verifica se l'utente corrente ha il ruolo di "ristoratore".
     *
     * @return {@code true} se l'utente è un ristoratore (confronto insensibile alle
     *         maiuscole/minuscole), {@code false} altrimenti.
     */
    public static boolean isRistoratore() {
        return getIstanza().ruolo;
    }

    /**
     * Verifica se l’utente è ospite (non loggato).
     *
     * @return true se ospite
     */
    public static boolean isOspite() {
        return !isUtenteLoggato();
    }

    /**
     * Svuota la sessione corrente e disconnette l’utente.
     */
    public static void pulisciSessione() {
        SessioneUtente sessione = getIstanza();
        sessione.nome = null;
        sessione.cognome = null;
        sessione.username = null;
        sessione.ruolo = false;
        sessione.citta = null;
        sessione.stato = null;
        sessione.email = null;
        sessione.isLoggato = false;

        System.out.println("DEBUG: Sessione utente pulita");
    }

    /**
     * Esegue il logout dell’utente.
     */
    public static void eseguiLogout() {
        pulisciSessione();
    }

    /**
     * Restituisce una rappresentazione testuale della sessione.
     *
     * @return stringa descrittiva della sessione
     */
    @Override
    public String toString() {
        if (isLoggato) {
            return String.format("SessioneUtente{nome='%s', cognome='%s', username='%s', ruolo='%s'}",
                    nome, cognome, username, ruolo);
        } else {
            return "SessioneUtente{Non loggato}";
        }
    }

    /**
     * Restituisce la stringa della sessione corrente.
     *
     * @return stringa sessione
     */
    public static String getStringaSessione() {
        return getIstanza().toString();
    }
}