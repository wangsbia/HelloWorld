package com.mr.clock.util;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class JDBCUtil {
    private static String driver_name;
    private static String username;
    private static String password;
    private static String url;
    private static Connection con=null;
    private static final String CONFIG_FILE="src/com.mr.clock.util/jdbc.properties";

    static {
        Properties pro=new Properties();
        try {
            File config=new File(CONFIG_FILE);
            if(!config.exists()){
                throw new FileNotFoundException("缺少文件:"+config.getAbsolutePath());
            }
            pro.load(new FileInputStream(config));
            driver_name=pro.getProperty("driver_name");
            username=pro.getProperty("username");
            password=pro.getProperty("password");
            url=pro.getProperty("url");
            if(driver_name==null||url==null){
                throw new ConfigurationException("jdbc.properties文件缺少配置信息");
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (ConfigurationException e){
            System.err.println("配置文件获取的内容:[driver_name="+driver_name+"],[username="+username+"],[password="+password+"],[url="+url+"]");
            e.printStackTrace();
        }
    }
    public static Connection getConnection(){
        try {
            if(con==null ||con.isClosed()){
                Class.forName(driver_name);
                con= DriverManager.getConnection(url,username,password);
            }
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return con;
    }

    public static void close(Statement stmt,PreparedStatement ps, ResultSet re){

    }
}
