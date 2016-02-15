import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.io.*;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server implements Runnable {

    private ServerSocket serverSocket = null;
    private static int numConnectedClients = 0;
    private Authenticator auth;

    public Server(ServerSocket ss) throws IOException {
        serverSocket = ss;
        newListener();
        try {
            auth = new Authenticator();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try {
            SSLSocket socket = (SSLSocket) serverSocket.accept();
            newListener();
            SSLSession session = socket.getSession();
            X509Certificate cert = (X509Certificate) session.getPeerCertificateChain()[0];
            System.out.println("Length of chain ------" + session.getPeerCertificateChain().length);
            String subject = cert.getSubjectDN().getName();
            String issurr = cert.getIssuerDN().getName();
            System.out.println("Subject name " + session.getPeerCertificateChain()[1].getSubjectDN());
            numConnectedClients++;
            System.out.println("client connected");
            System.out.println("client name (cert subject DN field): " + subject);
            System.out.println("issuer name (cert issuer DN field): " + issurr);
            System.out.println("serial (cert issuer DN field): " + cert.getSerialNumber());
            System.out.println(numConnectedClients + " concurrent connection(s)\n");

            PrintWriter out = null;
            BufferedReader in = null;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String clientMsg = null;
            while ((clientMsg = in.readLine()) != null) {
                String[] arguments = Parser.parseLine(clientMsg);
                Privileges request = Privileges.fromInteger(Integer.parseInt(arguments[0]));
                //TODO: Get user data from Clientside corretly.
                //TODO: From here it is assumed that the user has been correctly verified through certificate
//                StringBuilder sb = new StringBuilder();
                if (request == Privileges.Write){
                    arguments[0] = Privileges.Read.toString();
                }
                String[] response = getResponse(arguments);

                String data;
                for (int i = 0; i < response.length; i++) {
                    response[i] = response[i].replaceAll("\\r?\\n?\\s", " ").trim();
                }
                data = Parser.arrayToString(response).toString();
//                System.out.println("The recieved response: " + data);

                out.println(data);
                out.flush();

                System.out.println(data);

                //Should the option for Writing be chosen, the server will wait for the file data to be written
                if (request == Privileges.Write && ResponseCode.fromInteger(Integer.parseInt(response[0] )) == ResponseCode.Success) {
//                    System.out.println("Priviliges == write");
                    if ((clientMsg = in.readLine()) != null) {
//                        System.out.println("Creating new file here");

                        if (ResponseCode.fromInteger(Integer.parseInt(response[0])) == ResponseCode.Success) {
//                            System.out.println("Creating new file");
                            String filename = arguments[1];
//                            String length = arguments[0];
                            arguments = Parser.parseLine(clientMsg);
//                            arguments[0] = request.toString();
                            List<String> list = new ArrayList<String>(Arrays.asList(arguments));
                            list.add(1, filename);
//                            list.add(0,arguments[1]);
                            Object[] temp = list.toArray();
                            String [] writeInput = new String[temp.length];
                            for (int i = 0; i < temp.length ; i++) writeInput[i] = temp[i].toString();
                            writeInput[0] = request.toString();
//                            System.out.println(Arrays.toString(writeInput));
//                            System.out.println());
                            System.out.println("Inbefore the flush");
                            out.println(getResponse(writeInput)[0]);
                            out.flush();
//                            break;
                        }
                    }
                }
                out.println(data);
                out.flush();

            }
            in.close();
            out.close();
            socket.close();
            numConnectedClients--;
            System.out.println("client disconnected");
            System.out.println(numConnectedClients + " concurrent connection(s)\n");
        } catch (IOException e) {
            System.out.println("Client died: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    private String[] getResponse(String[] arguments) {
        return auth.authenticateAndRetrieveData(Privileges.fromInteger(Integer.parseInt(arguments[0])), new Doctor("doctorAlban", "Csk"), arguments);
    }

    private void newListener() {
        (new Thread(this)).start();
    } // calls run()

    public static void main(String args[]) {
//      args = new String [1];
//    	args[0] = "9876";

        System.out.println("\nServer Started\n");
        int port = -1;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        String type = "TLS";
        try {

            ServerSocketFactory ssf = getServerSocketFactory(type);
            ServerSocket ss = ssf.createServerSocket(port);
            ((SSLServerSocket) ss).setNeedClientAuth(true); // enables client authentication
            new Server(ss);
        } catch (IOException e) {
            System.out.println("Unable to start Server: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static ServerSocketFactory getServerSocketFactory(String type) {
        if (type.equals("TLS")) {
            SSLServerSocketFactory ssf = null;
            try { // set up key manager to perform server authentication
                SSLContext ctx = SSLContext.getInstance("TLS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                KeyStore ks = KeyStore.getInstance("JKS");
                KeyStore ts = KeyStore.getInstance("JKS");
                char[] password = "password".toCharArray();

                ks.load(new FileInputStream("serverkeystore"), password);  // keystore password (storepass)
                ts.load(new FileInputStream("servertruststore"), password); // truststore password (storepass)
                kmf.init(ks, password); // certificate password (keypass)
                tmf.init(ts);  // possible to use keystore as truststore here
                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                ssf = ctx.getServerSocketFactory();
                return ssf;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return ServerSocketFactory.getDefault();
        }
        return null;
    }
}
