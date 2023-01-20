package com.mr.clock.frame;

import com.mr.clock.service.HRService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog{
    private JTextField usernameField=null;      //       “用户名”  文本框
    private JPasswordField passwordField=null;     //       "密码"  输入框
    private JButton loginBtn=null;      //       “登录"按钮
    private JButton cancelBtn=null;  //       "取消" 按钮
    private final int WIDTH=300,HEIGHT=150;    //     对话框的宽高

    public LoginDialog(Frame owner){
        super(owner,"登录员管理",true);             //     阻塞主窗体
        setSize(WIDTH,HEIGHT);       //    设置宽高
        setLocation(owner.getX()+(owner.getWidth()-WIDTH)/2,owner.getY()+(owner.getHeight()-HEIGHT)/2);
        init();          //        组件初始化
        addListener();        //       为组件添加监听
    }

    private void init(){
        JLabel usernameLabel=new JLabel("王彪",JLabel.CENTER);
        JLabel passwordLabel=new JLabel("WangsBia221",JLabel.CENTER);
        usernameField=new JTextField();
        passwordField=new JPasswordField();
        loginBtn=new JButton("登录");
        cancelBtn=new JButton("取消");

        Container c=getContentPane();
        c.setLayout(new GridLayout(3,2));
        c.add(usernameLabel);
        c.add(usernameField);
        c.add(passwordLabel);
        c.add(passwordField);
        c.add(loginBtn);
        c.add(cancelBtn);
    }

    private void addListener(){
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginDialog.this.dispose();
            }
        });

        loginBtn.addActionListener(new ActionListener() {        //       “登录” 按钮的事件
            @Override
            public void actionPerformed(ActionEvent e) {
                String username=usernameField.getText().trim();   //     获取用户输入的用户名
                String password=new String(passwordField.getPassword());  //  获取用户输入的密码
                boolean result=HRService.userLogin(username,password);     //     检查用户名和密码是否正确
                if(result){                                         //    如果正确
                    LoginDialog.this.dispose();       //          销毁登录对话框
                }else {
                    JOptionPane.showMessageDialog(LoginDialog.this,"用户名或密码有误");   // 提示用户名或密码有误
                }
            }
        });

        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginBtn.doClick();
            }
        });

        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordField.grabFocus();
            }
        });
    }
}
