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

import util.ResponseCode;

/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */
public class Client {
	
	
	private static final char SUCCESS_RETURN_VALUE = '0';
	private static final String READ_COMMAND = "read";
	private static final String WRITE_COMMAND = "write";
	private static final String DELETE_COMMAND = "delete";
	private static final String CREATE_COMMAND = "create";
	private static final String LIST_COMMAND = "list";

	public Client(String host, int port)
	{
		try {
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
			
			String certificateName = "";
			char[] password = "password".toCharArray();
			
			System.out.println("Enter keystore path:");
			certificateName = "certificates/doctors/doctor1keystore";//inputReader.readLine();

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
			
			if (input.startsWith(WRITE_COMMAND + " ")) {
				input = "0$" + input.substring(WRITE_COMMAND.length()+1); 
				excecuteWriteCommand(input, serverReader, serverWriter);
			} else if (input.startsWith(READ_COMMAND + " ")) {
				input = "1$" + input.substring(READ_COMMAND.length()+1); 
				excecuteReadCommand(input, serverReader, serverWriter);
			} else if (input.startsWith(DELETE_COMMAND + " ")) {
				input = "2$" + input.substring(DELETE_COMMAND.length()+1); 
				excecuteDeleteCommand(input, serverReader, serverWriter);
			} else if (input.startsWith(CREATE_COMMAND + " ")) {
				input = "3$" + input.substring(CREATE_COMMAND.length()+1); 
				excecuteCreateCommand(input, serverReader, serverWriter);
			} else if (input.startsWith(LIST_COMMAND + " ")) {
				input = "4$" + input.substring(LIST_COMMAND.length()+1); 
				excecuteListCommand(input, serverReader, serverWriter);
			} else {
				System.out.println("Unknown command: " + input);
			}
		}
	}
	
	private void excecuteWriteCommand(String input, BufferedReader serverReader,
			PrintWriter serverWriter) {
		// TODO Auto-generated method stub
		
	}

	private void excecuteReadCommand(String input, BufferedReader serverReader,
			PrintWriter serverWriter) throws IOException {
		
		serverWriter.println(input);
		serverWriter.flush();

		String response = serverReader.readLine();
		
		ResponseCode responseCode = ResponseCode.fromInteger(Integer.parseInt(response.substring(0, 1)));
		
		if (responseCode == ResponseCode.Success) {
			System.out.println(response.substring(2).replace('$', '\n'));
		} else {
			printErrorCode(responseCode);
		}
	}

	private void excecuteDeleteCommand(String input, BufferedReader serverReader,
			PrintWriter serverWriter) {
		// TODO Auto-generated method stub
		
	}

	private void excecuteCreateCommand(String input, BufferedReader serverReader,
			PrintWriter serverWriter) {
		// TODO Auto-generated method stub
		
	}

	private void excecuteListCommand(String input, BufferedReader serverReader,
			PrintWriter serverWriter) {
		// TODO Auto-generated method stub
		
	}

	private void printErrorCode(ResponseCode responseCode) {
		switch (responseCode) {
		case Failure:
			System.out.println("Operation failed: access denied");
			break;
		case FileNotCreated:
			System.out.println("Operation failed: could not create file");
			break;
		case FileNotFound:
			System.out.println("Operation failed: file not found");
			break;
		default:
			System.out.println("Operation failed: unknown response");
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
