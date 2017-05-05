package com.muyundefeng.text2vertext;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class Vector implements WritableComparable<Vector> {

    private List<Double> vertex ;//表示一个向量

    private Text vector;//向量的Text表示

    private Text url;//表示的url信息

    public Vector() {
        super();
    }

    public Vector(Text url,List<Double> vertext){
        this.url = url;
        this.vertex = vertext;
        vector = new Text(vertext.toString());
    }

    /**
     * @param totalWords   　总共的单词数目
     * @param thisUrlWords 　该条url数目所包含的单词的数目
     * @param url          　该条url
     * @param urlNumbers   　总共的url数目(文档数目)
     * @param map          　统计的单词对应文档数目
     *                     <p>
     *                     IDF = 文档总数/包含该单词的文档总数
     */
    public Vector(List<String> totalWords, String thisUrlWords[], String url, int urlNumbers, Map<String, Integer> map) {
        List<String> thisUrlWordsList = Arrays.asList(thisUrlWords);
        vertex = new ArrayList<Double>();
        for (String words : totalWords) {
            int index = thisUrlWordsList.indexOf(words);
            if (index != -1) {
                System.out.println(Math.log((double) (urlNumbers / map.get(words))));
                vertex.add(Math.log((double) (urlNumbers / map.get(words))));
            } else {
                vertex.add((double) 0);
            }
        }
        vector = new Text(vertex.toString());
        this.url = new Text(url);
    }

    public void write(DataOutput dataOutput) throws IOException {
        url.write(dataOutput);
        vector.write(dataOutput);

    }

    public void readFields(DataInput dataInput) throws IOException {
        //获得url字段
        int length = WritableUtils.readVInt(dataInput);
        byte[] bytes = new byte[length];
        dataInput.readFully(bytes, 0, length);
        this.url = new Text(new String(bytes));

        //获得向量字段
        int length1 = WritableUtils.readVInt(dataInput);
        byte[] bytes1 = new byte[length1];
        dataInput.readFully(bytes1, 0, length1);
        this.vector = new Text(new String(bytes1));
    }


    public List<Double> getVertext() {
        return vertex;
    }

    public Text getUrl() {
        return url;
    }


    @Override
    public String toString() {
        return "{" +
                "\"vector\":" + vector +
                ", \"url\":\"" + url +"\""+
                '}';
    }

    public int compareTo(Vector o) {
        return 0;
    }
}
