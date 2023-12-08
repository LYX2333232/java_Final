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

    ServerSocket serverSocket = new ServerSocket(9090);
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
            this.name = name;
            InputStream is = socket.getInputStream();
            in = new DataInputStream(is);
            OutputStream os = socket.getOutputStream();
            out = new DataOutputStream(os);
        }

        @Override
        public void run() {
            while (true) {
                String message = "";
                String output = "";
                try {
                    message = in.readUTF();
                    System.out.println(message);
                }catch (SocketException | EOFException e){
                    //断开连接
                    clients.remove(this);
                    return;
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (ClientHandler client : clients) {
                    try {
                        if (client == this) {
                            output = "You:" + message;
                        } else {
                            output = name+":"+ message;
                        }
                        client.out.writeUTF(output);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    void update(){
        center.removeAll();
        users.removeAll();
        center.setBounds(0,0,300,300);
        center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        JLabel title = new JLabel("服务器端在线人数");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(title);
        users.setPreferredSize(new Dimension());
        users.setBorder(border);
        users.setLayout(new GridLayout());
        for (ClientHandler client : clients){
            String message = client.name+ client.socket.getInetAddress();
            System.out.println(message);
            users.add(new JLabel(message));
        }
        center.add(users);
        int number = clients.size();
        JLabel numberLabel = new JLabel("当前有"+ number +"个客户在线");
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(numberLabel);
        window.revalidate();
        window.repaint();
    }

    void pack(){
        window.setBounds(700,100,500,500);
        window.setLayout(new BorderLayout());
        window.setPreferredSize(new Dimension(500,500));
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
        update();
        window.add(center);
        window.pack();
        window.setVisible(true);


    }

    void start() throws IOException {
        pack();
        while (true) {
            Socket socket = serverSocket.accept();
            String ip = String.valueOf(socket.getInetAddress());
            System.out.println(ip);
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            String name = dis.readUTF();
            System.out.println(name);
            ClientHandler client = new ClientHandler(name,socket);
            StringBuilder users = new StringBuilder();
            for (ClientHandler c :clients){
                c.out.writeUTF(name+"来啦！\n");
                if (!users.toString().isEmpty()){
                    users.append("、");
                }
                users.append(c.name);
            }
            if (!users.toString().isEmpty()) {
                users.append("已经在聊天室\n");
                System.out.println(users);
                client.out.writeUTF(String.valueOf(users));
            }

            clients.add(client);
            System.out.println(clients);
            update();
            new Thread(client).start();

        }
    }

    public static void main(String[] args) throws IOException {
        new Server().start();
    }
}
