package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import db.MySQLDBUtil;

public class MySQLTableCreation {
	// run this as Java application to reset db schema.
			public static void main(String[] args) {
				try {
					// Step 1 Connect to MySQL.
					System.out.println("Connecting to " + MySQLDBUtil.URL);
					Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
					Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);

					if (conn == null) {
						return;
					}

					// Step 2 Drop tables in case they exist.
					// we need to drop table when we need to create a new table.
					Statement statement = conn.createStatement();
					String sql = "DROP TABLE IF EXISTS users";
					statement.executeUpdate(sql);

					sql = "DROP TABLE IF EXISTS login_record";
					statement.executeUpdate(sql);


					// Step 3 Create new tables
					sql = "CREATE TABLE users (" 
							+ "user_id VARCHAR(255) NOT NULL," 
							+ "password VARCHAR(255) NOT NULL,"
							+ "PRIMARY KEY (user_id)" 
							+ ")";
					statement.executeUpdate(sql);
					
					sql = "CREATE TABLE login_record (" 
							+ "user_id VARCHAR(255) NOT NULL," 
							+ "failure_num INT NOT NULL DEFAULT 0," 
							+ "login_time DATETIME,"
							+ "lock_flag VARCHAR(5) NOT NULL DEFAULT '0',"
							+ "PRIMARY KEY (user_id, login_time),"
							+ "FOREIGN KEY (user_id) REFERENCES users(user_id)"
							+ ")";
					statement.executeUpdate(sql);
					
					// Step 4: insert fake user 1111/2222
					sql = "INSERT INTO users VALUES('1111', '2222')";
					statement.executeUpdate(sql);


					conn.close();
					System.out.println("Import done successfully");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
}
