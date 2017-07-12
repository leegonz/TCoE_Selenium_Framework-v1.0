package auto.framework.web;

import org.openqa.selenium.By;

import auto.framework.assertion.Conditions;

public class FormElement extends Element {
	
	public static final String VALUE = "value";
	
	public FormElement(By by) {
		super(by);
	}
	
	public FormElement(String name, By by) {
		super(name, by);
	}
	
	public FormElement(String name, By by, Element parent) {
		super(name, by, parent);
	}
	
	public FormElement(By by, Element parent) {
		super(by, parent);
	}

	public Object getValue(){
		return this.getAttribute("value");
	}
	
	public void verifyValue(Object expected){
		if(expected instanceof Enum<?>){
			expected = expected.toString();
		}
		//ReportLog.verifyTrue(getValue().equals(expected), "[" + name + "] Value is '"+expected+"'");
		verifyProperty(FormElement.VALUE, expected);
	}
	
	public void assertValue(Object expected){
		if(expected instanceof Enum<?>){
			expected = expected.toString();
		}
		//ReportLog.assertTrue(getValue().equals(expected), "[" + name + "] Value is '"+expected+"'");
		assertProperty(FormElement.VALUE, expected, Conditions.equals);
	}
	
	@Override
	public Object getProperty(String property){
		switch(property){
		case VALUE:
			return getValue();
		default: 
			return super.getProperty(property);
		}
	}
	
}
