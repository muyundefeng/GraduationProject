package com.muyundfeng.termcut.core;

import com.muyundefeng.termcut.assitance.LabelGenerator;

import java.util.*;
import java.util.Map.Entry;

public class FindCore {

    private Map<String, List<Double>> vertexes = null;//进行相关文本处理的特征向量集

    private List<String> docIds = null;//文档标识符

    private Map<String, List<Map<Set<String>, List<Double>>>> gloableCluster = null;//要重构前面的类，主要是数据结构的变化，预留出接口,在未聚类表示一个大的聚类

    private double currentRMCut = Double.MAX_VALUE;//保存最小的RMCut标准

    private double originalRMCut = Double.MAX_VALUE;//保存上一次的RMCut计算公式

    private double Threshold = 0;//指定相关的阈值

    private LabelGenerator labels = null;

    private int clusterNumber = 0;//记录聚类的数量

    private double lastRMCut = 0;//保存上一个rmcut值

    public FindCore() {
        // TODO Auto-generated constructor stub
        labels = new LabelGenerator(Integer.MAX_VALUE);
        this.Threshold = 0.3;
    }

    public FindCore(double threshold) {
        // TODO Auto-generated constructor stub
        this.Threshold = threshold;
        labels = new LabelGenerator(Integer.MAX_VALUE);
    }

    /**
     * 初始化相关变量
     *
     * @param vertexes       　特征向量
     * @param docIds         　文档标识符
     * @param gloableCluster 　初始聚类
     */
    public void initVal(Map<String, List<Double>> vertexes, List<String> docIds,
                        Map<String, List<Map<Set<String>, List<Double>>>> gloableCluster) {
        this.vertexes = vertexes;
        this.docIds = docIds;
        this.gloableCluster = gloableCluster;
    }

    /**
     * termCut主函数
     */
    public void termCut() {
        while (true) {
            System.out.println("run");
            findCore(gloableCluster);
            if (terminate()) {
//				System.out.println(true);
                clusterNumber++;
                break;
            }
        }
    }

    /**
     * 循环终止的条件
     *
     * @return
     */
    public boolean terminate() {
        double difference = Math.abs(currentRMCut - originalRMCut);
        if (difference != 0.0)
            return difference > Threshold ? true : false;
        else
            return true;
    }

    /**
     * cluster 参数的含义最外层的Map是做好标签的聚类(就是所说的键)，整个map的值是本聚类所包括的所有的特征向量，用一个list存放
     * list里面存放的是一个map，map键为Set表示此特征向量覆盖的词，值为本文档所对应的特征向量
     *
     * @param cluster 已经分配好的聚类标签
     * @return
     */
    public Map<String, List<List<Map<Set<String>, List<Double>>>>> findCore(Map<String, List<Map<Set<String>, List<Double>>>> cluster) {
        String CoreTerm = null; //得到核心词汇
        double MinRMcut = Double.MAX_VALUE;//?
        originalRMCut = calRMCut(cluster);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Cb key---存放coreTerm,value---存放将一个聚类分裂之后的聚类信息
        Map<String, List<List<Map<Set<String>, List<Double>>>>> Cb = null;
        for (Entry<String, List<Map<Set<String>, List<Double>>>> entry : cluster.entrySet()) {
            String label = entry.getKey();//得到某个聚类的类别
            List<Map<Set<String>, List<Double>>> list = entry.getValue();//得到某个聚类

            if (list.size() == 1 || list.size() == 0)
                continue;
            //得到本聚类中的所有单词
            List<Set<String>> list2 = new ArrayList<Set<String>>();
            for (Map<Set<String>, List<Double>> map : list) {
                for (Entry<Set<String>, List<Double>> entry2 : map.entrySet()) {
                    Set<String> words = entry2.getKey();
                    list2.add(words);
                }
            }

            //单词去重
            Set<String> terms = getSet(list2);
            for (String word : terms) {
                //ck分裂之后形成的聚类，这些聚类并没有完成类别标签的选定
                List<Map<Set<String>, List<Double>>> newCluster1 = new ArrayList<Map<Set<String>, List<Double>>>();//分配一个新的的聚类类别
                List<Map<Set<String>, List<Double>>> newCluster2 = new ArrayList<Map<Set<String>, List<Double>>>();//分配一个新的的聚类类别

                //将ck聚类类别进行分裂操作
                Cb = new HashMap<String, List<List<Map<Set<String>, List<Double>>>>>();
                for (Map<Set<String>, List<Double>> map : list) {
                    for (Entry<Set<String>, List<Double>> entry2 : map.entrySet()) {
                        Set<String> words = entry2.getKey();
                        //如果包含term单词，就将聚类c一分为二，其中包含该单词的为聚类一，否则为聚类二
                        if (words.contains(word)) {
                            newCluster1.add(map);
                        } else {
                            newCluster2.add(map);
                        }
                    }
                }
                if (newCluster1.size() == 0 || newCluster2.size() == 0)
                    continue;
                //ck分裂之后的聚类与原始聚类一块参与计算，重构原始的聚类
                Map<String, List<Map<Set<String>, List<Double>>>> tempCluster = clone(cluster);
                tempCluster.remove(label);
                tempCluster.put(labels.getLabel(), newCluster1);
                tempCluster.put(labels.getLabel(), newCluster2);

                //计算RMCut标准
                double RMCut = calRMCut(tempCluster);
                //构建返回的特征向量，因为将一个聚类一分为二，所以用链表存放分割之后的聚类信息
                List<List<Map<Set<String>, List<Double>>>> returnVertex = new ArrayList<List<Map<Set<String>, List<Double>>>>();
                if (RMCut < MinRMcut) {

                    gloableCluster = tempCluster;
                    CoreTerm = word;
                    MinRMcut = RMCut;
                    currentRMCut = MinRMcut;
                    returnVertex.add(newCluster1);
                    returnVertex.add(newCluster2);
                    Cb.put(word, returnVertex);
                }
            }
        }
        System.out.println(originalRMCut);
        System.out.println(currentRMCut);
        System.out.println("globaleCluser =" + gloableCluster);
        return Cb;
    }

    /**
     * 单词库去重
     *
     * @param list
     * @return
     */
    public Set<String> getSet(List<Set<String>> list) {
        Set<String> set = new HashSet<String>();
        for (Set<String> words : list) {
            set.addAll(words);
        }
        return set;
    }

    /**
     * 计算公式标准是计算整个聚类的，而不是针对某一个聚类的
     *
     * @param map 存放所有的特征向量
     * @return
     */
    public double calRMCut(Map<String, List<Map<Set<String>, List<Double>>>> clusters) {
        double returnSum = 0;
        for (Entry<String, List<Map<Set<String>, List<Double>>>> entry : clusters.entrySet()) {
            double sum = 0;
            String label = entry.getKey();//得到某一个聚类的类别
            List<Map<Set<String>, List<Double>>> list1 = entry.getValue();//得到聚类Ck
            double denominator = calDenominator(list1);//计算ck之内的向量内积之和
            for (Entry<String, List<Map<Set<String>, List<Double>>>> entry1 : clusters.entrySet()) {
                String label1 = entry1.getKey();
                List<Map<Set<String>, List<Double>>> list2 = entry1.getValue();//得到c-ck
                if (!label.equals(label1)) {
                    sum += calSimilarity(list1, list2);
                }
            }
            if (denominator != 0.0)
                returnSum += sum / denominator;
        }
        return returnSum;
    }

    /**
     * 类别1与2之间的相似性判断
     * cut(Ck ,C − Ck )计算
     *
     * @param docIds1 　Ck
     * @param docIds2 C − Ck
     * @return
     */
    public double calSimilarity(List<Map<Set<String>, List<Double>>> vertex1, List<Map<Set<String>, List<Double>>> vertex2) {
        double similarity = 0;
        //获得第一个聚类的特征向量集合
        List<List<Double>> lists = new ArrayList<List<Double>>();
        for (Map<Set<String>, List<Double>> map : vertex1)
            for (Entry<Set<String>, List<Double>> entry : map.entrySet()) {
                lists.add(entry.getValue());
            }
        //获得第二个聚类的特征向量集合
        List<List<Double>> lists1 = new ArrayList<List<Double>>();
        for (Map<Set<String>, List<Double>> map : vertex2)
            for (Entry<Set<String>, List<Double>> entry : map.entrySet()) {
                lists1.add(entry.getValue());
            }

        //计算RMCut标准
        for (List<Double> list : lists) {
            for (List<Double> list1 : lists1) {
                similarity += calMulti(list, list1);
            }
        }
        return similarity;
    }

    /**
     * 计算两个特征向量的相似度
     *
     * @param list         特征向量1
     * @param another特征向量2
     * @return
     */
    public double calMulti(List<Double> list, List<Double> another) {
        double sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i) * another.get(i);
        }
        return sum;
    }

    /**
     * 计算分母公式Denominator
     *
     * @return
     */
    public double calDenominator(List<Map<Set<String>, List<Double>>> list1) {
        double sum = 0;
        for (Map<Set<String>, List<Double>> vertex : list1) {
            for (Entry<Set<String>, List<Double>> entry : vertex.entrySet()) {
                List<Double> vertext = entry.getValue();
                for (Map<Set<String>, List<Double>> vertex1 : list1) {
                    for (Entry<Set<String>, List<Double>> entry1 : vertex1.entrySet()) {
                        List<Double> vertext1 = entry1.getValue();
                        for (int i = 0; i < vertex.size(); i++) {
                            //计算向量积
                            sum += (vertext.get(i)) * (vertext1.get(i));
                        }
                    }
                }
            }
        }
        return sum;
    }

    /**
     * @param map 对整个cluster进行深层复制，返回复制后的结果
     * @return
     */
    public Map<String, List<Map<Set<String>, List<Double>>>> clone(Map<String, List<Map<Set<String>, List<Double>>>> map) {
        Map<String, List<Map<Set<String>, List<Double>>>> copyMap = new HashMap<String, List<Map<Set<String>, List<Double>>>>();
        for (Entry<String, List<Map<Set<String>, List<Double>>>> entry : map.entrySet()) {
            copyMap.put(entry.getKey(), entry.getValue());
        }
        return copyMap;
    }

}
