/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import connection.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;


public class DaoSignableImplementation {

    private Connection con;
    private PreparedStatement stmt;
    // fichero config.properties
    private ResourceBundle configFile;
    private String driverBD;
    private String urlBD;
    private String userBD;
    private String contraBD;
    private ConnectionPool pool ;
    //////sentencia SQL
    private final String singIn = "SELECT User.fullName from User Where User.login = ?  AND User.password = ?";
    private final String singUp = "INSERT INTO User(login,email,fullName,password) values (?,?,?,?)";

    public DaoSignableImplementation() {
        this.configFile = ResourceBundle.getBundle("connection.BdConfig");
        this.driverBD = configFile.getString("driver");
        this.urlBD = configFile.getString("con");
        this.userBD = configFile.getString("DBUSER");
        this.contraBD = configFile.getString("DBPASS");
    }

   

}
