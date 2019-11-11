package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import db.DBConnection;
import db.MySQLConnection;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		DBConnection conn = new MySQLConnection();

		try {
//			if the session doesn't exist, we don't create new session (false)
			HttpSession session = request.getSession(false);

			JSONObject obj = new JSONObject();

			if (session != null) {
				String userId = session.getAttribute("user_id").toString();
				obj.put("status", "OK").put("user_id", userId);
			} else {
				obj.put("status", "Invalid Session");
//				server refuses the request
				response.setStatus(403);
			}
			RpcHelper.writeJsonObject(response, obj);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			conn.close();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		DBConnection conn = new MySQLConnection();

		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			String password = input.getString("password");

			JSONObject obj = new JSONObject();
			
			//if the lock time expired, then unlock the account.
			int timeToWait = Integer.parseInt(conn.getRestTimeToUnLock(userId));
			if (conn.checkLocked(userId) && timeToWait <=0) {
				conn.clearLoginRecord(userId);
			}
			
			if (!conn.varifyUserId(userId)) {
				obj.put("status", "wrong_input");
				response.setStatus(401);
			} else {
				if (conn.checkLocked(userId)) {
					obj.put("status", "locked").put("wait_time", timeToWait);
//					server refuses the request
					response.setStatus(403);
				} else {
					if (conn.varifyLogin(userId, password)) {
						HttpSession session = request.getSession();
						session.setAttribute("user_id", userId);
						
//						The session keeps active 30 mins, session duration
						session.setMaxInactiveInterval(1800);
						obj.put("status", "OK").put("user_id", userId);
					} else {
						conn.addFailureNum(userId);
						if (conn.checkLocked(userId)) {
							obj.put("status", "locked").put("wait_time", "24");
//							server refuses the request
							response.setStatus(403);
						} else {
							int restAttempt = conn.getNumOfRestAttempt(userId);
							obj.put("status", "wrong_password").put("rest_attempt", restAttempt);
							response.setStatus(401);
						}
					}
				}
			}
			RpcHelper.writeJsonObject(response, obj);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			conn.close();
		}
	}

}
