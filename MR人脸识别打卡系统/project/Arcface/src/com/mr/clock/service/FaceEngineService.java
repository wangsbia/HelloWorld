package com.mr.clock.service;

import com.mr.clock.session.Session;

import javax.naming.ConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.Set;

public class FaceEngineService {
    private static String appId=null;
    private static String sdkKey=null;
    private static FaceEngine faceEngine=null;
    private static String ENGINE_PATH="ArcFace/WIN64";
    private static final String CONFIG_FILE="src/com.mr.clock/config/ArcFace.properties";

    static {
        Properties pro=new Properties();
        File config=new File(CONFIG_FILE);
        try {
            if(!config.exists()){
                throw new FileNotFoundException("缺少文件："+config.getAbsolutePath());
            }
            pro.load(new FileInputStream(config));
            appId=pro.getProperty("app_id");
            sdkKey=pro.getProperty("sdk_key");
            if(appId==null||sdkKey==null){
                throw new ConfigurationException("ArcFace.properties 文件缺少配置信息");
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (ConfigurationException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        File path=new File(ENGINE_PATH);
        faceEngine=new FaceEngine(path.getAbsolutePath());
        int errorCode=faceEngine.activeOnline(appId,sdkKey);
        if(errorCode!=ErrorInfo.MOK.getValue()&&errorCode!=ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()){
            System.err.println("ERROR.ArcFace 引擎激活失败，请检查激活码是否填写错误，或重新联网激活");
        }
        EngineConfiguration engineConfiguration=new FaceEngineService();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(1);
        engineConfiguration.setDetectFaceScaleVal(16);
        FunctionConfiguration functionConfiguration=new FaceEngineService();
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
        errorCode=faceEngine.init(engineConfiguration);
        if(errorCode!=ErrorInfo.MOK.getValue()){
            System.err.println("ERROR:ArcFace 引擎初始化失败");
        }
    }
    public static FaceFeature getFaceFeature(BufferedImage img){
        if(img==null){
            throw new NullPointerException("人脸图像为null");
        }
        BufferedImage face=new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_BGR);
        face.setData(img.getData());
        ImageInfo imageInfo=ImageFactory.bufferedImage2ImageInfo(face);
        List<FaceInfo> faceInfoList=new ArrayList<FaceInfo>();
        faceEngine.detectFaces(imageInfo.getImageData(),imageInfo.getWidth(),imageInfo.getHeight(),imageInfo.getImageFormat(),faceInfoList);
        return faceFeature;
    }

    public static void loadAllFaceFeature(){
        Set<String> keys= Session.IMAGE_MAP.keySet();
        for(String code:keys){
            -BufferedImage image=Session.IMAGE_MAP.get(code);
            FaceFeature faceFeature=getFaceFeature(image);
            Session.FACE_FEATURE_HASH_MAP.put(code,faceFeature);
        }
    }

    public static String detectFace(FaceFeature targetFaceFeature){
        if(targetFaceFeature==null){
            return null;
        }
        Set<String> keys=Session.FACE_FEATURE_MAP.keySet();
        float score=0;
        String resultCode=null;
        for(String code:keys){
            FaceFeature sourceFaceFeature=Session.FACE_FEATURE_MAP.get(code);
            FaceSimilar faceSimilar=new FaceSimilar();
            faceEngine.compareFaceFrature(targetFaceFeature,sourceFaceFeature,faceSimilar);
            if(faceSimilar.getScore()>score){
                score=faceSimilar.getScore();
                resultCode=code;
            }
        }
        if(score>0.9){
            return resultCode;
        }
        return null;
    }

    public static void dispost(){
        faceEngine.unInit();
    }
}
