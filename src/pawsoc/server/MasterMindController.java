package pawsoc.server;

import java.rmi.server.UnicastRemoteObject;

/**
 * Diese Klasse uebernimmt das Handeln und den Umgang mit eingaben des Clients<br/>
 * je nach eingabe des Clients wird entschieden welche Methode ausgeführt wird<br/>
 * Wenn eine Ausgabe oder eine Methode nicht so funktioniert wie es sein sollte
 * wird dem Server <br/>
 * eine leere Rueckgabe gesendet
 * 
 * @author Josef Sochovsky
 * @version 2012-01-29
 */
public class MasterMindController {
	// in dieser Variable werden die Zufallszahlen gespeichert
	private int[] ziel;
	// in dieser Variable werden die Rundenanzahl gespeichert
	private int runden;
	// um einen vernuenftigen Tip/hint abgeben zu koennen wird der letzte check
	// gespeichert
	private int[] last;
	// es wird gespeichert ob der Nutzer bereits gewonnen hat
	private boolean won;
	// um mehr oder weniger Runden zu spielen kann man diese Variable
	// umschreiben
	public final int MAX_ROUNDS = 15;

	/**
	 * Diese Methode soll den vom Server erhaltenen String bearbeiten<br/>
	 * Je nach eingabe wird eine Methode aufgerufen <br/>
	 * Wenn 4 Zahlen uebergeben werden, werden sie getrennt und dann der check
	 * Mehtode uebergeben <br/>
	 * 
	 * @param eingabe
	 *            den String den der Benutzer eingegeben hat
	 * @return je nach dem welche Methode aufgerufen wurde wird ein ausgewehrter<br/>
	 *         String an den Benutzer uebergeben <br/>
	 *         dies kann aber auch nichts sein wenn keine passende Methode <br/>
	 *         gefunden wurde
	 * 
	 */
	public String handle(String eingabe) {
		// der Benutzer moechte neustarten
		if (eingabe.equalsIgnoreCase("restart"))
			return restart();
		// der Benutzer moechte eine Hilfeleistung erhalten
		else if (eingabe.equalsIgnoreCase("hint"))
			return hint();
		// Der Benutzer moechte die Loesung erhalten
		else if (eingabe.equalsIgnoreCase("solve"))
			return solve();
		// Der Benutzer hat 4 Zeichen eingegeben es koennte ein check gemacht
		// werden sollen
		else if (eingabe.length() == 4) {
			// eine temporaere Variable die die neuen Zahlen speichert
			int[] temp = { 0, 0, 0, 0 };
			// jedes Zeichen wird abgearbeitet
			for (int i = 0; i < 4; i++) {
				// "" weil es sonst als char gewertet wird
				if (Character.isDigit(eingabe.charAt(i)))
					temp[i] = Integer.parseInt(eingabe.charAt(i) + "");
				// Wenn auch nur ein Zeichen ein Buchstabe oder Sonderzeichen
				// ist wird dem Server ein Fehler uebermittelt
				else
					return "";
			}
			// Wenn es 4 Zahlen waren wird der Methode die Zahlen uebermittelt
			// und dem Benutzer ein Ergebnis uebermittelt
			return check(temp);
		}
		return "";
	}

	/**
	 * Diese Methode soll einen hint geben je nach dem wie die letzte Eingabe <br/>
	 * war bzw. was bereits richtig war <br/>
	 * wird entschieden was getan werden soll <br/>
	 * 
	 * @return Je nach dem wie/wann es falsch war wird entschieden was
	 *         zurueckgegeben wird
	 */
	private String hint() {
		// wenn der Benutzer bereits gewonnen hat kann kein hint gegeben werden,
		// auch dann nicht wenn man noch keinen Versuch gehabt hat
		if (!won && runden != 0) {
			// Die Schleife durchlaeuft alle Zahlen des Zwischenspeichers
			for (int i = 0; i < 4; i++) {
				// wenn die naechstbeste Zahl nicht gleich war
				if (last[i] != ziel[i]) {
					// wird hier ueberprueft ob es zu klein oder zu gross war
					if (last[i] < ziel[i])
						return "Die " + (i + 1) + ". Zahl ist zu klein";
					if (last[i] > ziel[i])
						return "Die " + (i + 1) + ". Zahl ist zu gross";
				}
			}
		} else
			return "Momentan kannst du keinen hint erhalten";
		return "";
	}

	/**
	 * Diese Methode soll die Loesung fuer testzwecke zurueckgeben <br/>
	 * Diese Methode wird nicht richtig ausgefuehrt wenn der Benutzer bereits
	 * gewonnen hat <br/>
	 * 
	 * @return die Loesung
	 */
	private String solve() {
		if (!won)
			return "Loesung: " + ziel[0] + ziel[1] + ziel[2] + ziel[3];
		return "";
	}

	/**
	 * Generiert neue Zahlen die erraten werden sollen<br/>
	 */
	private void generate() {
		for (int i = 0; i < ziel.length; i++) {
			// es werden Zahlen von 1 bis 6 generiert
			ziel[i] = (int) (Math.random() * 6) + 1;
		}
	}

	/**
	 * Ueberprueft die Eingabe und gibt je nach richtigkeit eine Menge an S und <br/>
	 * W´s zurueck Ausserdem wird die neue Anfrage gespeichert um einen hint <br/>
	 * geben zu koennen <br/>
	 * 
	 * @param frage
	 *            Versuch des Clients
	 * @return die Antwort die geschrieben wird
	 */
	private String check(int[] frage) {
		// es wird die neue Eingabe zwischengespeichert
		last = frage;
		// hier werden die Zeichen fuer die Korrektur zwischengespeichert
		String temp = "";
		// runden muessen erhoeht werden
		runden++;
		// das boolean Array verhindert mehrfaches abpruefen Anfangswerte sind
		// nur falses weil noch nicht ueberprueft wurde
		boolean[][] checked = { { false, false }, { false, false },
				{ false, false }, { false, false } };
		// [][0] steht fuer das Ziel
		// [][1] steht fuer die frage des Users
		// Man soll nicht nocheinmal versuchen koennen wenn bereits runden auf
		// 15 steht
		if (runden == MAX_ROUNDS) {
			won = true;
			return "Das Spiel ist vorbei du kannst keine Zahlen mehr eingeben";
		}
		// die Eingabe muss 4 Stellen haben
		if (frage.length == 4) {
			// hier werd exakt geprueft auf S
			for (int i = 0; i < ziel.length; i++)
				if (frage[i] == ziel[i]) {
					// i ist nun fuer ueberpruefungen von frage und ziel
					// gesperrt
					checked[i][0] = true;
					checked[i][1] = true;
					temp += "S";
				}
			// hier wird halbrichtig geprueft
			for (int i = 0; i < frage.length; i++) {
				for (int j = 0; j < ziel.length; j++) {
					if (frage[i] == ziel[j] && !checked[i][0] && !checked[j][1]) {
						// wenn ein W gefunden wurde wird erneut i von ziel und
						// j von frage gesperrt
						checked[i][0] = true;
						checked[j][1] = true;
						temp += "W";
					}
				}
			}
			// wenn man gewonnen hat wird das ausgegeben
			if (temp.equals("SSSS")) {
				won = true;
				return "Du hast gewonnen " + temp;
				// wenn nicht wird die SW Kombination zurueckgegeben
			} else
				return temp;
		} else
			return "";

	}

	/**
	 * Startet die runden von neu und generiert neue Zahlen <br/>
	 */
	private String restart() {
		runden = 0;
		generate();
		won = false;
		return "Erfolgreich neugestartet";
	}

	/**
	 * Setzt die runden auf 0<br/>
	 * erstellt ein Ziel Array<br/>
	 * und generiert im Anschluss <br/>
	 * eine neue Loesung
	 */
	public MasterMindController() {
		runden = 0;
		ziel = new int[4];
		generate();
		won = false;
	}
}

