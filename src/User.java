
public abstract class User {
	
		public abstract Privileges[] getPrivileges();

		public boolean hasPrivilege(Privileges request) {
			return true;
			//TODO: implement
		}
		
}
