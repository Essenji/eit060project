package client;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.security.cert.X509Certificate;

/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */
public class Client {

	public Client(String host, int port)
	{
		try {
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
			
			String certificateName = "";
			char[] password = "password".toCharArray();
			
			System.out.println("Enter keystore path:");
			certificateName = inputReader.readLine();

			System.out.println("Enter keystore password:");
			password = inputReader.readLine().toCharArray();
			
			SSLSocketFactory factory = setupCertificates(certificateName,password);
			SSLSocket socket = setupSocket(host, port, factory);

			PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			communicate(inputReader, serverWriter, serverReader);
			serverReader.close();
			serverWriter.close();
			inputReader.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SSLSocketFactory setupCertificates(String keystoreName, char[] password) throws IOException {
		SSLSocketFactory factory = null;
		try {
			KeyStore keystore = KeyStore.getInstance("JKS");
			KeyStore truststore = KeyStore.getInstance("JKS");
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
			SSLContext context = SSLContext.getInstance("TLS");
			
			keystore.load(new FileInputStream(keystoreName), password);
			
			char[] trustPassword = "password".toCharArray();
			truststore.load(new FileInputStream("certificates/truststore"), trustPassword);
			
			keyManagerFactory.init(keystore, password);
			trustManagerFactory.init(truststore);
			context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
			factory = context.getSocketFactory();
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		return factory;
	}

	private SSLSocket setupSocket(String host, int port, SSLSocketFactory factory) 
			throws IOException, UnknownHostException, SSLPeerUnverifiedException 
	{
		SSLSocket socket = (SSLSocket) factory.createSocket(host, port);

		socket.startHandshake();

		SSLSession session = socket.getSession();
		X509Certificate cert = (X509Certificate) session.getPeerCertificateChain()[0];
		String subject = cert.getSubjectDN().getName();
		System.out.println("Server CN: " + subject);
		return socket;
	}

	private void communicate(BufferedReader inputReader, PrintWriter serverWriter,
			BufferedReader serverReader) throws IOException {
		//TODO
		String input;
		while (true) {
			System.out.print(">");
			input = inputReader.readLine();
			
			if (input.equalsIgnoreCase("quit")) {
				break;
			}
			
			
			serverWriter.println(input);
			serverWriter.flush();
			System.out.println("done");
	
			System.out.println("received '" + serverReader.readLine() + "' from server\n");
		}
	}

	
	public static void main(String[] args) throws Exception 
	{
		String host = null;
		int port = -1;
		
		if (args.length < 2) {
			System.out.println("USAGE: java Client hostname port");
			System.exit(-1);
		}
		
		try {
			host = args[0];
			port = Integer.parseInt(args[1]);
		} catch (IllegalArgumentException e) {
			System.out.println("USAGE: java Client hostname port");
			System.exit(-1);
		}
		
		new Client(host, port);
	}

}
