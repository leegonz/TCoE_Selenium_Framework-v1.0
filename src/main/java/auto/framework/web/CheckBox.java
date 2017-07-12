package auto.framework.web;

import org.openqa.selenium.By;

import auto.framework.ReportLog;

public class CheckBox extends FormElement {

	public CheckBox(By by) {
		super(by);
	}
	
	public CheckBox(String name, By by) {
		super(name, by);
	}
	
	public CheckBox(String name, By by, Element parent) {
		super(name, by, parent);
	}
	
	public CheckBox(By by, Element parent) {
		super(by, parent);
	}
	
	public void check(){
		check(true);
	}
	
	public void check(Boolean value){
		Boolean success = false;
		Throwable error=null;
		try {
			if(value && !getValue()) toWebElement().click();
			if(!value && getValue()) toWebElement().click();
			success = true;
		} catch(Throwable e){ //catch(Error e){}
			error = e;
		}
		finally {
			ReportLog.logEvent(success, "[" + name + "] " + (value ? "Check" : "Uncheck") );
			if(error!=null){
				ReportLog.failed(error.getClass().getSimpleName() + " : " + error.getMessage());
			}
		}
	}
	
	@Override
	public Boolean getValue(){
		return this.isSelected();
	}
	
	public Boolean isChecked(){
		return this.getValue();
	}
	
	public void verifyChecked(){
		verifyChecked(true);
	}
	
	public void verifyChecked(Boolean expected){
		ReportLog.verifyTrue(isChecked().equals(expected), "[" + name + "] is " + (expected?"checked":"unchecked") );
	}
	
	public void assertChecked(){
		assertChecked(true);
	}
	
	public void assertChecked(Boolean expected){
		ReportLog.assertTrue(isChecked().equals(expected), "[" + name + "] is " + (expected?"checked":"unchecked") );
	}
	
}
