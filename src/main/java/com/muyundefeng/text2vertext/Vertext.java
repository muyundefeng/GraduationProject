package com.muyundefeng.text2vertext;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自定义向量的表示
 * 数据格式表示如下:
 * url:{1,0,1.....}
 * 其中url表示的本条目的url信息
 * {}表示该url的向量表示.
 * Created by lisheng on 17-4-26.
 */
public class Vertext implements Writable {

    private List<Double> list;//表示一个向量

    private String url;//表示的url信息

    /**
     * @param totalWords   　总共的单词数目
     * @param thisUrlWords 　该条url数目所包含的单词的数目
     * @param url          　该条url
     * @param urlNumbers   　总共的url数目(文档数目)
     * @param map          　统计的单词对应文档数目
     */
    public Vertext(List<String> totalWords, String thisUrlWords[], String url, int urlNumbers, Map<String, Integer> map) {
        for (String words : thisUrlWords) {
            int index = totalWords.indexOf(words);
            if (index != -1) {
                list.add(Math.log((double) (urlNumbers / map.get(words))));
            } else {
                list.add((double) 0);
            }
        }
        this.url = url;
    }

    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeChars(url);
        dataOutput.writeChars(list.toString());
    }

    public void readFields(DataInput dataInput) throws IOException {
        url = dataInput.readLine();
        List<Double> list = new ArrayList<Double>();
        String vertext = dataInput.readLine();
        vertext = vertext.replace("[", "").replace("]", "");
        String strs[] = vertext.split(",");
        for (String str : strs) {
            double double1 = Double.parseDouble(str);
            list.add(double1);
        }
        this.list = list;
    }

    public List<Double> getVertext(){
        return list;
    }

    public String getUrl(){
        return url;
    }
}
