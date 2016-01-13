package br.com.evertontrindade.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oracle.jdbc.driver.OracleResultSet;
import oracle.jdbc.pool.OracleDataSource;
import oracle.sql.BLOB;

/**
 * Created by everton on 07/01/16.
 */
public class OracleDatabaseConnector implements DatabaseConnector {

	private Logger logger = null;
    private OracleDataSource ds = null;
    private Connection conn = null;
    
    public OracleDatabaseConnector() {
		this.logger = LoggerFactory.getLogger(getClass());
		logger.info("[OracleDatabaseConnector] init");
	}

    public void connect(final Properties props) throws SQLException, ClassNotFoundException {
    	ds = new OracleDataSource();
    	ds.setURL(props.getProperty("url"));
    	conn = ds.getConnection(props.getProperty("user"),props.getProperty("password")); 
    	
//    	Class.forName(props.getProperty("driver"));
//    	conn =  DriverManager.getConnection(
//    			props.getProperty("url"), 
//    			props.getProperty("user"), 
//    			props.getProperty("password")
//    	);
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

	public void insertData(Properties props) throws SQLException, IOException {
		logger.info("[insertData]");
    	if (!isNull(props.getProperty("insert.sql"))) {
        	logger.info("[insertData] executing...");
    		Statement st = conn.createStatement();
    		st.execute(props.getProperty("insert.sql"));
    		logger.info("[insertData] identification: " + props.getProperty("identitication.value"));
    		
    		ResultSet rs = st.executeQuery(props.getProperty("select.for.update.sql"));
    		if (rs.next()) {
    			BLOB blob = ((OracleResultSet)rs).getBLOB(3);
    			InputStream in = getClass().getResourceAsStream(props.getProperty("blob.value"));
    			OutputStream out = blob.setBinaryStream(1L);
    			int size = blob.getBufferSize();
    			byte[] buffer = new byte[size];
    			int length = -1;
    			while ((length = in.read(buffer)) != -1) {
    				out.write(buffer, 0, length);
    			}
				in.close();
				out.close();
    		}
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
