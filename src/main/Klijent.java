package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Klijent {

	static boolean login;
	static String userData;

	public static void main(String[] args) throws UnknownHostException, IOException {
		BufferedReader serverInput; // incoming
		PrintStream serverOutput; // outgoing
		login = false;
		int serverRequestCode;

		Scanner scanner = new Scanner(System.in);

		Socket communicationSocket = new Socket("localhost", 11253);

		serverInput = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));
		serverOutput = new PrintStream(communicationSocket.getOutputStream());

		System.out.println("klijent uspesno povezan na server na adresi");
		System.out.println(communicationSocket.getInetAddress().toString() + ":" + communicationSocket.getPort() + "\n");

		System.out.println("UPOZORENJE: \nmolimo korisnike da ne unose senzitivne podatke u program");
		System.out.println("ukoliko imate problem sa klijentskom aplikacijom javite se na ar20231086@student.fon.bg.ac.rs\n");

		// MENI MENI MENI MENI

		while (true) {
			serverRequestCode = Klijent.glavniMeni(scanner);
			serverOutput.println(serverRequestCode);

			switch (serverRequestCode) {
			case 0:
				communicationSocket.close();
				scanner.close();
				return;
			case 1:
				izvrsiUplatu(scanner, serverInput, serverOutput);
				break;
			case 2:
				System.out.println("trenutno stanje je " + serverInput.readLine() + "rsd");
				break;
			case 3:
				registracija(scanner, serverInput, serverOutput);
				break;
			case 4:
				login(scanner, serverInput, serverOutput);
				break;
			case 5:
				istorijaUplata(serverInput, serverOutput);
				break;
			case 6:
				if (login == true) {
					login = false;
					System.out.println("korisnik uspesno izlogovan");
					break;
				}
			default:
				System.out.println("nevazeci unos");
				break;
			}

		}

	}

	private static int glavniMeni(Scanner input) {
		int izbor = -1;
		while (izbor == -1) {
			System.out.println("=================================");
			System.out.println("RMT DOMACI 02 KLIJENT");
			if (login == true) {
				System.out.println("ulogovan korisnik:" + userData.split(";")[0]);
			}
			System.out.println("=================================");
			System.out.println("MOLIMO IZVRSITE IZBOR:");
			System.out.println("1) Izvrsi uplatu");
			System.out.println("2) Skupljena sredstva");
			System.out.println("3) Registracija");
			System.out.println("4) Prijava (za registrovane korisnike)");
			System.out.println("5) Pregled uplata");
			if (login == true) {
				System.out.println("6) Odjava");
			}
			System.out.println("0) IZLAZ");
			izbor = menuInputProof(input.nextLine());
		}
		return izbor;
	}

	private static int menuInputProof(String str) { // nepotrebno idiot proof-ovanje za meni; nema na cemu
		if (str == null || str.equals("")) {
			return -1;
		}
		char arr[] = str.toCharArray();
		for (char i : arr) {
			if (!(Character.isDigit(i)))
				return -1;
		}
		return Integer.parseInt(str);
	}

	private static void izvrsiUplatu(Scanner input, BufferedReader in, PrintStream out) throws IOException {
		String ime, prezime, adresa, iznos, brKartice = null, cvv = null;
		String data = null;
		if (login == true) {
			ime = userData.split(";")[2];
			prezime = userData.split(";")[3];
			adresa = userData.split(";")[4];
			brKartice = userData.split(";")[6];

			System.out.println("podaci korisnika:");
			System.out.println("ime: " + ime);
			System.out.println("prezime: " + prezime);
			System.out.println("br kartice: " + brKartice);
			while (true) {
				System.out.println("unesite iznos:");
				iznos = input.nextLine();
				if (Integer.parseInt(iznos) >= 200) {
					break;
				}

			}
			System.out.println("unesite CVV:");
			cvv = input.nextLine();

			if (cvv != null || brKartice != null) {
				data = ime + '!' + prezime + '!' + adresa + '!' + iznos + '!' + brKartice + '!' + cvv;
				out.println(data);
			}
			if (in.readLine().equals("OK")) {
				System.out.println("uplata izvrsena HVALA");
				data = data + "!" + in.readLine();
				BufferedWriter reportWriter = new BufferedWriter(new FileWriter("./file/report.txt"));
				data = data.replace('!', '\n');
				System.out.println("Klijentu uspesno dostavljen izvestaj na ./file/report.txt");
				reportWriter.write(data, 0, data.length());
				reportWriter.close();
				return;
			}
			System.out.println("nevazeci podaci o kartici...\nuplata obustavljena");
			return;

		}
		System.out.println("unesite vase ime:");
		ime = input.nextLine();
		System.out.println("unesite vase prezime:");
		prezime = input.nextLine();
		System.out.println("unesite vasu adresu:");
		adresa = input.nextLine();
		while (true) {
			System.out.println("unesite iznos:");
			iznos = input.nextLine();
			if (Integer.parseInt(iznos) < 200) {
				System.out.println("molimo unesite iznos jednak ili veci od 200");
				continue;
			}
			System.out.println("unesite broj kreditne kartice:  (xxxx-xxxx-xxxx-xxxx)");
			brKartice = input.nextLine();
			System.out.println("unesite vas cvv:");
			cvv = input.nextLine();
			if (cvv != null || brKartice != null) {
				data = ime + '!' + prezime + '!' + adresa + '!' + iznos + '!' + brKartice + '!' + cvv;
				out.println(data);
			} else {
				continue;
			}
			if (in.readLine().equals("OK")) {
				System.out.println("uplata izvrsena HVALA");
				data = data + "!" + in.readLine();
				BufferedWriter reportWriter = new BufferedWriter(new FileWriter("./file/report.txt"));
				data = data.replace('!', '\n');
				System.out.println("Klijentu uspesno dostavljen izvestaj na ./file/report.txt");
				reportWriter.write(data, 0, data.length());
				reportWriter.close();
				return;
			}
			System.out.println("nevazeci podaci o kartici; probajte ponovo");
		}

	}

	private static void registracija(Scanner input, BufferedReader in, PrintStream out) throws IOException {
		System.out.println("OBAVESTENJE: ukoliko ste ulogovani bicete izlogovani");
		login = false;
		String userData, buffer;
		// user
		System.out.println("unesite vas username");
		buffer = input.nextLine();
		if (buffer.contains(";")) {
			System.out.println("uneli ste nedozvoljen karakter...\nregistracija terminirana");
			return;
		}
		userData = buffer;
		// pass
		System.out.println("unesite vas password");
		buffer = input.nextLine();
		if (buffer.contains(";")) {
			System.out.println("uneli ste nedozvoljen karakter...\nregistracija terminirana");
			return;
		}
		userData = userData + ";" + buffer;
		// ime
		System.out.println("unesite vase ime");
		buffer = input.nextLine();
		if (buffer.contains(";")) {
			System.out.println("uneli ste nedozvoljen karakter...\nregistracija terminirana");
			return;
		}
		userData = userData + ";" + buffer;
		// prezime
		System.out.println("unesite vase prezime");
		buffer = input.nextLine();
		if (buffer.contains(";")) {
			System.out.println("uneli ste nedozvoljen karakter...\nregistracija terminirana");
			return;
		}
		userData = userData + ";" + buffer;
		// adresa
		System.out.println("unesite vasu adresu:");
		buffer = input.nextLine();
		if (buffer.contains(";")) {
			System.out.println("uneli ste nedozvoljen karakter...\nregistracija terminirana");
			return;
		}
		userData = userData + ";" + buffer;
		// jmbg
		System.out.println("unesite vas jmbg");
		buffer = input.nextLine();
		if (buffer.contains(";")) {
			System.out.println("uneli ste nedozvoljen karakter...\nregistracija terminirana");
			return;
		}
		if (buffer.length() != 13) {
			System.out.println("pogresan jmbg format...\nregistracija terminirana");
			return;
		}
		for (char i : buffer.toCharArray()) {
			if (Character.isDigit(i) == false) {
				System.out.println("pogresan jmbg format...\nregistracija terminirana");
				return;
			}
		}
		userData = userData + ";" + buffer;
		// kartica
		System.out.println("unesite broj vase kartice");
		buffer = input.nextLine();
		if (buffer.contains(";")) {
			System.out.println("uneli ste nedozvoljen karakter...\nregistracija terminirana");
			return;
		}
		if (buffer.length() != 19) {
			System.out.println("pogresan format kartice...\nregistracija terminirana");
			return;
		}
		for (int i = 0; i < buffer.toCharArray().length; i++) {
			if (i == 4 || i == 9 || i == 14) {
				if (buffer.toCharArray()[i] != '-') {
					System.out.println("pogresan format kartice...\nregistracija terminirana");
					return;
				}
				continue;
			}
			if (Character.isDigit(buffer.toCharArray()[i]) == false) {
				System.out.println("pogresan format kartice...\nregistracija terminirana   " + i);
				return;
			}
		}
		out.println(buffer);// salje karticu
		if (in.readLine().equals("ERR")) {
			System.out.println("unet nevazeci broj kartice...\nregistracija terminirana");
			return;
		}
		userData = userData + ";" + buffer;
		// email
		System.out.println("unesite vasu email adresu");
		buffer = input.nextLine();
		if (buffer.contains(";")) {
			System.out.println("uneli ste nedozvoljen karakter...\nregistracija terminirana");
			return;
		}
		if (buffer.contains("@") == false) {
			System.out.println("nevazeca email adresa...\nregistracija terminirana");
			return;
		}
		userData = userData + ";" + buffer;
		out.println(userData);
		buffer = in.readLine();
		if (buffer.equals("ERR")) {
			System.out.println("server prijavljuje da korisnik vec postoji...\nregistracija terminirana");
			return;
		}
		System.out.println("uspesno registrovao korisnika!");
	}

	private static void login(Scanner input, BufferedReader in, PrintStream out) throws IOException {
		System.out.println("OBAVESTENJE: ukoliko ste ulogovani bicete izlogovani");
		login = false;
		String user, pass;
		System.out.println("unesite vas username");
		user = input.nextLine();
		if (user.contains(";")) {
			System.out.println("uneli ste nedozvoljen karakter...\nlogin terminiran");
			return;
		}
		System.out.println("unesite vas password");
		pass = input.nextLine();
		if (pass.contains(";")) {
			System.out.println("uneli ste nedozvoljen karakter...\nlogin terminiran");
			return;
		}
		out.println(user + ";" + pass);
		if (in.readLine().equals("OK")) {
			userData = in.readLine();
			System.out.println("uspesan login!");
			login = true;
			return;
		}
		System.out.println("neuspesan login...");
	}

	private static void istorijaUplata(BufferedReader in, PrintStream out) throws IOException {
		if (login == false) {
			System.out.println("morate biti ulogovani da pristupite pregledu uplata:");
			out.println("ERR");
			return;
		}
		String buffer, report = "";
		out.println("OK");
		for (int i = 0; i < 10; i++) {
			buffer = in.readLine();
			report = report + buffer + "\n";
		}
		System.out.println("Prikaz poslednjih 10 uplata");
		System.out.printf("%s", report);
	}
}