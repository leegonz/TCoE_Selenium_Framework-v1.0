package auto.framework.reportlog;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ReportNode {

	protected final ReportXmlFactory xmlFactory;
	
	protected static XPath xPath = XPathFactory.newInstance().newXPath();
	
	private Node domNode;
	
	public ReportNode(Node node, ReportXmlFactory xmlFactory){
		domNode = node;
		this.xmlFactory = xmlFactory;
	}

	protected Node node(){
		return domNode;
	}
	
	public ReportNode setAttribute(String attributeName, String value){
		NamedNodeMap attributes = domNode.getAttributes();
	    Node attNode = domNode.getOwnerDocument().createAttribute(attributeName);
	    attNode.setNodeValue(value);
	    attributes.setNamedItem(attNode);
		return this;
	}
	
	public ReportNode createChild(String tagName){
		return appendChild(new ReportNode(xmlFactory.xmlStream.createElement(tagName), xmlFactory));
	}
	
	protected ReportNode appendChild(ReportNode node){
		domNode.appendChild(node.node());
		return node;
	}
	
	protected synchronized Node selectSingleNode(String expression){
		try {
			Node node = (Node) xPath.compile(expression).evaluate(domNode, XPathConstants.NODE);
			return node;
		} catch(NullPointerException | XPathExpressionException error){
			return null;
		} 
	}
	
}