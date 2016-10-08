package pawsoc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * TCPMMindCom ist die Serverklasse, sie bekommt von einem Client Nachrichten, <br/>
 * wie Tipps oder Schluesselwoerter und muss diese verarbeiten und dann an die <br/>
 * MasterMindController Klasse weitergeben. Anschliessend bekommt sie von dieser <br/>
 * Klasse eine Antwort, die dann wieder dem Client zu ueberbringen ist. <br/>
 * Ausserdem implementiert diese Klasse Runnable, sodass sie quasi einen Thread <br/>
 * von sich selbst erstellen kann, um alle 10 ms ueberpruefen zu koennen, ob der <br/>
 * User ueber den Client eine Eingabe getaetigt hat.
 * 
 * @author Gabriel Pawlowsky
 * @version 2012-01-29
 */

public class TCPMMindCom implements Runnable {

	// Attribute, die die fuer die Kommunikation notwenigen Objekte beinhalten
	private ServerSocket defSocket = null;
	private Socket srvSocket;
	private BufferedReader inStream = null;
	private PrintWriter outStream = null;
	private Thread process = null;
	private MasterMindController mmc = null;

	/**
	 * Konstruktor, der einen ServerSocket mit dem richtigen Port erstellt und <br/>
	 * einen MasterMindController erstellt, der spaeter die Berechnungen der <br/>
	 * Clientnachrichten uebernimmt.
	 */
	public TCPMMindCom() {
		try {
			defSocket = new ServerSocket(1234);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mmc = new MasterMindController();
	}

	/**
	 * run-Methode des Threads den man erstellen kann, da Runnable implementiert <br/>
	 * ist. Diese Methode managt die Kommunikation mit dem Server und wartet <br/>
	 * zwischendurch 10ms bevor ein neuer Versuch gestartet wird nachzusehen, ob <br/>
	 * der Client schon eine neue Nachricht uebermittelt hat
	 */
	public void run() {
		srvSocket = null;
		// Verbindung zum Client herstellen
		try {
			System.out.println("startServer() -> Server wartet auf Verbindung");

			// Acceept ist eine wartende Methode, sie wartet bis sich ein Client
			// auf dieses ServerSocket verbindet und speichert anschliessend die
			// Verbindung zum Client in den srvsocket und somit ist der Client
			// verbunden.
			srvSocket = defSocket.accept();

		} catch (IOException e) {
			System.out.println("startServer(): Fehler aufgetreten");
		}

		System.out.println("Verbindung hergestellt");

		try {
			// Diese Schleife fuerht die Kommunikation des Servers mit dem
			// Client durch
			while (true) {
				try {
					// 10 ms warten wie oben beschrieben
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// In- und OutputStream mit dem srvsocket erstellen, um mit
				// diesem kommunizieren zu koennen
				inStream = new BufferedReader(new InputStreamReader(
						srvSocket.getInputStream()));

				outStream = new PrintWriter(srvSocket.getOutputStream(), true);

				// Wenn sich seit dem letzten Schleifendurchlauf was im inStream
				// geaendert hat also der Client eine NAchricht gesendet hat, so
				// wird ab hier agiert
				if (inStream.ready()) {

					// Senden des inputs an den MasterMindController und
					// speichern des zurueckkommenden outputs
					String input = inStream.readLine();
					String output = mmc.handle(input);

					// Wenn der User stop eingegeben hat, soll die Verbindung
					// gestoppt werden, Wenn der Output leer ist dann ist im
					// MasterMindController ein Fehler ausgetreten und dies muss
					// ausgegeben werden und wenn alles funktioniert hat wird
					// die Ausgabe an den Client ubertragen
					if (input.equalsIgnoreCase("stop")) {
						outStream.print("Verbindung wird gestoppt\n");
						outStream.flush();
						break;
					} else if (output.isEmpty()) {
						outStream.print("Fehlerhafte Eingabe\n");
						outStream.flush();
					} else {
						outStream.print(output + "\n");
						outStream.flush();
					}

				}
			}
			// Wenn irgendwo hier in der Kommunikation ein Fehler auftritt muss
			// dies ebenfalls ausgegeben werden
		} catch (Exception e) {
			System.out.println("Fehler aufgetreten");
		}

		// Am Schluss soll die Verbindung beendet werden
		stopConnection();
	}

	/**
	 * Starten des Servers funktioniert ueber das Erzeugen und den Aufruf des
	 * Threads von sich selbst.
	 */
	public void startServer() {
		process = new Thread(this);
		process.start();
	}

	/**
	 * Stoppen der Connection, indem man alle Streams und Sockets schliesst und
	 * den Thread terminiert.
	 */
	public void stopConnection() {
		try {
			process.interrupt();
			inStream.close();
			outStream.close();
			srvSocket.close();
			if (defSocket != null)
				defSocket.close();
			System.out.println("Verbindung beendet");
		} catch (Exception e) {
			System.out.println("Fehler aufgetreten");
		}
	}

	/**
	 * main-Methode welche den sich selbst erzeugt und den Server startet.
	 */
	public static void main(String args[]) {
		TCPMMindCom comMMind = new TCPMMindCom();
		comMMind.startServer();
	}

}
