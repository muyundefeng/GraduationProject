package com.muyundefeng.text2vertext;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by lisheng on 17-5-3.
 */
public class TextToVertextReducer extends Reducer<LongWritable,Vector,NullWritable,Vector> {
    @Override
    protected void reduce(LongWritable key, Iterable<Vector> values, Context context) throws IOException, InterruptedException {
        for(Vector vertext:values){
            System.out.println(vertext.getUrl()+"================");
            context.write(null,vertext);
        }
    }
}
