package cloudapp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cloudapp.CertificateEntity;
import cloudapp.UserEntity;

import com.googlecode.objectify.ObjectifyService;

public class ContextInitializer implements ServletContextListener {
	public void contextDestroyed(ServletContextEvent arg) {}

	public void contextInitialized(ServletContextEvent arg) {
		ObjectifyService.register(CertificateEntity.class);
		ObjectifyService.register(UserEntity.class);
	}
}
