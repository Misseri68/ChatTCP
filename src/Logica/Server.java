package Logica;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Server {
    public int port = 60000;
    public final int MAX_CLIENTS = 100;
    public final ChatHandler chatHandler = new ChatHandler(this);

    private Set<ClientHandler> connectedClients = Collections.synchronizedSet(new HashSet<>());

    public Set<ClientHandler> getConnectedClients() {
        return connectedClients;
    }

    public void launchServer(){
        try(

                ServerSocket serverSocket = new ServerSocket(port);
        ) {
            System.out.println("Server initiated. Waiting for clients...");
            while (connectedClients.size() < MAX_CLIENTS) {
                Socket socketCliente = serverSocket.accept();
                new Thread(new ClientHandler(socketCliente, this, chatHandler)).start();
                System.out.println("Client's socket with reference: " + socketCliente + " connected. Waiting authentification...");
            }
        }catch(SocketException e){
            System.out.println("Conexion terminada abruptamente.");
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
        System.out.println("User " + clientHandler.getUser().getName() + " added to the list.");
    }

    public synchronized void removeConnectedClient(ClientHandler clientHandler){
        connectedClients.remove(clientHandler);
    }

    public void printToServer(String message){
        System.out.println(message);
    }

    public static void main(String[] args) {
        new Server().launchServer();
    }
}
