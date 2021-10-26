/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * Factoria de la interfaz Signable
 * @author Markel Lopez de uralde
 */
public class DaoSignableFactory {

    /**
     * Recoge una instacia e implementa la interfaz
     * @return interfaz
     */

    public static Signable getSignable() {
        return new DaoSignableImplementation();

    }

}
