package com.muyundefeng.text2vertext;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableUtils;
import sun.security.provider.certpath.Vertex;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

/**
 * 定义聚类数据结构
 * Created by lisheng on 17-4-26.
 */
public class Cluster implements WritableComparable<Cluster> {


    public List<Vector> vertext;//属于该聚类的向量

    public Text coreTerm;//刻画该聚类的核心词汇

    public Text cluster;//聚类的text表示

    public Cluster() {

    }

    public Cluster(List<Vector> vertext, String coreTerm) {
        this.vertext = vertext;
        this.coreTerm = new Text(coreTerm);
        cluster = new Text(vertext.toString());
    }

    public void write(DataOutput dataOutput) throws IOException {
        coreTerm.write(dataOutput);
        cluster.write(dataOutput);
    }


    public void readFields(DataInput dataInput) throws IOException {
        //获得coreTerm字段
        int length = WritableUtils.readVInt(dataInput);
        byte[] bytes = new byte[length];
        dataInput.readFully(bytes, 0, length);
        this.coreTerm = new Text(new String(bytes));
        System.out.println(coreTerm);

        //获得向量字段
        int length1 = WritableUtils.readVInt(dataInput);
        byte[] bytes1 = new byte[length1];
        dataInput.readFully(bytes1, 0, length1);
        this.cluster = new Text(new String(bytes1));
        System.out.println(cluster);
    }

    public List<Vector> getVertextes() {
        return vertext;
    }

    public void setVertext(List<Vector> vertexts) {
        this.vertext = vertexts;
    }

    public void setCoreTerm(String coreTerm) {
        this.coreTerm = new Text(coreTerm);
    }

    public int compareTo(Cluster o) {
        return 0;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "vertext=" + vertext +
                ", coreTerm=" + coreTerm +
                ", cluster=" + cluster +
                '}';
    }
}
