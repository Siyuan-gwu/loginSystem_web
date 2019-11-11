package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import db.DBConnection;
import db.MySQLDBUtil;

public class MySQLConnection implements DBConnection {

	private Connection conn;
	private SimpleDateFormat sdf;
	public static final int NUM_TO_ATTEMPT = 3;
//	private static final String pattern = "^(?![\\d]+$)(?![a-zA-Z]+$)(?![!#$%^&*]+$)[\\da-zA-Z!@#$&()]{8,18}$";

	public MySQLConnection() {
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
//			create a instance of DriverManager, and then get connection.
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[]) {
		DBConnection conn = new MySQLConnection();
//		conn.addFailureNum("zsy78006605");
		conn.clearLoginRecord("zsy78006605");
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void initialRecord(String userId) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return;
		}
		try {
			String sql = "INSERT IGNORE INTO login_record(user_id,failure_num, lock_flag) VALUES (?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setInt(2, 0);
			ps.setString(3, "0");
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean varifyUserId(String userId) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return false;
		}

		try {
			String sql = "SELECT user_id FROM users WHERE user_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean varifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return false;
		}

		try {
			String sql = "SELECT user_id FROM users WHERE user_id=? AND password=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, password);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				clearLoginRecord(userId);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean checkLocked(String userId) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return false;
		}
		String locked = "";
		try {
			String sql = "SELECT lock_flag FROM login_record WHERE user_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				locked = rs.getString("lock_flag");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return locked.equals("1");

	}

	@Override
	public String getRestTimeToUnLock(String userId) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return null;
		}
		String time = "";
		try {
			String sql = "SELECT login_time FROM login_record WHERE user_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				time = rs.getString("login_time");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long hours = 0;
		if (time.length() > 0) {
			long prev = 0;
			long cur = 0;
			try {
				prev = sdf.parse(time).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				cur = sdf.parse(sdf.format(new Date())).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hours = 24 - (cur - prev) / (1000 * 60 * 60);
		}
		return String.valueOf(hours);
	}

	@Override
	public int addFailureNum(String userId) {
		// TODO Auto-generated method stub
		if (conn == null) {
			System.out.println("connection failed");
			return 0;
		}
		int num = getFailureNum(userId) + 1;
		updateLoginTime(userId);
		if (num >= NUM_TO_ATTEMPT) {
			setLocked(userId);
		}
		try {
			String sql = "UPDATE login_record SET failure_num=? WHERE user_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, num);
			ps.setString(2, userId);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(num);
		return num;
	}
	
	private int getFailureNum(String userId) {
		int num = 0;
		try {
			String sql = "SELECT failure_num FROM login_record WHERE user_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				num = rs.getInt("failure_num");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}
	

	@Override
	public int getNumOfRestAttempt(String userId) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return 0;
		}
		int num = 0;
		try {
			String sql = "SELECT failure_num FROM login_record WHERE user_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				num = rs.getInt("failure_num");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NUM_TO_ATTEMPT - num;
		
	}

	private void updateLoginTime(String userId) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return;
		}
		try {
			String sql = "UPDATE login_record SET login_time=? WHERE user_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, sdf.format(new Date()));
			ps.setString(2, userId);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setLocked(String userId) {
		if (conn == null) {
			return;
		}
		try {
			String sql = "UPDATE login_record SET lock_flag=1 WHERE user_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void clearLoginRecord(String userId) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return;
		}
		try {
			String sql = "UPDATE login_record SET failure_num=?,login_time=?,lock_flag=? WHERE user_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, 0);
			ps.setString(2, sdf.format(new Date()));
			ps.setString(3, "0");
			ps.setString(4, userId);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean registerUser(String userId, String password) {
		// TODO Auto-generated method stub
		if (conn == null) {
			System.out.println("DB connection failed");
		}

		try {
			String sql = "INSERT IGNORE INTO users VALUES(?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, password);

			return ps.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}
