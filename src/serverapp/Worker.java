/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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

    private Signable dao;
    private Socket clientSocket;
    

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

            Message message;
            while ((message = (Message) ois.readObject()) != null)
            {
               if(message.getOrder() == Order.SIGN_IN){
               
               }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            try
            {
                if (oos != null)
                {
                    oos.close();
                }
                if (ois != null)
                {
                    ois.close();
                    clientSocket.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
