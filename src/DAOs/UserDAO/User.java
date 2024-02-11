package DAOs.UserDAO;

import Logica.ChatRoom;

public class User {
    /*@Id autoincrement... for when I update with a database.
    Long id_user;*/
    String name ="";
    ChatRoom currentChatRoom;

    public User(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public ChatRoom getCurrentChatRoom() {
        return currentChatRoom;
    }

    public void setCurrentChatRoom(ChatRoom currentChatRoom) {
        this.currentChatRoom = currentChatRoom;
    }
}
