import java.io.*;
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
        System.out.println("Filename: " + filename + " Buffered: " + br);
        String patient, doctor,nurse,division = null;
        try {
            br.skip(Variables.FIELDS[0].length());
            patient = br.readLine().trim();
            br.skip(Variables.FIELDS[1].length());
            doctor = br.readLine().trim();
            br.skip(Variables.FIELDS[2].length());
            nurse = br.readLine().trim();
            br.skip(Variables.FIELDS[3].length());
            division = br.readLine().trim();
        }catch (NullPointerException e){
           br.close();
            return new Journal("N/A","N/A","N/A","N/A", new File(filename));
        }
        br.close();

        return new Journal(patient, doctor, nurse, division, new File(filename));
    }

    private boolean createJournalFile(String[] filedata) {
        try {
            PrintWriter pw = new PrintWriter(new File(Variables.JOURNAL_FOLDER + filedata[0]));
           StringBuilder sb = Parser.createFieldStructure(Arrays.copyOfRange(filedata, 1, filedata.length));
            System.out.println("StringBuilder : " + sb.toString());
            pw.println(sb);
           pw.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }

    }

    /**
     * Retrieves a list of all journal files
     * @return
     */
    private String[] getJournalList() {

        File folder = new File(Variables.JOURNAL_FOLDER);
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < files.length; i++) {
                    sb.append(files[i].getName()+" ");


                }
                return new String[]{ResponseCode.Success.toString(), sb.length() + "", sb.toString()};
            }
        }
        {
            return new String[]{ResponseCode.Success.toString()};
        }

    }

    /**
     * This method authenticates the user and gives the corresponding data that is requested.
     * The return type is a string array with the data requested.
     *
     * @param request
     * @param user
     * @param filedata
     * @return
     */


    public String[] authenticateAndRetrieveData(Privileges request, User user, String[] filedata) {
        System.out.println("Authenticating..");
        Journal journal = null;
        if(request == Privileges.Create && user.hasPrivilege(request)) {
            System.out.println("Current request:" + request);
            return (createJournalFile(Arrays.copyOfRange(filedata, 1, filedata.length))) ? new String[]{ResponseCode.Success.toString()} : new String[]{ResponseCode.FileNotFound.toString()};

        }
        if (filedata.length > 1) {
            try {
                journal = createJournalFromFile(Variables.JOURNAL_FOLDER + filedata[1].trim());
            } catch (IOException e) {
                return new String[]{ResponseCode.FileNotFound.toString()};
            }
        }
        if (journal == null ) { //Journal not found
            if(user.hasPrivilege(request) && request == Privileges.List) return getJournalList();
            return new String[]{ResponseCode.FileNotFound.toString()};
        }
        System.out.println("Current request:" + request);

        if (journal.getAccess(user, request)) {

            switch (request) {
                case Read:
                    return getReturnString(journal);
                case Write:
                    return journal.writeToJournal(Parser.createFieldStructure(Arrays.copyOfRange(filedata, 2, filedata.length)).toString()) ? new String[]{ResponseCode.Success.toString()} : new String[]{ResponseCode.FileNotCreated.toString()};
                case Delete:
                    return journal.deleteJournal() ? new String[]{ResponseCode.Success.toString()} : new String[]{ResponseCode.FileNotFound.toString()};
//                case Create:
//                    return (createJournalFile(Arrays.copyOfRange(filedata, 1, filedata.length))) ? new String[]{ResponseCode.Success.toString()} : new String[]{ResponseCode.FileNotFound.toString()};


            }

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
        return new String[]{ResponseCode.Success.toString(), String.valueOf(j.length()), data};
    }
}
