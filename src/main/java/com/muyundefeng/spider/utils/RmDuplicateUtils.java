package com.muyundefeng.spider.utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.muyundefeng.spider.constant.SPIDER_CONSTANT;

import java.nio.charset.Charset;

/**
 * Created by lisheng on 17-4-20.
 */
public class RmDuplicateUtils {

    public static final BloomFilter rmDuplicate = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), SPIDER_CONSTANT.MAX_SPACE_BLOOM);

    public static boolean containElement(String str){
        return rmDuplicate.mightContain(str);
    }

    public static void add(String str){
        rmDuplicate.put(str);
    }
}
