package com.muyundefeng.text2vertext;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by lisheng on 17-5-3.
 */
public class InitReducer extends Reducer<LongWritable, Cluster, NullWritable, Cluster> {
    @Override
    protected void reduce(LongWritable key, Iterable<Cluster> values, Context context) throws IOException, InterruptedException {
        for (Cluster cluster : values) {
            System.out.println(cluster);
            context.write(null, cluster);
        }
    }
}
