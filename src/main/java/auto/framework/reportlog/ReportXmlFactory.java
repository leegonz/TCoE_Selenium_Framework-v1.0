package auto.framework.reportlog;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ReportXmlFactory {
	
	protected DocumentBuilderFactory docBuilderFactory;
	protected DocumentBuilder xmlReportBuilder;
	protected Document xmlStream;	
	protected static XPath xPath = XPathFactory.newInstance().newXPath();
	
	public ReportXmlFactory(){
		refresh();
	}
	
	public void refresh(){
		docBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			xmlReportBuilder= docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		xmlStream = xmlReportBuilder.newDocument();
	}
	
	public Node createElement(String nodeName){
		return xmlStream.createElement(nodeName);
	}
	
	public Node appendChild(Node node){
		return xmlStream.appendChild(node);
	}
	
	protected synchronized Node selectSingleNode(String expression){
		try {
			Node node = (Node) xPath.compile(expression).evaluate(xmlStream, XPathConstants.NODE);
			return node;
		} catch(NullPointerException | XPathExpressionException error){
			return null;
		} 
	}
	
}