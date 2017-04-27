package com.muyundefeng.text2vertext;

import com.muyundefeng.text2vertext.uitls.PropertiesUtil;
import com.muyundefeng.text2vertext.uitls.StopWordsDict;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.Progressable;

import java.io.*;
import java.net.URI;
import java.util.*;

/**
 * 构建文本向量
 * Created by lisheng on 17-4-26.
 */
public class TextToVertext extends Mapper<LongWritable, Text, NullWritable, Vertext> {

    private static List<String> list = new ArrayList<String>();

    private static Map<String, Integer> wordToDoc = new HashMap<String, Integer>();

    //这里的文档数目是指url数目
    private static int count = 0;

    private String path = PropertiesUtil.getSaveWOrdsPath();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Path pathes[] = context.getFileClassPaths();
        for (Path path : pathes) {
            FileSystem fileSystem = FileSystem.get(URI.create(path.toString()), context.getConfiguration());
            FSDataInputStream inputStream = fileSystem.open(path);
            String content = IOUtils.toString(inputStream, "utf-8");
            org.apache.hadoop.io.IOUtils.closeStream(inputStream);
            content = content.replaceAll(StopWordsDict.TRIPLE_W, "").replaceAll(StopWordsDict.SUFFIX, "")
                    .replaceAll(StopWordsDict.DATE, "").replaceAll(StopWordsDict.PROTOCAL, "");
            String lines[] = content.split("\\n");
            for (String line : lines) {
                count++;
                String words[] = line.split(StopWordsDict.SEPARATOR);
                for (String word : words) {
                    if (!list.contains(word))
                        list.add(word);//存放单词的记录
                    if (wordToDoc.containsKey(word)) {
                        wordToDoc.put(word, wordToDoc.get(word) + 1);
                    } else {
                        wordToDoc.put(word, 1);
                    }
                }
            }
        }
        //将所有的单词存放到文件中
        FileSystem fileSystem = FileSystem.get(URI.create(path), context.getConfiguration());
        OutputStream outputStream = fileSystem.create(new Path(path), new Progressable() {
            public void progress() {
                System.out.println(".");
            }
        });
        String words = "";
        for (String word : list) {
            words += word + "#";
        }
        InputStream inputStream = new ByteArrayInputStream(words.getBytes());
        org.apache.hadoop.io.IOUtils.copyBytes(inputStream, outputStream, 4096, true);
        org.apache.hadoop.io.IOUtils.closeStream(outputStream);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String afterUrl = line.replaceAll(StopWordsDict.DATE, "").replaceAll(StopWordsDict.PROTOCAL, "")
                .replaceAll(StopWordsDict.SUFFIX, "").replaceAll(StopWordsDict.TRIPLE_W, "");
        String words[] = afterUrl.split(StopWordsDict.SEPARATOR);
        context.write(null, new Vertext(list, words, line, count, wordToDoc));
    }
}
