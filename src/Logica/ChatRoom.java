package Logica;

import Excepciones.RoomException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChatRoom {
    //private int chatRoom_id ;
    //The ChatRoom will be identified by its name for now.
    private final ChatHandler chatHandler;
    private String name = "Unnamed_chatroom";

    private String chatCode = "";
    private Set<ClientHandler> users = Collections.synchronizedSet(new HashSet<>());

    public ChatRoom(String name, String chatCode, ChatHandler chatHandler) throws RoomException {
        setName(name);
        setChatCode(chatCode);
        this.chatHandler = chatHandler;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        if(name!=null) {
            if (name.length() >= 25) {
                this.name = name.substring(0, 25);
            }
            else {
                this.name = name;
            }
            notifyUsers("The chat's name was updated to: " + name + ".");

        }
        //I don't have better handling for possible inputs since the command processor will check the number of arguments, so it cannot be empty or null.
    }


    public String getChatCode() {
        return chatCode;
    }

    public void setChatCode(String chatCode) throws RoomException {
        if(chatCode !=null) {
            if(chatCode.length()>=10) throw new RoomException("You can't use more than 10 characters.");
            else {
                this.chatCode = chatCode;
            }
            notifyUsers("The chat's code was updated to: " + chatCode + ".");
        }
        else throw new RoomException("The name of the room cannot be null.");
    }


    public synchronized void sendToOthers(ClientHandler ch, String message){
        for (ClientHandler client : users){
            if(!client.equals(ch)){
                client.sendToClient(ch.getUser().getName() + ": " + message);
            }
        }
    }

    public synchronized void notifyUsers(String message){
        if(!users.isEmpty()){
            for (ClientHandler client : users){
                client.sendToClient("* * * ~ " + message + " ~ * * *");
            }
        }

    }

    public synchronized void addUser(ClientHandler ch){
        users.add(ch);
    }

    public synchronized void removeUser(ClientHandler ch){
        if(ch!=null){
            users.remove(ch);
            if(users.size()==0){
                chatHandler.removeRoom(this);
                this.setName(null);
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(name, chatRoom.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

//Podría añadirle una funcionalidad extra, cuando implemente IDs, que es una herencia (Admin extends User) y si es instanceof Admin -> puede borrar usuarios, etc... del chatRoom.