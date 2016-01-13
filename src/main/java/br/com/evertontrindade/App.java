package br.com.evertontrindade;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.evertontrindade.database.DatabaseConnector;
import br.com.evertontrindade.database.DerbyDatabaseConnector;
import br.com.evertontrindade.database.OracleDatabaseConnector;

/**
 * Hello world!
 *
 */
public class App {

	private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) {

        try {
        	
        	File f = new File(App.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        	String directory = f.getParent();
        	logger.info(directory);
        	
        	DatabaseConnector connector = null;
        	
        	String file = "app-oracle.properties";
        	if (args.length > 0) {
        		file = args[0];
        	}
        	
        	if (file.contains("oracle")) {
        		connector = new OracleDatabaseConnector();
        	} else {
        		connector = new DerbyDatabaseConnector();
        	}
        	
        	Properties props = new Properties();
			props.load(new FileInputStream(directory + File.separator + file));
	        connector.connect(props);
	        connector.createTable(props);
	    	connector.createSequence(props);
	        connector.insertData(props);
	        connector.parseBlobs(props);
		    connector.disconnect();
		} catch (IOException | SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

    }
}
