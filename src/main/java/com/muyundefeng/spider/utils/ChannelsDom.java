package com.muyundefeng.spider.utils;

import com.muyundefeng.spider.pageprocessor.PageProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

/**
 * package the chanel info into a class
 * using the class to load channel info from siteInfo.xml
 * usage:
 * 	provide the channel id,return the searchUrl and PageProcessor of the channel
 * @author buildhappy
 *
 */
public class ChannelsDom {
	private String channelId;
	private String name;
	private String searchUrl;
	private String pageProName = null;
	private PageProcessor pagePro;
	private String homeUrl = null;
	private String urlEncoding = null;
	private String fullPageProName = null;
	private PageProcessor fullPagePro;
	
	//private static Logger logger = Logger.getLogger(ChannelsDom.class);
	
	public ChannelsDom(){

	}
	public ChannelsDom(String channelId){
		this.channelId = channelId;
		load();
	}
	public ChannelsDom createChannelsDom(String channelId){
		this.channelId = channelId;
		if(load() == 1){
			return null;
		}else{
			return new ChannelsDom(channelId);
		}
	}
	public static ChannelsDom getChannelsDomById(String channelId){
		ChannelsDom channelsDom = null;
		try {
			//get websites informations for xml file
			DocumentBuilderFactory dbf = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();

			InputStream in = ChannelsDom.class.getClassLoader()
					.getResourceAsStream("sites.xml");

			// convert the xml file to dom tree
			Document doc = builder.parse(in);
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();
			// get node informations by xpath tool
			XPathExpression exp = xPath.compile("//site[@id='" + channelId
					+ "']/name");
			Node node = (Node) exp.evaluate(doc, XPathConstants.NODE);
			if(node != null){
				channelsDom = new ChannelsDom();
				channelsDom.setChannelId(channelId);

				String s = node.getTextContent();
				channelsDom.setSearchUrl(s.replace("!%!%!%", "&"));
				exp= xPath.compile("//site[@id='" + channelId
						+ "']/urlEncoding");
				node = (Node) exp.evaluate(doc, XPathConstants.NODE);
				String urlEncoding = null;
				if(node != null)
					urlEncoding = node.getTextContent();
				channelsDom.setUrlEncoding(urlEncoding);

				exp = xPath.compile("//site[@id='" + channelId
						+ "']/name");
				node = (Node) exp.evaluate(doc, XPathConstants.NODE);
				if(node != null){
					channelsDom.setName(node.getTextContent());
				}


				exp = xPath.compile("//site[@id='" + channelId
						+ "']/homePage");
				node = (Node) exp.evaluate(doc, XPathConstants.NODE);
				channelsDom.setHomeUrl(node.getTextContent());

				exp = xPath.compile("//site[@id='" + channelId + "']/pageProcessor");
				node = (Node) exp.evaluate(doc, XPathConstants.NODE);
				String pageProName = node.getTextContent();
				channelsDom.setPageProName(pageProName);
				channelsDom.setPagePro(dynamicCreateObjByName(pageProName));
//				exp = xPath.compile("//site[@id='" + channelId + "']/fullPageProcessor");
//				node = (Node) exp.evaluate(doc, XPathConstants.NODE);

				String fullpageProName = null;
				if(null != node){
					fullpageProName = node.getTextContent();
					channelsDom.setFullPageProName(fullpageProName);
					channelsDom.setFullPagePro(dynamicCreateObjByName(fullpageProName));
				}
				return channelsDom;
			}else{
				//logger.warn("could not find the channel for channelId=" + channelId);
				return null;
			}

		} catch (Exception e) {
			//logger.error(e.getStackTrace().toString());
		}
		return channelsDom;
	}
	public int load(){
		try {
			//get websites informations for xml file
			DocumentBuilderFactory dbf = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();

			InputStream in = ChannelsDom.class.getClassLoader()
					.getResourceAsStream("sites.xml");

			// convert the xml file to dom tree
			Document doc = builder.parse(in);
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();
			// get node informations by xpath tool
			XPathExpression exp = xPath.compile("//site[@id='" + channelId
					+ "']/searchUrl");
			Node node = (Node) exp.evaluate(doc, XPathConstants.NODE);
			if(node != null){
				String s = node.getTextContent();
				setSearchUrl(s.replace("!%!%!%", "&"));
				exp= xPath.compile("//site[@id='" + channelId
						+ "']/urlEncoding");
				node = (Node) exp.evaluate(doc, XPathConstants.NODE);
				if(node != null){
					urlEncoding = node.getTextContent();
				}else{
					urlEncoding = null;
				}
				
				exp = xPath.compile("//site[@id='" + channelId
						+ "']/name");
				node = (Node) exp.evaluate(doc, XPathConstants.NODE);
				if(node != null){
					name = node.getTextContent();
				}
				
				exp = xPath.compile("//site[@id='" + channelId
						+ "']/homePage");
				node = (Node) exp.evaluate(doc, XPathConstants.NODE);
				homeUrl = node.getTextContent();
				
				exp = xPath.compile("//site[@id='" + channelId + "']/pageProcessor");
				node = (Node) exp.evaluate(doc, XPathConstants.NODE);
				String pageProName = node.getTextContent();
				this.pageProName = pageProName;
				pagePro = dynamicCreateObjByName(pageProName);
				
				exp = xPath.compile("//site[@id='" + channelId + "']/fullPageProcessor");
				node = (Node) exp.evaluate(doc, XPathConstants.NODE);
				
				String fullpageProName = null;
				if(null != node){
					fullpageProName = node.getTextContent();
					this.fullPageProName = fullpageProName;
					fullPagePro = dynamicCreateObjByName(fullpageProName);
				}
				return 0;
			}else{
				return 1;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 通过类名创建类的对象，
	 * 
	 * @param name:为类的完整名，包括包名
	 * @return
	 * @throws Exception
	 */
	private static PageProcessor dynamicCreateObjByName(String name){

		PageProcessor o = null;
		try{
			Class c;
			c = Class.forName(name);
			o = (PageProcessor) (c.getClassLoader().loadClass(name)).newInstance();
		}catch(Exception e){
			 e.printStackTrace();
		}
		return o;
	}
	
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getSearchUrl() {
		return searchUrl;
	}
	public void setSearchUrl(String searchUrl) {
		this.searchUrl = searchUrl;
	}
	public PageProcessor getPagePro() {
		return pagePro;
	}
	public void setPagePro(PageProcessor pagePro) {
		this.pagePro = pagePro;
	}

	public String getHomeUrl() {
		return homeUrl;
	}
	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}
	
	public String getUrlEncoding() {
		return urlEncoding;
	}
	public void setUrlEncoding(String urlEncoding) {
		this.urlEncoding = urlEncoding;
	}
	
	public static void main(String[] args){
		ChannelsDom dom = new ChannelsDom("1");//load the channel with id=1
		System.out.println("channel id:" + dom.getChannelId());
		System.out.println("serch url:" + dom.getSearchUrl());
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public PageProcessor getFullPagePro() {
		return fullPagePro;
	}
	public void setFullPagePro(PageProcessor fullPagePro) {
		this.fullPagePro = fullPagePro;
	}

	public String getPageProName() {
		return pageProName;
	}

	public void setPageProName(String pageProName) {
		this.pageProName = pageProName;
	}

	public String getFullPageProName() {
		return fullPageProName;
	}

	public void setFullPageProName(String fullPageProName) {
		this.fullPageProName = fullPageProName;
	}

}
