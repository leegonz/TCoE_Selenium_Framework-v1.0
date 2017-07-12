package auto.framework.web;

import org.openqa.selenium.By;

import auto.framework.ReportLog;

public class RadioBtn extends FormElement {
	
	public RadioBtn(By by) {
		super(by);
	}
	
	public RadioBtn(String name, By by) {
		super(name, by);
	}
	
	public RadioBtn(String name, By by, Element parent) {
		super(name, by, parent);
	}
	
	public RadioBtn(By by, Element parent) {
		super(by, parent);
	}
	
	@Override
	public Boolean getValue(){
		return this.isSelected();
	}
	
	public Boolean isClicked(){
		return this.getValue();
	}
	
	public void verifyClicked(Boolean expected){
		ReportLog.verifyTrue(isClicked().equals(expected), "[" + name + "] is " + (expected?"ticked":"unticked") );
	}
	
	public void verifyClicked(){
		verifyClicked(true);
	}
}
