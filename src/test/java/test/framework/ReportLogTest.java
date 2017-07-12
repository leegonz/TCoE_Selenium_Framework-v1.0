package test.framework;

import auto.framework.ReportLog;
import auto.framework.TestBase;

public class ReportLogTest extends TestBase {

	//@Test
	public void test(){
		ReportLog.logEvent("Passed", "Validation -1");
		ReportLog.setTestName("Test Scenario 1");
		ReportLog.logEvent("Passed", "Validation 0.0");
		ReportLog.setTestCase("Test Case 1");
		ReportLog.logEvent("Passed", "Validation 1.0");
		ReportLog.setTestStep("Step 1.1");
		ReportLog.setTestStep("Step 1.2");
		ReportLog.logEvent("Passed", "Validation 1.2");
		ReportLog.setTestCase("Test Case 2");
		ReportLog.logEvent("Passed", "Validation 2.0");
		ReportLog.setTestStep("Step 2.1");
		ReportLog.logEvent("Passed", "Validation 2.1");
		ReportLog.setTestStep("Step 2.2");
		ReportLog.logEvent("Passed", "Validation 2.2.1").setValidation("equals", "1234", "1234").setValidation("equals", "234", "234");
		ReportLog.logEvent("Passed", "Validation 2.2.2");
		//ReportLog.save();
	}

}
