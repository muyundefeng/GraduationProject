package com.muyundefeng.analyselog;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


/**
 * Created by lisheng on 17-4-24.
 */
public class AnalyseLogDriver extends Configured implements Tool {

    public int run(String[] strings) throws Exception {
        if (strings.length < 2) {
            System.err.printf("Usage:%s [generic options]", getClass().getName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }
        Job job = Job.getInstance();
        job.setJarByClass(getClass());
        job.setMapperClass(AnalyseLogMapper.class);
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setReducerClass(AnalyseLogReducer.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job,new Path(strings[0]));
        FileOutputFormat.setOutputPath(job,new Path(strings[1]));
        return job.waitForCompletion(true)?0:1;
    }

    public static void main(String[] args) throws Exception {
        String str[]= {"/home/lisheng/桌面/log.log","/home/lisheng/桌面/output3/"};
        int exitcode = ToolRunner.run(new AnalyseLogDriver(),str);
        System.exit(exitcode);
    }
}