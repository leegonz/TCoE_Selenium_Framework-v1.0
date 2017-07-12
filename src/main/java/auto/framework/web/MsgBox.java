package auto.framework.web;

import org.openqa.selenium.By;

import auto.framework.ReportLog;

public class MsgBox extends Element {

	public MsgBox(By by) {
		super(by);
	}
	
	public MsgBox(String name, By by) {
		super(name, by);
	}
	
	public MsgBox(String name, By by, Element parent) {
		super(name, by, parent);
	}
	
	public MsgBox(By by, Element parent) {
		super(by, parent);
	}
	
	public Boolean contains(String substring){
		return getText().contains(substring);
	}

	public void verifyContains(String substring,Boolean expected){
		ReportLog.verifyTrue(contains(substring)==expected, "[" + name + "] "+(expected?"Contains":"Does not contain")+" '"+ substring +"'" );
	}
	
	public void assertContains(String substring,Boolean expected){
		ReportLog.assertTrue(contains(substring)==expected, "[" + name + "] "+(expected?"Contains":"Does not contain")+" '"+ substring +"'" );
	}
	
	public void verifyContains(String substring){
		verifyContains(substring,true);
	}
	
	public void assertContains(String substring){
		assertContains(substring,true);
	}
	
}
