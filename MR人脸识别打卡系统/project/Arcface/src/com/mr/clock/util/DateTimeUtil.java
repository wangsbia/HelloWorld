package com.mr.clock.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
    public static String timeNow(){
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static String dateNow(){
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static String datetimeNow(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static Integer[] now(){
        Integer []now=new Integer[6];
        Calendar c= Calendar.getInstance();
        now[0]=c.get(Calendar.YEAR);
        now[1]=c.get(Calendar.MONTH)+1;
        now[2]=c.get(Calendar.DAY_OF_MONTH);
        now[3]=c.get(Calendar.HOUR_OF_DAY);
        now[4]=c.get(Calendar.MINUTE);
        now[5]=c.get(Calendar.SECOND);
        return now;
    }

    public static int getLastDay(int year,int month){
        Calendar c=Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month-1);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Date dateof(String datetime) throws ParseException{
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
    }

    public static Date dateOf(int year,int month,int day,String time) throws ParseException{
        String datetime=String.format("%4d-%02d-%02d %s",year,month,day,time);
        return dateof(datetime);
    }

    public static boolean checkTimeStr(String time){
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        try {
            sdf.parse(time);
            return true;
        }catch (ParseException e){
            return false;
        }
    }
}
