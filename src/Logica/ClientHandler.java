package Logica;

import CSV.UserCSV;
import DAOs.UserDAO.User;
import Excepciones.RoomException;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable{
    private final Server server;
    private final ChatHandler chatHandler;
    private final Socket socket;
    private User user;
    private BufferedReader in;
    private BufferedWriter out;
    private boolean running = true;


    public ClientHandler(Socket socket, Server server, ChatHandler chatHandler) {
        this.socket = socket;
        this.server = server;
        this.chatHandler = chatHandler;
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
            sendToClient("You can also use /help to check the available commands.");
            while (running && (inputLine = in.readLine()) != null) {
                processInput(inputLine);
            }
        }catch (SocketException e){
            System.out.println("Connection to client was cut abruptly. All remaining connections will be clsoed..");
            closeConnection();
        }
        catch(IOException e){
            e.printStackTrace();
            closeConnection();
        }
    }

    public void sendToClient(String message){
        try{
            if(out != null){
                out.write(message + "\n");
                out.flush();
            }
            else System.out.println("Cannot send message to a null OutputStream.");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void processInput(String line) throws IOException {
            if(line.startsWith("/exit")) closeConnection();
            else if(line.startsWith("/help")) help();
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
                else if (line.startsWith("/")) sendToClient("Incorrect command.");
                else sendToClient("You must join a chatRoom to speak!");
            }
            else if (line.startsWith("/changeRoomName")) changeRoomName(line);
            else if (line.startsWith("/changeRoomCode")) changeRoomCode(line);
            else if (line.startsWith("/leaveRoom")) leaveRoom();
            else if (line.startsWith("/")) sendToClient("Incorrect command.");
            else sendToChat(line);
    }

    public void help(){
        sendToClient("Regarding your user:");
        sendToClient("\t /exit to close your session");
        sendToClient("\t /register 'name' 'password' 'repeatPassword'");
        sendToClient("\t /login 'name' 'password'");
        sendToClient("\t /changePassword 'name' 'password' 'newPassword'");
        sendToClient("\t /changeUsername 'name' 'newName' 'password' ");
        sendToClient("Regarding rooms:");
        sendToClient("\t /joinRoom 'roomName' 'code (can be none)'");
        sendToClient("\t /createRoom 'roomName' 'code (can be left blank)'");
        sendToClient("\t /changeRoomName 'newName' (only if you're inside the room)");
        sendToClient("\t /changeRoomCode 'newCode' (only if you're inside the room)");
        sendToClient("\t /leaveRoom");
    }

    public synchronized void login(String line) throws IOException {
        String[] splitLine = line.split(" ", 3);
        if(splitLine.length>= 3){
            String name = splitLine[1].trim();
            String pwd = splitLine[2].trim();
            if(server.isConnected(name)) sendToClient("The user " + name + " is already logged in.");
            else {
                boolean auth = UserCSV.authenticate(name, pwd);
                if (auth ){
                    sendToClient("Logged in successfully.");
                    setUser(new User(name));
                    server.addConnectedClient(this);
                    sendToClient("To speak, you must join a chatRoom using /joinRoom 'chatRoom name' 'chatRoom code (blank by default)' -  or create one using /createRoom 'name' 'code (can be left blank)'");
                }
                else sendToClient(UserCSV.userExists(name) ? "Incorrect password." : "The user '" + name + "' doesn't exist. Type /register 'name' 'password' 'repeat password' to create a user.");
            }
        }
        else sendToClient("There should be two arguments after the command.");
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
            boolean exists = UserCSV.userExists(newName);
            boolean changed = UserCSV.changeUsername(actualName, newName, pwd);
            sendToClient(exists ? "That username is already picked." :
                    (changed ? "Username changed successfully" : "Couldn't change the username."));
            if(changed){
                user.setName(newName);
            }
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
        else sendToClient("You need at least 3 arguments.");
    }

    /*I didn't consider necessary the use of synchronized in methods that only change the chatRoom instance of the user, or the name/code of that chatRoom. The thread
    that edited it last will be the one that prevail.*/
    public void joinRoom(String line){
        String[] splitLine = line.split(" ", 3);
        if(splitLine.length >= 2) {
            String roomName = splitLine[1];
            String roomCode = "";
            if(splitLine.length>=3){
                roomCode = splitLine[2];
            }
            chatHandler.joinRoom(this, roomName.trim(), roomCode.trim());
        }
    }
    public synchronized void createRoom(String line) {
        String[] splitLine = line.split(" ", 3);
        if (splitLine.length >= 2) {
            String roomName = splitLine[1];
            String roomCode = "";
            if(splitLine.length>= 3){
                roomCode= splitLine[2];
            }
            chatHandler.createRoom(this, roomName, roomCode);

        }
    }
    public void changeRoomName(String line){
        String[] splitLine = line.split(" ", 2);
        if(splitLine.length >= 2) {
            String newName = splitLine[1];
            user.getCurrentChatRoom().setName(newName);
        }
        else sendToClient("You need to add an argument.");
    }
    public void changeRoomCode(String line){
        String[] splitLine = line.split(" ", 2);
        if(splitLine.length >= 2) {
            String newCode = splitLine[1];
            try{
                user.getCurrentChatRoom().setChatCode(newCode);
            }catch(RoomException e){
                sendToClient(e.getMessage());
            }
        }
        else sendToClient("You need to add an argument.");
    }
    public void leaveRoom(){
        if(user.getCurrentChatRoom()!=null){
            chatHandler.leaveRoom(this);
            user.setCurrentChatRoom(null);
        }
    }

    public void sendToChat(String message){
        user.getCurrentChatRoom().sendToOthers(this, message);
    }


    public void closeConnection(){
            try {
                running = false;
                server.removeConnectedClient(this);
                if (user!=null && user.getCurrentChatRoom()!=null) {
                    chatHandler.leaveRoom(this);
                    user.setCurrentChatRoom(null);
                }
                if(in!=null) in.close();
                if(out!=null) out.close();
                if(socket!=null) socket.close();
                if(user!=null)this.setUser(null);
                System.out.println("Client disconnected successfully.");
            } catch (IOException e) {
                System.out.println("Error closing connection");
        }
    }
}





/*Notas borradas (ignorar):Aquí miré que para mayor rendimiento, es mejor usar un .charAt(0) después de que comprobar que el string no está vacío, ya que no crea un objeto String nuevo y etc, pero como mi aplicación no está pensada para soportar millones de mensajes, he preferido usar .startsWith() por mayor simplicidad y legibilidad.*/