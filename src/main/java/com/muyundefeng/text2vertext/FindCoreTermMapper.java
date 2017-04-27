package com.muyundefeng.text2vertext;

import com.muyundefeng.text2vertext.uitls.GenerateLabelUtils;
import com.muyundefeng.text2vertext.uitls.PropertiesUtil;
import com.muyundefeng.text2vertext.uitls.StopWordsDict;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Created by lisheng on 17-4-26.
 */
public class FindCoreTermMapper extends Mapper<NullWritable, Cluster, NullWritable, Cluster> {

    public static String path = PropertiesUtil.getSaveWOrdsPath();

    public static List<String> terms = new ArrayList<String>();

    //保存全局cluster
    public static Map<String, Cluster> clusters = new HashMap<String, Cluster>();

    public static double minRmCut = Double.MIN_VALUE;

    public static String coreTerm = null;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        FileSystem fs = FileSystem.get(URI.create(path), context.getConfiguration());
        FSDataInputStream inputStream = fs.open(new Path(path));
        String content = IOUtils.toString(inputStream, "utf-8");
        String words[] = content.split("#");
        for (String word : words) {
            terms.add(word);
        }
        org.apache.hadoop.io.IOUtils.closeStream(inputStream);
    }

    @Override
    protected void map(NullWritable key, Cluster value, Context context) throws IOException, InterruptedException {
        clusters.put(GenerateLabelUtils.getLabel(), value);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        Cluster cb = new Cluster();
        Cluster cb1 = new Cluster();
        for (Map.Entry entry : clusters.entrySet()) {
            String label = entry.getKey().toString();//得到该聚类的标签
            //得到该聚类中的所有关键词
            Cluster cluster = ((Cluster) (entry.getValue()));//得到该聚类
            //得到聚类中的所有向量
            List<Vertext> vertexts = cluster.getVertextes();
            Set<String> terms = new HashSet<String>();
            //得到所有向量中的单词词汇
            for (Vertext vertext : vertexts) {
                String url = vertext.getUrl();
                String afterUrl = url.replaceAll(StopWordsDict.PROTOCAL, "").replaceAll(StopWordsDict.DATE, "")
                        .replaceAll(StopWordsDict.SUFFIX, "").replaceAll(StopWordsDict.TRIPLE_W, "");
                String words[] = afterUrl.split(StopWordsDict.SEPARATOR);
                for (String word : words) {
                    terms.add(word);
                }
            }
            //分析该聚类中的所有单词
            for (String term : terms) {
                List<Vertext> list = new ArrayList<Vertext>();
                List<Vertext> anotherList = new ArrayList<Vertext>();
                for (Vertext vertext : vertexts) {
                    if (vertext.getUrl().contains(term)) {
                        list.add(vertext);
                    } else {
                        anotherList.add(vertext);
                    }
                }
                //根据单词是否存在的情况之下，一个聚类产生两个新的聚类，构建新的两个聚类
                clusters.remove(cluster);//移除去掉原来的聚类
                //新的聚类一分为二，产生一个新的聚类集合
                Cluster clusterk1 = new Cluster();
                clusterk1.setVertext(list);
                clusterk1.setCoreTerm("null");
                Cluster clusterk2 = new Cluster();
                clusterk2.setVertext(anotherList);
                clusterk2.setCoreTerm("null");
                clusters.put(GenerateLabelUtils.getLabel(), clusterk1);//为新的聚类打标签
                clusters.put(GenerateLabelUtils.getLabel(), clusterk2);

                if (calRMCut(clusters) < minRmCut) {
                    coreTerm = term;
                    minRmCut = calRMCut(clusters);
                    cb.setVertext(list);
                    cb.setCoreTerm(coreTerm);
                    cb1.setCoreTerm(coreTerm + "-except");
                    cb1.setVertext(anotherList);
                }
            }
        }
        //写入文件
        context.write(null, cb);
        context.write(null, cb1);
    }

    public double calRMCut(Map<String, Cluster> clusters) {
        List<Cluster> clusterList = new ArrayList<Cluster>();
        for (Map.Entry entry : clusters.entrySet()) {
            Cluster cluster = (Cluster) entry.getValue();
            clusterList.add(cluster);
        }
        List<Cluster> tmp = clusterList;
        double sum = 0;
        for (Cluster cluster : clusterList) {
            //确定ck
            Cluster ck = cluster;
            //c-ck
            tmp.remove(ck);
            List<Cluster> c_ck = tmp;
            tmp = clusterList;//还原tmp链表
            sum += calCulateMolecule(ck, c_ck) / calCulateDenominator(ck);
        }
        return sum;
    }

    /**
     * 计算聚类之间的关系
     * 就是计算标准公式中的分子
     *
     * @param ck
     * @param c_ck
     * @return
     */
    public static double calCulateMolecule(Cluster ck, List<Cluster> c_ck) {
        double sum = 0;
        //获取聚类ck中的向量集合
        List<Vertext> vertextList = ck.getVertextes();
        for (Vertext vertext : vertextList) {
            List<Double> ver = vertext.getVertext();//得到单个文本向量
            for (Cluster clusterL : c_ck) {
                List<Vertext> vertexts = clusterL.getVertextes();//得到某个非ck聚类
                for (Vertext vertext1 : vertexts) {//得到某个非ck聚类中单个文本向量
                    List<Double> anotherVertext = vertext1.getVertext();
                    sum += getSimilarBettwenText(ver, anotherVertext);
                }
            }
        }
        return sum;
    }

    /**
     * 计算向量文本之间的相似性
     *
     * @param ver1
     * @param ver2
     * @return
     */
    public static double getSimilarBettwenText(List<Double> ver1, List<Double> ver2) {
        int size = ver1.size();
        double sum = 0;
        for (int i = 0; i < size; i++) {
            sum += ver1.get(i) + ver2.get(i);
        }
        return sum;
    }

    /**
     * 计算标准公式中的分母，总的来说就是ck聚类内部之间的相似性
     *
     * @param cluster
     */
    public static double calCulateDenominator(Cluster cluster) {
        double sum = 0;
        List<Vertext> vertexts = cluster.getVertextes();
        int size = vertexts.size();
        for (int i = 0; i < size; i++) {
            Vertext di = vertexts.get(i);
            for (int j = 0; j < size; j++) {
                Vertext dj = vertexts.get(j);
                sum += getSimilarBettwenText(di.getVertext(), dj.getVertext());
            }

        }
        return sum / 2;
    }
}
