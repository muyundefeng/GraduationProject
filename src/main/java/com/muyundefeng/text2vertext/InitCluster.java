package com.muyundefeng.text2vertext;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 初始化聚类，形成一个聚类,开始的时候聚类标签为null
 * Created by lisheng on 17-4-26.
 */
public class InitCluster extends Mapper<NullWritable, Vertext, NullWritable, Cluster> {

    public static List<Vertext> vertexts = new ArrayList<Vertext>();

    @Override
    protected void map(NullWritable key, Vertext value, Context context) throws IOException, InterruptedException {
        vertexts.add(value);
        context.write(null, new Cluster(vertexts,"null"));
    }
}
