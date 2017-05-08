package com.muyundefeng.text2vertext.uitls;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by lisheng on 17-4-27.
 */
public class GenerateLabelUtils {

    private Set<String> set = new HashSet<String>();

    private int bound;

    public GenerateLabelUtils(int bound) {
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

}
