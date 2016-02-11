import java.io.File;
import java.io.FileNotFoundException;
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

	public boolean deleteJournal(User user) {
		if (getAccess(user, Privileges.Delete)) {
			return file.delete();
		} else
			return false;

	}
	public long length(){
		return file.length();
	}
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
