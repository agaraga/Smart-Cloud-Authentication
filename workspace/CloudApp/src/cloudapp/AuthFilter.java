package cloudapp;

import java.io.IOException;

import javax.servlet.*;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class AuthFilter implements Filter {
    public void doFilter(ServletRequest req,
			 ServletResponse resp,
			 FilterChain chain) 
               throws ServletException, IOException {

    	resp.setContentType("text/plain");
    	
    	// authenticate user
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        
        if (user != null) {
	        Objectify ofy = ObjectifyService.begin();
        	UserEntity userEntity = ofy.find(UserEntity.class, user.getNickname());
        	if (userEntity == null) {
        		resp.getWriter().println("User not found in the database. Ask the administrator to create one");
        		return;
        	}
        	if (userEntity.getType() != UserEntity.Type.ADMIN) {
        		resp.getWriter().println("This is a normal account. You must be logged in as an administrator");
        		return;
        	}  
        	
        } else {
        	//resp.sendRedirect(userService.createLoginURL(req.)));    	
    		//resp.getWriter().println("Not logged in");
        }    	
    	chain.doFilter(req, resp);
   }

   public void init(FilterConfig config) throws ServletException {
       //work on config
   }

   public void destroy() {
       //work on clean up
   }
}
