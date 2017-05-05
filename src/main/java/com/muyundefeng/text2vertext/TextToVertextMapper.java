package com.muyundefeng.text2vertext;

import com.muyundefeng.text2vertext.uitls.ProcessUrlUtils;
import com.muyundefeng.text2vertext.uitls.PropertiesUtil;
import com.muyundefeng.text2vertext.uitls.StopWordsDict;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
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
public class TextToVertextMapper extends Mapper<LongWritable, Text, LongWritable, Vector> {

    private static List<String> list = new ArrayList<String>();//存放所有单词的记录

    private static Map<String, Integer> wordToDoc = new HashMap<String, Integer>();

    //这里的文档数目是指url数目
    private static int count = 0;

    private String path = PropertiesUtil.getSaveWOrdsPath();

    @Override
    protected void setup(Context context) {
        System.out.println("call------------------");

        //  Path pathes[] = context.getFileClassPaths();
        Path pathes1[] = {new Path("hdfs://localhost:9000/user/hadoop/part-r-00000")};

        for (Path path : pathes1) {
            FileSystem fileSystem = null;
            try {
                fileSystem = FileSystem.get(URI.create(path.toString()), context.getConfiguration());
                FSDataInputStream inputStream = fileSystem.open(path);
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                IOUtils.copyBytes(inputStream, byteStream, 4096, false);//文件指针移动移动到最后
                String content = byteStream.toString();
                org.apache.hadoop.io.IOUtils.closeStream(inputStream);
                String lines[] = content.split("\\n");

                for (String line : lines) {
                    count++;
                    String words[] = ProcessUrlUtils.getTermsArrayFromUrl(line);
                    for (String word : words) {
                        System.out.println(word);
                        if (!list.contains(word))
                            list.add(word);//存放单词的记录
                        if (wordToDoc.containsKey(word)) {
                            wordToDoc.put(word, wordToDoc.get(word) + 1);
                        } else {
                            wordToDoc.put(word, 1);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(count);
        System.out.println(wordToDoc);
        //将所有的单词存放到文件中
        try {
            FileSystem fileSystem = FileSystem.get(URI.create(path), context.getConfiguration());
            OutputStream outputStream = fileSystem.create(new Path(path), new Progressable() {
                public void progress() {
                    System.out.println(".");
                }
            });
            String words = "";
            for (String word : list) {
                System.out.println(word);
                words += word + "#";
            }
            InputStream inputStream = new ByteArrayInputStream(words.getBytes());
            org.apache.hadoop.io.IOUtils.copyBytes(inputStream, outputStream, 4096, true);
            org.apache.hadoop.io.IOUtils.closeStream(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果想要批量处理相关的数据，首先保证mapper映射到不同的位置，如果输出键定义为null,则所有的值会映射到同一个位置，只   会处理第一条
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        if (line.equals("http://www.qq.com/"))
            return;
        String words[] = ProcessUrlUtils.getTermsArrayFromUrl(line);
        context.write(key,new Vector(list, words, value.toString(), count, wordToDoc));
    }
}
