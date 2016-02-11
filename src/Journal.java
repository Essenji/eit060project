import java.io.File;

public class Journal {
	private String patient;
	private String nurse;
	private String doctor;
	private String division;
	private File file;
	
	public Journal(String patient, String nurse, String doctor, String division){
		this.patient = patient;
		this.nurse = nurse;
		this.doctor = doctor;
		this.division = division;
	}
	
	public boolean getAccess(User user, Privileges request){
		if(!user.hasPrivilege(request)){return false;}
		else return true;
	}
	
	
}
