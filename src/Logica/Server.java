package Logica;

import DAOs.UserCSV;
import DAOs.UserDAO.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Server {
    //TODO lista de clientes conectados???
    public int puerto = 60000;
    Set<ClientHandler> connectedClients = Collections.synchronizedSet(new HashSet<>());
    ChatHandler chatHandler = new ChatHandler(this);

    public Set<ClientHandler> getConnectedClients() {
        return connectedClients;
    }


    public void launchServer(){
        try(
                ServerSocket serverSocket = new ServerSocket(puerto);
        ){
        while(true){
            Socket socketCliente = serverSocket.accept();
            new ClientHandler(socketCliente, this).run();
            System.out.println("Nuevo conexion con el socket: " + socketCliente.toString());
        }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void authorizeClient(ClientHandler ch, String username, String pwd){
        //if(User)
    }

    public synchronized void addConnectedClient(ClientHandler clientHandler){
        connectedClients.add(clientHandler);
    }

    public synchronized void removeConnectedClient(ClientHandler clientHandler){
        connectedClients.remove(clientHandler);
    }

    public synchronized boolean isConnected(String clientName){
        for (ClientHandler client : connectedClients) {
            String connectedClientName = client.getCliente().getUser().getName().toLowerCase();
            if (connectedClientName.equals(clientName.toLowerCase().trim())) return true;
        }
        return false;
    }
}
