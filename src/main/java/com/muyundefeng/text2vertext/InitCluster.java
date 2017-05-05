package com.muyundefeng.text2vertext;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 初始化聚类，形成一个聚类,开始的时候聚类标签为null
 * Created by lisheng on 17-4-26.
 */
public class InitCluster extends Mapper<LongWritable, Vector, LongWritable, Cluster> {

    public static List<Vector> vectors = new ArrayList<Vector>();

    public static final String path = "/home/lisheng/桌面/output18/part-r-00000";

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        FileInputStream is = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader in = new BufferedReader(isr);
        String line = null;
        while ((line = in.readLine()) != null) {
            JSONObject jsonObject = new JSONObject(line);
            JSONArray jsonArray = (JSONArray) jsonObject.get("vertex");
            Iterator<Object> iterator = jsonArray.iterator();
            List<Double> vectext = new ArrayList<Double>();
            while (iterator.hasNext()) {
                double ele = (Double) iterator.next();
                vectext.add(ele);
            }
            String url = jsonObject.get("url").toString();
            Vector vector = new Vector(new Text(url), vectext);
            vectors.add(vector);
        }
        System.out.println(vectors);
    }

    @Override
    protected void map(LongWritable key, Vector value, Context context) throws IOException, InterruptedException {
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        context.write(new LongWritable(1), new Cluster(vectors, "global"));
    }
}
