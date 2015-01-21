package ctrl;

import db.database;

public class Controller {
	private String id;

	public Controller(String personalId) {
		this.id = personalId;
	}
	
	/**
	 * Connects to the database and creates a Driver with the login number
	 * @param id  -- the controller's personal id
	 * @return    -- the controller if login was successful or null otherwise
	 */
	public static Controller tryLogin(String id) {
		if (database.busDatabase == null)             // initialise database when logging in
			database.openBusDatabase();
		
		// TODO: include controller in the database and read it from there
		if (!id.equals("superController"))
			return null;
		return new Controller("superController");
	}
}
