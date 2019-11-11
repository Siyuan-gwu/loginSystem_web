package db;

public interface DBConnection {
	/**
	 * Close the connection.
	 */
	public void close();
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public boolean varifyUserId(String userId);
	
	/**
	 * 
	 * @param userId
	 */
	public void initialRecord(String userId);
	
	/**
	 *  varify the username and password
	 * @param userId
	 * @param password
	 * @return
	 */
	public boolean varifyLogin(String userId, String password);
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public boolean checkLocked(String userId);
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public String getRestTimeToUnLock(String userId);
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public int addFailureNum(String userId);
	
	/**
	 * 
	 * @param userId
	 */
	public void clearLoginRecord(String userId);
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public int getNumOfRestAttempt(String userId);
	
	
	/**
	 * 
	 * @param userId
	 * @param password
	 * @return
	 */
	public boolean registerUser(String userId, String password);
}
