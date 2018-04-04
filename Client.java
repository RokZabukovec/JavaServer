import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

	public static void main(String[] args) throws IOException {
		// ------  Izpis število vrstic iz url-ja  ----------
		URL url = new URL("https://www.fis.unm.si/si/");
		URLConnection open = url.openConnection();
		String datoteka = "vsebina.txt";

		BufferedReader in = new BufferedReader(new InputStreamReader(open.getInputStream()));
		PrintWriter out = new PrintWriter(new FileWriter(datoteka));

		int counter = 0;
		String vrstice;
		while ((vrstice = in.readLine()) != null) {
			counter++;
			out.println(vrstice);
		}
		in.close();
		out.close();
		System.out.println("Povezal sem se na naslov " + url + ", prebral sem vsebino in jo zapisal v datoteko "
				+ datoteka + " in zapisal " + counter + " število vrstic.");

		// ------------------------  UDP povezava ---------------------------
		// ------------------------------------------------------------------

		int UDPport = 2227;
		String server = "localhost";

		try {
			// --------  ustvari Socket  ------------------------------------
			DatagramSocket vticnica = new DatagramSocket();

			// --------  za pridobivanja naslova serverja--------------------
			InetAddress IP = InetAddress.getByName(server);

			// --------  število pretvori v string in nato v byte-e----------
			byte[] sendBuffer = new byte[1024];
			sendBuffer = Integer.toString(counter).getBytes();
			// -------- PAKET -----------------------------------------------
			// -------- pošiljanje ------------------------------------------
			DatagramPacket outPaket = new DatagramPacket(sendBuffer, sendBuffer.length, IP, UDPport);
			vticnica.send(outPaket);
			System.out.println("Podatke so poslani...");

			// -------- sprejemanje -----------------------------------------
			byte[] receiveBuffer = new byte[1024];
			DatagramPacket inPaket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			// -------- čas za sprejem paketa -------------------------------
			vticnica.setSoTimeout(10000);
			vticnica.receive(inPaket);
			String prejeto = new String(inPaket.getData());
			if (prejeto.trim().equals("10")) {
				System.out.println("Prejel sem potrdilno sporočilo in s tem UDP odjemalec zaključuje z delovanjem.");
			} else {
				System.out.println("Prejel sem: " + prejeto + " namesto 10. NAPAKA!");
			}
			vticnica.close();
		} catch (SocketTimeoutException e) {
			System.out.println("Čas je potekel...");
		} catch (UnknownHostException ex) {
			System.out.println("Naslova ne morem razrešiti na IP.");
		} catch (IOException ex) {
			System.out.println(ex);
		}

		// -----------------------  TCP  ------------------------------------
		// ------------------------------------------------------------------

		// trenutna nit zaspi za 1s
		try {
			Thread.sleep(5);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		int TCPPort = 2228;
		Socket TCPSocket = null;
		try {
			TCPSocket = new Socket(server, TCPPort);

		} catch (UnknownHostException e) {
			System.out.println("Ne morem razresiti domene");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Ne morem povezati");
			e.printStackTrace();
			System.exit(1);
		}

		// Za komunikacijo s streznikom
		BufferedReader input = new BufferedReader(new InputStreamReader(TCPSocket.getInputStream()));
		PrintWriter output = new PrintWriter(TCPSocket.getOutputStream(), true);

		try (Scanner sc = new Scanner(new FileInputStream("xanadu.txt"))) {
			int count = 0;
			while (sc.hasNext()) {
				String word = sc.next();
				if (word.indexOf("\\") == -1) {
					count++;
				}

			}
			String countString = Integer.toString(count);
			output.println(countString);
			String sprejemOdgovora;
			sprejemOdgovora = input.readLine();

			if (sprejemOdgovora.equals("11")) {
				System.out.println("Prejel sem potrdilno sporočilo.");
			}
			Scanner vnos = new Scanner(System.in);
			System.out.println("Vnesi celo številko");
			int stevilka = vnos.nextInt();
			String stevilkaString = Integer.toString(stevilka);
			output.println(stevilkaString);
			vnos.close();

		}
		in.close();
		out.close();
		TCPSocket.close();
	}
}
