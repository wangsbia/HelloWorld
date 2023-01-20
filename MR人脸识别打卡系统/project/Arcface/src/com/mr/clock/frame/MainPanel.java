package com.mr.clock.frame;

import com.mr.clock.pojo.Employee;
import com.mr.clock.service.CameraService;
import com.mr.clock.service.FaceEngineService;
import com.mr.clock.service.HRService;
import com.mr.clock.session.Session;
import com.mr.clock.util.DateTimeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Date;

public class MainPanel extends JPanel{
    private MainFrame parent;      //    主窗体
    private JToggleButton daka;   //    打卡按钮
    private JButton kaoqin;       //   考勤按钮
    private JButton yuangong;      //   员工按钮
    private JTextArea area;    //   提示信息文本域
    private DetectFaceThread dft;     //  人脸识别线程
    private JPanel center;      //  中部面板


    public MainPanel(MainFrame parent){
        this.parent=parent;
        init();
        addListener();
    }


    public void init(){
        parent.setTitle("MR人脸识别打卡系统");
        center=new JPanel();
        center.setLayout(null);
        area=new JTextArea();
        area.setEnabled(false);
        area.setFont(new Font("宋体",Font.BOLD,18));
        JScrollPane scroll=new JScrollPane(area);
        scroll.setBounds(0,0,275,300);
        center.add(scroll);

        daka=new JToggleButton("打  卡");
        daka.setFont(new Font("宋体",Font.BOLD,40));
        daka.setBounds(330,300,240,70);
        center.add(daka);

        JPanel blakPanel=new JPanel();
        blakPanel.setBounds(286,16,320,240);
        blakPanel.setBackground(Color.BLACK);
        center.add(blakPanel);

        setLayout(new BorderLayout());
        add(center,BorderLayout.CENTER);

        JPanel bottom=new JPanel();
        kaoqin=new JButton("考勤报表");
        yuangong=new JButton("员工管理");
        bottom.add(kaoqin);
        bottom.add(yuangong);
        add(bottom,BorderLayout.SOUTH);

    }

    private void releaseCamera() {
        CameraService.releaseCamera();        //      释放摄像头
        area.append("摄像头已关闭 \n");        //  添加提示信息
        if (dft != null) {                   //      如果人脸识别线程被创建
            dft.stopThread();                //   停止线程
        }
        daka.setText("打  卡");             //     更改 “打卡” 按钮的文本
        daka.setSelected(false);           //     “打卡” 按钮变为未选中状态
        daka.setEnabled(true);           //        “打卡” 按钮可用
    }

    private class DetectFaceThread extends Thread {
        boolean work = true;   //  人脸识别线程是否继续扫描image

        public void run() {
            while (work) {
                if (CameraService.cameraIsOpen()) {    //    如果摄像头已开启
                    BufferedImage frame = CameraService.getCameraFrame();    //    获取摄像头的当前帧
                    if (frame != null) {   //   获取当前帧中出现的人脸对应的特征码
                        String code = FaceEngineService.detectFace(FaceEngineService.getFaceFeature(frame));
                        if (code != null) {   //     如果特征码不为null,表明画面中存在某员工的人脸
                            Employee e = HRService.getEmp(code);    //   根据特征码获取员工对象
                            HRService.addClockInRecord(e);         //     为此员工添加打卡记录
                            area.append("\n" + DateTimeUtil.datetimeNow() + "\n");   //  文本域添加提示信息
                            area.append(e.getName() + " 打卡成功  \n\n");
                            releaseCamera();          //    释放摄像头
                        }
                    }
                }
            }
        }
        public synchronized void stopThread() {
            work = false;          //    停止人脸识别线程
        }
    }

    public void addListener(){
        daka.addActionListener(new ActionListener() {          //         “打卡” 按钮的事件
            @Override
            public void actionPerformed(ActionEvent e) {
                if(daka.isSelected()){                        //     如果“打卡”按钮是选中状态
                    area.append("正在开启摄像头,请稍后-.....\n");
                    daka.setEnabled(false);                //       打卡按钮不可用
                    daka.setText("关闭摄像头");            //     更改打卡按钮的文本
                    Thread cameraThread=new Thread(){
                        public void run(){
                            if(CameraService.startCamera()){
                                area.append("请面向摄像头打卡 \n");       //    添加提示
                                daka.setEnabled(true);              //        打卡按钮可用
                                JPanel cameraPanel=CameraService.getCameraPanel();
                                cameraPanel.setBounds(286,16,320,240);
                                center.add(cameraPanel);          //      放到中部面板当中
                            }else {
                                JOptionPane.showMessageDialog(parent,"未检测到摄像头 ");
                                releaseCamera();       //       释放摄像头资源
                                return;           //        停止方法
                            }
                        }
                    };
                    cameraThread.start();         //      启动临时线程
                    dft=new DetectFaceThread();  //       创建人脸识别线程
                    dft.start();            //              启动人脸识别线程
                }else {                //      如果打卡按钮不是选中状态
                    releaseCamera();     //         释放摄像头资源
                }
            }
        });

        kaoqin.addActionListener(new ActionListener() {           //     考勤报表按钮的事件
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Session.user==null){                           //      如果没有管理员登录
                    LoginDialog Id=new LoginDialog(parent);
                    Id.setVisible(true);                  //         展示登录对话框
                }
                if(Session.user!=null){
                    AttendanceManagementPanel amp=new AttendanceManagementPanel(parent);     //创建考勤报表面板
                    parent.setPanel(amp);              //     主窗体切换至考勤面板
                    releaseCamera();      //         释放摄像头
                }
            }
        });

        yuangong.addActionListener(new ActionListener() {         //  员工管理按钮的事件
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Session.user==null){          //        如果没有管理员登录
                    LoginDialog Id=new LoginDialog(parent);
                    Id.setVisible(true);
                }
                if(Session.user!=null){               //        如果管理员已登录
                    EmployeeManagementPanel emp=new EmployeeManagementPanel(parent); //        创建员工管理面板
                    parent.setPanel(emp);           //      主窗体切换至考勤面板
                    releaseCamera();         //       释放摄像头资源
                }
            }
        });
    }
}
