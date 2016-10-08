package pawsoc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * TCPMMindClient ist eine Client-Klasse, welche sich mit einem Server verbindet <br/>
 * und es ermoeglicht Mastermind zu spielen. Dabei schickt der Client durch eine <br/>
 * Usereingabe entweder die geratenen Zahlen oder ein Schluesselwort an einen <br/>
 * Server, dieser Fuehrt dann die gewuenschten Aktionen aus und gibt ein <br/>
 * Ergebnis zurueck.
 * 
 * @author Gabriel Pawlowsky
 * @version 2012-01-29
 */

public class TCPMMindClient {

	// Attribute: BufferedReader fuer den Erhalt von Daten vom Server und
	// PrintWriter zum Senden von Nachrichten an den Server
	private BufferedReader inStream = null;
	private PrintWriter outStream = null;

	/**
	 * Konstruktor welcher dem User die Interaktion mit einem Server ermoeglicht
	 */
	public TCPMMindClient(String ip) {
		// Einfuehrungsnachricht anzeigen, die das Spiel erklaert
		System.out
				.println("Willkommen zu Client-Server-Mastermind\nGeben sie nun 4 Zahlen je zwischen 1 und 6 ein, um einen Tipp abzugeben\nMit 'restart' koennen sie den Server und somit das Spiel neu starten und \nmit 'hint und 'solve' kann man sich einen Tipp oder die Loesung geben lassen.\nViel Spass beim Spielen!");
		// Erstellen des Serversocket mit null, um ihn spaeter benutzen zu
		// koennen
		Socket server = null;
		try {
			// Serversocket, welcher sich mit dessen IP/Hostname zum Server
			// verbindet
			server = new Socket(ip, 1234);

			// In- und OutputStream mit dem Serversocket erstellen, um mit
			// diesem kommunizieren zu koennen
			inStream = new BufferedReader(new InputStreamReader(
					server.getInputStream()));
			outStream = new PrintWriter(server.getOutputStream(), true);

			// Schleife die es dem User unendlich lange ermoeglicht Eingaben zu
			// taetigen
			while (true) {
				// Abfragen einer Konsoleneingabe vom User
				String message = new BufferedReader(new InputStreamReader(
						System.in)).readLine();
				// Senden dieser Eingabe an den Server (\n und das flush werden
				// benoetigt, da die Nachricht sonst nicht gesendet wird)
				outStream.print(message + "\n");
				outStream.flush();

				// Abfragen und Ausgeben der Antwort vom Server, damit der User
				// beim naechsten Schleifendurchlauf weiss was passiert ist
				String result = inStream.readLine();
				System.out.println(result);

				// Wenn die Eingabe stop war soll der Client beendet werden
				if (message.equalsIgnoreCase("stop"))
					break;
			}

		} catch (UnknownHostException e) {
			System.out.println("Ungueltige/r IP/Hostname.");
			// e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Verbindung fehlgeschlagen!");
			// Am Schluss soll der Serversocket auf jeden Fall geschlossen
			// werden.
		} finally {
			if (server != null)
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * main-Methode, welche den Konstruktor in Gange bringt(einen Client <br/>
	 * erstellt) oder bei Falscheingabe eine Synopsis ausgibt.
	 * 
	 * @param args
	 *            Konsolenargumente, die beim Aufruf des Clients uebergbene <br/>
	 *            werden muessen, hierbei ist eine IP-Adress oder ein Hostname <br/>
	 *            wichtig, da der Client sonst nicht weiss wohin er sich <br/>
	 *            verbinden soll.
	 */
	public static void main(String args[]) {
		// Bei einer Falschen Anzahl an uebergebenen Parameter wird eine
		// Synopsis ausgegeben und das Programm beendet
		if (args.length == 1)
			new TCPMMindClient(args[0]);
		else
			System.err
					.println("Works like this: \nfile.jar IP-address/Hostname");
	}
}
