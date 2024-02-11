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

    public Set<ChatRoom> getSalasCreadas() {
        return createdRooms;
    }

    public ChatRoom findRoomByName(String name){
        for (ChatRoom room : createdRooms) {
            if(room.getName().equals(name)) return room;
        }
        return null;
    }

    public void createRoom(String name, String pwd){
        try{
            createdRooms.add(new ChatRoom(name, pwd));
        }catch (RoomException e){

        }
    }

    public void joinRoom(String name, String pwd){
        if(createdRooms.contains(new ChatRoom(name))){
            //TODO conectar clientes al chatroom
        }
        else{
            //TODO informar al usuario de que no existe y _----que si lo quiere crear---/ o avisarle de usar join
        }
    }


    public void removeRoom(String name, String pwd){

        }


    public void getConnectedClientsRefresh(){
        Set<ClientHandler> connectedClients = server.getConnectedClients();
    }

}
