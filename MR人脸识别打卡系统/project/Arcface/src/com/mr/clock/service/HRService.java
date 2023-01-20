package com.mr.clock.service;

import com.mr.clock.dao.DAO;
import com.mr.clock.dao.DAOMysqlImpl;
import com.mr.clock.pojo.Employee;
import com.mr.clock.pojo.User;
import com.mr.clock.pojo.WorkTime;
import com.mr.clock.session.Session;
import com.mr.clock.util.DateTimeUtil;

import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.util.*;

public class HRService {
    private static final String CLOCK_IN="I";
    private static final String CLOCK_OUT="O";
    private static final String LATE="L";
    private static final String LEFT_EARLY="E";
    private static final String ABSENT="A";
    private static DAO dao= DAOMysqlImpl.DAOFactory.getDAO();

    public static void loadAllEmp(){
        Session.EMP_SET.clear();
        Session.EMP_SET.addAll(dao.getAllEmp());
    }

    public static boolean userLogin(String username,String password){
        User user=new User(username,password);
        if(dao.userLogin(user)){
            Session.user=user;
            return true;
        }else {
            return false;
        }
    }

    public static Employee addEmp(String name, BufferedImage face){
        String code= UUID.randomUUID().toString().replace("-","");
        Employee e=new Employee(null,name,code);
        dao.addEmp(e);
        e=dao.getEmp(code);
        Session.EMP_SET.add(e);
        return e;
    }

    public static void deleteEmp(int id){
        Employee e=getEmp(id);
        if(e!=null){
            Session.EMP_SET.remove(e);
        }
        dao.deleteEmp(id);
        dao.deleteClockInRecord(id);
        ImageService.deleteFaceImage(e.getCode());
        Session.FACE_FEATURE_HASH_MAP.remove(e.getCode());
        Session.RECORD_MAP.remove(e.getId());
    }


    public static Employee getEmp(int id){
        for(Employee e:Session.EMP_SET){
            if(e.getId().equals(id)){
                return e;
            }
        }
        return null;
    }

    public static Employee getEmp(String code){
        for(Employee e:Session.EMP_SET){
            if(e.getCode().equals(code)){
                return  e;
            }
        }
        return null;
    }

    public static void addClockInRecord(Employee e){
        Date now=new Date();
        dao.addCLockInRecord(e.getId(),now);
        if(!Session.RECORD_MAP.containsKey(e.getId())){
            Session.RECORD_MAP.put(e.getId(),new HashSet<>());
        }
        Session.RECORD_MAP.get(e.getId()).add(now);
    }

    public static void loadAllClockInRecord(){
        String record[][]=dao.getAllClockInRecord();
        if(record==null){
            System.err.println("表中无打卡数据");
            return;
        }
        for(int i=0,length=record.length;i<length;i++){
            String r[]=record[i];
            Integer id=Integer.valueOf(r[0]);
            if(!Session.RECORD_MAP.containsKey(id)){
                Session.RECORD_MAP.put(id,new HashSet<>());
            }
            try {
                Date recordDate= DateTimeUtil.dateOf(r[1]);
                Session.RECORD_MAP.get(id).add(recordDate);
            }catch (ParseException e){
                e.printStackTrace();
            }
        }
    }

    public static void loadWorkTime(){
        Session.workTime=dao.getWorkTime();
    }
    public static void updateWorkTime(WorkTime time){
        dao.updateWorkTime(time);
        Session.workTime=time;
    }

    private static Map<Employee,String> getOneDayRecordData(int year,int month,int day){
        Map<Employee,String> record=new HashMap<>();
        Date zeroTime=null,noonTime=null,lastTime=null,workTime=null,closingTime=null;
        try {
            zeroTime=DateTimeUtil.dateOf(year,month,day,"00:00:00");
            noonTime=DateTimeUtil.dateOf(year,month,day,"12:00:00");
            lastTime=DateTimeUtil.dateOf(year,month,day,"23:59:59");
            WorkTime wt=Session.workTime;
            workTime=DateTimeUtil.dateOf(year,month,day,wt.getStart());
            closingTime=DateTimeUtil.dateOf(year,month,day,wt.getEnd());
        }catch (ParseException e){
            e.printStackTrace();
        }

        for(Employee e:Session.EMP_SET){
            String report=" ";
            if(Session.RECORD_MAP.containsKey(e.getId())){
                boolean isAbsent=true;
                Set<Date> lockinSet=Session.RECORD_MAP.get(e.getId());
                for(Date r:lockinSet){
                    isAbsent=false;
                    if(r.before(workTime)||r.equals(workTime)){
                        report+=CLOCK_IN;
                    }
                    if(r.after(workTime) &&r.before(noonTime)){
                        report+=LATE;
                    }
                    if(r.after(noonTime) &&r.before(closingTime)){
                        report+=LEFT_EARLY;
                    }
                }
                if(isAbsent){
                    report=ABSENT;
                }
            }else {
                report=ABSENT;
            }
            record.put(e,report);
        }
        return record;
    }

    public static String getDayReport(int year,int month,int day){
        Set<String> lateSet=new HashSet<>();
        Set<String> leftSet=new HashSet<>();
        Set<String> absentSet=new HashSet<>();
        Map<Employee,String> record=HRService.getOneDayRecordData(year,month,day);
        for(Employee e:record.keySet()){
            String oneRecord=record.get(e);
            if(oneRecord.contains(LATE)&&!oneRecord.contains(CLOCK_IN)){
                lateSet.add(e.getName());
            }
            if(oneRecord.contains(LEFT_EARLY) &&!oneRecord.contains(CLOCK_OUT)){
                leftSet.add(e.getName());
            }
            if(oneRecord.contains(ABSENT)){
                absentSet.add(e.getName());
            }
        }

        StringBuilder report=new StringBuilder();
        int count=Session.EMP_SET.size();
        report.append("----  "+year+"年"+month+"月"+day+"日 ------\n");
        report.append("应到人数: "+count+"\n");
        report.append("缺席人数:"+absentSet.size()+"\n");
        report.append("缺席名单");
        if(absentSet.isEmpty()){
            report.append("(空)\n");
        }else {
            Iterator<String> it=absentSet.iterator();
            while (it.hasNext()){
                report.append(it.next()+" ");
            }
            report.append("\n");
        }
        report.append("迟到人数:"+lateSet.size()+"\n");
        report.append("迟到名单");
        if(lateSet.isEmpty()){
            report.append("(空) \n");
        }else {
            Iterator<String> it=lateSet.iterator();
            while (it.hasNext()){
                report.append(it.next()+" ");
            }
            report.append("\n");
        }
        report.append("早退人数:"+leftSet.size()+"\n");
        report.append("草退名单:");
        if(leftSet.isEmpty()){
            report.append("(空)\n");
        }else {
            Iterator<String> it=leftSet.iterator();
            while (it.hasNext()){
                report.append(it.next()+" ");
            }
            report.append("\n");
        }
        return report.toString();
    }

    public static String[][] getMonthReport(int year,int month){
        int lastDay=DateTimeUtil.getLastDay(year,month);
        int count=Session.EMP_SET.size();
        Map<Employee,ArrayList<String>> reportCollection=new HashMap<>();
        for (int day = 0; day < lastDay; day++) {
            Map<Employee,String> recordOneDay=HRService.getOneDayRecordData(year,month,day);
            for(Employee e:recordOneDay.keySet()){
                if(!reportCollection.containsKey(e)){
                    reportCollection.put(e,new ArrayList<>(lastDay));
                }
                reportCollection.get(e).add(recordOneDay.get(e));
            }
        }
        String report[][]=new String[count][lastDay+1];
        int row=0;
        for(Employee e:reportCollection.keySet()){
            report[row][0]=e.getName();
            ArrayList<String> list=reportCollection.get(e);
            for (int i=0,length=list.size();i<length;i++) {
                report[row][i+1]="";
                String record=list.get(i);
                if(record.contains(ABSENT)){
                    report[row][i+1]="【缺席】";
                }
                else if(record.contains(CLOCK_IN) &&record.contains(CLOCK_OUT)){
                    report[row][i+1]=" ";
                }else {
                    if(record.contains(LATE) &&! record.contains(CLOCK_IN)){
                        report[row][i+1]="【迟到】";
                    }
                    if(record.contains(LEFT_EARLY) &&!record.contains(CLOCK_OUT)){
                        report[row][i+1]="【早退】";
                    }
                    if(!record.contains(LATE)&&!record.contains(CLOCK_IN)){
                        report[row][i+1]="【上班未打卡】";
                    }
                    if(!record.contains(LEFT_EARLY)&&!record.contains(CLOCK_OUT)){
                        report[row][i+1]+="【下班未打卡】";
                    }
                }
            }
            row++;
        }
        return report;
    }
}
