import java.io.*;
import java.util.Arrays;

/**
 * Created by Tank on 2/11/2016.
 */
public class Authenticator {

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
        if(!user.hasPrivilege(request)) return new String[]{ResponseCode.Failure.toString()};
        Journal journal = null;
        if(request == Privileges.Create && user.hasPrivilege(request)) {
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
            return new String[]{ResponseCode.Failure.toString()};
        }
        if (journal.getAccess(user, request)) {
            switch (request) {
                case Read:
                    return readData(journal);
                case Write:
                    return Parser.printToFile(Arrays.copyOfRange(filedata, 1, filedata.length))  ? new String[]{ResponseCode.Success.toString()} : new String[]{ResponseCode.FileNotCreated.toString()};
                case Delete:
                    return journal.deleteJournal() ? new String[]{ResponseCode.Success.toString()} : new String[]{ResponseCode.FileNotFound.toString()};

            }

        }
        return new String[]{ResponseCode.Failure.toString()};
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
            return new String[]{ResponseCode.FileNotFound.toString()};
        }
        return new String[]{ResponseCode.Success.toString(), String.valueOf(j.length()), data};
    }
}
