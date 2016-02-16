import java.io.*;
import java.util.Arrays;

/**
 * Created by Tank on 2/11/2016.
 */
public class Authenticator {

    public static final String[] Success = new String[]{ResponseCode.Success.toString()};
    public static final String[] FileNotFound = new String[]{ResponseCode.FileNotFound.toString()};
    public static final String[] FileNotCreated = new String[]{ResponseCode.FileNotCreated.toString()};
    public static final String[] Failure = new String[]{ResponseCode.Failure.toString()};

    public Authenticator() throws IOException {
    }

    private Journal createJournalFromFile(String filename) throws IOException {
        return Parser.createJournalFromFile(filename);
    }


    private boolean createJournalFile(String[] filedata) {
        return Parser.printToFile(filedata);
    }

    private String[] getJournalList() {
        return Parser.listJournals();

    }


    /**
     * This method authenticates the user and gives the corresponding data that is requested.
     * The return type is a string array with the data requested.
     * @param request
     * @param user
     * @param filedata
     * @return
     */
    public String[] authenticateAndRetrieveData(Privileges request, User user, String[] filedata) {
        if(!user.hasPrivilege(request)) return Failure;
        Journal journal = null;
        if (filedata.length > 1 ) {
            try {
                journal = createJournalFromFile(Variables.JOURNAL_FOLDER + filedata[1].trim());
            } catch (IOException e) {
                if(request == Privileges.Create && user.hasPrivilege(request)){
                    return Parser.printToFile(Arrays.copyOfRange(filedata, 1, filedata.length))  ? Success : FileNotCreated;
                }
                return FileNotFound;
            }
        }
        System.out.println("Now trying to create journal");
        if(request == Privileges.Create && user.hasPrivilege(request)) {
            System.out.println("Priviliges checked");

            if(journal == null){
                System.out.println("Journal nonexistant");
            return (createJournalFile(Arrays.copyOfRange(filedata, 1, filedata.length))) ? Success : FileNotFound;
            }
            else{
                System.out.println("Journal exists");
                return journal.getAccess(user, request) && createJournalFile(Arrays.copyOfRange(filedata, 1, filedata.length))? Success : Failure;

            }


        }
        if (journal == null ) { //Journal not found
            if(user.hasPrivilege(request) && request == Privileges.List) return getJournalList();
            return Failure;
        }
        if (journal.getAccess(user, request)) {
            switch (request) {
                case Read:
                    return readData(journal);
                case Write:
                    return Parser.printToFile(Arrays.copyOfRange(filedata, 1, filedata.length))  ? Success : FileNotCreated;
                case Delete:
                    return journal.deleteJournal() ? Success : FileNotFound;

            }

        }
        return Failure;
    }

    /**Concatenates the whole journal file.
     * @param j the Journal Object containing the File with the journal data
     * @return a String with data
     */
    private String[] readData(Journal j) {
        String data = "";
        try {
            data = j.getData();
        } catch (FileNotFoundException e) {
            return Authenticator.FileNotFound;
        }
        return new String[]{ResponseCode.Success.toString(), String.valueOf(j.length()), data};
    }
}
