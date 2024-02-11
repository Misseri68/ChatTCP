package Logica;

import CSV.UserCSV;
import DAOs.UserDAO.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Server {
    public int port = 60000;
    public final int MAX_CLIENTS = 100;

    Set<ClientHandler> connectedClients = Collections.synchronizedSet(new HashSet<>());
    ChatHandler chatHandler = new ChatHandler(this);

    public Set<ClientHandler> getConnectedClients() {
        return connectedClients;
    }

    public void launchServer(){
        try(
                ServerSocket serverSocket = new ServerSocket(port);
        ){
            System.out.println("Server initiated. Waiting for clients...");
            while(connectedClients.size()<MAX_CLIENTS){
                Socket socketCliente = serverSocket.accept();
                new Thread(new ClientHandler(socketCliente, this)).start();
                System.out.println("Client's socket with reference: " + socketCliente + " connected. Waiting authentification...");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public synchronized boolean isConnected(String clientName){
        for (ClientHandler clientHandler : connectedClients) {
            String connectedClientName = clientHandler.getUser().getName().toLowerCase();
            if (connectedClientName.equals(clientName.toLowerCase().trim())) return true;
        }
        return false;
    }


    public synchronized void addConnectedClient(ClientHandler clientHandler){
        connectedClients.add(clientHandler);
    }

    public synchronized void removeConnectedClient(ClientHandler clientHandler){
        connectedClients.remove(clientHandler);
    }


    public static void main(String[] args) {
        new Server().launchServer();
    }
}
