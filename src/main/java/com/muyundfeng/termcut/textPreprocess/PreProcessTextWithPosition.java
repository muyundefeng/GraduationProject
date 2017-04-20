package com.muyundfeng.termcut.textPreprocess;

import com.muyundefeng.termcut.assitance.LabelGenerator;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * 进行相关的文本预处理，将文本转化为空间向量
 *
 * @author lisheng
 */
public class PreProcessTextWithPosition {

    private List<String> docs = new ArrayList<String>();//存放文档的id

    private Set<String> terms = new HashSet<String>();

    private Map<String, Set<String>> idToContens = new HashMap<String, Set<String>>();

    private Map<String, List<Double>> vertexes = new HashMap<String, List<Double>>();//存放文档特征向量

    private Map<String, List<Map<Set<String>, List<Double>>>> gloableCluster = null;//初始的聚类

    private Map<String, Map<String, Integer>> tfs = new HashMap<String, Map<String, Integer>>();

    private Map<String, Integer> dfs = new HashMap<String, Integer>();

    private LabelGenerator labels = null;

    public PreProcessTextWithPosition() {
        // TODO Auto-generated constructor stub
        labels = new LabelGenerator(Integer.MAX_VALUE);
    }

    /**
     * @param filePath 输入文件的路径
     * @throws IOException
     * @return　文件内容
     */
    public String getContent(String filePath) {
        File file = new File(filePath);
        String content = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                content = content + temp + " ";
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 得到停用词列表
     *
     * @param filePath
     * @return
     */
    public List<String> getStopWrods() {
        List<String> stopWord = new ArrayList<String>();
        File file = new File("/home/lisheng/SearchRedis/TermCut/src/main/resources/stopWords.json");
        String content = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                content = content + temp + " ";
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String stopWords[] = content.split(",");
        stopWord = Arrays.asList(stopWords);
        return stopWord;
    }

    /**
     * 统计词频
     *
     * @param text  文档的内容
     * @param docId 　文档的标识符
     */
    public void handleText(String text, String docId) {
        docs.add(docId);
        String afterProcessNumber = text.replaceAll("\\d", "");
        String afterProcessStop = afterProcessNumber.replaceAll("\\p{Punct}", " ");

        String temp[] = StringUtils.split(afterProcessStop, " ");
        List<String> words = new ArrayList<String>();
        for (String word : temp) {
            words.add(word);
        }
        words.removeAll(getStopWrods());

        terms.addAll(words);
        Set<String> terms = new HashSet<String>(words);
        idToContens.put(docId, terms);
    }

    public void TFStastic(String text, String docId) {
        String afterProcessNumber = text.replaceAll("\\d", "");
        String afterProcessStop = afterProcessNumber.replaceAll("\\p{Punct}", " ");
        String temp[] = StringUtils.split(afterProcessStop, " ");
        List<String> words = new ArrayList<String>();
        for (String word : temp) {
            words.add(word);
        }
        words.removeAll(getStopWrods());

        Map<String, Integer> termToTf = new HashMap<String, Integer>();
        for (String word : words) {
            int TF = StringUtils.countMatches(afterProcessStop, word);
            termToTf.put(word, TF);
        }
        tfs.put(docId, termToTf);
    }

    /**
     * 统计文档频率
     */
    public void DFStastic() {
        for (String word : terms) {
            int df = 0;
            for (String id : docs) {
                if (idToContens.get(id).contains(word)) {
                    df++;
                }
            }
            dfs.put(word, df);
        }
    }

    /**
     * 构建特征向量
     */
    public void buildVertex() {
        System.out.println(terms.toString());
        for (String doc : docs) {
            List<Double> vertex = new ArrayList<Double>();
            int index = 0;
            for (String word : terms) {
                //先要得到词频
                if (!tfs.get(doc).containsKey(word))
                    vertex.add(index, (double) 0);
                else {
                    int tf = tfs.get(doc).get(word);
                    int df = dfs.get(word);
                    double tf_idf = getWeight(df, tf);
                    vertex.add(index, (double) df);
//					vertex.add(index, tf_idf);
                }
                index++;
            }
            vertexes.put(doc, vertex);
        }
        System.out.println("vertexes=" + vertexes.toString());
    }
//	

    /**
     * tf-idf计算公式
     *
     * @param df  文档频率
     * @param tf  　词频
     * @return tf-idf
     */
    public double getWeight(Integer df, Integer tf) {
        return tf * (Math.log((docs.size() / df)));
    }

    /**
     * 构建初始化的聚类
     */
    public void initCluser() {
        String label = labels.getLabel();
        List<Map<Set<String>, List<Double>>> list = new ArrayList<Map<Set<String>, List<Double>>>();
        for (String docId : docs) {
            Set<String> words = idToContens.get(docId);
            List<Double> vertex = vertexes.get(docId);
            Map<Set<String>, List<Double>> map = new HashMap<Set<String>, List<Double>>();
            map.put(words, vertex);
            list.add(map);
        }
        gloableCluster = new HashMap<String, List<Map<Set<String>, List<Double>>>>();
        gloableCluster.put(label, list);
    }

    //获得私有变量的方法
    public List<String> getDocs() {
        return docs;
    }

    public Set<String> getTerms() {
        return terms;
    }

    public Map<String, Set<String>> getIdToContens() {
        return idToContens;
    }

    public Map<String, List<Double>> getVertexes() {
        return vertexes;
    }

    public Map<String, List<Map<Set<String>, List<Double>>>> getGloableCluster() {
        return gloableCluster;
    }

    public Map<String, Map<String, Integer>> getTfs() {
        return tfs;
    }

    public Map<String, Integer> getDfs() {
        return dfs;
    }

    public LabelGenerator getLabels() {
        return labels;
    }
}
