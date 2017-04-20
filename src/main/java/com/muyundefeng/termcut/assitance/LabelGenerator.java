package com.muyundefeng.termcut.assitance;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 类别生成器
 *
 * @author lisheng
 */
public class LabelGenerator {

    private Set<String> set = new HashSet<String>();

    private int bound;

    public LabelGenerator(int bound) {
        // TODO Auto-generated constructor stub
        this.bound = bound;
    }

    public String getLabel() {
        String label = null;
        Random random = new Random();
        int ad = random.nextInt(bound);
        while (true) {
            if (set.add(ad + "")) {
                label = ad + "";
                break;
            }
        }
        return label;
    }

    public static void main(String[] args) {

    }
}
