package auto.framework.web;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import auto.framework.ReportLog;
import auto.framework.WebManager;

public class TextBox extends FormElement {

	public TextBox(By by) {
		super(by);
	}
	
	public TextBox(String name, By by) {
		super(name, by);
	}
	
	public TextBox(String name, By by, Element parent) {
		super(name, by, parent);
	}
	
	public TextBox(By by, Element parent) {
		super(by, parent);
	}
	
	public Boolean type(String text){
		String type;
		try{ 
			WebElement element = toWebElement();
			type=element.getAttribute("type").toLowerCase();
			switch(type){
			case "file":
				break;
			default:
				element.click();
				element.clear();
				break;
			}
		} catch(Error e){}
		return sendKeys(text);
	}
	
	public void verifyType(String string){
		verifyType(string, string);
	}
	
	public void verifyType(String string, final String expected){
		ReportLog.verifyTrue( type(string) && expected.equals(getValue()), "[" + name + "] Verify that typed value is \"" + expected + "\"");
	}
	
	public void assertType(String string){
		assertType(string, string);
	}
	
	public void assertType(String string, String expected){
		ReportLog.assertTrue( type(string) && expected.equals(getValue()), "[" + name + "] Verify that typed value is \"" + expected + "\"");
		
	}
	
	public void sendKeys(Keys key){
		Boolean prereq = isInteractive();
		Boolean success = false;
		if(prereq) { 
			WebElement webElement = toWebElement();
			webElement.sendKeys(key);
			fireIEEvent("blur");
			success = true;
			//success = string.trim().equals(getText());
		}
		ReportLog.logEvent(success, "[" + name + "] Type \"" + key.toString() + "\"");
	}
	public void customJavaType(String value){
		Boolean prereq = isInteractive();
		Boolean success = false;
		if(prereq) { 
			WebDriver driver = WebManager.getDriver();
			WebElement webElement = toWebElement();
			((JavascriptExecutor)driver).executeScript("arguments[0].innerText = '"+value+"'", webElement);
			success = true;
		}
		ReportLog.logEvent(success, "[" + name + "] Type \"" + value + "\"");
	}

	
}
