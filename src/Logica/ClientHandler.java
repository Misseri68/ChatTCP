package Logica;

import CSV.UserCSV;
import DAOs.UserDAO.User;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable{
    private Server server;
    private Socket socket;
    private User user;
    private BufferedReader in;
    private BufferedWriter out;


    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        initializeIO();
    }

    public void initializeIO(){
        if(socket!=null){
            try{
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    /* If user is null it means it hasn't logged in yet.
    Once logged in, if chatRoom is null, it means it hasn't joined a room yet and the messages wont be redirected anywhere.*/

    @Override
    public void run() {
        String inputLine;
        try {
            sendToClient("Please use '/login (username) (password)' to log into your account, " +
                    "or use '/register (username) (password) (repeat password)' to use the chat.");
            while ((inputLine = in.readLine()) != null) {
                processInput(inputLine);
            }
        }catch (SocketException e){
            closeConnection();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            closeConnection();
        }
    }

    public void sendToClient(String message) throws IOException {
        if(out != null){
            out.write(message + "\n");
            out.flush();
        }
        else System.err.println("Cannot send message to a null OutputStream.");
    }
    public void processInput(String line) throws IOException {
        //Aquí miré que para mayor rendimiento, es mejor usar un .charAt(0) después de que comprobar que el string no está vacío, ya que no crea un objeto String nuevo y etc, pero
        //como mi aplicación no está pensada para soportar millones de mensajes, he preferido usar .startsWith() por mayor simplicidad y legibilidad.
        if(line.startsWith("/")){
            if(line.startsWith("/exit")) closeConnection();
        //If the user is null, it means the client hasn't logged in, and doesn't have any access to the chatRooms.
            else if(user == null){
                if(line.startsWith("/login")) login(line);
                else if(line.startsWith("/register")) register(line);
                else sendToClient("You need to login before you can type!");
            }
            else if(line.startsWith("/changePassword")) changePassword(line);
            else if (line.startsWith("/changeUsername")) changeUsername(line);
        //If the chatRoom is null, the user doesn't have access to chat yet. It must be in a chatRoom to speak.
            else if (user.getCurrentChatRoom() == null){
                if(line.startsWith("/joinRoom")) joinRoom(line);
                else if (line.startsWith("/createRoom")) createRoom(line);
                else sendToClient("You must join a chatRoom to speak!");
            }
            else if (line.startsWith("/changeRoomName")) changeRoomName();
            else if (line.startsWith("/changeRoomCode")) changeRoomCode();
            else if (line.startsWith("/leaveRoom")) leaveRoom();
        }

    }

    public synchronized void login(String line) throws IOException {
        String[] splitLine = line.split(" ", 3);
        if(splitLine.length>= 3){
            String name = splitLine[1];
            String pwd = splitLine[2];
            if(server.isConnected(name)) sendToClient("The user " + name + " is already logged in.");
            else {
                sendToClient(UserCSV.authenticate( name, pwd)? "Logged in successfully." :
                        (UserCSV.userExists(name)) ? "Incorrect password." : "The user " + name + "doesn't exist. Type /register 'name' 'password' 'repeat password' to create a user.");
                server.addConnectedClient(this);
                setUser(new User(name));
                sendToClient("To speak, you must join a chatRoom using /joinRoom 'chatRoom name' 'chatRoom code (blank by default)' -  or create one using /createRoom 'name' 'code (can be left blank)'");
            }
        }
        else sendToClient("There should be three arguments after the command.");
    }
    public synchronized void register(String line) throws IOException {
        String[] splitLine = line.split(" ", 4);
        if (splitLine.length >=4){
            String name = splitLine[1];
            String pwd = splitLine[2];
            String repeatPwd = splitLine[3];
            if(pwd.equals(repeatPwd)){
                sendToClient(UserCSV.createUser(name,pwd)? "User created successfully. Now use /login 'name' 'password' - to log in and chat." :
                        (UserCSV.userExists(name))? "The user " + name + " already exists. Please use another username." : "Couldn't create the user. Please contact an admin.");
            }
            else sendToClient("Your passwords don't match. Try again.");
        }
        else sendToClient("There should be three arguments after the command.");

    }
    public synchronized void changeUsername(String line) throws IOException {
        String[] splitLine = line.split(" ", 4);
        if(splitLine.length >= 4){
            String actualName = splitLine[1];
            String newName = splitLine[2];
            String pwd = splitLine[3];
            sendToClient(UserCSV.userExists(newName) ? "That username is already picked." :
                    (UserCSV.changeUsername(actualName, newName, pwd) ? "Username changed successfully" : "Couldn't change the username. Contact an admin."));
        }
        else sendToClient("There should be three arguments after the command.");
    }
    public synchronized void changePassword(String line) throws IOException {
        String[] splitLine = line.split(" ", 4);
        if(splitLine.length >= 4){
            String name = splitLine[1];
            String currPwd = splitLine[2];
            String newPwd = splitLine[3];
            sendToClient(UserCSV.changePwd(name, currPwd, newPwd) ? "Password changed succesfully" : "Couldn't change password.");
        }
    }

    /*I didn't consider necessary the use of synchronized in methods that only change the chatRoom instance of the user, or the name/code of that chatRoom. The thread
    that edited it last will be the one that prevail.*/
    public void joinRoom(String line){

    }
    public synchronized void createRoom(String line){}
    public void changeRoomName(){}
    public void changeRoomCode(){}
    public void leaveRoom(){}



    public void closeConnection(){
            try {
                server.removeConnectedClient(this);
                if(in!=null) in.close();
                if(out!=null) out.close();
                if(socket!=null) socket.close();
                this.setUser(null);
                //TODO: cerrar de chatrooms tambien
            } catch (IOException e) {
                System.out.println("Error closing connection");

        }
    }
}
