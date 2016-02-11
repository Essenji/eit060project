
public enum Privileges {
	Write, Read, Delete, Create;
	public static Privileges fromInteger(int x) {
		switch(x) {
			case 0:
				return Write;
			case 1:
				return Read;
			case 2:
				return Delete;
			case 3:
				return Create;
		}
		return null;
	}
}
