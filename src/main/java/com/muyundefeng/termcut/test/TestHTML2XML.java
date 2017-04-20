package com.muyundefeng.termcut.test;

import org.w3c.tidy.Tidy;

import java.io.*;
import java.net.URL;

public class TestHTML2XML {
    private String url;
    private String outFileName;
    private String errOutFileName;

    public TestHTML2XML(String url, String outFileName, String
            errOutFileName) {
        this.url = url;
        this.outFileName = outFileName;
        this.errOutFileName = errOutFileName;
    }

    public void convert() {
        URL u;
        BufferedInputStream in;
        FileOutputStream out;

        Tidy tidy = new Tidy();

        //Tell Tidy to convert HTML to XML
        tidy.setXmlOut(true);

        try {
            //Set file for error messages
            tidy.setErrout(new PrintWriter(new FileWriter(errOutFileName), true));
            u = new URL(url);

            //Create input and output streams
            in = new BufferedInputStream(u.openStream());
            out = new FileOutputStream(outFileName);

            //Convert files
            tidy.parse(in, out);

            //Clean up
            in.close();
            out.close();

        } catch (IOException e) {
            System.out.println(this.toString() + e.toString());
        }
    }

    public static void main(String[] args) {
    /*
	* Parameters are:
	* URL of HTML file
	* Filename of output file
	* Filename of error file 
	*/
        TestHTML2XML t = new TestHTML2XML("http://www.cnblogs.com/shenba/archive/2009/04/12/1434050.html", "./src/main/resources/p.xml", "./src/main/resources/error.txt");
        t.convert();
    }
}
