
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import exceptions.DatabaseNotAvailableException;
import java.sql.Connection;
import java.sql.DriverManager;
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
    private String urlDB;
    private String userDB;
    private String passDB;

    private static ConnectionPool pool;
    // en este tipo
    private Stack<Connection> poolStack;

    /**
     * Metodo para hacer la conexion con la base de datos
     *
     * @throws exceptions.DatabaseNotAvailableException
     */
    public void makeConnection() throws DatabaseNotAvailableException {
        this.configFile = ResourceBundle.getBundle("model.config");
        this.driverBD = configFile.getString("DRIVER");
        this.urlDB = configFile.getString("DB_URL");
        this.userDB = configFile.getString("DB_USER");
        this.passDB = configFile.getString("DB_PASS");
        try {
            Connection conn = DriverManager.getConnection(urlDB, userDB, passDB);
            //push añadir una conexion a la pila 
            poolStack.push(conn);
        } catch (SQLException e) {
            throw new DatabaseNotAvailableException();
        }
    }
    /**
     * creamos la pila para almacenar las conexines
     *
     * @throws SQLException lanza una excepcion 
     * @throws exceptions.DatabaseNotAvailableException lanza una excepcion 
     */
    public void createStackPool() throws SQLException, DatabaseNotAvailableException {
        //Creamos  un Stack donde se alcenaran las conexiones.
        poolStack = new Stack<>();
        this.makeConnection();
    }
/**
 * 
 * @return controla que solo haya un pool  ya que es una clase Singletoon
 */
    public static ConnectionPool poolInstance(){
        if (pool == null) {
            pool = new ConnectionPool();
            return pool;
        } else {
           return pool;
        }
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
