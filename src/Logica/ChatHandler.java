package Logica;

import Excepciones.RoomException;

import java.util.HashSet;
import java.util.Set;

public class ChatHandler {
    Set<ChatRoom> createdRooms;
    Server server;

    public ChatHandler(Server server) {
        this.createdRooms = new HashSet<>();
        this.server = server;
    }

    public synchronized Set<ChatRoom> getSalasCreadas() {
        return createdRooms;
    }

    public ChatRoom findRoomByName(String name){
        for (ChatRoom room : createdRooms) {
            if(room.getName().equals(name)) return room;
        }
        return null;
    }

    public synchronized void createRoom(ClientHandler ch, String name, String pwd){
        try {
            if(findRoomByName(name)==null){
                createdRooms.add(new ChatRoom(name, pwd, this));
                String created = "Room " + name + " created.";
                ch.sendToClient(created);
                server.printToServer(created);
            }
        } catch (RoomException e) {
            ch.sendToClient(e.getMessage());
        }
    }

    public void removeRoom(ChatRoom room){
        createdRooms.remove(room);
        server.printToServer("ChatRoom " + room.getName() + " removed.");

    }

    public void joinRoom(ClientHandler ch, String name, String code){
        ChatRoom room = findRoomByName(name);
        if(room != null){
            if(room.getChatCode().equals(code)){
                ch.getUser().setCurrentChatRoom(room);
                room.addUser(ch);
                room.notifyUsers("User " + ch.getUser().getName() + " joined the chat.");

            }
            else ch.sendToClient("Wrong chat code.");
        }
        else{
            ch.sendToClient("The chatroom doesn't exist. Please join another room or create one.");
        }
    }


    public void leaveRoom(ClientHandler ch){
        ChatRoom room = ch.getUser().getCurrentChatRoom();
        if(room!=null){
            room.removeUser(ch);
            room.notifyUsers("User " + ch.getUser().getName() + " left the chat.");
            ch.sendToClient("You left " + room.getName() + " successfully.");
        }
        else ch.sendToClient("You're not in a room??? If you got here, I must've done a bad job.");
    }
}
