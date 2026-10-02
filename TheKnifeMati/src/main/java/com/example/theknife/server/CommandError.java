package com.example.theknife.server;

public class CommandError extends Exception{
   public CommandError() {
      super("Errore nei comandi inseriti!");
   } 
}
