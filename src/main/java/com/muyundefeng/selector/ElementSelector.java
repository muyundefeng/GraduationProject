package com.muyundefeng.selector;

import org.jsoup.nodes.Element;

import java.util.List;

public interface ElementSelector {


    public String select(Element element);


    public List<String> selectList(Element element);

}
