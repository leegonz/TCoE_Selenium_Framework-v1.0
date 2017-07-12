package auto.framework.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SOAP {
			
//	private SOAPMessage storeRequest;
	private static Document xmlStream;	
	
	public Document getXmlStream() {
		return xmlStream;
	}
	
	public RequestXML request() throws Exception{
		return new RequestXML();		
	}

	public SOAP(String source) throws Exception{		
		initializeTemplate(source);
	}
	
	private void initializeTemplate(String source) throws Exception{
		File fpath;	
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;			
		FileInputStream  fis = null;
		fpath = new File(source);				
		fis = new FileInputStream(fpath);			
	    factory = DocumentBuilderFactory.newInstance();
	    builder = factory.newDocumentBuilder();			 		
	    xmlStream  = builder.parse(fis);		
	}
	
		
	public Node selectNode(String expression) throws Exception{
		XPath xPath =  XPathFactory.newInstance().newXPath();
			Node node = (Node) xPath.compile("//*[name()='"+expression+"']").evaluate(xmlStream, XPathConstants.NODE);
			return node;
	}
	
	public NodeList selectNodeList(String expression) throws Exception{
		return xmlStream.getElementsByTagName(expression);
	}
	
	public class RequestXML{
		
		private SOAPMessage msg;
		
		public RequestXML() throws Exception{
			msg = toSOAPMessage();			
		}
		
		@SuppressWarnings("unused")
		public SOAPMessage submitRequest(String strEndpoint) throws Exception, SOAPException {						
			Document responseStream;
			Document requestStream;
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();		
			return soapConnection.call(msg, strEndpoint);
		}
		
	private SOAPMessage toSOAPMessage() throws Exception {				 
			MessageFactory mfactory  = MessageFactory.newInstance();
			return mfactory.createMessage(new MimeHeaders(), toInputStream());			
	}
	
	public SOAPMessage getRequestMessage() throws Exception {				 		
		return msg;		
}

	private InputStream toInputStream() throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(xmlStream);
		Result outputTarget = new StreamResult(outputStream);
		TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);		 
		return new ByteArrayInputStream(outputStream.toByteArray());	
	}	
  }
}
