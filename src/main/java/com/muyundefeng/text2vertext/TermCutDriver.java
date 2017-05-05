package com.muyundefeng.text2vertext;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * Created by lisheng on 17-5-2.
 */
public class TermCutDriver extends Configured implements Tool {

    public int run(String[] strings) throws Exception {

//        if (strings.length < 3) {
//            System.err.printf("Usage: %s [generic options]<input><output>\n", getClass().getSimpleName());
//            ToolRunner.printGenericCommandUsage(System.err);
//            return -1;
//        }
        String input = "/home/lisheng/桌面/output7/part-r-00000";
        String output = "/home/lisheng/桌面/output4";

        Job job = Job.getInstance(getConf());
        job.setJarByClass(getClass());
//
        FileInputFormat.addInputPath(job, new Path(input));
        FileOutputFormat.setOutputPath(job, new Path(output));
//        ChainMapper.addMapper(job, TextToVertextMapper.class, LongWritable.class, Text.class, NullWritable.class, Vertext.class, getConf());
//        ChainMapper.addMapper(job, InitCluster.class, NullWritable.class, Vertext.class, NullWritable.class, Cluster.class, getConf());
//        ChainMapper.addMapper(job, FindCoreTermMapper.class, NullWritable.class, Cluster.class, NullWritable.class, Cluster.class, getConf());

//        //使用ChainMapper作为多个mapper的链接
//
        //将问本转化为向量　job
        job.setMapperClass(TextToVertextMapper.class);
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(Vector.class);
        job.setReducerClass(TextToVertextReducer.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Vector.class);
        job.waitForCompletion(true);

        //新建一个job主要完成初始化聚类

//        Job job1 = Job.getInstance();
//        FileInputFormat.addInputPath(job1, new Path(input));
//        FileOutputFormat.setOutputPath(job1, new Path(output));
//        job1.setMapperClass(FindCoreTermMapper.class);
//        job1.setOutputKeyClass(NullWritable.class);
//        job1.setOutputValueClass(Cluster.class);

//
//        //初始化聚类，总的来说就一种聚类情况
//        FileInputFormat.addInputPath(job, new Path(strings[1]));
//        FileOutputFormat.setOutputPath(job, new Path(strings[2]));
//
//        job.setMapperClass(InitCluster.class);
//        job.setReducerClass(InitReducer.class);
//
//        job.setOutputKeyClass(NullWritable.class);
//        job.setOutputValueClass(Cluster.class);
//
//        FileInputFormat.addInputPath(job, new Path(strings[2]));
//        FileOutputFormat.setOutputPath(job, new Path(strings[3]));

//        job.setOutputKeyClass(NullWritable.class);
//        job.setOutputValueClass(Cluster.class);
//        job.setMapperClass(FindCoreTermMapper.class);
//        job.setReducerClass(FinalReducer.class);
//
//        job.setOutputKeyClass(NullWritable.class);
//        job.setOutputValueClass(Cluster.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new TermCutDriver(), args);
        System.exit(exitCode);

    }
}
