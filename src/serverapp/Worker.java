/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverapp;

import exceptions.ConnectionNotAvailableException;
import exceptions.DatabaseNotAvailableException;
import exceptions.LoginExistException;
import exceptions.LoginNotFoundException;
import exceptions.PasswordNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.DaoSignableFactory;
import model.Message;
import model.Order;
import model.Signable;
import model.User;

/**
 * Esta clase se ocupa de...
 *
 * @author Alex Hurtado
 */
class Worker extends Thread {

    private static final Logger LOG = Logger.getLogger(Worker.class.getName());
    private Socket clientSocket;
    private Message response;
    private Signable dao;

    Worker(Socket clientSocket) throws SQLException, DatabaseNotAvailableException {
        this.dao = DaoSignableFactory.getDaoSignableImplementation();
        this.clientSocket = clientSocket;
    }


    @Override
    public void run() {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {
            sleep(1);
            ois = new ObjectInputStream(clientSocket.getInputStream());
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            Message message = (Message) ois.readObject();
            //si el intento de login falla muere el hilo
            //si es correcto el hilo vive hasta que se haga el logout
            //en el main añadir 2 métodos estáticos para incrementar o decrementar el contador de conexiones (métodos sincronizados)
            //si ha ido bien o ha ido mal se cierra la conexión con la diferencia de que el contador no se decrementa si ha ido bien
            if(ServerApplication.currentConnections <= ServerApplication.SERVER_MAX_CONNECTIONS){
                if (message.getOrder() == Order.SIGN_IN) {
                try {
                    User clientUser = message.getUser();
                    User responseUser = dao.signIn(clientUser);
                    response = new Message(responseUser, Order.OK);
                    oos.writeObject(response);
                } catch (PasswordNotFoundException ex) {
                    LOG.log(Level.SEVERE, "La contraseña no es correcta");
                    response = new Message(null, Order.PASSWORD_NOT_FOUND);
                    oos.writeObject(response);
                } catch (LoginNotFoundException e) {
                    LOG.log(Level.SEVERE, "El usuario no es correcto");
                    response = new Message(null, Order.LOGIN_NOT_FOUND);
                    oos.writeObject(response);
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Se ha producido un error");
                }
            }
            if (message.getOrder() == Order.SIGN_UP)
                {
                    User clientUser = message.getUser();
                    try
                    {
                        boolean signUpSucces = dao.signUp(clientUser);
                        response = new Message(null, Order.OK);
                        oos.writeObject(response);
                    } catch (LoginExistException ex)
                    {
                        response = new Message(null, Order.LOGIN_EXIST);
                        oos.writeObject(response);
                        LOG.log(Level.SEVERE, ex.getMessage());
                    } catch (ConnectionNotAvailableException ex)
                    {
                        response = new Message(null, Order.CONNECTION_NOT_AVAILABLE);
                        oos.writeObject(response);
                        LOG.log(Level.SEVERE, ex.getMessage());
                    } catch (Exception ex)
                    {
                        LOG.log(Level.SEVERE, "Se ha producido un error");
                    }
                }
            }else{
                response = new Message(null, Order.CONNECTION_NOT_AVAILABLE);
                oos.writeObject(response);
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } catch (ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (ois != null) {
                    ois.close();
                }
                ServerApplication.decreaseNumberOfConnections();
                clientSocket.close();
                this.interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
