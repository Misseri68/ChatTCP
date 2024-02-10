package Logica;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Server server;
    private Socket clientSocket;
    private Client cliente;
    private PrintWriter out;
    private BufferedReader in;
    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    /*
    * La idea es que el cliente no pueda conectarse a ningún lado hasta que no se haya autenticado.
    * Habría que pasarle el nombre al Server*/

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public Client getCliente() {
        return cliente;
    }

    public void setCliente(Client cliente) {
        this.cliente = cliente;
    }



    @Override
    public void run() {
        while(true){

        }
    }
}
