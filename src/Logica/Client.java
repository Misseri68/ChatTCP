package Logica;

import Excepciones.UserException;
import DAOs.UserDAO.User;

import java.net.Socket;

public class Client {
    User user;
    Socket socket;
    ChatRoom currentChatRoom;
    public Client(Socket socket) {
        this.socket = socket;
    }

    public void setUser(User user) throws UserException {
        if (user == null) throw new UserException("The user cannot be null.");
    }

    public User getUser() {
        return user;
    }

}
