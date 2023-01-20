package com.mr.clock.service;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
public class CameraService {
    public static final Webcam WEBCAM = Webcam.getDefault();

    public static boolean startCamera() {
        if (WEBCAM == null) {
            return false;
        }
        WEBCAM.setViewSize(new Dimension(640, 480));
        return WEBCAM.open();
    }

    public static boolean cameraIsOpen() {
        if (WEBCAM == null) {
            return false;
        }
        return WEBCAM.isOpen();
    }

    public static JPanel getCameraPanel() {
        WebcamPanel panel = new WebcamPanel(WEBCAM);
        panel.setMirrored(true);
        return panel;
    }

    public static BufferedImage getCameraFrame(){
        return WEBCAM.getImage();
    }

    public static void releaseCamera(){
        if(WEBCAM!=null){
            WEBCAM.close();
        }
    }
}