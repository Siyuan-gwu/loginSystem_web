package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

//write JSONArray and JSONObject to the response body
public class RpcHelper {

	// 用户和Session绑定关系
	public static final Map<String, HttpSession> USR_SESSION = new HashMap<String, HttpSession>();
	// SessionId和用户的绑定关系
	public static final Map<String, String> SESSIONID_USR = new HashMap<String, String>();

	// Writes a JSONArray to http response.
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException {
		response.setContentType("application/json");
		response.getWriter().print(array);
	}

	// Writes a JSONObject to http response.
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setContentType("application/json");
		response.getWriter().print(obj);
	}

	public static void userLoginHandle(HttpServletRequest request) {
		// 当前登录的用户
		String userId = request.getParameter("user_id");
		// 当前sessionId
		String sessionId = request.getSession().getId();
		// 删除当前sessionId绑定的用户，用户--HttpSession
		USR_SESSION.remove(SESSIONID_USR.remove(sessionId));
		// 删除当前登录用户绑定的HttpSession
		HttpSession session = USR_SESSION.remove(userId);
		if (session != null) {
			SESSIONID_USR.remove(session.getId());
			session.setAttribute("msg", "您的账号已在另外一台设备登陆");
		}
	}

	public static JSONObject readJSONObject(HttpServletRequest request) {

		StringBuilder sb = new StringBuilder();

		try {
			BufferedReader reader = request.getReader();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return new JSONObject(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new JSONObject();
	}

}
