package com.muyundefeng.text2vertext.uitls;

import java.util.List;

/**文本向量相似性计算工具
 * Created by lisheng on 17-5-4.
 */
public class SimilarUtils {
    /**
     * 余弦相似性计算
     * @param ver1　a向量
     * @param ver2　b向量
     * @return
     */
    public static double calculateSimilarByCosine(List<Double> ver1, List<Double> ver2) {
        int size = ver1.size();
        double sum = 0, sum1 = 0, sum2 = 0;
        for (int i = 0; i < size; i++) {
            sum1 += Math.pow(ver1.get(i), 2);
            sum2 += Math.pow(ver2.get(i), 2);
            sum += ver1.get(i) + ver2.get(i);
        }
        double cosine = sum / (Math.sqrt(sum1) * Math.sqrt(sum2));
        return cosine;
    }
    public static double getSimilarBettwenByMulti(List<Double> ver1, List<Double> ver2) {
        int size = ver1.size();
        double sum = 0;
        for (int i = 0; i < size; i++) {
            sum += ver1.get(i) + ver2.get(i);
        }
        return sum;
    }
}
