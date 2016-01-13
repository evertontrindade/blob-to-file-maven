package br.com.evertontrindade.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by everton on 07/01/16.
 */
public class DerbyDatabaseConnector implements DatabaseConnector {

	private Logger logger = null;
    private Connection conn = null;

	public DerbyDatabaseConnector() {
		this.logger = LoggerFactory.getLogger(getClass());
		logger.info("[DerbyDatabaseConnector] init");
	}

	public void connect(final Properties props) throws SQLException, ClassNotFoundException {
    	logger.info("[connect]");
    	Class.forName(props.getProperty("driver"));
    	conn =  DriverManager.getConnection(
    			props.getProperty("url"), 
    			props.getProperty("user"), 
    			props.getProperty("password")
    	);
        conn.setAutoCommit(false);
    	logger.info("[connect] finish");
    }

    public void createTable(Properties props) throws SQLException {
    	logger.info("[createTable]");
    	if (!isNull(props.getProperty("create.table.sql"))) {
        	logger.info("[createTable] creating...");
    		Statement st = conn.createStatement();
    		st.execute(props.getProperty("create.table.sql"));
    		conn.commit();
    	}
    	logger.info("[createTable] finish");
    }

	public void createSequence(Properties props) throws SQLException {
		logger.info("[createSequence]");
    	if (!isNull(props.getProperty("create.sequence.sql"))) {
        	logger.info("[createSequence] creating...");
    		Statement st = conn.createStatement();
    		st.execute(props.getProperty("create.sequence.sql"));
    		conn.commit();
    	}
    	logger.info("[createSequence] finish");
	}

	public void insertData(Properties props) throws SQLException {
		logger.info("[insertData]");
    	if (!isNull(props.getProperty("insert.sql"))) {
        	logger.info("[insertData] executing...");
    		PreparedStatement st = conn.prepareStatement(props.getProperty("insert.sql"));
    		logger.info("[insertData] identification: " + props.getProperty("identification.value"));
    		st.setString(1, props.getProperty("identification.value"));
    		st.setBinaryStream(2, getClass().getResourceAsStream(props.getProperty("blob.value")));
    		st.execute();
    		conn.commit();
    	}
		logger.info("[insertData] finish");
    }

	public void parseBlobs(Properties props) throws SQLException, IOException {
		logger.info("[parseBlobs]");
        PreparedStatement st = conn.prepareStatement(props.getProperty("select.sql"));
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
        	Blob blob = rs.getBlob(props.getProperty("blob.column"));
            File file = new File(props.getProperty("images.dir")+ File.separator + rs.getString(props.getProperty("identification.column")) + props.getProperty("image.extension"));
            FileOutputStream fos = new FileOutputStream(file);
            IOUtils.copy(blob.getBinaryStream(), fos);
    		logger.info("[parseBlobs] CRM: " + rs.getString(props.getProperty("identification.column")) + "... OK");
        }
        conn.commit();
        rs.close();
        st.close();
		logger.info("[parseBlobs] finish");
	}

	@Override
	public void disconnect() throws SQLException {
		logger.info("[disconnect]");
		conn.close();
	}

	private boolean isNull(String value) {
		return value == null || "".equals(value);
	}
}
