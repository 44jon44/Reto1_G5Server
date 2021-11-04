/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Signable;

/**
 *
 * @author 2dam
 */
public class ServerApplication {

    private static final int SERVER_PORT = 5000;
    private static final Logger LOG = Logger.getLogger(ServerApplication.class.getName());
    private static final int MAX_SERVER_CONNECTIONS = Integer.valueOf(ResourceBundle.getBundle("serverapp.serverConfig").getString("MAX_CONN"));
    private static int currentConnections = 0;
    private static Signable dao;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOG.info("Entrando a metodo main de el ServerApplication");
        //el hilo seguirá trabajando mientras el usuario no haya cerrado sesión
        //si el usuario cierra sesión el hilo trabajador muere y se libera la conexión que utiliza
        try
        {
            ServerSocket server = null;
            //el servidor escucha en el puerto 5000
            server = new ServerSocket(SERVER_PORT);
            while (true)
            {
                Socket socket = server.accept();
                if (currentConnections < MAX_SERVER_CONNECTIONS)
                {
                    //currentConnections ++;
                    Worker worker = new Worker(dao, socket);
                    worker.run();
                }

            }
        } catch (IOException ex)
        {
            Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void increaseNumberOfConnections(){
        ServerApplication.currentConnections += 1;
    }
    
    public static void decreaseNumberOfConnections(){
        ServerApplication.currentConnections -= 1;
    }
}
