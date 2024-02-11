package Logica;

import Excepciones.RoomException;

import java.util.Objects;

public class ChatRoom {
    private int chatRoom_id ;

    private String name = "Unnamed chatroom";

    private String chatPassword = "";



    /*TODO: Cómo diferencio entre el cliente que ha mandado el mensaje y los demás? Mandando el socket del enviador?
    el que envia el mensaje usaría out., mientras que los que lo reciben sería in., Coger el cliente enviador como
    argumento en metodo sentMessage()?*/

    public ChatRoom(String name, String chatPassword) throws RoomException {
        setName(name);
        setChatPassword(chatPassword);
    }
    public ChatRoom(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws RoomException {
        if(name!=null) {
            if (name.length() >= 25) {
                //TODO como mando un mensaje desde la app  a cada usuario?
                this.name = name.substring(0, 25);
            }
            else if (name.isEmpty()){
                //TODO mandarle mensaje al usuario de que no puede estar vacio
            }
            else{
                this.name = name;
            }
        }
        else throw new RoomException("The name of the room cannot be null.");
    }

    public String getChatPassword() {
        return chatPassword;
    }

    public void setChatPassword(String chatPassword) throws RoomException {
        if(chatPassword!=null) {
            if(chatPassword.length()>=10) throw new RoomException("You can't use more than 10 characters.");
            else {
                this.chatPassword = chatPassword;
            }
        }
        else throw new RoomException("The name of the room cannot be null.");

    }

    public void disconnectClients(){

    }

    public void disconnectClient(){}

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
