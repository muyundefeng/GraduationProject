package com.muyundefeng.text2vertext;

import com.muyundefeng.text2vertext.uitls.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.provider.certpath.Vertex;

import java.io.*;
import java.net.URI;
import java.util.*;

/**
 * Created by lisheng on 17-4-26.
 */
//public class FindCoreTermMapper extends Mapper<NullWritable, Cluster, NullWritable, Cluster> {
public class FindCoreTermMapper {


    public static String path = PropertiesUtil.getSaveWOrdsPath();

    public static List<String> terms = new ArrayList<String>();

    //保存全局cluster
    public static Map<String, Cluster> clusters = new HashMap<String, Cluster>();//更新聚类


    public static String coreTerm = null;

    public static final double TRESH_HOLD = 100;

    public static int cluster_number = 1;

    public static List<Vector> vectors = new ArrayList<Vector>();

    public static final String path1 = "/home/lisheng/桌面/output4/part-r-00000";

    public static int bound = 1000;

    public static GenerateLabelUtils generateLabelUtils = new GenerateLabelUtils(bound);

    public static Logger logger = LoggerFactory.getLogger(FindCoreTermMapper.class);

    @SuppressWarnings("Duplicates")
    protected void start() throws IOException, InterruptedException {
        initCluster();
        logger.info("init cluster");
//        FileSystem fs = FileSystem.get(URI.create(path), context.getConfiguration());
//        FSDataInputStream inputStream = fs.open(new Path(path));
//        String content = IOUtils.toString(inputStream, "utf-8");
//        String words[] = content.split("#");
//        for (String word : words) {
//            terms.add(word);
//        }
//        org.apache.hadoop.io.IOUtils.closeStream(inputStream);
        FileInputStream is = new FileInputStream("/home/lisheng/words.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader in = new BufferedReader(isr);
        String line1 = null;
        String content = "";
        while ((line1 = in.readLine()) != null) {
            content += line1;
        }
        String words[] = content.split("#");
        for (String word : words) {
            terms.add(word);
        }
        logger.info("starting find core term and cluster urls!");
        while (true) {
            //记录原始的rmCut标准
            double originalRmCut = calRMCut(clusters);
            Map<String, Cluster> clusterMap = findCore(clusters);
            clusters = clusterMap;
            logger.info("clusters number is=" + clusters.toString().split("\\[\\{").length);
            logger.info("originalRmCut=" + originalRmCut);
            double newRmCut = calRMCut(clusterMap);
            System.out.println("newRmCut=" + newRmCut);
            if (Math.abs(originalRmCut - newRmCut) > 7 && Math.abs(originalRmCut - newRmCut) <30 ) {
                logger.info("final clusters is:" + clusters);
                logger.info("cluster number =" + cluster_number);
                break;
            }
        }
    }

    public Map<String, Cluster> findCore(Map<String, Cluster> clusters) {
        Map<String, Cluster> myCloneClusters = clone(clusters);
        for (Map.Entry entry : clusters.entrySet()) {
            double minRmCut = Double.MAX_VALUE;
            String label = entry.getKey().toString();//原始的聚类标签

            //得到该聚类中的所有关键词
            //下面分裂该聚类
            Cluster clusterK = ((Cluster) (entry.getValue()));//得到该聚类
            //得到聚类中的所有向量
            List<Vector> vertexts = clusterK.getVertextes();
            Set<String> terms = new HashSet<String>();
            //得到所有向量中的单词词汇
            for (Vector vertext : vertexts) {
                String words1[] = ProcessUrlUtils.getTermsArrayFromUrl(vertext.getUrl().toString());
                for (String word : words1) {
                    terms.add(word);
                }
            }
//                Map<String, Cluster> bisect = null;
            Cluster globalK1 = null;
            Cluster globalK2 = null;

            //分析该聚类中的所有单词
            for (String term : terms) {
                List<Vector> list = new ArrayList<Vector>();
                List<Vector> anotherList = new ArrayList<Vector>();
                for (Vector vertext : vertexts) {
                    if (ProcessUrlUtils.getTermsListFromUrl(vertext.getUrl().toString()).contains(term)) {
                        list.add(vertext);
                    } else {
                        anotherList.add(vertext);
                    }
                }
                //根据单词是否存在的情况之下，一个聚类产生两个新的聚类，构建新的两个聚类
                Map<String, Cluster> cloneClusters = clone(clusters);
                cloneClusters.remove(label);//移除去掉原来的聚类
                //clusterK聚类一分为二，产生一个新的聚类集合
                Cluster clusterk1 = new Cluster();
                clusterk1.setVertext(list);
                clusterk1.setCoreTerm(term);//设定核心词汇
                Cluster clusterk2 = new Cluster();
                clusterk2.setVertext(anotherList);
                clusterk2.setCoreTerm("not " + term);//非该聚类，不设定核心词汇，前缀用not来代替
                String label1 = (bound++) + "";
                String label2 = (bound++) + "";
                cloneClusters.put(label1, clusterk1);//为新的聚类打标签
                cloneClusters.put(label2, clusterk2);
                double value = calRMCut(cloneClusters);

                //因为要在关键词中寻找最合适的核心词汇，需要遍历整个单词，从中选出合适的单词
                if (value < minRmCut) {
                    logger.info("find a proper term meet min RMcut");
//                        coreTerm = term;
//                        selectedCoreTerm = term;
                    minRmCut = value;
                    globalK1 = clusterk1;
                    globalK2 = clusterk2;

//                        bisect = new HashMap<String, Cluster>();
//                        bisect.put(label1, clusterk1);
//                        bisect.put(label2, clusterk2);
//                        cluster_number++;
//                        clusters.remove(label);
//                        clusters.put(label1, clusterk1);
//                        clusters.put(label2, clusterk2);
                }
            }
            logger.info("globalK1=" + globalK1);
            logger.info("globalK2=" + globalK2);
            if (globalK1 != null && globalK2 != null) {
                myCloneClusters.remove(label);
                myCloneClusters.put((bound++) + "", globalK1);
                myCloneClusters.put((bound++) + "", globalK2);
            }
        }
        return myCloneClusters;
    }

//    @Override
//    protected void map(NullWritable key, Cluster value, Context context) throws IOException, InterruptedException {
////        clusters.put(GenerateLabelUtils.getLabel(), value);
//    }
//
//    /**
//     * cleanup 函数要循环执行，直到小于相关的阈值
//     *
//     * @param context
//     * @throws IOException
//     * @throws InterruptedException
//     */
//    @Override
//    protected void cleanup(Context context) throws IOException, InterruptedException {
//
//
//        //写入文件，非写入文件
////        context.write(null, cb);
////        context.write(null, cb1);
//    }

    public double calRMCut(Map<String, Cluster> clusters) {

        List<Cluster> clusterList = new ArrayList<Cluster>();//临时变量，复制，不影响原来的cluster
        List<Cluster> tmp = new ArrayList<Cluster>();//临时变量，复制，不影响原来的cluster
        for (Map.Entry entry : clusters.entrySet()) {
            Cluster cluster = (Cluster) entry.getValue();
            clusterList.add(cluster);
            tmp.add(cluster);
        }
//        List<Cluster> tmp = clusterList;
        double sum = 0;
        for (Cluster cluster : clusterList) {
            //确定ck
            Cluster ck = cluster;
            //c-ck
            tmp.remove(ck);
            List<Cluster> c_ck = tmp;

//            logger.info("calCulateMolecule=" + calCulateMolecule(ck, c_ck));
//            logger.info("calCulateDenominator=" + calCulateDenominator(ck));
            sum += Math.log10(calCulateMolecule(ck, c_ck)) / Math.log10(calCulateDenominator(ck));
            tmp.add(ck);//还原tmp链表
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
        List<Vector> vertextList = ck.getVertextes();
        for (Vector vertext : vertextList) {
            List<Double> ver = vertext.getVertext();//得到单个文本向量
            for (Cluster clusterL : c_ck) {
                List<Vector> vertexts = clusterL.getVertextes();//得到某个非ck聚类
                for (Vector vertext1 : vertexts) {//得到某个非ck聚类中单个文本向量
                    List<Double> anotherVertext = vertext1.getVertext();
//                    sum += getSimilarBettwenText(ver, anotherVertext);
                    sum += SimilarUtils.getSimilarBettwenByMulti(ver, anotherVertext);

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
        List<Vector> vertexts = cluster.getVertextes();
        int size = vertexts.size();
        for (int i = 0; i < size; i++) {
            Vector di = vertexts.get(i);
            for (int j = 0; j < size; j++) {
                Vector dj = vertexts.get(j);
                sum += SimilarUtils.getSimilarBettwenByMulti(di.getVertext(), dj.getVertext());
            }

        }
//        logger.info("sum=" + sum);
        return sum / 2;
    }

    /**
     * 初始化聚类
     *
     * @throws IOException
     */
    @SuppressWarnings("Duplicates")
    public static void initCluster() throws IOException {
        FileInputStream is = new FileInputStream(path1);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader in = new BufferedReader(isr);
        String line = null;
        while ((line = in.readLine()) != null) {
            JSONObject jsonObject = new JSONObject(line);
            JSONArray jsonArray = (JSONArray) jsonObject.get("vector");
            Iterator<Object> iterator = jsonArray.iterator();
            List<Double> vectext = new ArrayList<Double>();
            while (iterator.hasNext()) {
                double ele = (Double) iterator.next();
                vectext.add(ele);
            }
            String url = jsonObject.get("url").toString();
            Vector vector = new Vector(new Text(url), vectext);
            vectors.add(vector);
        }
        clusters.put(generateLabelUtils.getLabel(), new Cluster(vectors, "null"));
    }

    /**
     * 克隆一个 clusters
     *
     * @param clusters
     * @return
     */
    public static Map<String, Cluster> clone(Map<String, Cluster> clusters) {

        Map<String, Cluster> cloneClusters = new HashMap<String, Cluster>();
        for (Map.Entry entry : clusters.entrySet()) {
            cloneClusters.put(entry.getKey().toString(), (Cluster) entry.getValue());
        }
        return cloneClusters;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FindCoreTermMapper findCoreTermMapper = new FindCoreTermMapper();
        findCoreTermMapper.start();
    }
}
