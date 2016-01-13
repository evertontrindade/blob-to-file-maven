package br.com.evertontrindade.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by everton on 07/01/16.
 */
public interface DatabaseConnector {

    void connect(Properties props) throws SQLException, ClassNotFoundException;
    void createTable(Properties props) throws SQLException;
    void createSequence(Properties props) throws SQLException;
    void insertData(Properties props) throws SQLException, IOException;
    void parseBlobs(Properties props) throws SQLException, IOException;
    void disconnect() throws SQLException;

}
