package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    Socket socket;
    JFrame window = new JFrame("客户端");

    JPanel header = new JPanel();
    JTextField name = new JTextField(8);
    JButton login = new JButton("登陆");

    JPanel center = new JPanel();
    JTextArea messages = new JTextArea(10,50);

    JPanel bottom = new JPanel();
    JTextArea input = new JTextArea(3, 50);
    JButton send = new JButton("发送");

    class Client1 implements Runnable{
        Socket socket;
        DataInputStream in;
        DataOutputStream out;

        public Client1(Socket socket) throws IOException {
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        }
        @Override
        public void run() {
            while (true){
                String message;
                try {
                    message = in.readUTF();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                messages.append(message);
                window.revalidate();
                window.repaint();
            }
        }
    }

    void init() {
        // 客户端主窗口
        window.setLayout(new BorderLayout());
        window.addWindowListener(new WindowAdapter() {
            @Override  //关闭
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // 头部布局
        header.setLayout(new FlowLayout());
        header.add(new JLabel("name:"));
        header.add(name);
        login.addActionListener(e -> {
            if (socket != null){
                JDialog dialog = new JDialog(window,"错误");
                dialog.setBounds(200,200,300,300);
                dialog.add(new JLabel("请勿重复登录！！"));
                dialog.setVisible(true);
                return;
            }
            //点击登陆按钮事件
            try {
                socket = new Socket("127.0.0.1", 9090);
            } catch (Exception ex) {
                JDialog dialog = new JDialog(window,"报错");
                dialog.setBounds(200,200,300,300);
                dialog.add(new JLabel("连接失败！！"));
                dialog.setVisible(true);
            }
            OutputStream os;
            try {
                os = socket.getOutputStream();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            DataOutputStream dos = new DataOutputStream(os);
            try {
                dos.writeUTF(name.getText());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            JDialog dialog = new JDialog(window,"成功");
            dialog.setBounds(200,200,300,300);
            dialog.add(new JLabel("连接成功！！"));
            dialog.setVisible(true);
            Client1 client1;
            try {
                client1 = new Client1(socket);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            new Thread(client1).start();
        });
        header.add(login);
        window.add(header, BorderLayout.NORTH);

        // 中心布局
        center.add(messages);
        window.add(center, BorderLayout.CENTER);

        // 底部布局
        // 上传信息
        send.addActionListener(e -> {
            try {
                DataOutputStream dos =  new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(input.getText() + "\n");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            input.setText("");
        });
        bottom.setLayout(new FlowLayout());
        bottom.add(input);
        bottom.add(send);
        window.add(bottom, BorderLayout.SOUTH);

        window.setBounds(100, 100, 200, 200);
        window.pack();
        window.setVisible(true);
    }


    public static void main(String[] args) {
        new Client().init();
        // new Client().start();
    }
}
