package it.polimi.ingsw.core;

// necessary imports of Java SE
import java.util.Scanner;
import it.polimi.ingsw.network.ClientThread;
import it.polimi.ingsw.network.ServerThread;

public class Startup {
	public static void main( String[] args )
	{
		System.out.println( "\n\nBenvenuto in Santorini.s\nPotrai giocare dopo pochi semplici passi di configurazione." );
		System.out.print( "\nC : attiverò la modalità Client\nS : attiverò la modalità Server\nInnanzitutto, decidi se attivare il programma in modalità Server o Client:" );
		Scanner scanner = new Scanner(System.in);
		char c = 'a';
		while(c != 'c' && c != 'C' && c != 's' && c != 'S'){
			c = scanner.next().charAt(0);
			if(c == 'c' || c == 'C') (new Thread(new ClientThread())).start();
			else if(c == 's' || c == 'S') (new Thread(new ServerThread())).start();
			else if(c != '\n') {
				System.out.println("Errore nell'immissione del carattere: reimmetti il carattere correttamente");
			}
		}
	}
}