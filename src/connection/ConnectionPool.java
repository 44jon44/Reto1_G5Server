
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import exceptions.DatabaseNotAvaiableException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * @author Ibai,jon,markel,alex
 */
public class ConnectionPool {
    private static final Logger LOG = Logger.getLogger(ConnectionPool.class.getName());
    // fichero config.properties
    private ResourceBundle configFile;
    private String driverBD;
    private String urlBD;
    private String userBD;
    private String contraBD;
    /////
    private ConnectionPool pool;
    private Connection con;
    private PreparedStatement stmt;
    // en este tipo
    private Stack<Connection> poolStack;

    /**
     * Metodo para hacer la conexion con la base de datos
     * @throws exceptions.DatabaseNotAvaiableException
     */
    public void makeConnection() throws DatabaseNotAvaiableException {
        this.configFile = ResourceBundle.getBundle("connection.BdConfig");
        this.driverBD = configFile.getString("driver");
        this.urlBD = configFile.getString("con");
        this.userBD = configFile.getString("DBUSER");
        this.contraBD = configFile.getString("DBPASS");
        try {
            Connection conn = null;
            conn = DriverManager.getConnection(urlBD, userBD, contraBD);
        } catch (SQLException e) {
            throw new DatabaseNotAvaiableException();
        }
    }
    /**
     * creamos la pila para almacenar las conexines
     *
     * @throws SQLException
     * @throws exceptions.DatabaseNotAvaiableException
     */
    public void createStackPool() throws SQLException, DatabaseNotAvaiableException {
        //Creamos  un Stack donde se alcenaran las conexiones.
        poolStack = new Stack<>();
        this.makeConnection();
    }

    /**
     * Se obtendra una conexion por parte del pool del Stack
     *
     * @return retornamos una conexion que se ha añadido
     * @throws InterruptedException
     */
    public Connection getConnection() throws InterruptedException {
        Connection conn = null;
        if (poolStack.size() > 0) {
            conn = poolStack.pop();
        }
        return conn;
    }

    /**
     * liberar una conexion cuando se deje de usar por parte del usuario
     *
     * @param con Objeto conexion
     * @throws InterruptedException
     */
    public void releaseConnection(Connection con) throws InterruptedException {
        LOG.info("liberar una conexion ");
        poolStack.push(con);
    }
}
