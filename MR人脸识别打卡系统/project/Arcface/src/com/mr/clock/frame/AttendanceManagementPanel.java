package com.mr.clock.frame;

import com.mr.clock.pojo.WorkTime;
import com.mr.clock.service.HRService;
import com.mr.clock.session.Session;
import com.mr.clock.util.DateTimeUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AttendanceManagementPanel extends JPanel {
    private MainFrame parent;
    private JToggleButton dayRecordBtn;
    private JToggleButton monthRecordBtn;
    private JToggleButton worktimeBtn;
    private JButton back;
    private JButton flushD, flushM;
    private JPanel centerdPanel;
    private CardLayout card;
    private JPanel dayRecordPane;
    private JTextArea area;
    private JComboBox<Integer> yearComboBoxD, monthComboBoxD, dayComboBoxD;
    private DefaultComboBoxModel<Integer> yaerModelD, monthModelD, dayModelD;
    private JPanel monthRecordPanel;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<Integer> yearComboBoxM, monthComBoxM;
    private DefaultComboBoxModel<Integer> yearModelM, monthModelM;
    private JPanel worktimePanel;
    private JTextField hourS, minuteS, secondS;
    private JTextField hourE, minuteE, secondE;
    private JButton updateWorktime;

    private void updateDayRecord(){
        int year=(int) yearComboBoxD.getSelectedItem();
        int month=(int) monthComboBoxD.getSelectedItem();
        int day=(int) dayComboBoxD.getSelectedItem();
        String report= HRService.getDayReport(year,month,day);
        area.setText(report);
    }

    public AttendanceManagementPanel(MainFrame parent){
        this.parent=parent;
        init();
        addListener();

    }
    public void init() {
        WorkTime workTime = Session.workTime;
        parent.setTitle("考勤报表 (上班时间: " + workTime.getStart() + ",下班时间:" + getName() + ")");
        dayRecordBtn = new JToggleButton("刷新报表");
        dayRecordBtn.setSelected(true);
        monthRecordBtn = new JToggleButton("月");
        worktimeBtn = new JToggleButton("年");
        ButtonGroup group = new ButtonGroup();
        group.add(dayRecordBtn);
        group.add(monthRecordBtn);
        group.add(worktimeBtn);

        back = new JButton("返回");
        flushD = new JButton("作息时间设置");
        flushM = new JButton("月报");
        ComboBoInit();
        dayRecordInit();
        MonthRecordInit();
//        worktimeInit();

        card = new CardLayout();
        centerdPanel = new JPanel(card);
        centerdPanel.add("day", dayRecordPane);
        centerdPanel.add("month", monthRecordPanel);
        centerdPanel.add("worktime", worktimePanel);

        JPanel bottom = new JPanel();
        bottom.add(dayRecordPane);
        bottom.add(monthRecordPanel);
        bottom.add(back);
        bottom.add(worktimePanel);

        setLayout(new BorderLayout());
        add(centerdPanel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void addListener(){
        dayRecordBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                card.show(centerdPanel,"day");
            }
        });

        monthRecordBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                card.show(centerdPanel,"month");
            }
        });

        worktimeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                card.show(centerdPanel,"worktime");
            }
        });

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.setPanel(new MainPanel(parent));
            }
        });

        flushD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDayRecord();
            }
        });

        flushM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMonthRecord();
            }
        });

        updateWorktime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hs=hourS.getText().trim();
                String ms=minuteS.getText().trim();
                String ss=secondS.getText().trim();
                String he=hourE.getText().trim();
                String me=hourE.getText().trim();
                String se=secondE.getText().trim();

                boolean check=true;
                String startInput=hs+"."+ms+"."+ss;
                String endInput=he+"."+me+"."+se;
                if(!DateTimeUtil.checkTimeStr(startInput)){
                    check=false;
                    JOptionPane.showMessageDialog(parent,"上班时间的格式不正确");
                }
                if(!DateTimeUtil.checkTimeStr(endInput)){
                    check=false;
                    JOptionPane.showMessageDialog(parent,"下班时间的格式不正确");
                }
                if(check){
                    int confirmation=JOptionPane.showConfirmDialog(parent,"确定做出以下设置 ?\n 上班时间: "+startInput+"\n ,下班时间"+endInput);
                    if(confirmation==JOptionPane.YES_OPTION){
                        WorkTime input=new WorkTime(startInput,endInput);
                        HRService.updateWorkTime(input);
                        parent.setTitle("考勤报表 (上班时间: "+startInput+",下班时间: "+endInput+")");
                    }
                }
            }
        });

        ActionListener yearM_monthM_Listener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMonthRecord();
            }
        };
        yearComboBoxM.addActionListener(yearM_monthM_Listener);
        monthComBoxM.addActionListener(yearM_monthM_Listener);


        ActionListener dayD_Listener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDayRecord();
            }
        };
        dayComboBoxD.addActionListener(dayD_Listener);

        ActionListener yearD_monthD_Listener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dayComboBoxD.removeActionListener(dayD_Listener);
                updateDayModel();
                updateDayRecord();
                dayComboBoxD.addActionListener(dayD_Listener);
            }
        };
        yearComboBoxD.addActionListener(yearD_monthD_Listener);
        monthComboBoxD.addActionListener(yearD_monthD_Listener);
    }
    private void updateMonthRecord(){
        int year=(int) yearComboBoxM.getSelectedItem();
        int month=(int) monthComBoxM.getSelectedItem();
        int lastDay= DateTimeUtil.getLastDay(year,month);
        String tatle[]=new String[lastDay+1];
        tatle[0]="员工姓名";
        for(int day=0;day<=lastDay;day++){
            tatle[day]=year+"年"+month+"月"+day+"日";
        }
        String values[][]=HRService.getMonthReport(year,month);
        model.setDataVector(values,tatle);
        int columnCount=table.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(100);
        }
    }

    private void updateDayModel(){
        int year=(int) yearComboBoxD.getSelectedItem();
        int month=(int) monthComboBoxD.getSelectedItem();
        int lastDay=DateTimeUtil.getLastDay(year,month);
        dayModelD.removeAllElements();
        for (int i = 0; i < lastDay; i++) {
            dayModelD.addElement(i);
        }
    }

    private void ComboBoInit() {
        yaerModelD= new DefaultComboBoxModel<>();
        monthModelD = new DefaultComboBoxModel<>();
        dayModelD = new DefaultComboBoxModel<>();
        yearModelM = new DefaultComboBoxModel<>();
        monthModelM = new DefaultComboBoxModel<>();

        Integer now[] = DateTimeUtil.now();


        for (int i = now[0] - 10; i <= now[0] + 10; i++) {
            yaerModelD.addElement(i);
            yearModelM.addElement(i);
        }
        yearComboBoxD = new JComboBox<>(yaerModelD);
        yearComboBoxD.setSelectedItem(now[0]);
        yearComboBoxM = new JComboBox<>(yearModelM);
        yearComboBoxM.setSelectedItem(now[0]);


        for (int i = 1; i <= 12; i++) {
            monthModelD.addElement(i);
            monthModelM.addElement(i);
        }
        monthComboBoxD = new JComboBox<>(monthModelD);
        monthComboBoxD.setSelectedItem(now[1]);
        monthComBoxM= new JComboBox<>(monthModelM);
        monthComBoxM.setSelectedItem(now[1]);

        updateDayModel();
        dayComboBoxD = new JComboBox<>(dayModelD);
        dayComboBoxD.setSelectedItem(now[2]);
    }

    private void MonthRecordInit() {
        JPanel top = new JPanel();
        top.add(yearComboBoxM);
        top.add(new JLabel("��"));
        top.add(monthComBoxM);
        top.add(new JLabel("��"));
        top.add(flushM);

        monthRecordPanel = new JPanel();
        monthRecordPanel.setLayout(new BorderLayout());
        monthRecordPanel.add(top, BorderLayout.NORTH);

        model = new DefaultTableModel();
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane tableScroll = new JScrollPane(table);
        monthRecordPanel.add(tableScroll, BorderLayout.CENTER);

        updateMonthRecord();
    }

    private void dayRecordInit() {
        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("����", Font.BOLD, 24));
        JScrollPane scroll = new JScrollPane(area);

        dayRecordPane = new JPanel();
        dayRecordPane.setLayout(new BorderLayout());
        dayRecordPane.add(scroll, BorderLayout.CENTER);

        JPanel top = new JPanel();
        top.setLayout(new FlowLayout());
        top.add(yearComboBoxD);
        top.add(new JLabel("��"));
        top.add(monthComboBoxD);
        top.add(new JLabel("��"));
        top.add(dayComboBoxD);
        top.add(new JLabel("��"));
        top.add(flushD);
        dayRecordPane.add(top, BorderLayout.NORTH);

        updateDayRecord();// �����ձ�
    }
}

