package com.mr.clock.frame;

import com.mr.clock.pojo.Employee;
import com.mr.clock.service.CameraService;
import com.mr.clock.service.FaceEngineService;
import com.mr.clock.service.HRService;
import com.mr.clock.service.ImageService;
import com.mr.clock.session.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class AddEmployeePanel extends JPanel{
    private MainFrame parent;
    private JTextField nameField;
    private JLabel message;
    private JButton submit;
    private JButton back;
    private JPanel center;

    public AddEmployeePanel(MainFrame parent){
        this.parent=parent;
        init();
        addListener();
    }

    private void init(){
        parent.setTitle("录入新员工");
        JLabel nameLabel=new JLabel("员工名称",JLabel.RIGHT);
        nameField=new JTextField(15);
        submit=new JButton("拍照并录入");
        back=new JButton("返回");

        setLayout(new BorderLayout());
        JPanel bottom=new JPanel();
        bottom.add(nameLabel);
        bottom.add(nameField);
        bottom.add(submit);
        bottom.add(back);
        add(bottom,BorderLayout.SOUTH);

        center=new JPanel();
        center.setLayout(null);

        message=new JLabel("请正面面向摄像头");
        message.setFont(new Font("宋体",Font.BOLD,40));
        message.setBounds((640-400)/2,20,400,50);
        center.add(message);

        JPanel blackPanel=new JPanel();
        blackPanel.setBackground(Color.BLACK);
        blackPanel.setBounds(150,75,320,240);
        center.add(blackPanel);

        add(center,BorderLayout.CENTER);

        Thread cameraThread=new Thread(){
            public void run() {
                if (CameraService.startCamera()) {
                    message.setText("请正面面向摄像头");
                    JPanel caneraPanel = CameraService.getCameraPanel();
                    caneraPanel.setBounds(150, 75, 320, 240);
                    center.add(caneraPanel);
                } else {
                    JOptionPane.showMessageDialog(parent, "未检测到摄像头");
                    back.doClick();
                }
            }
        };
        cameraThread.start();
    }

    private void addListener() {
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name=nameField.getText().trim();
                if(name==null || "".equals(name)){
                    JOptionPane.showMessageDialog(parent,"名字不能为空!");
                    return;
                }
                if(!CameraService.cameraIsOpen()){
                    JOptionPane.showMessageDialog(parent,"摄像头尚未开启,请稍后.");
                    return;
                }
                BufferedImage image=CameraService.getCameraFrame();
                FaceFeature ff = FaceEngineService.getFaceFeature(image);
                if(ff==null){
                    JOptionPane.showMessageDialog(parent,"未检测到有效人脸信息");
                    return;
                }
                Employee e1=HRService.addEmp(name,image);
                ImageService.saveFaceImage(image,e1.getCode());
                Session.FACE_FEATURE_HASH_MAP.put(e1.getCode(),ff);
                JOptionPane.showMessageDialog(parent,"员工添加成功!");
                back.doClick();
            }
        });

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CameraService.releaseCamera();
                parent.setPanel(new EmployeeManagementPanel(parent));
            }
        });
    }
}
