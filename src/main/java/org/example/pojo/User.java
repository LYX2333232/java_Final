package org.example.pojo;


public class User {
    public String name;
    public Integer port;

    @Override
    public String toString() {
        return name+":"+port.toString();
    }
    public User(String name, Integer port){
        this.name = name;
        this.port = port;
    }
}
