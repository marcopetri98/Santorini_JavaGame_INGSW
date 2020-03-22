package it.polimi.ingsw.core;

import java.util.Scanner;

public class StartupV1_1 {

    public static void main( String[] args )
    {
        System.out.println( "\n\nBenvenuto in Santorini.\nPotrai giocare dopo pochi semplici passi di configurazione." );
        System.out.println( "\nInnanzitutto, decidi se attivare il programma in modalità Server o Client:\n\nC : attiverò la modalità Client\nS : attiverò la modalità Server\n" );
        Scanner scanner = new Scanner(System.in);
        char c = 'a';
        while(c != 'c' && c != 'C' && c != 's' && c != 'S'){
            c = scanner.next().charAt(0);
            if(c == 'c' || c == 'C')    (new Thread(new ClientThread())).start();
            else if(c == 's' || c == 'S')   (new Thread(new ServerThread())).start();
            else if(c != '\n'){
                System.out.println("Errore nell'immissione del carattere: reimmetti il carattere correttamente");
            }
        }
    }
}