package com.mr.clock.service;

import com.mr.clock.session.Session;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageService {
    private static final File FACE_DIR=new File("src/faces");
    private static final String SUFFIX="png";
    public static Map<String, BufferedImage> loadAllImage(){
        if(!FACE_DIR.exists()){
            System.err.println("src\\face\\人脸图像文件夹丢失");
            return null;
        }
        File faces[]=FACE_DIR.listFiles();
        for(File f:faces){
            try {
                BufferedImage img= ImageIO.read(f);
                String fileName=f.getName();
                String code=fileName.substring(0,fileName.indexOf('.'));
                Session.IMAGE_MAP.put(code,img);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }

     public static void saveFaceImage(BufferedImage image,String code){
        try {
            ImageIO.write(image,SUFFIX,new File(FACE_DIR,code+"."+SUFFIX));
            Session.IMAGE_MAP.put(code,image);
        }catch (IOException e){
            e.printStackTrace();
        }
     }

     public static void deleteFaceImage(String code){
        Session.IMAGE_MAP.remove(code);
        File image=new File(FACE_DIR,code+"."+SUFFIX);
        if(image.exists()){
            image.delete();
            System.out.println(image,getAbsoultePath()+"---已删除");
        }
     }
}
