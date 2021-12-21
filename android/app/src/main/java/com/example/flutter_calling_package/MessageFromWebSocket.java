package com.example.flutter_calling_package;

import java.util.ArrayList;

public class MessageFromWebSocket {
    String message;
    String type;
    String username;
    String audio;
    ArrayList<String> users;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }
}

