package com.muyundefeng.myClusterByClass;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 文件名生成器
 * Created by lisheng on 17-5-8.
 */
public class FileNameGenerateUtils {
    private Set<String> set = new HashSet<String>();

    private int bound;

    public FileNameGenerateUtils(int bound) {
        // TODO Auto-generated constructor stub
        this.bound = bound;
    }

    @SuppressWarnings("Duplicates")
    public String getFileName() {
        String fileName = null;
        Random random = new Random();
        int ad = random.nextInt(bound);
        while (true) {
            if (set.add(ad + "")) {
                fileName = ad + "";
                break;
            }
        }
        return fileName;
    }
}
