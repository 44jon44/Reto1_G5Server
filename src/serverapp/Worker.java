/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverapp;

import exceptions.ConnectionNotAvailableException;
import exceptions.DatabaseNotAvailableException;
import exceptions.EmailFormatException;
import exceptions.FullNameFormatExeception;
import exceptions.PasswordFormatException;
import exceptions.PasswordNotFoundException;
import exceptions.RepeatPasswordException;
import exceptions.UserFormatException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Message;
import model.Order;
import model.Signable;

/**
 * Esta clase se ocupa de...
 *
 * @author Alex Hurtado
 */
class Worker implements Runnable {

    private static final Logger LOG = Logger.getLogger(Worker.class.getName());
    private Signable dao;
    private Socket clientSocket;
    private Message response;

    public Worker(Signable dao, Socket socket) {
        this.dao = dao;
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try
        {

            ois = new ObjectInputStream(clientSocket.getInputStream());

            oos = new ObjectOutputStream(clientSocket.getOutputStream());

            Message message = (Message) ois.readObject();
            //si el intento de login falla muere el hilo
            //si es correcto el hilo vive hasta que se haga el logout
            //en el main añadir 2 métodos estáticos para incrementar o decrementar el contador de conexiones (métodos sincronizados)
            //si ha ido bien o ha ido mal se cierra la conexión con la diferencia de que el contador no se decrementa si ha ido bien
            if (message.getOrder() == Order.SIGN_IN)
            {
                try
                {
                    response.setUser(dao.signIn(message.getUser()));
                } catch (PasswordNotFoundException ex)
                {
                    LOG.log(Level.SEVERE, "La contraseña no es correcta");
                    response.setOrder(Order.PASSWORD_NOT_FOUND);
                } catch (Exception ex)
                {
                    LOG.log(Level.SEVERE, "Se ha producido un error");
                }
                //si el usuario se ha loggeado bien 
                if(response.getUser().getId() != 0){
                    response.setOrder(Order.OK);
                }

            }
            if (message.getOrder() == Order.SIGN_UP)
            {
                try
                {
                    dao.signUp(message.getUser());
                } catch (ConnectionNotAvailableException ex)
                {
                    LOG.log(Level.SEVERE, "Error, no hay conexiones disponibles");
                    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (DatabaseNotAvailableException ex)
                {
                    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (EmailFormatException ex)
                {
                    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FullNameFormatExeception ex)
                {
                    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (PasswordFormatException ex)
                {
                    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RepeatPasswordException ex)
                {
                    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UserFormatException ex)
                {
                    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex)
                {
                    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //enviamos la respuesta al cliente
            oos.writeObject(response);

        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            if(response.getOrder() == Order.OK){
                ServerApplication.increaseNumberOfConnections();
            }else{
                ServerApplication.decreaseNumberOfConnections();
            }
            try
            {
                if (oos != null)
                {
                    oos.close();
                }
                if (ois != null)
                {
                    ois.close();
                }
                clientSocket.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }
}
