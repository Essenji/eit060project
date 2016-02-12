import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class Journal {
	private String patient;
	private String nurse;
	private String doctor;
	private String division;
	private File file;

	public Journal(String patient, String doctor, String nurse, String division, File file) {
		this.patient = patient;
		this.doctor = doctor;
		this.nurse = nurse;
		this.division = division;
		this.file = file;
	}

	public boolean getAccess(User user, Privileges request) {
		if (!user.hasPrivilege(request)) {
			return false;
		}
		System.out.println("Type of doctor class : " + Doctor.class.getSimpleName());
		if (user.getUserType().equals(Doctor.class.getSimpleName())) {
			System.out.println("Class check passed");
			return user.identifyUser(doctor) || user.belongsTo(division) && request.equals(Privileges.Read);
		} else if (user.getUserType().equals(Nurse.class.getSimpleName())) {
			return user.identifyUser(nurse) || user.belongsTo(division) && request.equals(Privileges.Read);
		} else if (user.getUserType().equals(Patient.class.getSimpleName())) {
			return user.identifyUser(patient);
		} else if (user.getUserType().equals(Government.class.getSimpleName())) {
			return true;
		}
		return false;
	}

	public File fileOp(User user, Privileges request) {
		if (request.equals(Privileges.Read) || request.equals(Privileges.Write)) {
			if (getAccess(user, request)) {
				return file;
			}
		}
		return null;
	}

	/**
	 * Writes the String s to the file of the journal. All previous information is replaced with the current data.
	 * Unlike the method appendToJournal, this method creates the file if it doesn't exist. Will enter a newline
	 * after the string s has been written to the file.
	 * @param s
	 * @return false if the file could not be written to or file didn't exist.
	 */
	public boolean writeToJournal(String s){
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.println(s);
			pw.close();
		} catch (FileNotFoundException e) {
			return false;
		}
		return true;
	}

	/**
	 * Appends the String s to the file of the journal. This does not remove previous entered information.
	 * @param s
	 * @return false if the append failed or file didn't exist.
	 */
	public boolean appendToJournal(String s){
		try {
			Files.write(Paths.get(file.getPath()), s.getBytes(), StandardOpenOption.APPEND);
		}catch (IOException e) {
			return false;
		}
		return true;
	}
	public boolean deleteJournal(User user) {

		//TODO: Perhaps no access check is needed. This should be done by the authenticator.
		if (getAccess(user, Privileges.Delete)) {
			return file.delete();
		} else
			return false;

	}
	public long length(){
		return file.length();
	}

	/**
	 * Retrieves all data from the file.
	 * @return String containing the data of the file.
	 * @throws FileNotFoundException
	 */
	public String getData() throws FileNotFoundException {
		return new Scanner(file).useDelimiter("\\A").next();
	}

	@Override
	public boolean equals(Object o) {

		if(o instanceof File){
			return file.getName().equals(((File)o).getName());
		}
		return false;
	}

//	@Override
//	public int hashCode() {
//		return file != null ? file.hashCode() : 0;
//	}
}
