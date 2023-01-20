package com.mr.clock.frame;

import com.mr.clock.pojo.Employee;
import com.mr.clock.service.HRService;
import com.mr.clock.session.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

public class EmployeeManagementPanel extends JPanel{
    private MainFrame parent;  //   主窗体
    private JTable table;  //    员工信息表格
    private DefaultTableModel model;      //     表格的数据模型
    private JButton add;   //          录入新员工按钮
    private JButton delete;    //  删除员工按钮
    private JButton back;         //      返回按钮

    public EmployeeManagementPanel(MainFrame parent){
        this.parent=parent;
        init();
        addListener();
    }
    private class EmpTable extends JTable{
        public EmpTable(TableModel dm){
            super(dm);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        public boolean isCellEditable(int row,int column){
            return false;
        }

        public TableCellRenderer getDefaultRenderer(Class<?> columnClass){
            DefaultTableCellRenderer cr=(DefaultTableCellRenderer) super.getDefaultRenderer(columnClass);
            cr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
            return cr;
        }
    }

    public  void init(){
        parent.setTitle("员工管理");
        add=new JButton("录入新员工");
        delete=new JButton("删除员工");
        back=new JButton("返回");

        model=new DefaultTableModel();
        String columnName[]={"员工编号","员工名称"};
        int count= Session.EMP_SET.size();
        String value[][]=new String[count][2];
        Iterator<Employee> it=Session.EMP_SET.iterator();
        for(int i=0;it.hasNext();i++){
            Employee e=it.next();
            value[i][0]=String.valueOf(e.getId());
            value[i][1]=e.getName();
        }
        model.setDataVector(value,columnName);
        table=new EmpTable(model);
        JScrollPane scroll=new JScrollPane(table);
        setLayout(new BorderLayout());
        add(scroll,BorderLayout.CENTER);

        JPanel bottom=new JPanel();
        bottom.add(add);
        bottom.add(delete);
        bottom.add(back);
        add(bottom,BorderLayout.SOUTH);
    }

    private void addListener(){
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.setPanel(new AddEmployeePanel(parent));
            }
        });

        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectRow=table.getSelectedRow();
                if(selectRow!=-1){
                    int deleteCode=JOptionPane.showConfirmDialog(parent,"确定删除该员工?","提示!",JOptionPane.YES_NO_OPTION);
                    if(deleteCode==JOptionPane.YES_OPTION){
                        String id=(String) model.getValueAt(selectRow,0);
                        HRService.deleteEmp(Integer.parseInt(id));
                        model.removeRow(selectRow);
                    }
                }
            }
        });
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.setPanel(new MainPanel(parent));
            }
        });
    }
}
