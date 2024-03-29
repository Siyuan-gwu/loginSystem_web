package db;

public class MySQLDBUtil {
	private static final String HOSTNAME = "localhost";
	private static final String PORT_NUM = "3306";
	public static final String DB_NAME = "customers";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
	//The url will be used in creation class to create tables.
	public static final String URL = "jdbc:mysql://" 
			+ HOSTNAME + ":" + PORT_NUM + "/" + DB_NAME 
			+ "?user=" + USERNAME+ "&password=" + PASSWORD 
			+ "&autoReconnect=true&serverTimezone=UTC";
}
