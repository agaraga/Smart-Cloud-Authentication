package cloudapp;

import java.io.IOException;

import javax.servlet.http.*;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class UserListServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		try {
			resp.setContentType("text/plain");
			String userName = req.getParameter("user");
			
			Objectify ofy = ObjectifyService.begin();
			UserEntity userEntity = new UserEntity(userName, UserEntity.Type.ADMIN);
			if (ofy.find(UserEntity.class, userName) != null) {
				resp.getWriter().println("User already created");
				return;
			}
			ofy.put(userEntity);
			resp.getWriter().println("User successfully created");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
