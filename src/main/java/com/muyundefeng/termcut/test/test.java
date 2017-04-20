package com.muyundefeng.termcut.test;

import com.muyundfeng.termcut.core.FindCore;
import com.muyundfeng.termcut.textPreprocess.PreProcessText;

/**
 * 测试类
 *
 * @author lisheng
 */
public class test {

    public static void main(String[] args) {
        String Threshold = 100 + "";
        PreProcessText handle = new PreProcessText();
        String filePaths[] = {"./src/main/resources/a.txt", "./src/main/resources/a1.txt", "./src/main/resources/a2.txt",
                "./src/main/resources/a3.txt", "./src/main/resources/a4.txt", "./src/main/resources/b.txt", "./src/main/resources/c.txt"
                , "./src/main/resources/d.txt", "./src/main/resources/e.txt", "./src/main/resources/f.txt", "./src/main/resources/g.txt", "./src/main/resources/h.txt"};
        for (String path : filePaths) {
            String content = handle.getContent("/home/lisheng/SearchRedis/TermCut" + path.substring(1));
            content = content.replaceAll("the", " ");
            handle.handleText(content, path);
        }
        for (String path : filePaths) {
            String content = handle.getContent("/home/lisheng/SearchRedis/TermCut" + path.substring(1));
            handle.TFStastic(content, path);
        }
        handle.DFStastic();
        handle.buildVertex();
        handle.initCluser();
        FindCore core = new FindCore(Double.parseDouble(Threshold));
        core.initVal(handle.getVertexes(), handle.getDocs(), handle.getGloableCluster());
        core.termCut();
    }
}
