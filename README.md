# TheKnife

## Descrizione

TheKnife è un'applicazione sviluppata in Java che permette di consultare e gestire informazioni relative ai ristoranti della Guida Michelin.

L'applicazione permette agli utenti di visualizzare dettagli sui ristoranti, tra cui:
- nome;
- indirizzo e città;
- tipologia di cucina;
- fascia di prezzo;
- contatti;
- sito web;
- premi Michelin;
- Stella Verde Michelin;
- servizi disponibili;
- descrizione;
- recensioni.

Il progetto utilizza un'architettura client-server con comunicazione tramite RMI.

---

# Funzionalità principali

## Visualizzazione ristoranti

L'utente può:
- cercare ristoranti presenti nel database;
- aprire la pagina dei dettagli;
- consultare tutte le informazioni del ristorante selezionato.

---

## Dettagli Michelin

Per ogni ristorante possono essere visualizzati:
- premi Michelin (es. Bib Gourmand);
- riconoscimenti della Guida Michelin;
- Stella Verde Michelin.

---

## Recensioni

L'applicazione permette di:
- visualizzare le recensioni dei clienti;
- leggere il testo delle recensioni;
- vedere la valutazione tramite stelle;
- visualizzare eventuali risposte del ristoratore;
- aggiungere recensioni.

---

## Preferiti

Gli utenti registrati possono:
- aggiungere ristoranti ai preferiti;
- rimuovere ristoranti dai preferiti.

Gli utenti ospiti possono consultare i ristoranti senza modificare i dati.

---

## Gestione utenti

Il sistema permette:

- registrazione di nuovi utenti;
- login;
- gestione del profilo personale;
- gestione della sessione utente.

Sono presenti diversi ruoli:
- cliente;
- ristoratore;
- ospite.

---

## Area ristoratore 

I ristoratori possono accedere a una dashboard dedicata per gestire le informazioni relative al proprio ristorante.

---

## Collegamenti esterni

È possibile:
- aprire il sito web del ristorante;
- visualizzare la posizione tramite Google Maps;
- copiare il numero di telefono.

---

# Tecnologie utilizzate

- Java
- JavaFX
- FXML
- RMI (Remote Method Invocation)
- Database SQL
- Git e GitHub

---

# Struttura del progetto
TheKnife
│
├── client
│ │
│ ├── App.java
│ ├── ClientMain.java
│ ├── LoginController.java
│ ├── RegistrazioneController.java
│ ├── ControllerDatiUtenti.java
│ ├── RecensioniController.java
│ ├── RistoranteDetailController.java
│ ├── RistoranteInputController.java
│ ├── RistoratoreDashboardController.java
│ ├── UserProfileController.java
│ ├── SessioneUtente.java
│ └── Gestione interfaccia grafica e logica client
│
├── common
│ │
│ ├── DBService.java
│ ├── Ristorante.java
│ ├── Recensione.java
│ └── Utente.java
│
├── server
│ │
│ └── ServerMain.java
│
├── resources
│ │
│ └── File FXML delle schermate JavaFX
│
└── data
|    │
|    └── img
|        └── logo.png


---

# Architettura del sistema

## Client

Il client gestisce:

- interfaccia grafica JavaFX;
- caricamento delle schermate FXML;
- gestione degli eventi;
- comunicazione con il server tramite RMI.

## Common

Contiene le classi condivise tra client e server:

- modelli dei dati;
- oggetti trasferiti tramite RMI;
- interfaccia del servizio database.

## Server

Gestisce:

- connessione al database;
- operazioni sui dati;
- servizi remoti utilizzati dal client.

---

# Avvio dell'applicazione

Per eseguire il progetto:

1. Avviare il server tramite `ServerMain`.
2. Avviare il client tramite `ClientMain`.
3. Effettuare il login oppure accedere come ospite.
4. Utilizzare le funzionalità disponibili.

---

# Autori

Progetto realizzato da:

Claudio Bonci, 759939, Sede CO
Anna Eleonora Caredda, 762576, Sede CO
Filippo Crippa, 762174, Sede CO
Matilde Lecchi, 759875, Sede CO

---

# Versione

**Versione progetto:** 1.0  
**Anno:** 2026
