package dev.andrepontde.retailmanager.retail_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the Retail Management System.
 * 
 * @author Andre Pont
 * @since 1.0
 */
@SpringBootApplication
@EnableTransactionManagement
public class RetailSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetailSystemApplication.class, args);
	}

}
