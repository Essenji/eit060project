import java.io.File;

/**
 * Created by Tank on 2/11/2016.
 */
public class RequestParser {
    public static String[] parseLine(String filename){
//        switch (code){
//            case 0:
//                return new String[]{filename.split("\\r?\\n")[0]};
//            case 1:
//                return new String[]{filename.split("\\r?\\n")[0]};
//            case 2:
//                return new String[]{filename.split("\\r?\\n")[0]};
//            case 3:
                return filename.split("\\r?\\n?\\s");
//            case 4:
//                break;

        }



    public static StringBuilder getJournalList() {
        StringBuilder sb = new StringBuilder();
        File temp = new File(Variables.JOURNAL_FOLDER);
        for (File f : temp.listFiles()) {
            sb.append(f.getName() + "\n");
        }
        return sb;
    }
    public static StringBuilder arrayToString(String[] array){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i] + " ");
        }
        return sb;
    }
//        return null;


}
