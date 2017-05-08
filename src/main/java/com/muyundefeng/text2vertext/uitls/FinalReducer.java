package com.muyundefeng.text2vertext.uitls;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Cluster;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by lisheng on 17-5-3.
 */
public class FinalReducer extends Reducer<NullWritable,Cluster,NullWritable,Cluster> {
    @Override
    protected void reduce(NullWritable key, Iterable<Cluster> values, Context context) throws IOException, InterruptedException {
        for(Cluster cluster:values){
            context.write(null,cluster);
        }
    }
}
