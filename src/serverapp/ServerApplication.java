/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverapp;


import exceptions.DatabaseNotAvailableException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex Hurtado, Ibai Arriola 
 */
public class ServerApplication {

    private static final int SERVER_PORT = Integer.valueOf(ResourceBundle.getBundle("model.config").getString("SERVER_PORT"));
    private static final Logger LOG = Logger.getLogger(ServerApplication.class.getName());
    public static final int SERVER_MAX_CONNECTIONS = Integer.valueOf(ResourceBundle.getBundle("model.config").getString("SERVER_MAX_CONN"));
    public static int currentConnections = 0;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOG.info("Entrando a metodo main de el ServerApplication");
        //el hilo seguirá trabajando mientras el usuario no haya cerrado sesión
        //si el usuario cierra sesión el hilo trabajador muere y se libera la conexión que utiliza
      
        
        /**
         *  esta clase anonima se  encarga de que se pueda cerrar el servidor de 
         * forma manunal escribiendo exit 
         */
        Thread consola = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scr = new Scanner(System.in);
                System.out.println("Running.....");
                while (true) {
                    System.out.print(">");
                    if (scr.nextLine().equalsIgnoreCase("EXIT")) {
                        System.out.println("CIERRE MANUAL DEL SERVIDOR");
                        System.exit(0); 
                    }
                }
            }
        });

        try {
           ServerSocket server = null;
           //controlamos que se pueda cerrar el servidor
           consola.start();
            //el servidor escucha en el puerto 5000
            server = new ServerSocket(SERVER_PORT);
            //esta en un bucle eterno eschuchando y creando hilos
            while (true) {
                Socket socket = server.accept();
                increaseNumberOfConnections();
                if (currentConnections <= SERVER_MAX_CONNECTIONS) {
                    Worker worker = new Worker(socket);
                    worker.start();
                }else{
                    Worker worker = new Worker(socket);
                    worker.start();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DatabaseNotAvailableException ex) {
            Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized static void increaseNumberOfConnections() {
        ServerApplication.currentConnections += 1;
    }

    public synchronized static void decreaseNumberOfConnections() {
        ServerApplication.currentConnections -= 1;
    }
}
