package auto.framework.reportlog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.testng.asserts.Assertion;
import org.testng.asserts.SoftAssert;
import org.w3c.dom.Node;

import auto.framework.ReportLog.TestCase;
import auto.framework.ReportLog.TestLog;
import auto.framework.ReportLog.TestRoot;
import auto.framework.ReportLog.TestScenario;
import auto.framework.ReportLog.TestStep;
import auto.framework.web.WebControl;

public class ReportLogInstance {
	
	protected static DateFormat dateFormatTimeStamp = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa");
	
	protected static ThreadLocal<ReportLogInstance> reportInstance = new InheritableThreadLocal<ReportLogInstance>(){
        @Override
        protected ReportLogInstance initialValue() { return new ReportLogInstance(); }
    };
	
	public static ReportLogInstance getInstance(){
		return reportInstance.get();
	}
	
	public static void setInstance(ReportLogInstance report){
		reportInstance.set(report);
	}
	
	//private static DateFormat dateFormatResult = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private Assertion hardAssert = new Assertion(){
        	/*	@Override
        		public void onAssertFailure(IAssert assertCommand, AssertionError error) {
        			System.err.println("Assertion Error: "+error.getMessage()); 
        		}	*/
        	};
        	
	private SoftAssert softAssert = new SoftAssert();
		
	protected ReportXmlFactory xmlFactory = new ReportXmlFactory();

	protected TestRoot rootElement;
	protected TestScenario testScenario;
	protected Boolean logEvents = true;
	
//		public TestCase testCase;
//		public TestStep testStep;
	
	public ReportLogInstance(){
		rootElement = new TestRoot(xmlFactory.createElement("root"), xmlFactory);
		xmlFactory.appendChild(rootElement.node());
		
		testScenario = new TestScenario(xmlFactory.createElement("test-scenario"), xmlFactory);
		//testScenario.setAttribute("name", name);
		rootElement.appendChild(testScenario);
	}
	
	public TestScenario setTestName(String name){
		return testScenario.setName(name);
	}
	
	public TestCase setTestCase(String name){
		return testScenario.addTestCase(name);
	}
	
	public TestStep setTestStep(String name){
		TestCase currentCase = testScenario.getCurrentCase()!=null ? testScenario.getCurrentCase() : testScenario.addTestCase("Default");
		return currentCase.addTestStep(name);
	}
	
	public TestLog logEvent(Boolean status, String description){
		return getCurrentStep().logEvent(status, description);
	}
	
	public TestLog logEvent(String status, String description){
		return getCurrentStep().logEvent(status, description);
	}
	
	public TestLog addInfo(String info){
		return getCurrentStep().addInfo(info);
	}
	
	public ReportNode setSummaryDetails(String name, String desc){
		return getCurrentScenario().getSummary().setInfo(name, desc);
	}
	
	public ReportNode addDescription(String name, String desc){
		return getCurrentScenario().getDetails().addInfo(name, desc);
	}
	
	public Boolean verifyTrue(Boolean status,String description){
		logEvent( (status ? "Passed" : "Failed") , description);
		if(!status){
			WebControl.takeScreenshot();
		}
		softAssert.assertTrue(status,description);
		return status;
	}
	
	public Boolean assertTrue(Boolean status,String description){
		logEvent( (status ? "Passed" : "Failed") , description);	
		hardAssert.assertTrue(status,description);
		return status;
	}
	
	public void assertAll(){
		softAssert.assertAll();
	}
	
    public Boolean logEvents() {
		return logEvents;
	}
    
    public void logEvents(Boolean value) {
		logEvents = value;
	}
	
//		Node details = getTestDetails();
//		Node detailsField = createNode(details,"info");
//		setAttribute(detailsField,"name", name.trim());
//		setAttribute(detailsField,"desc", desc);
//		log("\t\t%s : %s\n", name, desc);
	
	protected TestScenario getCurrentScenario(){
		return testScenario;
	}
	
	protected TestCase getCurrentCase(){
		return testScenario.getCurrentCase()!=null ? testScenario.getCurrentCase() : testScenario.addTestCase("Default");
	}
	
	protected TestStep getCurrentStep(){
		TestCase currentCase = getCurrentCase();
		return currentCase.getCurrentStep()!=null ? currentCase.getCurrentStep() : currentCase.addTestStep(null);
	}
	
	protected ReportNode getSS(){
		Node summaryNode = rootElement.selectSingleNode("//summary-section");
		if(summaryNode==null) {
			summaryNode = rootElement.node().appendChild(rootElement.xmlFactory.createElement("summary-section"));
		}
		return new ReportNode(summaryNode, rootElement.xmlFactory);
	}
	
	protected ReportNode getCSS(){
		Node summaryNode = rootElement.selectSingleNode("//consolidated-summary-section");
		if(summaryNode==null) {
			summaryNode = rootElement.node().appendChild(rootElement.xmlFactory.createElement("consolidated-summary-section"));
		}
		return new ReportNode(summaryNode, rootElement.xmlFactory);
	}
	
	protected ReportNode getOSS(String name){
		ReportNode summarySection = getSS();
		Node ossNode = rootElement.selectSingleNode(".//overall-summary[@name='"+name+"']");
		if(ossNode==null) {
			return summarySection.createChild("overall-summary").setAttribute("name", name);
		} 
		return new ReportNode(ossNode, rootElement.xmlFactory);
	}
	
	public ReportNode setSummaryReport(String validationCategory, String validationType, String expected, String actual, String status){
		return getOSS(validationCategory).createChild("info")
			.setAttribute("name", validationType)
			.setAttribute("exp", expected)
			.setAttribute("act", actual)
			.setAttribute("status", status);
	}
	
	public ReportNode setConsolidatedSummaryReport(String validationType, String expected, String actual, String status){
		return getCSS().createChild("info")
			.setAttribute("name", validationType)
			.setAttribute("exp", expected)
			.setAttribute("act", actual)
			.setAttribute("status", status);
	}
	
	public void attachScreenshot(String name, String base64){
		getCurrentStep().attachScreenshot(name, base64);
	}
	
	public void attachFile(String name, String file){
		getCurrentStep().attachFile(name, file);
	}
	
	//TODO: Work In Progress
	synchronized public void save() {
//		try {
//			//TransformerFactory transformerFactory = TransformerFactory.newInstance();
//			File outputFile = new File("./src/test/resources/reports/consolidated/report2.xml");
//		 	
//			DOMSource source = new DOMSource(xmlFactory.xmlStream);
//			
//			outputFile.getParentFile().mkdirs();
//			OutputStream htmlFile = new FileOutputStream(outputFile.getCanonicalPath());
//            //xslTansform.transform(source, new StreamResult(htmlFile));
//            
//            TransformerFactory tFactory = TransformerFactory.newInstance();
//            Transformer transformer = tFactory.newTransformer();
//            transformer.transform(source,new StreamResult(htmlFile));
//            
//            System.out.println(outputFile.getCanonicalPath());
//		} catch (Exception e){
//			e.printStackTrace();
//		}
	}
	
}