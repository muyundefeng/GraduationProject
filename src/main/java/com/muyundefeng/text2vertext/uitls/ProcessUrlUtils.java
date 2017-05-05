package com.muyundefeng.text2vertext.uitls;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lisheng on 17-5-4.
 */
public class ProcessUrlUtils {

    public static String[] getTermsArrayFromUrl(String line){
        line = line.replace(StopWordsDict.HOST_NAME, "");
        line = line.replaceAll(StopWordsDict.TRIPLE_W, "").replaceAll(StopWordsDict.SUFFIX, "")
                .replaceAll(StopWordsDict.DATE, "").replaceAll(StopWordsDict.PROTOCAL, "")
                .replaceAll("\\d", "").replaceAll(StopWordsDict.HTML, "").replace(StopWordsDict.HTM,"").replaceAll(StopWordsDict.LINE, "")
                .replaceAll("&", "").replaceAll("_", "");
        String words[] = StringUtils.split(line, StopWordsDict.SEPARATOR);
        return words;
    }

    public static List<String> getTermsListFromUrl(String line){
        return Arrays.asList(getTermsArrayFromUrl(line));
    }
}
