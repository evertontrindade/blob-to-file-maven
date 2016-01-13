package br.com.evertontrindade.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Created by everton on 07/01/16.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OracleDatabaseConnectorTest {

    private static Properties props = null;
    private static DatabaseConnector connector = null;

    @BeforeClass
    public static void setUp() throws IOException {
        props = new Properties();
        props.load(OracleDatabaseConnectorTest.class.getResourceAsStream("/app-oracle.properties"));
        connector = new OracleDatabaseConnector();
    }

    @Test
    public void step1_testConnect() throws SQLException, ClassNotFoundException {
        connector.connect(props);
    }

    @Test
    public void step2_testCreateTable() throws SQLException {
        connector.createTable(props);
    }

    @Test
    public void step3_testCreateSequence() throws SQLException {
        connector.createSequence(props);
    }

    @Test
    public void step4_testInsertData() throws SQLException, IOException {
        connector.insertData(props);
    }

    @Test
    public void step5_testGetBlobs() throws SQLException, IOException {
        connector.parseBlobs(props);
    }
    
    @AfterClass
    public static void tearDown() throws SQLException {
    	connector.disconnect();
    }
}
