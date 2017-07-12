package auto.framework.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import auto.framework.WebManager;
import auto.framework.model.BillerDataEntity;
import auto.framework.model.ProfileDataEntity;

public class DataLoggerService {
	
	private static ThreadLocal<dateEntity> dataHandler = new InheritableThreadLocal<dateEntity>(){
        @Override
        protected dateEntity initialValue() { return new dateEntity(); }
	};
	
	private static ArrayList<dateEntity> dataEntries = new ArrayList<dateEntity>();
	
	private static dateEntity getDataHandler() {
		return dataHandler.get();
	}
	
	public void saveAll() throws XPathExpressionException {
		getDataHandler().saveAll();
	}
	
	public void save(String Name) throws XPathExpressionException {
		getDataHandler().save(Name);
	}
	
	public void logData(ProfileDataEntity pData, BillerDataEntity bData){
		
		if(pData!=null){
			getDataHandler().createTSNode(pData.getName());
			getDataHandler().createScenarioCoverageNode(pData.getScenarioCoverage());
			
			getDataHandler().createProfileNode("");
			getDataHandler().createAGNode(pData.getAccountGrouop());
			getDataHandler().createLoginNode(pData.getLogin1());
			if(pData.getLogin2()!=null){
				getDataHandler().createLoginNode(pData.getLogin2());
				getDataHandler().createLiabilityNode(pData.getLiability());		
				getDataHandler().createFANNode(pData.getFan());	
			}
			
		} else{
			getDataHandler().createProfileNode("No Profile was created due to a technical Error");
		}
			
		
		if(bData!=null){
			getDataHandler().createBillerNode("");
			getDataHandler().createBANNode(bData.getBAN());
			
			if(bData.getCnList()==null){
				getDataHandler().createBillerNode("No Biller was created due to a technical Error");				
			} else{
				for(String ctn : bData.getCnList()){
					getDataHandler().createCTNNode(ctn);					
				}
			}
		}else{
			getDataHandler().createBillerNode("No Biller was created due to a technical Error");
		}
		
		dataEntries.add(getDataHandler());		
		
		if(pData==null){
			getDataHandler().save("undefined");
		}else{
			getDataHandler().save(pData.getName());
		}
	};
		
	public void logData(ProfileDataEntity pData){

		if(pData!=null){
			
			String Logins;
			getDataHandler().createTSNode(pData.getName());
			getDataHandler().createScenarioCoverageNode(pData.getScenarioCoverage());
			getDataHandler().createProfileNode("");
			getDataHandler().createAGNode(pData.getAccountGrouop());
									
			if(pData.getLogin2()!=null){
				Logins = pData.getLogin1() + "," + pData.getLogin2();				
			}else{
				Logins = pData.getLogin1();
			}
						
			getDataHandler().createLoginNode(Logins);
			
			getDataHandler().createLiabilityNode(pData.getLiability());
			getDataHandler().createFANNode(pData.getFan());
			getDataHandler().save(pData.getName());
			
		} else{
			getDataHandler().createTSNode("Undefined");			
			getDataHandler().createProfileNode("No Profile was created due to a technical Error");
			getDataHandler().save("Undefined");
		}
			
		dataEntries.add(getDataHandler());		
		
		
	};
	
	@SuppressWarnings("null")
	public void logData(BillerDataEntity bData){
		
		
		if(bData!=null){
			getDataHandler().createTSNode(bData.getName());
			getDataHandler().createScenarioCoverageNode(bData.getScenarioCoverage());							
			getDataHandler().createBillerNode("");
			getDataHandler().createBANNode(bData.getBAN());
			
			if(bData.getCnList().size()==0){
				getDataHandler().createBillerNode("No Biller was created due to a technical Error");				
			} else{
				for(String ctn : bData.getCnList()){
					getDataHandler().createCTNNode(ctn);					
				}
			}
			
			getDataHandler().save(bData.getName());
		}else{
			getDataHandler().createTSNode("Undefined");			
			getDataHandler().createBillerNode("No Biller was created due to a technical Error");
			getDataHandler().save("Undefined");
		}
		
		dataEntries.add(getDataHandler());		
		
	};
	
	
	public void init(){
		dateEntity singleEntry = new dateEntity();
		dataHandler.set(singleEntry);    			
		getDataHandler().initialize();		
	};
	
	
	
	public static class dateEntity {

			private String path_reports="./src/test/resources/reports/";
	    	private DocumentBuilderFactory docBuilderFactory;
			private DocumentBuilder xmlReportBuilder;
			protected Document xmlStream;	
			protected Document xmlConsolidatedStream;
			private Element rootElement;
			private XPath xPath;
			
			protected String filePath;
			protected String fileName;
			
			public void initialize(){
				docBuilderFactory = DocumentBuilderFactory.newInstance();
				xPath =  XPathFactory.newInstance().newXPath();
				
				try {
					xmlReportBuilder= docBuilderFactory.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				xmlStream = xmlReportBuilder.newDocument();
				synchronized (xmlStream) {
					rootElement = xmlStream.createElement("root");	
					xmlStream.appendChild(rootElement);
				}				
			}
			
			public String createFilePath(String fileName) {

				this.fileName = fileName.trim();
				
				try {
					this.filePath = (new File(getReportDir() + "/" + this.fileName)).getCanonicalPath();
				} catch (IOException e1) {
					this.filePath = (new File(getReportDir() + "/" + this.fileName)).getAbsolutePath();
				}
				
				return filePath;
			}
			
			
			public  void saveAll() throws XPathExpressionException{
				
				xmlConsolidatedStream = xmlReportBuilder.newDocument();
				synchronized (xmlConsolidatedStream) {
					rootElement = xmlConsolidatedStream.createElement("root");	
					xmlConsolidatedStream.appendChild(rootElement);
				}
								
				Iterator<dateEntity> iter = dataEntries.iterator();
			
				
				while(iter.hasNext()){
				Document xmlStream = iter.next().xmlStream;
				Node TSNode = xmlConsolidatedStream.importNode((Node) xPath.compile("/root/test-scenario").evaluate(xmlStream, XPathConstants.NODE),true);
				
					if(TSNode!=null) {						
						Node node = (Node) xPath.compile("/root").evaluate(xmlConsolidatedStream, XPathConstants.NODE);
						node.appendChild(TSNode);									
					}else{
						continue;
					}
				}				
				
				
				try {
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
				 	String outputFileName = createFilePath("GeneratedData.html");
				 	String outputXMLFileName = createFilePath("GeneratedTestData.xml");
					DOMSource source = new DOMSource(xmlConsolidatedStream);
				
					String styleSheet = "/reportlog/stylesheet.xsl"; 
						
						String configPath = "./src/test/resources/config/defaults.properties";
							File configFile = new File( configPath );
							if(configFile.exists()){
								FileInputStream fileInput = new FileInputStream(configFile);
								Properties properties = new Properties();
								try {
									properties.load(fileInput);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								String dataPath = properties.getProperty("datastyleSheet");
								if(dataPath!=null){								
										styleSheet = "/reportlog/" + dataPath;
									} else {
										styleSheet = "/reportlog/datastylesheet.xsl";
									}
								
							}
					
					Source stylesheet = new StreamSource(WebManager.class.getClass().getResourceAsStream(styleSheet));
					Transformer xslTansform = transformerFactory.newTransformer(stylesheet);
					
					Transformer transformer = transformerFactory.newTransformer();				
					
					new File(outputXMLFileName).getParentFile().mkdirs();
					StreamResult result = new StreamResult(outputXMLFileName);			     	
				    transformer.transform(source, result);			
					
					new File(outputFileName).getParentFile().mkdirs();
					OutputStream htmlFile = new FileOutputStream(outputFileName);
		            xslTansform.transform(source, new StreamResult(htmlFile));							
					
				} catch (TransformerException | FileNotFoundException e){
					e.printStackTrace();
				}
				
				
			}
			
			
			private String getReportDir(){
				String ReportPath = path_reports+"data/";// + dateFormatReport.format(new Date()) + " Runs";
				File dir = new File(ReportPath);	//  + dateFormatReport.format(new Date())	
				dir.mkdir();
				return dir.getAbsolutePath();
			}
			
			
			public void save(String Name) {
				try {
					
					int randomNum = 1 + (int)(Math.random()*1000000); 
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
				 	String outputFileName = createFilePath("archives/"+Name.replace(" ", "_")+ String.valueOf(randomNum) +".html");
				 	String outputXMLFileName = createFilePath("archives/"+Name.replace(" ", "_")+ String.valueOf(randomNum)+".xml");
					DOMSource source = new DOMSource(xmlStream);
				
					String styleSheet = "/reportlog/stylesheet.xsl"; 
						
						String configPath = "./src/test/resources/config/defaults.properties";
							File configFile = new File( configPath );
							if(configFile.exists()){
								FileInputStream fileInput = new FileInputStream(configFile);
								Properties properties = new Properties();
								try {
									properties.load(fileInput);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								String dataPath = properties.getProperty("styleSheet");
								if(dataPath!=null){								
										styleSheet = "/reportlog/" + dataPath;
									} else {
										styleSheet = "/reportlog/stylesheet.xsl";
									}
								
							}
					
					Source stylesheet = new StreamSource(WebManager.class.getClass().getResourceAsStream(styleSheet));
					Transformer xslTansform = transformerFactory.newTransformer(stylesheet);
					
					Transformer transformer = transformerFactory.newTransformer();				
					
					new File(outputXMLFileName).getParentFile().mkdirs();
					StreamResult result = new StreamResult(outputXMLFileName);			     	
				    transformer.transform(source, result);			
					
					new File(outputFileName).getParentFile().mkdirs();
					OutputStream htmlFile = new FileOutputStream(outputFileName);
		            xslTansform.transform(source, new StreamResult(htmlFile));							
					
				} catch (TransformerException | FileNotFoundException e){
					e.printStackTrace();
				}
			}
			
			public synchronized Node createTSNode(String name){
				Node node = createNode(getRootNode(),"test-scenario");
				setAttribute(node,"name",name);
				log("[Test Data] %s\n", name);
				return node;
			}
			
			
			public synchronized Node createBillerNode(String name){
				Node node = createNode(getTSNode(),"biller-data");
				setAttribute(node,"name",name);
				log("\t[Biller Data] %s\n", name);
				return node;
			}
			
			public synchronized Node createScenarioCoverageNode(String name){
				Node node = createNode(getTSNode(),"scen-coverage");
				setAttribute(node,"name",name);
				log("\t[Scenario Coverage] %s\n", name);
				return node;
			}
			
			public synchronized Node createProfileNode(String name){
				Node node = createNode(getTSNode(),"profile-data");
				setAttribute(node,"name",name);
				log("\t[Profile Data] %s\n", name);
				return node;
			}
			
			public synchronized Node createLoginNode(String name){
			Node node = createNode(getProfileNode(),"profile-login");
			setAttribute(node,"name",name);
			log("\t\t[Login] %s\n", name);
			return node;
			}
			
			public synchronized Node createLiabilityNode(String name){
				Node node = createNode(getProfileNode(),"profile-liability");
				setAttribute(node,"name",name);
				log("\t\t[Liability] %s\n", name);
				return node;
				}
			
			public synchronized Node createFANNode(String name){
				Node node = createNode(getProfileNode(),"profile-FAN");
				setAttribute(node,"name",name);
				log("\t\t[FAN] %s\n", name);
				return node;
				}
			
			public synchronized Node createBANNode(String name){
				Node node = createNode(getBillerNode(),"biller-BAN");
				setAttribute(node,"name",name);
				log("\t\t[BAN] %s\n", name);
				return node;
				}
			
			public synchronized Node createCTNNode(String name){
				Node node = createNode(getBillerNode(),"biller-CTN");
				setAttribute(node,"name",name);
				log("\t\t[CTN] %s\n", name);
				return node;
				}
			
			public synchronized Node createAGNode(String name){
				Node node = createNode(getProfileNode(),"profile-AG");
				setAttribute(node,"name",name);
				log("\t\t[Account Group] %s\n", name);
				return node;
				}

	
			private synchronized Node selectSingleNode(String expression){
				try {
					Node node = (Node) xPath.compile(expression).evaluate(xmlStream, XPathConstants.NODE);
					return node;
				} catch(NullPointerException | XPathExpressionException error){
					return null;
				} 
			}
			
			private synchronized Node createNode(Node parentNode, String nodeName) {
				Node newNode = (Node) xmlStream.createElement(nodeName);		
				return parentNode.appendChild(newNode);
			}
			
			private static void setAttribute(Node node, String attName, String val) {
			    NamedNodeMap attributes = node.getAttributes();
			    Node attNode = node.getOwnerDocument().createAttribute(attName);
			    attNode.setNodeValue(val);
			    attributes.setNamedItem(attNode);
			}
			
			protected synchronized Node getRootNode(){
				return selectSingleNode("/root");
			}
			
			
			
			protected synchronized Node getTSNode(){
				Node node = selectSingleNode("//test-scenario[last()]");
				return node!=null ? node : createTSNode("Undefined");
			}
			
			
		
		
			private synchronized Node getProfileNode(){
				Node node = selectSingleNode("//test-scenario[last()]//profile-data[last()]");
				return node!=null ? node : createProfileNode("Undefined");
			}
			
			private synchronized Node getBillerNode(){
				Node node = selectSingleNode("//test-scenario[last()]//biller-data[last()]");
				return node!=null ? node : createBillerNode("Undefined");
			}	
						
			public void log(String text) {
				System.out.println(text);
			}
			
			public void log(String format, Object ... args) {
				System.out.format(format, args);
			}
			
			public void logE(String format, Object ... args) {
				System.err.format(format, args);
			}
			
		}	
		
}
