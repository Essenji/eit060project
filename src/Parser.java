import java.io.*;
import java.util.Arrays;

/**
 * Created by Tank on 2/11/2016.
 */
public class Parser {
    public static String[] parseLine(String filename){
                return filename.split("\\$");
        }

//    public static StringBuilder getJournalList() {
//        StringBuilder sb = new StringBuilder();
//        File temp = new File(Variables.JOURNAL_FOLDER);
//        for (File f : temp.listFiles()) {
//            sb.append(f.getName() + "\n");
//        }
//        return sb;
//    }
    public static StringBuilder arrayToString(String[] array){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i] + " ");
        }
        return sb;
    }

    public static StringBuilder createFieldStructure(String[] array){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; (i <array.length && i < Variables.FIELDS.length) ; i++) {
            if(i < Variables.FIELDS.length-1){
            sb.append(Variables.FIELDS[i] + " "+ array[i] + "\n");}
            else sb.append(Variables.FIELDS[i] + " "+ array[i]+ " ");

        }
        for (int i = Variables.FIELDS.length; i < array.length; i++) {
            sb.append(array[i]+ " ");
        }
        return sb;
    }
    public static String formatNewLine(String[] response) {
        String data;
        for (int i = 0; i < response.length; i++) {
            response[i] = response[i].replaceAll("\\r?\\n?\\s", " ").trim();
        }
        data = Parser.arrayToString(response).toString();
        return data;
    }
    public static boolean printToFile(String[] filedata) {
        try {
            PrintWriter pw = new PrintWriter(new File(Variables.JOURNAL_FOLDER + filedata[0]));
            StringBuilder sb = Parser.createFieldStructure(Arrays.copyOfRange(filedata, 1, filedata.length));
            pw.println(sb);
            pw.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }catch (ArrayIndexOutOfBoundsException e){
            return false;
        }
    }
//    public static boolean
    public static Journal createJournalFromFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String patient, doctor,nurse,division;
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
            return null;
        }
        br.close();
        return new Journal(patient, doctor, nurse, division, new File(filename));
    }

    public static String[] listJournals() {
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
        return new String[]{ResponseCode.FileNotFound.toString()};
    }



}
