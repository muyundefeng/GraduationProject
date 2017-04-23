package com.muyundefeng.spider.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * define a web page
 * Created by lisheng on 17-4-20.
 */
public class Page {

    public String content;
    public Request request;
    public List<Request> requestList;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void addRequests(List<Request> requests) {
        requestList = requests;
    }

    public List<Request> getRequestList() {
        return requestList;
    }

    public void addRequest(String str) {
        if (requestList == null)
            requestList = new ArrayList<Request>();
        else {
            Request request = new Request();
            request.setUrl(str);
            requestList.add(request);
        }
    }
}
