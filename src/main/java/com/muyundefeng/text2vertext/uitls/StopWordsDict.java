package com.muyundefeng.text2vertext.uitls;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by lisheng on 17-4-26.
 */
public class StopWordsDict {

    public static final String DATE = "\\d{8}";

    public static final String PROTOCAL = "(http:|https:)";

    public static final String TRIPLE_W = "www";

    public static final String SEPARATOR = "/.?=";

    public static final String SUFFIX = "com";

    public static final String HTML = "html";

    public static final String HTM = "htm";

    public static final String LINE = "-";

    public static final String HOST_NAME = "qq.com";

    public static void main(String[] args) {
        String str = "http://chuangshi.qq.com/bk/xh/71186487-1.html?g_f=70016";
        str = str.replaceAll(PROTOCAL,"").replaceAll("\\d","").replaceAll(HTML,"").replaceAll(LINE,"");
        String strs[] = StringUtils.split(str, "/.?=");
        for (String str1 : strs) {
            System.out.println(str1);
        }
    }
}
