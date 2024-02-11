package Logica;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client {
    private int port ;
    private String host ;
    BufferedReader in ;
    BufferedWriter out ;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void launchClient(){
        try (
                Socket clientSocket = new Socket(host, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        ) {
            System.out.println("You've been connected to the server. Welcome!.");
            this.in = in;
            this.out = out;
            new Thread(this::receiveMessage).start();
            sendMessage();
        }catch (SocketException ignored){

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("Session closed.");
        }
    }

    public void sendMessage() throws IOException {
        Scanner sc = new Scanner(System.in);
        String message;
        while (out != null) {
            message = sc.nextLine();
            out.write(message + "\n");
            out.flush();
            if(message.equals("/exit")){
               throw new SocketException(); //to get to the finally block quickly.
            }
        }
    }

    //Here the compiler asks me to handle the exception inside this method, which is the method the thread will run with.
    public void receiveMessage(){
        String inputLine;
            try {
                while ((inputLine = in.readLine()) != null) {

                    System.out.println(inputLine);
                }
            } catch (SocketException ignored){

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("There was an error when receiving the message. Try logging in again.");
            }

    }

    public static void main(String[] args) {
        new Client(60000, "localhost").launchClient();
    }

}

/*
* Pequeño apunte que me pareció interesante para el hilo extra de esta clase para poder leer a la vez que se escribe de forma asíncrona:
* La versión antes de java 8: (La que haría yo), aunque tambien se puede crear una clase nueva para separar las responsabilidades y etc.
* new Thread(new Runnable() {
    @Override
    public void run() {
        readMessages();
    }
}).start();
*
* Con lambda:
* new Thread(() -> this.readMessages()).start();

* con referencia a método:
*        new Thread(this::readMessages).start();
*
* this es la instancia actual de la clase Client
* "::" es el operador de referencia a un metodo de una instancia (this) o de una clase estática (Clase. ....)
* readMessages, el metodo que pasará al hilo. No puede ni devolver ni tomar como argumento nada para poder usar "new Thread()" (el constructor, vaya) con lambda o referencia a metodo.
**/