package com.muyundefeng.text2vertext.uitls;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static Properties pro = null;

    static {
        pro = new Properties();
        InputStream in = PropertiesUtil.class.getResourceAsStream("/base.properties");
        try {
            pro.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSaveWOrdsPath(){
        return pro.get("words.save.path").toString();
    }

    public static void main(String[] args) {
        System.out.println(getSaveWOrdsPath());
    }
}