package cloudapp;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class AuthServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			resp.getWriter().println(e.getLocalizedMessage());
		}
	}
}
