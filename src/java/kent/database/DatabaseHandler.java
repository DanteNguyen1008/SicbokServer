/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kent
 */
public class DatabaseHandler {

    /**
     * Singleton implementation
     */
    private static DatabaseHandler INSTANCE = null;

    private DatabaseHandler() {
    }

    public static DatabaseHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseHandler();
        }
        return INSTANCE;
    }
    private Connection connection = null;//The database connection.
    private String strError;
    private CallableStatement proc;
    /*
     private String strHostURL = "mysql-sicbogame.jelastic.servint.net";
     private String strUsername = "kibowvn";
     private String strPassword = "nJUGP3yULEqmVJeQ";
     private String strDatabaseName = "kb_sicbok";
     */
    private String strHostURL = "localhost:3306";
    private String strUsername = "root";
    private String strPassword = "";
    private String strDatabaseName = "kb_sicbok";

    //<editor-fold defaultstate="collapsed" desc="Connect Mysql">
    /**
     *
     * @param strServerName
     * @param strUser
     * @param strPass
     * @param strDatabase
     * @return
     * @throws SQLException
     */
    public Connection connectMySql() throws SQLException {

        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(
                    "jdbc:mysql://" + strHostURL + "/" + strDatabaseName,
                    strUsername,
                    strPassword);
            System.out.println("Connected");

        } catch (ClassNotFoundException e) {
            this.strError = "Error:" + e.getMessage();
        } catch (InstantiationException e) {
            this.strError = "Error:" + e.getMessage();
        } catch (IllegalAccessException e) {
            this.strError = "Error:" + e.getMessage();
        }
        return con;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Execute SQL statement">
    /**
     * Execute SQL statement
     *
     * @param strProcedureName
     * @param parameterNames
     * @param parameterValues
     * @return
     */
    public int executeSQL(
            String strProcedureName,
            String[] parameterNames,
            Object[] parameterValues) throws SQLException {
        int j = 0;


        try {
            if (this.connection == null || this.connection.isClosed()) {
                this.connection = this.connectMySql();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            String parameterCount = "";
            for (int i = 0; i < parameterNames.length; i++) {
                if (i == (parameterNames.length - 1)) {
                    parameterCount += "?";
                } else {
                    parameterCount += "?,";
                }
            }

            this.proc = this.connection.prepareCall("{call " + strProcedureName + " (" + parameterCount + ")}");
            for (int i = 0; i < parameterNames.length; i++) {
                this.proc.setObject(i + 1, parameterValues[i]);
            }
            j = this.proc.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return j;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Execute SQL query. Return ResultSet">
    /**
     *
     * @param procedureName
     * @param parameterNames
     * @param parameterValues
     * @return
     */
    public ResultSet executeQuery(
            String procedureName,
            String[] parameterNames,
            Object[] parameterValues) {

        try {
            System.out.println("Sql query");
            if (this.connection == null || this.connection.isClosed()) {
                this.connection = connectMySql();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        ResultSet resultSet = null;
        try {
            String parameterCount = "";
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    if (i == (parameterNames.length - 1)) {
                        parameterCount += "?";
                    } else {
                        parameterCount += "?,";
                    }
                }
            }

            System.out.println(procedureName + " - " + parameterCount);
            this.proc = this.connection.prepareCall("{call " + procedureName + " (" + parameterCount + ")}");
            //Ex: this.proc = connection.prepareCall("{call SELECT_LOGIN('a1provip002@mail.com','123456')}");
            if (parameterValues != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    this.proc.setObject(i + 1, parameterValues[i]);
                }
            }
            resultSet = this.proc.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return resultSet;
    }
    //</editor-fold>

    public int executeSQLAndGetId(
            String strTableName,
            String strTableId,
            String strProcedureName,
            String[] parameterNames,
            Object[] parameterValues) throws SQLException {
        int j = 0;
        int insertedId = 0;

        try {
            if (this.connection == null || this.connection.isClosed()) {
                this.connection = this.connectMySql();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            String parameterCount = "";
            for (int i = 0; i < parameterNames.length; i++) {
                if (i == (parameterNames.length - 1)) {
                    parameterCount += "?";
                } else {
                    parameterCount += "?,";
                }
            }

            this.proc = this.connection.prepareCall("{call " + strProcedureName + " (" + parameterCount + ")}");
            for (int i = 0; i < parameterNames.length; i++) {
                this.proc.setObject(i + 1, parameterValues[i]);
            }
            j = this.proc.executeUpdate();

            // Select last inserted Id
            ResultSet rs2 = this.proc.executeQuery(
                    "SELECT * FROM " + strTableName
                    + " ORDER BY " + strTableId + " DESC LIMIT 1");
            while (rs2.next()) {
                insertedId = rs2.getInt(1);
            }


        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return insertedId;
    }
}