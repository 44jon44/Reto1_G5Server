/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import exceptions.DatabaseNotAvailableException;
import java.sql.SQLException;

/**
 * factoria que accede a la DaoSignableImplementation
 * @author alex y markel
 * 
 */
public class DaoSignableFactory {
       public static DaoSignableImplementation getDaoSignableImplementation() throws SQLException, DatabaseNotAvailableException{
        return new DaoSignableImplementation();
    }
}
