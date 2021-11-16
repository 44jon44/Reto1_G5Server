
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
    private ResourceBundle configFile = ResourceBundle.getBundle("model.config");
    private String urlDB = configFile.getString("DB_URL");
    private String userDB = configFile.getString("DB_USER");
    private String passDB = configFile.getString("DB_PASS");
    private Connection conn;
    private static ConnectionPool pool;
    // en este tipo
    private static Stack<Connection> poolStack;

    /**
     * Metodo para hacer la conexion con la base de datos
     *
     * @return Connection La nueva conexión creada
     * @throws DatabaseNotAvailableException
     */
    public Connection makeConnection() throws DatabaseNotAvailableException {
        LOG.info("Crear una conexión");
        try
        {
            //creamos una neva conexión
            conn = DriverManager.getConnection(urlDB, userDB, passDB);
            //devolvemos la conexión creada
            return conn;
        } catch (SQLException e)
        {
            throw new DatabaseNotAvailableException();
        }
    }

    /**
     * Creamos una pila (stack) para almacenar las conexines
     *
     * @throws SQLException lanza una excepcion
     * @throws exceptions.DatabaseNotAvailableException lanza una excepcion
     */
    public static void createStackPool() throws SQLException, DatabaseNotAvailableException {
        //Creamos  un Stack donde se alcenaran las conexiones.
        poolStack = new Stack<>();
    }

    /**
     * Este método controla que solo haya una instancia de la clase ConnectionPool.
     * Para ello utilizamos el patrón de diseño Singleton
     * @return @throws java.sql.SQLException
     * @throws exceptions.DatabaseNotAvailableException
     */
    public static ConnectionPool poolInstance() throws SQLException, DatabaseNotAvailableException {
        LOG.info("Obtener instancia de ConnectionPool");
        if (pool == null)
        {
            pool = new ConnectionPool();
            createStackPool();
            return pool;
        } else
        {
            return pool;
        }
    }

    /**
     * Método que devuelve una conexión desde el pool de conexiones.
     *
     * @return Connection
     * @throws InterruptedException
     * @throws exceptions.DatabaseNotAvailableException
     */
    public Connection getConnection() throws InterruptedException, DatabaseNotAvailableException {
        LOG.info("Obtener una conexión");
        if (poolStack.isEmpty())
        {
            //si la pila no contiene ninguna conexión creamos una
            conn = makeConnection();
            //guardamos la conexión creada en la pila
            poolStack.push(conn);
        }
        //devolvemos una conexión desde la pila
        return poolStack.pop();
    }

    /**
     * Método que libera una conexión cuando se deje de usar por parte del usuario
     *
     * @param con Objeto conexion
     * @throws InterruptedException
     */
    public void releaseConnection(Connection con) throws InterruptedException {
        LOG.info("Liberar una conexión");
        poolStack.push(con);
    }

}
