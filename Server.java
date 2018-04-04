import java.io.*;
import java.net.*;

public class Server {

	public static void main(String[] args) throws IOException {

		int UDPport = 2227;
		int prvaStevilka = 0;
		try {
			DatagramSocket vticnica = new DatagramSocket(UDPport);
			// prejem podatkov
			byte[] prejemniBuffer = new byte[1024];
			DatagramPacket prejemniPodatki = new DatagramPacket(prejemniBuffer, prejemniBuffer.length);
			System.out.println("Cakam na UDP paket.");
				
				vticnica.receive(prejemniPodatki);
				String odpakiraniPodatki = new String(prejemniPodatki.getData());
				prvaStevilka = Integer.parseInt(odpakiraniPodatki.trim());
				System.out.println("Prejel sem UDP paket z vsebino " + odpakiraniPodatki);
				
				// pošiljanje odgovora
				byte[] poslaniBuffer = new byte[1024];
				InetAddress IP = prejemniPodatki.getAddress();
				int clientPort = prejemniPodatki.getPort();
				int odgovor = 10;
				String odgovorString = Integer.toString(odgovor);
				poslaniBuffer = odgovorString.getBytes();
				DatagramPacket odposlaniPodatki = new DatagramPacket(poslaniBuffer, poslaniBuffer.length, IP, clientPort );
				vticnica.send(odposlaniPodatki);
				System.out.println("Odgovor poslan...");
				vticnica.close();
			
		} catch (SocketException e) {
			System.out.println("Vtičnice ni mogoče zasesti");
			e.getStackTrace();
			
			System.exit(1);
			
		} catch (IOException e) {
			System.out.println("Napačen vnos.");
			System.exit(1);
		}
		// -----------------------  TCP  ------------------------------------
		// ------------------------------------------------------------------
		
		int TCPPort = 2228;
		ServerSocket serverVticnica = null;
		try {
			serverVticnica = new ServerSocket(TCPPort);
		}catch (IOException e) {
            System.out.println("Nisem mogel zasesti vrat" + TCPPort);
            System.exit(1);
        }
        Socket clientSocket = null;
        try {
            clientSocket = serverVticnica.accept();
        }catch (IOException e) {
            System.out.println("Nisem mogel dobiti povezave");
            System.exit(1);
        }
        System.out.println("Povezan, cakam na promet.");
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
    		String sprejem;
    		sprejem = in.readLine();
    		System.out.println("Prejel sem podatke. Število besed v datoteki je " + sprejem);
    		int drugaStevilka = Integer.parseInt(sprejem);
    		out.println("11");
    		String uporabnikovaStevilka = in.readLine();
    		System.out.println("Uporabnik je vnesel številko: " + uporabnikovaStevilka);
    		int tretjaStevilka = Integer.parseInt(uporabnikovaStevilka);
    		
    		int sestevek = prvaStevilka + drugaStevilka + tretjaStevilka;
    		System.out.println("Seštevek vseh treh številk je: " + sestevek);
    		
    		in.close();
    		out.close();
    		serverVticnica.close();
    		clientSocket.close();
	}

}
