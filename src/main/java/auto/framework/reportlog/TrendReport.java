package auto.framework.reportlog;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.testng.Reporter;
import org.testng.annotations.Test;

import Exception.FilloException;
import Fillo.Connection;
import Fillo.Fillo;
import Fillo.Recordset;
import auto.framework.Resources;
import auto.framework.TestManager;

public class TrendReport {

	@Test
	public void Trend() throws Exception {
		
		updateRecord("Regression","Scenario1",TestManager.Preferences.getPreference("BUILD_NUMBER", "local"),"Pass1");
		
	}
	
	public static Connection connection;
	
	public static Connection getFillo() throws Exception {
		if(connection==null){
			String suiteName = Reporter.getCurrentTestResult()
					.getTestContext()
					.getSuite()
					.getName();
			File outputFile = new File("./src/test/resources/reports/consolidated/trends/"+suiteName+".xls");
			if(!outputFile.exists()){
				File templateFile = new File(Resources.findResource("/reportlog/TrendTemplate.xls") );
				FileUtils.copyFile(templateFile, outputFile);
			}
			Fillo fillo=new Fillo();
			connection= fillo.getConnection( outputFile.getCanonicalPath() );
		}
		return connection;
	}
	
	public static TestRecord findRecord(String suitName,String testName) throws Exception {
		String strQuery="Select * from "+suitName+" where Test_Name='"+testName+"'";
		try {
			Recordset record = getFillo().executeQuery(strQuery);
			record.next();
			return new TestRecord(record);
		} catch(FilloException e){
			if(e.getMessage().contains("No records found")){
				return new TestRecord(null);
			} else {
				throw e;
			}
		}
	}
	
	public static TestRecord addRecord(String suitName,String testName) throws Exception {
		String strQuery="INSERT INTO "+suitName+"(Test_Name) VALUES('"+testName+"')";
		try {
			Connection conn = getFillo();
			conn.executeUpdate(strQuery);
//			conn.close();
//			connection = null;
		} catch(FilloException e){
			throw e;
		}
		return findRecord(suitName, testName);
	}
	
	public static void updateRecord(String suitName,String testName, String buildName, String status) throws Exception {
		TestRecord testRecord = (testRecord=findRecord(suitName,testName)).isFound() ? testRecord : addRecord(suitName,testName); 
		Boolean newStatus = !status.equalsIgnoreCase("Skipped");
		Boolean skippedLast = testRecord.getField("Latest").equalsIgnoreCase("Skipped");
		if(newStatus || !skippedLast){
			Connection conn = getFillo();
			if( !skippedLast ) {
				for(int i=9; i>=1; i--){
					String prev = testRecord.getField( i-1 >= 1 ? "Prev"+(i-1) : "Latest");
					String strQuery="Update "+suitName+" Set Prev"+i+"='"+prev+"' where Test_Name='"+testName+"'";
					conn.executeUpdate(strQuery);
				}
				String strQuery="Update "+suitName+" Set Latest='"+status+"', Last_Build='"+buildName+"' where Test_Name='"+testName+"'";
				conn.executeUpdate(strQuery);
			} else {
				String strQuery="Update "+suitName+" Set Latest='"+status+"' where Test_Name='"+testName+"'";
				conn.executeUpdate(strQuery);
			}
				
			
		}
	}
	
	public static class TestRecord {
		
		private Recordset record;
		
		public TestRecord(Recordset record){
			this.record = record;
		}
		
		public Boolean isFound(){
			return record!=null;
		}
		
		public String getField(String field) throws Exception {
			return (String) record.getField(field);
		}
		
	}

}
