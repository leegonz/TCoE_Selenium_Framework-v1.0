package auto.framework.web;

import java.util.regex.PatternSyntaxException;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import auto.framework.ReportLog;
import auto.framework.WebManager;

public class Page implements ISearchContext {
	
	private String url = ".*/business/index.jsp.*";
	private String name = "Page object";
	
	public Page(String name, String url){
		this.name = name;
		this.url = url;
	}
	
	public Boolean inURL(){
		String currentURL = WebManager.getDriver().getCurrentUrl();
		try {
			return currentURL.equalsIgnoreCase(url) || currentURL.matches(url);
		} catch(PatternSyntaxException e){
			return false;
		}
	}
	
	public void verifyURL(){
		verifyURL(true, 10);
	}
	
	public void assertURL(){
		assertURL(true, 10);
	}
	
	public void verifyURL(final Boolean expected, int timeOutInSeconds){
		WebDriverWait wait = new WebDriverWait(WebManager.getDriver(),timeOutInSeconds);
		Boolean success = true;
		
		wait.ignoring(StaleElementReferenceException.class);
		try {
			ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driver) {
					return expected.equals(inURL());
				}
			};
			wait.until(expectedCondition);
		} catch(TimeoutException e){
			success = false;
		}
		
		ReportLog.verifyTrue(success, (expected?"":"not ")+"in " + name + " page");
	}
	
	public void assertURL(final Boolean expected, int timeOutInSeconds){
		WebDriverWait wait = new WebDriverWait(WebManager.getDriver(),timeOutInSeconds);
		Boolean success = true;
		
		wait.ignoring(StaleElementReferenceException.class);
		try {
			ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driver) {
					return expected.equals(inURL());
				}
			};
			wait.until(expectedCondition);
		} catch(TimeoutException e){
			success = false;
		}
		
		ReportLog.assertTrue(success, (expected?"":"not ")+"in " + name + " page");
	}
	
	public void verifyURL(Boolean expected){
		ReportLog.verifyTrue(expected.equals(inURL()), (expected?"":"not ")+"in " + name + " page");
	}
	
	public void assertURL(Boolean expected){
		ReportLog.assertTrue(expected.equals(inURL()), (expected?"":"not ")+"in " + name + " page");
	}
	
	public ISearchContext findElement(String name, By by){
		return new Element(name, by);
	}
	
	public Element findElement(By by){
		return new Element(by);
	}

	@Override
	public ISearchContext getParent() {
		return null;
	}
	
	public WebControl.Window switchWithUrl(){
		return WebControl.switchWithUrl(url);
	}
	
	public String getName(){
		return name;
	}

	
	
/*	public Boolean isTextPresent(String text){
	    WebElement bodyElement = WebManager.getDriver().findElement(By.tagName("body"));
	    return bodyElement.getText().contains(text);
	}
	
	public void verifyTextPresent(String text){
		ReportLog.verifyTrue(isTextPresent(text), "Verify that text is in " + name + " page");
	} */

	
}