import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by Tank on 2/11/2016.
 */
public class Authenticator {

    public Authenticator() throws IOException {
//        journals = new HashMap<String, String>();
//        File af = new File(Variables.JOURNAL_FOLDER);
//        System.out.println("Abspath: " + af.getAbsolutePath());
//        System.out.println("Normpath " + af.getPath());
//        File[] fileList = new File(Variables.JOURNAL_FOLDER).listFiles();
//        if (fileList != null) {
//            System.out.println(fileList);
//            for (File f : fileList) {
//                journals.put(f.getName(), createJournalFromFile(f.getPath()));
//            }
//        }
    }

    private Journal createJournalFromFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        br.skip(Variables.FIELDS[0].length());
        String patient = br.readLine().trim();
        br.skip(Variables.FIELDS[1].length());
        String doctor = br.readLine().trim();
        br.skip(Variables.FIELDS[2].length());
        String nurse = br.readLine().trim();
        br.skip(Variables.FIELDS[3].length());
        String division = br.readLine().trim();
        br.close();

        return new Journal(patient, doctor, nurse, division, new File(filename));
    }

    private boolean createJournalFile(String[] filedata) {
        Field[] fields = Variables.class.getFields();
        try {
            PrintWriter pw = new PrintWriter(new File(Variables.JOURNAL_FOLDER+filedata[0]));
            for (int i = 1; i <=5 ; i++) {
                pw.println(Variables.FIELDS[i-1] + " " + filedata[i].trim());
            }
            pw.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }

    }

    /**
     * This method authenticates the user and gives the corresponding data that is reuqested.
     * The return type is a string array with the data requested.
     *
     * @param request
     * @param user
     * @param filedata
     * @return
     */

    public String[] authenticateAndRetrieveData(Privileges request, User user, String[] filedata) {


        Journal journal;
        if (request == Privileges.Create) {
            if (user.hasPrivilege(request)) {
                return (createJournalFile(Arrays.copyOfRange(filedata, 1, filedata.length))) ? new String[]{"0"} : new String[]{"3"};
            }else return new String[]{"1"};
        }


        try {
            journal = createJournalFromFile(Variables.JOURNAL_FOLDER + filedata[1].trim());
        } catch (IOException e) {
            return new String[]{ResponseCode.FileNotFound.toString()};
        }

        if (journal == null) { //Journal not found
            return new String[]{ResponseCode.FileNotFound.toString()};
        }

        if (journal.getAccess(user, request)) {

            switch (request) {
                case Read:
                    return getReturnString(journal);
                case Write:
                    return getReturnString(journal);
                case Delete:
                    return journal.deleteJournal() ? new String[]{ResponseCode.Success.toString()} : new String[]{ResponseCode.FileNotFound.toString()};


            }

            String data = "";
            try {
                data = journal.getData();
            } catch (FileNotFoundException e) {
                return new String[]{ResponseCode.FileNotFound.toString()};
            }

            return new String[]{ResponseCode.Success.toString(), "\n" + String.valueOf(journal.length()), "\n" + data};

        }

        return new String[]{ResponseCode.Failure.toString()};
    }

    private String[] getReturnString(Journal j) {
        String data = "";
        try {
            data = j.getData();
        } catch (FileNotFoundException e) {
            return new String[]{ResponseCode.FileNotFound.toString()};
        }
        return new String[]{ResponseCode.Success.toString(), "\n" + String.valueOf(j.length()), "\n" + data};
    }
}
