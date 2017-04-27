package com.muyundefeng.text2vertext;

import org.apache.hadoop.io.Writable;
import sun.security.provider.certpath.Vertex;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

/**
 * 定义聚类数据结构
 * Created by lisheng on 17-4-26.
 */
public class Cluster implements Writable {


    public List<Vertext> vertext;

    public String coreTerm;

    public Cluster() {

    }

    public Cluster(List<Vertext> vertext, String coreTerm) {
        this.vertext = vertext;
        this.coreTerm = coreTerm;
    }

    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeChars(vertext.toString());
    }


    public void readFields(DataInput dataInput) throws IOException {
    }

    public List<Vertext> getVertextes() {
        return vertext;
    }

    public void setVertext(List<Vertext> vertexts) {
        this.vertext = vertexts;
    }

    public void setCoreTerm(String coreTerm) {
        this.coreTerm = coreTerm;
    }
}
