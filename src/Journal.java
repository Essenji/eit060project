import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Journal {
	private String patient;
	private String nurse;
	private String doctor;
	private String division;
	private File file;

	public Journal(String patient, String nurse, String doctor, String division) {
		this.patient = patient;
		this.nurse = nurse;
		this.doctor = doctor;
		this.division = division;
	}

	public boolean getAccess(User user, Privileges request) {
		if (!user.hasPrivilege(request)) {
			return false;
		}
		if (user.getUserType().equals(Doctor.class.getSimpleName())) {
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

}
