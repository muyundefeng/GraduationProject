package com.muyundefeng.analyselog;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by lisheng on 17-4-24.
 */
public class AnalyseLogMapper extends Mapper<LongWritable,Text,LongWritable,Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        if(line.contains("downloading")){
            String url = line.split("url:")[1];
            context.write(key,new Text(url));
        }
    }
}
