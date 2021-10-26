/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import connection.ConnectionPool;
import exceptions.ConnectionNotAvaiableException;
import exceptions.DatabaseNotAvaiableException;
import exceptions.EmailFormatException;
import exceptions.FullNameFormatExeception;
import exceptions.LoginExistException;
import exceptions.LoginNotFoundException;
import exceptions.PasswordFormatException;
import exceptions.PasswordNotFoundException;
import exceptions.RepeatPasswordException;
import exceptions.UserFormatException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

public class DaoSignableImplementation implements Signable {

    private Connection con;
    private PreparedStatement stmt;
    
    //Pool para hacer conexion con la base de datos 
    private ConnectionPool pool;
    
    //////sentencia SQL
    private final String singIn = "SELECT * from User Where User.login = ?  AND User.password = ?";
    private final String singUp = "INSERT INTO User(login,email,fullName,status,privilege,password,lastPasswordChange) values (?,?,?,?,?,?,?)";

    @Override
    public User signIn(User user) throws LoginNotFoundException, ConnectionNotAvaiableException, PasswordNotFoundException, Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public User signUp(User user) throws LoginExistException, ConnectionNotAvaiableException, DatabaseNotAvaiableException, EmailFormatException, FullNameFormatExeception, PasswordFormatException, RepeatPasswordException, UserFormatException, Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
