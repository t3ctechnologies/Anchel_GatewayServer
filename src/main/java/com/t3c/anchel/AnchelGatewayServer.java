package com.t3c.anchel;

import java.io.File;
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

import com.t3c.anchel.gateway.ftp.ExecGatewayFtpServer;
import com.t3c.anchel.gateway.ftp.ServerInitDatabase;

public class AnchelGatewayServer extends ContextLoaderListener {

	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("Anchel gateway server is starting");
		File waarpFile = new File(this.getClass().getClassLoader().getResource("config-serverA.xml").getFile());
		File gatewayFile = new File(this.getClass().getClassLoader().getResource("Gg-FTP.xml").getFile());
		String waarppath = null;
		String gatewayppath = null;
		if (waarpFile.exists() && gatewayFile.exists()) {
			waarppath = waarpFile.toString();
			gatewayppath = gatewayFile.toString();
		}
		String[] configFiles = { gatewayppath, waarppath };
		Properties properties = new Properties();
		try {
			properties.load(getClass().getClassLoader().getResourceAsStream("waarpdb.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String propcondition = properties.getProperty("com.sgs.waarpdb.auto");
		String mycondition1 = new String("create");
		if (propcondition.equals(mycondition1)) {
			String[] gatearray = { gatewayppath, "-initdb" };
			System.out.println("Anchel gateway server, databse is initiating");
			ServerInitDatabase.initGatewayDB(gatearray);
			System.out.println("Anchel gateway server databse is initiated");
		}
		System.out.println("Anchel gateway server is starting");
		ExecGatewayFtpServer.initGatewayServer(configFiles);
		System.out.println("Anchel gateway server is started");
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (driver.getClass().getClassLoader() == cl) {
				try {
					System.out.println("Deregistering JDBC driver {}" + driver);
					DriverManager.deregisterDriver(driver);
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("Error deregistering JDBC driver {}" + driver + e);
				}
			} else {
				System.out.println(
						"Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader" + driver);
			}
		}
		System.out.println("Anchel gateway server terminated");
	}
}
