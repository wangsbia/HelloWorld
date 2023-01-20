package com.mr.clock.session;

import com.mr.clock.pojo.Employee;
import com.mr.clock.pojo.User;
import com.mr.clock.pojo.WorkTime;
import com.mr.clock.util.JDBCUtil;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Session {
    public static User user=null;
    public static WorkTime workTime=null;
    public static final HashSet<Employee> EMP_SET=new HashSet<>();
    public static final HashMap<String,FaceFeature> FACE_FEATURE_HASH_MAP=new HashMap<>();
    public static final HashMap<String, BufferedImage> IMAGE_MAP=new HashMap<>();
    public static final HashMap<Integer, Set<Date>> RECORD_MAP=new HashMap<>();

    public static void init(){
        ImageService.loadAllImage();
        HRService.loadWorkTime();
        HRService.loadAllEmp();
        HRService.loadAllClockInRecord();
        FaceEngineService.loadAllFaceFeature();
    }

    public static void  dispose(){
        FaceEngineService.dispost();
        CameraService.releaseCamera();
        JDBCUtil.closeConnection();
    }
}
