/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import connection.ConnectionPool;
import exceptions.DatabaseNotAvailableException;
import exceptions.LoginExistException;
import exceptions.LoginNotFoundException;
import exceptions.PasswordNotFoundException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
/**
 * Clase DAO que implementa la interfaz Signable
 * @author Alex Hurtado 
 */
public class DaoSignableImplementation implements Signable {

    private static final Logger LOG = Logger.getLogger(DaoSignableImplementation.class.getName()); 
    private  ConnectionPool pool;
    private Connection con;
    private PreparedStatement stmt;
    private CallableStatement callStmt;
    private ResultSet rs;
    // Sentencias SQL
    private final String SIGN_IN = "SELECT id, login, email, fullname, password FROM user WHERE login = ?";
    private final String TEN_SIGNIN_CHECK = "{call ten_signin_check(?)}";  
    private final String SIGN_UP = "INSERT INTO user(login,email,fullname,password) VALUES (?,?,?,?)";

    //Constructor
    public DaoSignableImplementation() throws SQLException, DatabaseNotAvailableException {
        this.pool = ConnectionPool.poolInstance();
    }

    /**
     * Este método se ocupa de validar los campos "login" y "password" de un usuario para que pueda iniciar sesión en la aplicación
     * @param user El usuario que recibe desde el cliente que solo tiene los atributos de "login" y "password"
     * @return User El usuario que tiene los atributos "id", "login", "fullname", "email" y "password"
     * @throws SQLException 
     * @throws exceptions.LoginNotFoundException 
     * @throws exceptions.PasswordNotFoundException 
     * @throws java.lang.InterruptedException 
     * @throws exceptions.DatabaseNotAvailableException 
     */
    @Override
    public User signIn(User user) throws SQLException, LoginNotFoundException, PasswordNotFoundException, InterruptedException, DatabaseNotAvailableException {
        User login = new User();
        con = pool.getConnection();
        stmt = con.prepareStatement(SIGN_IN);
        stmt.setString(1, user.getLogin());
        rs = stmt.executeQuery();
        if (rs.next()) {
            //comprobamos si la contraseña introducida es igual a la almacenada en la base de datos
            if (rs.getString("password").equals(user.getPassword())) {
                login.setId(rs.getInt("id"));
                login.setLogin(rs.getString("login"));
                login.setFullName(rs.getString("fullname"));
                login.setEmail(rs.getString("email"));
                login.setPassword(rs.getString("password"));
                //como la contraseña es válida llamamos al procedimiento almacenado que se ocupa
                //de gestionar el histórico de los 10 últimos login
                callStmt = con.prepareCall(TEN_SIGNIN_CHECK);
                callStmt.setInt(1, login.getId());
                callStmt.executeQuery();
            } else {//si la contaseña no es correcta lanzamos la excepción  PasswordNotFoundException
                throw new PasswordNotFoundException();
            }
        } else {//si el usuario no existe lanzamos la exepción LoginNotFoundException
            throw new LoginNotFoundException();
        }
        //devolvemos la conexión al pool
        pool.releaseConnection(con);
        return login;
    }
    
    /**
     * Metodo que se ocupa de dar de alta un nuevo usuario en la base de datos de la aplicacion
     * @param user Objeto User que consta de los campos "id", "login", "fullname", "email" y "password"
     * @return Un booleano que indica si se ha dado de alta de forma correcta el usuario
     * @throws SQLException
     * @throws LoginExistException 
     * @throws java.lang.InterruptedException 
     */
    @Override
    public  boolean  signUp(User user) throws SQLException, LoginExistException, InterruptedException, DatabaseNotAvailableException {
        int idGenerated = -1;
        ResultSet rsId;
        con = pool.getConnection();
        //comprobamos si el usuario ya se ha dado de alta
        stmt = con.prepareStatement(SIGN_IN);
        stmt.setString(1, user.getLogin());
        rs = stmt.executeQuery();
        if (rs.next()) {
            throw new LoginExistException();
        } else {
            stmt = con.prepareStatement(SIGN_UP, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getPassword());
            stmt.executeUpdate();
            rsId = stmt.getGeneratedKeys();
        }
        if(rsId != null && rsId.next()){
            idGenerated = rsId.getInt(1);
            if(idGenerated != -1){
                return true;
            }
        }
        //liberamos la conexión
        pool.releaseConnection(con);
        return false;
    }
}

