package com.muyundefeng.text2vertext.uitls;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by lisheng on 17-4-27.
 */
public class GenerateLabelUtils {

    public final static int RANGE = (int) Math.pow(10, 7);

    public final static Set<String> set = new HashSet<String>();

    public static String getLabel() {
        String label = label();
        while (set.add(label)) {
            break;
        }
        return label;
    }

    public static String label() {
        return new Random(RANGE).toString();
    }
}
