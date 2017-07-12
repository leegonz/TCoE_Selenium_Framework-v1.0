package auto.framework;

import java.io.File;
//import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
//import java.util.Properties;
import java.util.concurrent.TimeUnit;

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

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.TestNG;
//import org.testng.annotations.Listeners;
import org.testng.asserts.Assertion;
import org.testng.asserts.SoftAssert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import auto.framework.TestBase.Regex;
import auto.framework.web.WebControl;

//@Listeners({ReportLog_ForDeletion.Listener.class})
public class ReportLog_ForDeletion {
	
	public static class Status {
		
		public static final String PASSED = "Passed";
		public static final String FAILED = "Failed";
		public static final String NOT_APPLICABLE = "N/A";
		public static final String NOT_AUTOMATED = "Not Automated";
		
	}
	
	public static class TestCase {
		
		public void setStatus(String status){ //TODO
			
		}
		
	}
	
//	private final static String path_stylesheet=WebManager.class.getClass().getResource("/reportlog/stylesheet.xsl").getFile().replace("%20", " ");
	private final static String path_reports="./src/test/resources/reports/";
	public static class Listener extends  TestNG.ExitCodeListener implements ISuiteListener {
		
		private DateFormat timestamp = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa");
		
		@Override
	    public void onTestStart(ITestResult result) {
			
			Reporter.setCurrentTestResult(result);
			
//			String unloadedPref = result.getTestContext().getCurrentXmlTest().getParameter("execute");
//			Boolean executeScript = Boolean.valueOf(TestManager.Preferences.getPreference("execute","true")) && (unloadedPref==null || Boolean.valueOf(unloadedPref));
			
			String executePref = TestManager.Preferences.getPreference("execute","true");
			Boolean executeScript = Boolean.valueOf(executePref);
			if(!executeScript && executePref!=null ) {
				String var = Regex.findMatch(executePref, "(.*?)(?=\\[)");
				String label = Regex.findMatch(executePref, "(?<=\\[)(.*?)(?=\\])");
				//System.out.println(var+" : "+label);
				if(var!=null && label!=null){
					String varValue = TestManager.Preferences.getPreference(var,"");
					if(Regex.matches(varValue, "^(?<=\\\")(.*?)(?=\\\")$")){
						varValue = varValue.substring(1, varValue.length());
					}
					String[] varValues = varValue.split(",");
					executeScript = Arrays.asList(varValues).contains(label);
				}
			}
			
			if(executeScript){				
				ReportFileHandler reporter = new ReportFileHandler.IndividualReport();
				ReportLog_ForDeletion.fileHandler.set(reporter);			
				softAssert.set(new SoftAssert());			
				ReportLog_ForDeletion.setTestName(result.getTestContext().getName());	
				ReportFileHandler.consolidated.addReport(reporter);
				reporter.setSummarySection();
				reporter.setConsolidatedSummarySection();	
				reporter.setSummary("Time Started", timestamp.format(new Date()));
				reporter.setSummary("Time Ended", "N/A");
				reporter.setSummary("Time Elapsed", "N/A");	
				try {
					WebDriver driver = WebManager.getDriver();
					Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
					//System.out.println("Running: [" + WebManager.getProxySettings().getProxyType() +"] "+ caps.getBrowserName() + " "+caps.getVersion());
					reporter.setSummary("Browser",caps.getBrowserName() + " "+caps.getVersion());
				} catch (Exception e){
				}
			}
			
	    }
		
		/*
		 * (non-Javadoc)
		 * @see org.testng.TestNG.ExitCodeListener#onTestFailure(org.testng.ITestResult)
		 */
		@Override
		  public void onTestFailure(ITestResult tr) {
			Throwable error = tr.getThrowable();
			if(error instanceof SkipException) return;
			
		    if (error!=null) {
		    	failed(error.getClass().getSimpleName() + " : " + error.getMessage());
		    	if (error instanceof NoSuchElementException){
			    	WebControl.takeScreenshot();
			    } else if (error instanceof StaleElementReferenceException){
			    	WebControl.takeScreenshot();		    		    
			    } else if (!(error instanceof AssertionError)) {
			    	WebControl.takeScreenshot();		    		    
			    }
		    } 
		    
		    try {
				WebManager.endDriver();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		    saveSingleReport();
		    
		  }
		
		private void saveSingleReport(){
			ReportFileHandler reporter = ReportLog_ForDeletion.fileHandler.get();
		    reporter.setSummary("Time Ended", timestamp.format(new Date()));
			reporter.setSummary(
					"Time Elapsed", 
					String.format(
							"%.2f mins",
							(float)(Reporter.getCurrentTestResult().getEndMillis() - Reporter.getCurrentTestResult().getStartMillis()
									)/(60 * 1000)));
		    ReportLog_ForDeletion.save();
			ReportFileHandler.consolidated.save();
		}
		
		@Override
		  public void onTestSuccess(ITestResult tr) {						
			try{
				ReportLog_ForDeletion.assertAll();
			} catch(AssertionError error){
				tr.setThrowable(error);
				tr.setStatus(ITestResult.FAILURE);				
			} finally {
				saveSingleReport();
			}
			
		  }

		@Override
		public void onFinish(ISuite arg0) {
			// TODO Auto-generated method stub
			ReportFileHandler.consolidated.save();
		}

		@Override
		public void onStart(ISuite arg0) {
			// TODO Auto-generated method stub
			
		}

	}
	
	//TODO consolidated
	private static ThreadLocal<Assertion> hardAssert = new InheritableThreadLocal<Assertion>(){
        @Override
        protected Assertion initialValue() { 
        	return new Assertion(){
        	/*	@Override
        		public void onAssertFailure(IAssert assertCommand, AssertionError error) {
        			System.err.println("Assertion Error: "+error.getMessage()); 
        		}	*/
        	};
        }
    };
	private static ThreadLocal<SoftAssert> softAssert = new InheritableThreadLocal<SoftAssert>(){
        @Override
        protected SoftAssert initialValue() { return new SoftAssert(); }
    };
	
	protected static ThreadLocal<ReportFileHandler> fileHandler = new InheritableThreadLocal<ReportFileHandler>(){
        @Override
        protected ReportFileHandler initialValue() { return new ReportFileHandler.IndividualReport(); }
    };
    
    private static ThreadLocal<Boolean> logEvents = new InheritableThreadLocal<Boolean>(){
        @Override
        protected Boolean initialValue() { return true; }
    };
    
    public static Boolean logEvents() {
		return logEvents.get();
	}
    
    public static void logEvents(Boolean value) {
		logEvents.set(value);
	}

	private static ReportFileHandler getFileHandler() {
		return fileHandler.get();
	}
	
	private static class ReportFileHandler {
		
		public static class IndividualReport extends ReportFileHandler {
			
			private static DateFormat dateFormatResult = new SimpleDateFormat("yyyyMMddHHmmss");
			
			@Override
			public String createFilePath(String fileName) {
				ReportFileHandler.consolidated.addReport(this);
				String generatedFilePath = super.createFilePath(dateFormatResult.format((new Date())) + " " + fileName + ".html");
				log("test: " + generatedFilePath );
				return generatedFilePath;
			}
			
			@Override
			protected String getReportDir(){
				String ReportPath = path_reports+"tests/";// + dateFormatReport.format(new Date()) + " Runs";
				File dir = new File(ReportPath);	//  + dateFormatReport.format(new Date())	
				dir.mkdir();
				return dir.getAbsolutePath();
			}
			
		}
		
		private static class ConsolidatedReport extends ReportFileHandler {
			
			public ConsolidatedReport(){
				String generatedFilePath = createFilePath("report.html");
				log("report: " + generatedFilePath );
			}
			
			private static ArrayList<ReportFileHandler> reports = new ArrayList<ReportFileHandler>();
			
			/**
			 * <blockquote>THIS method will add a report in the form of an 
			 * {@linkplain ReportFileHandler} which must be passed as a param</blockquote>
			 * 
			 * @param report - {@link ReportFileHandler} object that caller wants to add as a report
			 */
			protected void addReport(ReportFileHandler report){
				if(report==this) return;
				if(!reports.contains(report)) reports.add(report);
			}
			
			/*
			 * (non-Javadoc)
			 * @see auto.framework.ReportLog_ForDeletion.ReportFileHandler#getReportDir()
			 */
			@Override
			protected String getReportDir(){
				String ReportPath = path_reports+"consolidated/";// + dateFormatReport.format(new Date()) + " Runs";
				File dir = new File(ReportPath);	//  + dateFormatReport.format(new Date())	
				dir.mkdir();
				return dir.getAbsolutePath();
			}
			
			/*
			 * (non-Javadoc)
			 * @see auto.framework.ReportLog_ForDeletion.ReportFileHandler#save()
			 */
			@Override
			public void save() {
				createFilePath(this.fileName);
				Iterator<ReportFileHandler> iter1 = reports.iterator();
				Iterator<ReportFileHandler> iter2 = reports.iterator();
				Iterator<ReportFileHandler> iter3 = reports.iterator();
																								
				while(iter1.hasNext()){
					Node SSNode = iter1.next().getSSNode();
					if(SSNode!=null) {
						Node summarySection = xmlStream.importNode(SSNode,true);
						getRootNode().appendChild(summarySection);						
					}else{
						continue;
					}
				}							
							
				while(iter3.hasNext()){							
					Node TIOSSNode = geTIOSSNode();
					Node TOSSNode = iter3.next().geTOSSNode();					
					if(TOSSNode!=null) {						
						Node conSummarySection = xmlStream.importNode(TOSSNode,true);
						if(conSummarySection!=null) {
							TIOSSNode.appendChild(conSummarySection.getFirstChild());
						}												
					} else {
						continue;					
					}
				}
				
				while(iter2.hasNext()){
					Node scenario = xmlStream.importNode(iter2.next().getTSNode(),true);					
					getRootNode().appendChild(scenario);
				}
				super.save();
			}
			
		}
		
		protected static ConsolidatedReport consolidated = new ConsolidatedReport();
		
		private DocumentBuilderFactory docBuilderFactory;
		private DocumentBuilder xmlReportBuilder;
		protected Document xmlStream;	
		private Element rootElement;
		private XPath xPath;
			
		private static DateFormat dateFormatTimeStamp = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa");
		//private static DateFormat dateFormatReport = new SimpleDateFormat("[yyyy-MM-dd]");
		
		protected String filePath;
		protected String fileName;
		
		protected String getReportDir(){
			String ReportPath = path_reports;// + dateFormatReport.format(new Date()) + " Runs";
			File dir = new File(ReportPath);	//  + dateFormatReport.format(new Date())	
			dir.mkdir();
			return dir.getAbsolutePath();
		}
		
		public String createFilePath(String fileName) {

			this.fileName = fileName.trim();
			
			try {
				this.filePath = (new File(getReportDir() + "/" + this.fileName)).getCanonicalPath();
			} catch (IOException e1) {
				this.filePath = (new File(getReportDir() + "/" + this.fileName)).getAbsolutePath();
			}
			
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
			
			return filePath;
		}
		
		protected String getFilePath(){
			if(filePath==null) return createFilePath(fileName!=null ? fileName : "Undefined");
			return filePath;
		}
		
		public void save() {
			try {
/*				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(xmlStream);
				StreamResult result = new StreamResult(new File(getFilePath() + ".xml"));
		 
				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
		 
				transformer.transform(source, result);
				
				System.out.println("File saved!"); */
				
				
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
			 	String outputFileName = getFilePath();

				DOMSource source = new DOMSource(xmlStream);
				//Source xslDoc = new StreamSource(path_stylesheet);
				//Transformer xslTansform = transformerFactory.newTransformer(xslDoc);
								
			
//				String styleSheet = "/reportlog/stylesheet.xsl"; 
//				
//				String configPath = "./src/test/resources/config/defaults.properties";
//					File configFile = new File( configPath );
//					if(configFile.exists()){
//						FileInputStream fileInput = new FileInputStream(configFile);
//						Properties properties = new Properties();
//						try {
//							properties.load(fileInput);
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						
//						String dataPath = properties.getProperty("styleSheet");
//						if(dataPath!=null){								
//								styleSheet = "/reportlog/" + dataPath;
//							} else {
//								styleSheet = "/reportlog/stylesheet.xsl";
//							}
//						
//					}
			
				String styleSheetFile = TestManager.Preferences.getPreference("styleSheet", "/reportlog/stylesheet.xsl");
				String styleSheet = Resources.findResource(styleSheetFile);
				if(styleSheet==null){
					styleSheet = Resources.findResource("/reportlog/"+styleSheetFile);
				}
				
				//Source stylesheet = new StreamSource(WebManager.class.getClass().getResourceAsStream(styleSheet));
				Source stylesheet = new StreamSource(new File(styleSheet));
				Transformer xslTansform = transformerFactory.newTransformer(stylesheet);
											
				
			/*	
				Transformer transformer = transformerFactory.newTransformer();				
				//File xmlOutputFileName = new File( outputFileName );				
				StreamResult result = new StreamResult(new File("./sample.xml"));			     	
			    transformer.transform(source, result);		*/				
				
				new File(outputFileName).getParentFile().mkdirs();
				OutputStream htmlFile = new FileOutputStream(outputFileName);
	            xslTansform.transform(source, new StreamResult(htmlFile));
				
				/*TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				StringWriter writer = new StringWriter();
				transformer.transform(new DOMSource(rootElement), new StreamResult(writer));
				String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
				System.out.println(output);
				
				log("save to: " + getFilePath() ); */
				
			} catch (TransformerException | FileNotFoundException e){
				e.printStackTrace();
			}
		}
		
		public synchronized Node createTSNode(String name){
			Node node = createNode(getRootNode(),"test-scenario");
			setAttribute(node,"name",name);
			log("[Test Scenario] %s\n", name);
			return node;
		}
		
		public synchronized Node createSSNode(String name){
			Node node = createNode(getRootNode(),"summary-section");
			setAttribute(node,"name",name);
			log("[Summary Section] %s\n", name);
			return node;			
		}
		
		public synchronized Node createTCNode(String name){
			Node node = createNode(getTSNode(),"test-case");
			setAttribute(node,"name",name);
			log("\t[Test Case] %s\n", name);
			return node;
		}
		
		public synchronized Node createOSSNode(String name){
			Node node = createNode(getRootNode(),"consolidated-summary-section");
			setAttribute(node,"name",name);
			//node.appendChild(createNode(node, "overall-summary"));
			return node;
		}
		
		public synchronized Node createStepNode(String name){
			Node node = createNode(getTCNode(),"test-step");
			setAttribute(node,"name",name);
			log("\t\t[Test Step] %s\n", name);
			return node;
		}
		/*
		public synchronized Node createSummary(String name){
			Node node = createNode(getRootNode(),"summary");
			setAttribute(node,"name",name);
			log("[Summary] %s\n", name);
			return node;
		}*/
		
		public synchronized Node createScreenshotNode(String name, String base64){
			String timestamp = dateFormatTimeStamp.format(new Date());
			Node node = createNode(getStepOrTCNode(),"screenshot");
			setAttribute(node,"name", name);
			setAttribute(node,"base64", base64);
			setAttribute(node,"timestamp", timestamp);
			log("\t\t%s | %s | %s \n", timestamp, "Screenshot", name);
			return node;
		}
		
		public synchronized Node createAttachNode(String name, String file){
			String timestamp = dateFormatTimeStamp.format(new Date());
			Node node = createNode(getStepOrTCNode(),"attachment");
			setAttribute(node,"file", file);
			setAttribute(node,"name", name);
			setAttribute(node,"timestamp", timestamp);
			log("\t\t%s | %s | %s \n", timestamp, "Attachment", file);
			return node;
		}
	/*	
	<xsl:template match="attachment">
		<div class="line"><span class="label">Attachment</span> : <a>
		  <xsl:attribute name="href">
			<xsl:value-of select="@file"/>
		  </xsl:attribute>
		  <xsl:value-of select="@name"/>
		</a></div>
	</xsl:template>*/
		
		protected void setSummarySection(){
			createSSNode("summary-section");
		}
		
		protected void setConsolidatedSummarySection(){
			createOSSNode("summary-section");
		}
		
		protected void setSummary(String name, String desc){
			Node summary = getTestSummary();
			Node summaryField = selectSingleNode("//test-scenario[last()]//summary//info[@name='"+name+"']");
			if(summaryField==null){
				summaryField = createNode(summary,"info");
				setAttribute(summaryField,"name", name);
			}
			setAttribute(summaryField,"desc", desc);
			//log("\t\t%s : %s\n", name, desc);
		}
		
		protected void setSummarySectionDetails(String validation, String name, String expected, String actual, String status){
			Node summary = getTestSummarySection(validation,actual);
			
			Node summaryField = selectSingleNode("//summary-section//overall-summary//info[@name='"+name+"']");
			if(summaryField==null){
				summaryField = createNode(summary,"info");
				setAttribute(summaryField,"name", name);
			}
			setAttribute(summaryField,"exp", expected);
			setAttribute(summaryField,"act", actual);
			setAttribute(summaryField,"status", status);
			//log("\t\t%s : %s\n", name, desc);
		}
		
		protected void setConsolidatedSummarySectionDetails(String name, String expected, String actual, String status){
			Node summary = getTestConsolidatedSummarySection(actual);
			
			Node summaryField = selectSingleNode("//consolidated-summary-section//overall-summary//info[@name='"+name+"']");
			if(summaryField==null){
				summaryField = createNode(summary,"info");
				setAttribute(summaryField,"name", name);
			}
			
			setAttribute(summaryField,"exp", expected);
			setAttribute(summaryField,"act", actual);
			setAttribute(summaryField,"status", status);
			//log("\t\t%s : %s\n", name, desc);
		}
				
		protected void addInfo(String name, String desc){
			Node details = getTestDetails();
			Node detailsField = createNode(details,"info");
			setAttribute(detailsField,"name", name.trim());
			setAttribute(detailsField,"desc", desc);
			log("\t\t%s : %s\n", name, desc);
		}
		
		public void logEvent(String status, String description){
			String timestamp = dateFormatTimeStamp.format(new Date());
			//Node node = createNode(getStepNode(),"report");
			Node node = createNode(getStepOrTCNode(),"report");
			//Node node = createNode(getTCNode(),"report");
			setAttribute(node,"desc", description);
			setAttribute(node,"status", status);
			setAttribute(node,"timestamp", timestamp);
			if(status=="Failed"){
				logE("\t\t%s | %s | %s\n", timestamp, status, description);
			} else {
				log("\t\t%s | %s | %s\n", timestamp, status, description);
			}
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
		
		protected synchronized Node getSSNode(){
			Node node = selectSingleNode("//summary-section");
			return node;													
		}
		
		protected synchronized Node getCSSNode(){
			Node node = selectSingleNode("//consolidated-summary-section");
			return node!=null ? node : createNode(getRootNode(),"consolidated-summary-section");
		}
		
		protected synchronized Node geTOSSNode(){
			Node node = selectSingleNode("//consolidated-summary-section/overall-summary");
			return node;													
		}
		
		protected synchronized Node geTIOSSNode(){
			Node node = selectSingleNode("//consolidated-summary-section");
			return node!=null ? node : createOSSNode("Consolidated Summary");	
		}
		
		private synchronized Node getTCNode(){
			Node node = selectSingleNode("//test-case[last()]");
			return node!=null ? node : createTCNode("Undefined");
		}
		
		private synchronized Node getStepOrTCNode(){
			Node node = selectSingleNode("//test-case[last()]//test-step[last()]");
			return node!=null ? node : getTCNode();
		}
		
		protected synchronized Node getTestSummary(){
			Node node = selectSingleNode("//test-scenario[last()]//summary");
			return node!=null ? node : createNode(getTSNode(),"summary");
		}
		
		protected synchronized Node getTestDetails(){
			Node node = selectSingleNode("//test-scenario[last()]//details");
			return node!=null ? node : createNode(getTSNode(),"details");
		}

		protected synchronized Node getTestSummarySection(String name, String act){
			Node node = selectSingleNode("//summary-section//overall-summary[@name='"+name+"']");
			if(node==null ) {
				node = createNode(getSSNode(),"overall-summary");
				setAttribute(node,"name",name);
				setAttribute(node,"act",act);
				return node;
			} else {
				return node;
			}			
		}

		protected synchronized Node getTestConsolidatedSummarySection(String act){
			Node node = selectSingleNode("//consolidated-summary-section//overall-summary");
			if(node==null ) {
				node = createNode(getCSSNode(),"overall-summary");				
				setAttribute(node,"act",act);
				return node;
			} else {
				return node;
			}			
		}
		
		
//		private synchronized Node getStepNode(){
//			Node node = selectSingleNode("//test-case[last()]//test-step[last()]");
//			return node!=null ? node : createStepNode("Undefined");
//		}
		
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
	
	public static void setTestName(String testName){	
		getFileHandler().createFilePath(testName);
		getFileHandler().createSSNode(testName);
		getFileHandler().createTSNode(testName);
		System.out.println("Test: " + testName );
	}
	
	public static void setSummaryReport(String validationCategory, String validatioinType, String expected, String actual, String status){
		getFileHandler().setSummarySectionDetails(validationCategory, validatioinType, expected, actual, status);								
		
	}
	
	public static void setConsolidatedSummaryReport(String validatioinType, String expected, String actual, String status){
		getFileHandler().setConsolidatedSummarySectionDetails(validatioinType, expected, actual, status);								
		
	}
	
	public static TestCase setTestCase(String testCase){
		getFileHandler().createTCNode(testCase);
		return new TestCase();
	}
	
	public static void setTestStep(String testStep){
		getFileHandler().createStepNode(testStep);
	}
	
	public static void setSummaryDetails(String Summary){		
		//getFileHandler().logEvent( "info" , Summary);
		getFileHandler().setSummary("Test", Summary);
	}
	
	public static void setSummaryDetails(String Name, String Summary){		
		getFileHandler().setSummary(Name, Summary);
	}
	
	public static void addInfo(String Info){
		getFileHandler().logEvent( "Info" , Info);
	}
	
	public static void addDescription(String name, String desc){
		//temporary code
		getFileHandler().addInfo(name, desc);
	}
	
	public static void attachScreenshot(String name, String base64){
		getFileHandler().createScreenshotNode(name,base64);
	}
	
	public static void attachFile(String name, String file){
		getFileHandler().createAttachNode(name,file);
	}
	
	public static void verifyManual(String description){
		getFileHandler().logEvent( "No Run" , description);
	}
	
	public static Boolean verifyTrue(Boolean status,String description){
		getFileHandler().logEvent( (status ? "Passed" : "Failed") , description);
		softAssert.get().assertTrue(status,description);
		return status;
	}
	
	public static Boolean assertTrue(Boolean status,String description){
		getFileHandler().logEvent( (status ? "Passed" : "Failed") , description);
		hardAssert.get().assertTrue(status,description);
		return status;
	}
	
	public static void logEvent(Boolean status,String description){
		//System.out.println( "Event | " + description);
		if(logEvents()){
			verifyTrue(status,description);
		}
	}
	
	public static void assertAll(){
		softAssert.get().assertAll();
	}
	
	public static void passed(String Description){
		verifyTrue(true,Description);
	}
	
	public static void failed(String Description){
		verifyTrue(false,Description);
	
	}
	
	public static void assertFailed(String Description){
		assertTrue(false,Description);
	}
	
	public static void warning(String Description){
		getFileHandler().logEvent( "Warning" , Description);
	}
	
	public static void save(){
		getFileHandler().save();
	}
	
	public static void getScreenshot(String App) throws Exception {
		 
	       WebDriver driver = WebManager.getDriver();
	      
	       String filename = App +"_"+System.currentTimeMillis()+".png";
	       String path = "c:\\test\\"+ filename; // we can change the path according on our preference where to save screenshot file.
	      
	       driver.manage().deleteAllCookies();
	       driver.manage().window().maximize();
	       driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	      
	       File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
	      
	           FileUtils.copyFile(scrFile, new File(path));
	           ReportLog_ForDeletion.attachFile(filename, "file:///"+path);
	    }

}
