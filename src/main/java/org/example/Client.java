package org.example;

import org.example.pojo.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    Socket socket;
    JFrame window = new JFrame("客户端");

    JPanel header = new JPanel();
    JTextField name = new JTextField("123");
    JButton login = new JButton("登陆");

    JPanel center = new JPanel();
    List<JTextArea> messages = new ArrayList<>(
            List.of(new JTextArea[] { new JTextArea(10, 50), new JTextArea(10, 50) }));
    Integer index = 0;

    JPanel right = new JPanel();
    DefaultListModel<User> model = new DefaultListModel<>();
    JList<User> userList = new JList<>(model);

    JPanel bottom = new JPanel();
    JTextArea input = new JTextArea(3, 50);
    JButton send = new JButton("发送");

    void getUser(String name, Integer port) {
        User user = new User(name, port);
        model.addElement(user);
        messages.add(new JTextArea(10, 50));
    }

    void init() {
        // 客户端主窗口
        window.setLayout(new BorderLayout());
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // 头部布局
        header.setLayout(new FlowLayout());
        header.add(new JLabel("name:"));
        header.add(name);
        header.add(new JLabel("port:"));
        login.addActionListener(e -> {
            try {
                socket = new Socket("127.0.0.1", 8080);
            } catch (Exception ex) {
                JDialog dialog = new JDialog(window,"报错");
                dialog.setBounds(200,200,300,300);
                dialog.add(new JLabel("连接失败！！"));
                dialog.setVisible(true);
            }
        });
        header.add(login);
        window.add(header, BorderLayout.NORTH);

        // 中心布局
        center.add(messages.get(index));
        window.add(center, BorderLayout.CENTER);

        // 右侧布局
        for (int i = 1; i <= 100; i++) {
            getUser("user" + i, i);
        }
        userList.addListSelectionListener(e -> {
            User select = userList.getSelectedValue();
            Integer i = userList.getAnchorSelectionIndex();
            System.out.println(select);
            System.out.println(i);

            index = i;
            center.remove(0);
            center.add(messages.get(index));
            window.revalidate();
            window.repaint();
        });
        right.setPreferredSize(new Dimension(50, 90));
        right.add(userList);
        window.add(right, BorderLayout.EAST);

        // 底部布局
        // 上传信息
        send.addActionListener(e -> {
            String prefix = "You " + " :";
            messages.get(index).append(prefix + input.getText() + "\n");
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
