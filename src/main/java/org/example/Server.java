package org.example;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    JFrame window =new JFrame();

    JPanel center = new JPanel();
    Border border = BorderFactory.createLineBorder(Color.BLACK);
    JPanel users = new JPanel();

    ServerSocket serverSocket = new ServerSocket(8080);
    List<ClientHandler> clients = new ArrayList<>();

    public Server() throws IOException {
    }

    class ClientHandler implements Runnable{
        String name;
        public Socket socket;
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
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });

        center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        JLabel title = new JLabel("服务器端在线人数");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(title);
        users.setPreferredSize(new Dimension());
        users.setBorder(border);
        users.setLayout(new GridLayout());
        for (ClientHandler client : clients){
            String message = String.valueOf(client.socket.getInetAddress());
            users.add(new JLabel(message));
        }
        center.add(users);

        int number = clients.size();
        JLabel numberLabel = new JLabel("当前有"+ number +"个客户在线");
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(numberLabel);

        window.add(center);
        window.pack();
        window.setVisible(true);


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
