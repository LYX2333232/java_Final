package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class Server {
    JFrame window =new JFrame();

    JPanel center = new JPanel();
    ServerSocket serverSocket = new ServerSocket(8080);
    List<ClientHandler> clients;

    public Server() throws IOException {
    }

    class ClientHandler implements Runnable{
        String name;
        Socket socket;
        DataInputStream in;
        DataOutputStream out;


        public ClientHandler(String name,Socket socket) throws IOException {
            this.socket = socket;
            InputStream is = socket.getInputStream();
            in = new DataInputStream(is);
            OutputStream os = socket.getOutputStream();
            out = new DataOutputStream(os);
        }

        @Override
        public void run() {
            while (true) {
                String message = null;
                try {
                    message = String.valueOf(in.read());
                }catch (SocketException | EOFException e){
                    //断开连接
                    clients.remove(this);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (ClientHandler client : clients) {
                    try {
                        if (message != null) {
                            if (client == this){
                                message = "You:" + message;
                            }
                            else {
                                message = name + message;
                            }
                            client.out.writeUTF(message);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    void pack(){
        window.setLayout(new BorderLayout());


    }

    void start() throws IOException {
        pack();
        while (true) {
            Socket socket = serverSocket.accept();
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            String res = dis.readUTF();
            String name = res.split(":")[0];
            String message = res.split(":")[1];
            ClientHandler client = new ClientHandler(name,socket);
            clients.add(client);
            new Thread(client).start();
        }
    }

    public static void main(String[] args) throws IOException {
        new Server().start();
    }
}
