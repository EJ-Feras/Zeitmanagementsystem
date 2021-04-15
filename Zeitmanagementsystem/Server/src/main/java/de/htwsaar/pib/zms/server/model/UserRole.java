package de.htwsaar.pib.zms.server.model;

public class UserRole {

	public static final String USER = "ROLE_USER";
	public static final String ADMIN = "ROLE_ADMIN";
	public static final String SUPERUSER = "ROLE_SUPERUSER";

	/**
	 * Test, ob es die Rolle(String) gibt
	 * 
	 * @param roleName = rolenname der geprüft wird
	 * @return true, falls es die Rolle gibt
	 */
	public static boolean roleExists(String roleName) {
		if (roleName.equals(USER) || roleName.equals(ADMIN) || roleName.equals(SUPERUSER)) {
			return true;
		}
		return false;
	}
}
