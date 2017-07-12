package auto.framework;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.TestNG;
import org.w3c.dom.Node;

import auto.framework.TestBase.Regex;
import auto.framework.reportlog.ReportLogConsolidator;
import auto.framework.reportlog.ReportLogInstance;
import auto.framework.reportlog.ReportNode;
import auto.framework.reportlog.ReportXmlFactory;
import auto.framework.reportlog.TrendReport;
import auto.framework.web.WebControl;

public class ReportLog {//extends ReportLog_ForRevamp {
	
	protected static DateFormat dateFormatTimeStamp = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa");
	
	protected static ReportLogConsolidator reportConsolidator = new ReportLogConsolidator();
	
	protected static ISuite currentSuite;
	
//Static Methods:
    
	public static void logEvents(Boolean value) {
		ReportLogInstance.getInstance().logEvents(value);
	}

	public static Boolean logEvents() {
		return ReportLogInstance.getInstance().logEvents();
	}
    
	public static TestScenario setTestName(String testName){
		return ReportLogInstance.getInstance().setTestName(testName);
	}
	
	public static TestCase setTestCase(String testCase){
    	return ReportLogInstance.getInstance().setTestCase(testCase);
	}
	
	public static TestStep setTestStep(String testStep) {
    	return ReportLogInstance.getInstance().setTestStep(testStep);
	}
	
	public static TestLog logEvent(Boolean status, String description){
		return ReportLogInstance.getInstance().logEvent(status, description);
	}
	
	public static TestLog logEvent(String status, String description){
		return ReportLogInstance.getInstance().logEvent(status, description);
	}
	
	public static void setSummaryReport(String validationCategory, String validationType, String expected, String actual, String status){
		ReportLogInstance.getInstance().setSummaryReport(validationCategory, validationType, expected, actual, status);
	}
	
	public static void setConsolidatedSummaryReport(String validationType, String expected, String actual, String status){
		ReportLogInstance.getInstance().setConsolidatedSummaryReport(validationType, expected, actual, status);
	}
	
	public static ReportNode setSummaryDetails(String name, String desc){	
		return ReportLogInstance.getInstance().setSummaryDetails(name, desc);
	}
	
	public static TestLog addInfo(String info){
		return ReportLogInstance.getInstance().addInfo(info);
	}
	
	public static ReportNode addDescription(String name, String desc){
		return ReportLogInstance.getInstance().addDescription(name, desc);
	}
	
	public static void attachScreenshot(String name, String base64){
		ReportLogInstance.getInstance().attachScreenshot(name, base64);
	}
	
	public static void attachFile(String name, String file){
		ReportLogInstance.getInstance().attachFile(name, file);
	}
	
	public static TestLog verifyManual(String description){
		return logEvent( "No Run" , description);
	}
	
	public static Boolean verifyTrue(Boolean status,String description){
		return ReportLogInstance.getInstance().verifyTrue(status, description);
	}
	
	public static Boolean assertTrue(Boolean status,String description){
		return ReportLogInstance.getInstance().assertTrue(status, description);
	}
	
	public static void assertAll(){
		ReportLogInstance.getInstance().assertAll();
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
		logEvent( "Warning" , Description);
	}
	
	public static void save() {
    	ReportLogInstance.getInstance().save();
	}
	
	@Deprecated public static void getScreenshot(String App) throws Exception {
		
	}
	
	@Deprecated public static ReportNode setSummaryDetails(String desc){	
		return ReportLogInstance.getInstance().setSummaryDetails("Test", desc);
	}

//Public Classes:
	
	public static class Console {
	
		public static void log(String text) {
			System.out.println(text);
		}
		
		public static void log(String format, Object ... args) {
			System.out.format(format, args);
		}
		
		public static void logE(String format, Object ... args) {
			System.err.format(format, args);
		}
		
	}

	public static class Status {
		
		public static final String PASSED = "Passed";
		public static final String FAILED = "Failed";
		public static final String NOT_APPLICABLE = "N/A";
		public static final String NOT_AUTOMATED = "Not Automated";
		
	}
	
	public static class TestRoot extends ReportNode {

		public TestRoot(Node node, ReportXmlFactory xmlFactory) {
			super(node, xmlFactory);
		}
		
	}
	
	public static class ScenarioCaseStep extends ReportNode {

		private SummaryDetails testSummary;
		private SummaryDetails testDetails;
		
		public ScenarioCaseStep(Node node, ReportXmlFactory xmlFactory) {
			super(node, xmlFactory);
		}
		
//		public void setSummaryDetails(String Summary){
//			//getFileHandler().setSummary("Test", Summary);
//		}
//		
//		public void setSummaryDetails(String Name, String Summary){		
//			//getFileHandler().setSummary(Name, Summary);
//		}
		
		public TestLog addInfo(String info){
			String timestamp = dateFormatTimeStamp.format(new Date());
			TestLog testLog = new TestLog(xmlFactory.createElement("report"), xmlFactory);
			testLog.setAttribute("desc", info);
			testLog.setAttribute("status", "Info");
			Console.log("\t\t%s | %s | %s\n", timestamp, "Info", info);
			return (TestLog) appendChild(testLog);
		}
		
		public void addDescription(String name, String desc){
			//getFileHandler().addInfo(name, desc);
		}
		
		public SummaryDetails getSummary(){
			if(testSummary==null) {
				testSummary = new SummaryDetails(xmlFactory.createElement("summary"), xmlFactory);
				return (SummaryDetails) appendChild(testSummary);
			} return testSummary;
		}
		
		public SummaryDetails getDetails(){
			if(testDetails==null) {
				testDetails = new SummaryDetails(xmlFactory.createElement("details"), xmlFactory);
				return (SummaryDetails) appendChild(testDetails);
			} return testDetails;
		}
		
		public static class SummaryDetails extends ReportNode {

			public SummaryDetails(Node node, ReportXmlFactory xmlFactory) {
				super(node, xmlFactory);
			}
			
			public ReportNode addInfo(String name,String desc){
				ReportNode infoNode = new ReportNode(xmlFactory.createElement("info"), xmlFactory);
				infoNode.setAttribute("name", name).setAttribute("desc", desc);
				return appendChild(infoNode);
			}
			
			public ReportNode setInfo(String name,String desc){
				Node infoDom = selectSingleNode(".//info[@name='"+name+"']");
				if(infoDom!=null){
					return new ReportNode(infoDom, xmlFactory).setAttribute("desc", desc);
				} return addInfo(name,desc);
			}
			
		}
		
		public ReportNode attachScreenshot(String name, String base64){
			String timestamp = dateFormatTimeStamp.format(new Date());
			ReportNode node = createChild("screenshot")
				.setAttribute("name", name)
				.setAttribute("base64", base64)
				.setAttribute("timestamp", timestamp);
			Console.log("\t\t%s | %s | %s \n", timestamp, "Screenshot", name);
			return node;
		}
		
		public ReportNode attachFile(String name, String file){
			String timestamp = dateFormatTimeStamp.format(new Date());
			ReportNode node = createChild("attachment")
				.setAttribute("file", file)
				.setAttribute("name", name)
				.setAttribute("timestamp", timestamp);
			Console.log("\t\t%s | %s | %s \n", timestamp, "Attachment", name);
			return node;
		}
		
	}
	
	public static class TestScenario extends ScenarioCaseStep {

		private TestCase currentCase;
		
		public TestScenario(Node node, ReportXmlFactory xmlFactory) {
			super(node, xmlFactory);
		}
		
		public TestScenario setName(String name){
			setAttribute("name", name);
			Console.log("[Test Scenario] %s\n", name);
			return this;
		}
		
		public TestScenario setStatus(String status){
			setAttribute("status", status);
			return this;
		}
		
		public TestCase addTestCase(String name){
			currentCase = new TestCase(xmlFactory.createElement("test-case"), xmlFactory);
			getCurrentCase().setName(name);
			Console.log("\t[Test Case] %s\n", name);
			return (TestCase) appendChild(getCurrentCase());
		}

		public TestCase getCurrentCase() {
			return currentCase;
		}

//		public void setCurrentCase(TestCase currentCase) {
//			this.currentCase = currentCase;
//		}
		
	}
	
	public static class TestCase extends ScenarioCaseStep {
		
		private TestStep currentStep;
		
		public TestCase(Node node, ReportXmlFactory xmlFactory) {
			super(node, xmlFactory);
		}
		
		public TestCase setName(String name){
			setAttribute("name", name);
			return this;
		}
		
		public TestCase setStatus(String status){
			setAttribute("status", status);
			return this;
		}
		
		public TestStep addTestStep(String stepDesc){
			setCurrentStep(new TestStep(xmlFactory.createElement("test-step"), xmlFactory));
			if(stepDesc!=null){
				getCurrentStep().setAttribute("name",stepDesc);
				Console.log("\t\t[Test Step] %s\n", stepDesc);
			}
			return (TestStep) appendChild(getCurrentStep());
		}

		public TestStep getCurrentStep() {
			return currentStep;
		}

		public void setCurrentStep(TestStep currentStep) {
			this.currentStep = currentStep;
		}
		
	}
	
	public static class TestStep extends ScenarioCaseStep {

		public TestStep(Node node, ReportXmlFactory xmlFactory) {
			super(node, xmlFactory);
		}
		
		public TestStep setStatus(String status){
			setAttribute("status", status);
			return this;
		}
		
		public TestLog logEvent(Boolean status, String description){
			return logEvent((status ? "Passed" : "Failed"), description);
		}
		
		public TestLog logEvent(String status, String description){
			String timestamp = dateFormatTimeStamp.format(new Date());
			TestLog testLog = new TestLog(xmlFactory.createElement("report"), xmlFactory);
			testLog.setAttribute("desc", description);
			testLog.setAttribute("status", status);
			testLog.setAttribute("timestamp", timestamp);
			//if(status.trim().equalsIgnoreCase("Failed")){
			if(status=="Failed"){
				Console.logE("\t\t%s | %s | %s\n", timestamp, status, description);
			} else {
				Console.log("\t\t%s | %s | %s\n", timestamp, status, description);
			}
			return (TestLog) appendChild(testLog);
		}
		
	}
	
	public static class TestLog extends ReportNode {

		public TestLog(Node node, ReportXmlFactory xmlFactory) {
			super(node, xmlFactory);
		}
		
		public TestLog setStatus(String status){
			setAttribute("status", status);
			return this;
		}
		
		public TestLog setTimeStamp(String timestamp){
			setAttribute("timestamp", timestamp);
			return this;
		}
		
		/**
		 * Sets validation target
		 * @param name
		 * @param action
		 */
		public TestLog setTarget(String name, String action){
			ReportNode testTarget = new ReportNode(xmlFactory.createElement("target"), xmlFactory);
			testTarget.setAttribute("name", name);
			testTarget.setAttribute("action", action);
			appendChild(testTarget);
			return this;
		}
		
		/**
		 * Sets validation details
		 * @param criteria
		 * @param expected
		 * @param actual
		 */
		public TestLog setValidation(String criteria, String expected, String actual){
			ReportNode testValidation = new ReportNode(xmlFactory.createElement("validation"), xmlFactory);
			testValidation.setAttribute("criteria", criteria);
			testValidation.setAttribute("expected", expected);
			testValidation.setAttribute("actual", actual);
			appendChild(testValidation);
			return this;
		}
		
	}
	
	public static class Listener extends  TestNG.ExitCodeListener implements ITestListener {
		
		private DateFormat timestamp = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa");
		
		@Override
	    public void onTestStart(ITestResult result) {
			
			Reporter.setCurrentTestResult(result);
			
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
				ReportLogInstance reporter = new ReportLogInstance();
				reporter.setTestName( result.getTestContext().getName());
				ReportLogInstance.setInstance(reporter);
				reportConsolidator.addReport(reporter);
				reporter.setSummaryDetails("Time Started", timestamp.format(new Date()));
				reporter.setSummaryDetails("Time Ended", "N/A");
				reporter.setSummaryDetails("Time Elapsed", "N/A");	
				try {
					WebDriver driver = WebManager.getDriver();
					Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
					//System.out.println("Running: [" + WebManager.getProxySettings().getProxyType() +"] "+ caps.getBrowserName() + " "+caps.getVersion());
					reporter.setSummaryDetails("Browser",caps.getBrowserName() + " "+caps.getVersion());
				} catch (Exception e){
				}
			}
			
		}

		@Override
		  public void onTestFailure(ITestResult tr) {
			
			Reporter.setCurrentTestResult(tr);
			
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
			ReportLogInstance reporter = ReportLogInstance.getInstance();
		    reporter.setSummaryDetails("Time Ended", timestamp.format(new Date()));
			reporter.setSummaryDetails(
					"Time Elapsed", 
					String.format(
							"%.2f mins",
							(float)(Reporter.getCurrentTestResult().getEndMillis() - Reporter.getCurrentTestResult().getStartMillis()
									)/(60 * 1000)));
			reporter.save();
			try {
				String testName = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getName();
				String testResult = null;
				switch(Reporter.getCurrentTestResult().getStatus()){
					case ITestResult.FAILURE:
						testResult = "Failed";
						break;
					case ITestResult.SUCCESS:
						testResult = "Passed";
						break;
					case ITestResult.SKIP:
						testResult = "Skipped";
						break;
					default:
						//System.out.println("Unknown Result");
						testResult = "Unknown";
				}
				if(testName!=null){
					//System.out.println("Report creating..");
					TrendReport.updateRecord("Regression",testName,TestManager.Preferences.getPreference("build.number", "local"),testResult);
					//System.out.println("Report created");
				} else {
					//System.out.println("Report not created");
				}
			} catch(Throwable e){
				e.printStackTrace();
			}
		}
		
		@Override
		public void onTestSkipped(ITestResult tr) {
			saveSingleReport();
		}
		
		@Override
		public void onTestSuccess(ITestResult tr) {

			Reporter.setCurrentTestResult(tr);
			
			try{
				ReportLog.assertAll();
			} catch(AssertionError error){
				tr.setThrowable(error);
				tr.setStatus(ITestResult.FAILURE);				
			} finally {
				saveSingleReport();
			}
		  }
		
		@Override
		public void onStart(ITestContext context) {
			currentSuite = context.getSuite();
		}

		@Override
		public void onFinish(ITestContext context) {
			reportConsolidator.save();
		}
		
		
		
		//currentSuite
		
	}



	
	
}
