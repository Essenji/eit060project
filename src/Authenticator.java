import java.io.*;
import java.util.HashMap;

/**
 * Created by Tank on 2/11/2016.
 */
public class Authenticator {
    private HashMap<String, Journal> journals;

    public Authenticator() throws IOException {
        journals = new HashMap<String, Journal>();
        for (File f : new File(Variables.JOURNAL_FOLDER).listFiles()) {
            journals.put(f.getName(), createJournal(f.getPath()));
        }
    }

    private Journal createJournal(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        br.skip(Variables.FIELD1.length());
        String patient = br.readLine().trim();
        br.skip(Variables.FIELD2.length());
        String doctor = br.readLine().trim();
        br.skip(Variables.FIELD3.length());
        String nurse = br.readLine().trim();
        br.skip(Variables.FIELD4.length());
        String division = br.readLine().trim();

        return new Journal(patient, doctor, nurse, division, new File(filename));
    }


    public String[] authenticate(Privileges request, User user, String filename) {

        //TODO implement functionality for other actions
        System.out.println(journals.size());
        Journal j = journals.get(filename.trim());
        if (j == null) { //Journal not found
            return new String[]{"2", "Journal Not Found"};
        }
        boolean authenticated = j.getAccess(user, request);
        String data = "";
        try {
            data = j.getData();
        } catch (FileNotFoundException e) {
            return new String[]{"2", "Journal Not Found"};
        }
        if (authenticated) {
            return new String[]{"0", "\n"+String.valueOf(j.length()),"\n"+data };
        } else return new String[]{"1"};


    }
}
