package auto.framework.web;

//import java.util.List;

import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import auto.framework.ReportLog;
import auto.framework.WebManager;
import auto.framework.assertion.Conditions;
import auto.framework.assertion.Conditions.Condition;

public class Element implements ISearchContext {
	
	protected String name;
	protected Element parent;
	protected By by;
	
	public Element(String name, By by, Element parent){
		this.name = name;
		this.by = by;
		this.parent = parent;
	}
	
	public Element(String name, By by){
		this.name = name;
		this.by = by;
	}

	public Element(By by, Element parent){
		this.name = "Element";
		this.by = by;
		this.parent = parent;
	}
	
	public Element(By by){
		this.name = "Element";
		this.by = by;
	}
	
	public Mouse mouse(){
		return new Mouse(this);
	}
	
	public String getName(){
		return name;
	}

	@Override
	public Element findElement(String name, By by) {
		return new Element(name, by, this);
	}
	
	@Override
	public Element findElement(By by) {
		return new Element(by, this);
	}

	@Override
	public ISearchContext getParent() {
		if(parent != null) return parent;
		return null;//Page;
	}
	
	protected void loadElement(){
		
	}
	
	protected void switchFrame(){
		WebManager.getDriver().switchTo().defaultContent();
		//System.out.println("Switch to default");
	}
	
	protected Frame getFrame(){
		if(this.parent!=null && this.parent instanceof Element){
			Element parentElement = (Element) this.parent;
			
			if(parentElement instanceof Frame){
				return (Frame) parentElement;
			} else {
				return parentElement.getFrame();
			}
		} return null;
	}
	
	public Boolean isExisting(){ //TODO
		
		if(this.parent!=null && this.parent instanceof Element){
			Element parentElement = (Element) this.parent;
			if(!parentElement.isExisting()) return false;
		}
		
		SearchContext searchContext = WebManager.getDriver();
		
		if(this.parent!=null && this.parent instanceof Element){
			Element parentElement = (Element) this.parent;
			
			if(parentElement instanceof Frame){
				parentElement.switchFrame();
			} else {
				searchContext = parentElement.toWebElement();
			}
		} else {
			WebManager.getDriver().switchTo().defaultContent();
		}
		
		List<WebElement> elements = searchContext.findElements(by);
		return !elements.isEmpty();
		
	}
	
	public WebElement toWebElement(){

		loadElement();
		
		SearchContext searchContext = WebManager.getDriver();
		
		if(this.parent!=null && this.parent instanceof Element){
			Element parentElement = (Element) this.parent;
			
			if(parentElement instanceof Frame){
				parentElement.switchFrame();
			} else {
				searchContext = parentElement.toWebElement();
			}
		} else {
			WebManager.getDriver().switchTo().defaultContent();
		}
		
		List<WebElement> elements = searchContext.findElements(by);
		Iterator<WebElement> iterator = elements.iterator();
		while(iterator.hasNext()){
			WebElement element = iterator.next();
			if(element.isDisplayed()) return element;
		}
		return searchContext.findElement(by);
		
	}
	
	protected Boolean isInteractive(){
		try {
//			WebElement webElement = toWebElement();
			this.waitForDisplay(true, 10);
			return this.isDisplayed() && this.isEnabled();
		} catch(Error|StaleElementReferenceException error){
			//error.printStackTrace();
			WebControl.takeScreenshot();
			throw error;
//			return false;
		}
	}
	
	protected Boolean isInteractive(Boolean Report){
		try {	
			return this.isDisplayed() && this.isEnabled();
		} catch(Error error){
			return false;
		}
	}
	
	
	
	public void highlight() {
		WebElement element = toWebElement();
		WebDriver driver = WebManager.getDriver();
		for (int i = 0; i <3; i++) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			/*js.executeScript(""
				+ "element = arguments[0];"
				+ "oStyle = element.getAttribute('style');"
				+ "element.setAttribute('style','color:yellow; background: yellow; border: 1px solid yellow;'+oStyle);"
				+ "setTimeout(function(){"
					+ "element.setAttribute('style',oStyle);"
				+ "},300);"
				, element);*/
			String style = element.getAttribute("style");
			try{
				js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "background: yellow; border: 1px solid yellow;"+style);
				js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, style);
			} catch(Error e){}
		}
	}
	
	//TODO
	private void focus() {
		WebDriver driver = WebManager.getDriver();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		//Actions action = new Actions(driver);
		
		WebElement element = toWebElement();

		Point coords = ((Locatable)element).getCoordinates().inViewPort();
		try {
			//action.moveToElement(element).build().perform();
			if( ExpectedConditions.elementToBeClickable(element).apply(driver) == null){
				throw new WebDriverException("Element is not clickable");
			}

			Point pageCoords = ((Locatable)element).getCoordinates().onPage();

		    if( (boolean) js.executeScript("return arguments[0] != document.elementFromPoint(arguments[1], arguments[2]);", element, pageCoords.x, pageCoords.y) ){
		    	
				throw new WebDriverException("Element is not clickable");
		    }
			
		}
		catch(WebDriverException e2){
			Dimension elementSize = element.getSize();
			coords.moveBy(elementSize.width/2, elementSize.height/2);
	
			System.err.println("Element Size ("+elementSize.width+","+elementSize.height+")");
			System.err.println("Element Center ("+coords.x+","+coords.y+")");
			
			Dimension winSize = driver.manage().window().getSize();
			System.err.println("Window ("+winSize.width+","+winSize.height+")");
			
			Point scroll = new Point(coords.x-(winSize.width/2), coords.y-(winSize.height/2));
			System.err.println("ScrollBy ("+scroll.x+","+scroll.y+")");
		    js.executeScript("javascript:window.scrollBy(arguments[0],arguments[1])", scroll.x, scroll.y);
		}
	}
	
	public Boolean waitForDisplay(final Boolean expected,long timeOutInSeconds){
		Boolean success = waitForProperty(Element.DISPLAYED, expected, timeOutInSeconds);
		if(success) ReportLog.logEvent(success, "[" + name + "] Wait for element to "+ (expected? "display" :"finish displaying")+" ("+timeOutInSeconds+")");
		//ReportLog.addInfo("["+name+"] Wait for element to "+ (expected?"display":"not display")+" ("+timeOutInSeconds+")");
		return success;
	}
	
	//TODO
	public Boolean waitForExist(final Boolean expected,long timeOutInSeconds){
		return waitForProperty(Element.EXISTS, expected, timeOutInSeconds);
	}
	
	public Boolean waitForEnable(final Boolean expected,long timeOutInSeconds){
		return waitForProperty(Element.ENABLED, expected, timeOutInSeconds);
	}
	
	public void verifyClick(){
		ReportLog.verifyTrue(click(), "[" + name + "] Click element");
	}
	
	public void assertClick(){
		ReportLog.assertTrue(click(), "[" + name + "] Click element");
	}
	
	public void fireEvent(String event){
		JavascriptExecutor js = (JavascriptExecutor) WebManager.getDriver();
		String script = getEventScript(event);
		js.executeScript(script, toWebElement());
	}
	
	protected void fireIEEvent(String event){
		WebDriver driver = WebManager.getDriver();
		Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
		String browserName = caps.getBrowserName();
		if(!browserName.equals("internet explorer")) return;
		
//		String browserVersion = caps.getVersion();
//		Boolean nativeEvents = (Boolean) caps.getCapability(InternetExplorerDriver.NATIVE_EVENTS);
//		if(!nativeEvents && Long.valueOf(browserVersion)<=8){
//			JavascriptExecutor js = (JavascriptExecutor) driver;
//			String script = "arguments[0].fireEvent(arguments[1]);";
//			js.executeScript(script, toWebElement(), "on"+event);
//			//dispatchEvent for ie9+
//		}
		Boolean nativeEvents = (Boolean) caps.getCapability(InternetExplorerDriver.NATIVE_EVENTS);
		if(!nativeEvents){
			JavascriptExecutor js = (JavascriptExecutor) driver;
			String script = getEventScript(event);
			try{
				js.executeScript(script, toWebElement());
			} catch(NoSuchElementException|StaleElementReferenceException e){
			}
		}
	}
	
	protected void fireIEMouseEvent(String event){
		WebDriver driver = WebManager.getDriver();
		Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
		String browserName = caps.getBrowserName();
		if(!browserName.equals("internet explorer")) return;
		
//		String browserVersion = caps.getVersion();
//		Boolean nativeEvents = (Boolean) caps.getCapability(InternetExplorerDriver.NATIVE_EVENTS);
//		if(!nativeEvents && Long.valueOf(browserVersion)<=8){
//			JavascriptExecutor js = (JavascriptExecutor) driver;
//			String script = "arguments[0].fireEvent(arguments[1]);";
//			js.executeScript(script, toWebElement(), "on"+event);
//			//dispatchEvent for ie9+
//		}
		Boolean nativeEvents = (Boolean) caps.getCapability(InternetExplorerDriver.NATIVE_EVENTS);
		if(!nativeEvents){
			JavascriptExecutor js = (JavascriptExecutor) driver;
			String script = getMouseEventScript(event);
			js.executeScript(script, toWebElement());
		}
	}
	
	private String getEventScript(String eventName){
		String script = "var element = arguments[0];"
			+ "var event; // The custom event that will be created\n"
			+ "if (document.createEvent) {\n"
			+ "		event = document.createEvent(\"HTMLEvents\");\n"
			+ "		event.initEvent(\""+eventName+"\", true, true);\n"
			+ "} else {\n"
			+ "		event = document.createEventObject();\n"
			+ "		event.eventType = \""+eventName+"\";\n"
			+ "}\n"
			+ "event.eventName = \""+eventName+"\";\n"
			+ "event.srcElement = element;\n"
			+ "event.target = element;\n"
			+ "if (document.createEvent) {\n"
			+ "		element.dispatchEvent(event);\n"
			+ "} else {\n"
			+ "		element.fireEvent(\"on\" + event.eventType, event);\n"
			+ "}";
		return script;
	}
	

	private String getMouseEventScript(String eventName){
		String script = "var element = arguments[0];"
			+ "var event; // The custom event that will be created\n"
			+ "if (document.createEvent) {\n"
			+ "		event = document.createEvent(\"MouseEvents\");\n"
			+ "		event.initMouseEvent(\""+eventName+"\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);\n"
			+ "} else {\n"
			+ "		event = document.createEventObject();\n"
			+ "		event.eventType = \""+eventName+"\";\n"
			+ "}\n"
			+ "event.eventName = \""+eventName+"\";\n"
			+ "event.srcElement = element;\n"
			+ "event.target = element;\n"
			+ "if (document.createEvent) {\n"
			+ "		element.dispatchEvent(event);\n"
			+ "} else {\n"
			+ "		element.fireEvent(\"on\" + event.eventType, event);\n"
			+ "}";
		return script;
	}
	
	protected void doClick(){
		//focus(); //TODO
		//WebDriver driver = WebManager.getDriver();
		//Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
		if(this.getTagName().equalsIgnoreCase("input")){
			switch(this.getAttribute("type").toLowerCase()){
			case "radio":
			case "text":
			case "checkbox":
				toWebElement().click();
				fireIEEvent("click");
				break;
			default:
				toWebElement().click();
				//fireIEEvent("click");
				break;
			}
		} else {
			toWebElement().click();
		}
//		Boolean nativeEvents = (Boolean) caps.getCapability(InternetExplorerDriver.NATIVE_EVENTS);
//		if(!nativeEvents){
//			WebControl.waitForPageToLoad( Long.valueOf(TestManager.Preferences.getPreference("webdriver.timeouts.pageLoadTimeOut","60")) );
//		}
	}
	public Boolean jsClick() {
		Boolean success = false;
		Boolean prereq = isInteractive();
		
		if(prereq) {
			try {
				WebDriver driver = WebManager.getDriver();
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", this.toWebElement());
				success = true;
			} catch(StaleElementReferenceException|Error e){
				ReportLog.logEvent(false, "[" + name + "] Click element(using JS)");
				WebControl.takeScreenshot();
				throw e;
			} catch(Exception e){
				throw e;
			}
		}
		
		ReportLog.logEvent(success, "[" + name + "] Click element(using JS)");
		return success;
		
	}
	
	
	public Boolean click(){
		Boolean prereq = isInteractive();
		Boolean success = false;
		if(prereq) {
			try{
//				try {
//					doClick();
//				} catch(WebDriverException e){
//					scrollToElement();
//					doClick();
//				}
				doClick();
				success = true;
			} catch(StaleElementReferenceException|Error e){
				ReportLog.logEvent(false, "[" + name + "] Click element");
				WebControl.takeScreenshot();
				throw e;
			} catch(Exception e){
				throw e;
			}
		}
		ReportLog.logEvent(success, "[" + name + "] Click element");
		return success;
	}
	
	public Boolean click(Boolean Report){
		Boolean prereq = isInteractive(false);
		Boolean success = false;
		if(prereq) {
			try{
				doClick();
				
				success = true;
			} catch(StaleElementReferenceException|Error e){
				
					ReportLog.logEvent(false, "[" + name + "] Click element");
				
				throw e;
			} catch(Exception e){
				throw e;
			}
		}
			if(Report){
				ReportLog.logEvent(success, "[" + name + "] Click element");
			}
		return success;
	}

//sendKeys
	public void verifySendKeys(String string){
		verifySendKeys(string, string);
	}
	
	public void verifySendKeys(String string, final String expected){
		ReportLog.verifyTrue( sendKeys(string) && expected.equals(getAttribute("value")), "[" + name + "] Verify that typed value is \"" + expected + "\"");
	}
	
	public void assertSendKeys(String string){
		assertSendKeys(string, string);
	}
	
	public void assertSendKeys(String string, String expected){
		ReportLog.assertTrue( sendKeys(string) && expected.equals(getAttribute("value")), "[" + name + "] Verify that typed value is \"" + expected + "\"");
		
	}
	
	public Boolean sendKeys(String string){
		Boolean prereq = isInteractive();
		Boolean success = false;
		if(prereq) { 
			WebElement webElement = toWebElement();
			webElement.sendKeys(string);
//			fireIEEvent("change");
//			fireIEEvent("focus");
//			fireIEEvent("focusout");
//			fireIEEvent("input");
//			fireIEEvent("keydown");
//			fireIEEvent("keypress");
			fireIEEvent("keyup");
			fireIEEvent("blur");
			success = true;
			//success = string.trim().equals(getText());
		}
		ReportLog.logEvent(success, "[" + name + "] Type \"" + string + "\"");
		return success;
	}

	public Boolean submit() {
		Boolean prereq = isInteractive();
		Boolean success = false;
		if(prereq) { 
			WebElement webElement = toWebElement();
			webElement.submit();
			success = true;
		}
		ReportLog.logEvent(success, "[" + name + "] Submit");
		return success;
	}

/*	public void sendKeys(CharSequence... keysToSend) {
		
	} */

//clear
	public void clear() {
		ReportLog.logEvent(true, "[" + name + "] Clear");
		toWebElement().clear();
	}

//tagName
	public String getTagName() {
		return toWebElement().getTagName();
	}
	
	public void verifyTagName(String expected) {
		verifyProperty(Element.TAGNAME, expected, Conditions.equals);
	}
	
	public void assertTagName(String expected) {
		assertProperty(Element.TAGNAME, expected, Conditions.equals);
	}
	
	public void scrollToElement(int viewportX, int viewportY) {
		WebElement element = toWebElement();
		
	    WebDriver driver = WebManager.getDriver();
		JavascriptExecutor js = (JavascriptExecutor) driver;

		Point scroll = ((Locatable)element).getCoordinates().inViewPort();
		scroll = scroll.moveBy(-viewportX, -viewportY);
		
		
		Frame frame = getFrame();
		if(frame!=null){
			WebElement frameElement = frame.toWebElement();
			Point scroll2 = ((Locatable)frameElement).getCoordinates().inViewPort();
			scroll = scroll.moveBy(-scroll2.x, -scroll2.y);
			//System.err.println("Frame ScrollBy ("+scroll.x+","+scroll.y+")");
			js.executeScript("arguments[0].contentWindow.scrollBy(arguments[1],arguments[2])", frameElement, scroll.x, scroll.y);
		} else {

			//System.err.println("ScrollBy ("+scroll.x+","+scroll.y+")");
			js.executeScript("javascript:window.scrollBy(arguments[0],arguments[1])", scroll.x, scroll.y);
		}
	    
	}
	
	public void scrollToElement() {
		WebElement element = toWebElement();
		
		Actions action = new Actions(WebManager.getDriver());
	    action.moveToElement(element,0,0).build().perform();
	}
	
//attribute
	public String getAttribute(String name) {
		return toWebElement().getAttribute(name);
	}
	
	public void verifyAttribute(String attribute, String expected) {
		verifyProperty("@"+attribute, expected, Conditions.equals);
	}
	
	public void assertAttribute(String attribute, String expected) {
		assertProperty("@"+attribute, expected, Conditions.equals);
	}	

//selected
	public boolean isSelected() {
		return toWebElement().isSelected();
	}
	
//editable
	public boolean isEditable() {
		switch(getTagName()){
		case "input":
		case "select":
		case "textarea":
			return isInteractive();
		}
		return false;
	}
	
	public void verifyEditable() {
		verifyEditable(true);
	}
	
	public void verifyEditable(Boolean expected) {
		if(expected)
			ReportLog.verifyTrue(isEditable(), "[" + name + "] is editable");
		else
			ReportLog.verifyTrue(!isEditable(), "[" + name + "] is not editable");
	}
	
	public void assertEditable() {
		assertEditable(true);
	}
	
	public void assertEditable(Boolean expected) {
		if(expected)
			ReportLog.assertTrue(isEditable(), "[" + name + "] is editable");
		else
			ReportLog.assertTrue(!isEditable(), "[" + name + "] is not editable");
	}

//enabled
	public Boolean isEnabled() {
		return toWebElement().isEnabled();
	}
	
	public void verifyEnabled() {
		verifyEnabled(true);
	}
	

	public void verifyEnabled(final Boolean expected,long timeOutInSeconds){
		waitForEnable(expected,timeOutInSeconds);
		verifyEnabled(expected);
	}
	
	public void verifyEnabled(Boolean expected) {
		verifyProperty(Element.ENABLED, expected);
	}
	
	public void assertEnabled() {
		assertEnabled(true);
	}
	
	public void assertEnabled(Boolean expected) {
		if(expected)
			ReportLog.assertTrue(isEnabled(), "[" + name + "] is enabled");
		else
			ReportLog.assertTrue(!isEnabled(), "[" + name + "] is disabled");
	}

	
//text	
	public String getText() {
		try {
			return toWebElement().getText();
		} catch(Error error) {
			return "";
		}
	}
	
	public void verifyText(String compare, Boolean match) {
		if(match)
			verifyProperty(Element.TEXT, compare);
		else
			verifyProperty(Element.TEXT, compare, Conditions.notEquals);
	}
	
	public void verifyText(String expected) {
		verifyText(expected,true);
	}
	
	public void verifyText(Enum<?> E) {
		verifyText(E.toString());
	}
	
	public void verifyText(Enum<?> E, Boolean match) {
		verifyText(E.toString(), match);
	}
	
	public void assertText(String compare, Boolean match) {
		assertProperty(Element.TEXT, compare, match ? Conditions.equals : Conditions.notEquals);
	}
	
	public void assertText(String expected) {
		assertText(expected,true);
	}
	
	public void assertText(Enum<?> E) {
		assertText(E.toString());
	}
	
	public void assertText(Enum<?> E, Boolean match) {
		assertText(E.toString(), match);
	}
	
	public void verifyTextMatch(String compare, Boolean match) {
		verifyProperty(Element.TEXT, compare, match ? Conditions.matches : Conditions.notMatches);
	}
	
	public void verifyTextMatch(Enum<?> E, Boolean match) {
		verifyTextMatch(E.toString(), match);
	}
	
	public void assertTextMatch(String compare, Boolean match) {
		assertProperty(Element.TEXT, compare, match ? Conditions.matches : Conditions.notMatches);
	}
	
	public void assertTextMatch(Enum<?> E, Boolean match) {
		assertTextMatch(E.toString(), match);
	}

	/*
	public List<Element> findElements(By by) {
		// TODO Auto-generated method stub
		List<Element> elements;
		return null;
	}*/

//display
	public Boolean isDisplayed() {
		try {
			return toWebElement().isDisplayed();
		} catch(NoSuchElementException|StaleElementReferenceException e){
			return false;
		} catch(WebDriverException e){
			if(e.getMessage().contains("Error determining if element is displayed")) return false;
			throw e;
		}
	}
	
	public void verifyDisplayed() {
		verifyDisplayed(true);
	}
		
	public void verifyDisplayed(Boolean expected) {
		/*try {
			highlight();
		} catch(Error e){} */
//		if(expected)
//			ReportLog.verifyTrue(isDisplayed(), "[" + name + "] is displayed");
//		else
//			ReportLog.verifyTrue(!isDisplayed(), "[" + name + "] is not displayed");
		verifyProperty(Element.DISPLAYED, expected);
	}

	public void verifyDisplayed(final Boolean expected,long timeOutInSeconds){
		waitForDisplay(expected,timeOutInSeconds);
		verifyDisplayed(expected);
	}
	
	public void assertDisplayed() {
		assertDisplayed(true);
	}
	
	public void assertDisplayed(Boolean expected) {
		try {
			highlight();
		} catch(Throwable e){}
//		if(expected)
//			ReportLog.assertTrue(isDisplayed(), "[" + name + "] is displayed");
//		else
//			ReportLog.assertTrue(!isDisplayed(), "[" + name + "] is not displayed");
		assertProperty(Element.DISPLAYED, expected, Conditions.equals);
	}
	
	public void customJavaClick() {
		try {
			JavascriptExecutor exec = (JavascriptExecutor) WebManager.getDriver();
			exec.executeScript("window.showModalDialog = function( sURL,vArguments, sFeatures) { window.open(sURL, 'modal', sFeatures); }");
			exec.executeScript("arguments[0].click()", this.toWebElement());
			ReportLog.passed("["+ this.name + "] Click element");
			} 
		catch(TimeoutException e) {
			ReportLog.failed("["+ this.name + "] Click element");
			}		
	}
	
	public String getToolTip(){
		return this.getAttribute("title");
	}
	
	public void verifyToolTip(String expected){
		//ReportLog.verifyTrue(getToolTip().equals(expected), "[" + name + "] Tooltip is '"+expected+"'");
		verifyProperty(Element.TOOLTIP, expected, Conditions.equals);
	}
	
	public void assertToolTip(String expected){
		//ReportLog.verifyTrue(getToolTip().equals(expected), "[" + name + "] Tooltip is '"+expected+"'");
		assertProperty(Element.TOOLTIP, expected, Conditions.equals);
	}

//location
	public Point getLocation() {
		return toWebElement().getLocation();
	}

//size
	public Dimension getSize() {
		return toWebElement().getSize();
	}

//cssValue
	public String getCssValue(String propertyName) {
		return toWebElement().getCssValue(propertyName);
	}

	
	public static final String TAGNAME = "tagname";
	public static final String CLASS = "class";
	public static final String ENABLED = "enabled";
	public static final String SELECTED = "selected";
	public static final String EDITABLE = "editable";
	public static final String TEXT = "text";
	public static final String EXISTS = "exists";
	public static final String DISPLAYED = "displayed";
	public static final String TOOLTIP = "tooltip";
	public static final String LOCATION_X = "location.x";
	public static final String LOCATION_Y = "location.y";
	public static final String LINK = "link";

	public Object getProperty(String property){
		switch(property){
		case CLASS:
			return getAttribute(CLASS);
		case EXISTS:
			return isExisting();
		case DISPLAYED:
			return isDisplayed();
		case EDITABLE:
			return isEditable();
		case ENABLED:
			return isEnabled();
		case SELECTED:
			return isSelected();
		case TAGNAME:
			return getTagName();
		case TEXT:
			return getText();
		case TOOLTIP:
			return getToolTip();
		case LOCATION_X:
			return getLocation().x;
		case LOCATION_Y:
			return getLocation().y;
		case LINK:
			if(getTagName().equalsIgnoreCase("img")){
				try {
					return findElement(By.xpath(".//ancestor::a")).getAttribute("href");
				} catch (NoSuchElementException e){
					return null;
				}
			} else {
				try {
					String href = findElement(By.xpath("./descendant-or-self::*[@href]|.//ancestor::a")).getAttribute("href");
					return href;
				} catch (NoSuchElementException e){
					return getAttribute("href");
				}
			}
		default:
			break;
		}
		if(String.valueOf(property).startsWith("@")){
			return getAttribute(String.valueOf(property).substring(1));
		}
		return null;
	};
	
/*
 * Property Validations
 */
	
	public <Expected> Boolean verifyProperty(Enum<?> property,Expected expected){
		return verifyProperty(property.toString(), expected, Conditions.equals);
	};
	
	public <Expected> Boolean verifyProperty(String property,Expected expected){
		return verifyProperty(property, expected, Conditions.equals);
	};
	
	public <Expected> Boolean verifyProperty(Enum<?> property,Condition<Expected> validation){
		return verifyProperty(property.toString(), validation);
	};
	
	public <Expected> Boolean verifyProperty(Enum<?> property,Expected expected,Condition<Expected> validation){
		return verifyProperty(property.toString(), expected, validation);
	};
	
	public <Expected> Boolean verifyProperty(String property,Condition<Expected> validation){
		try { 
			return assertProperty(property, validation);
		} catch (AssertionError e) {
			return false;
		} catch (NoSuchElementException|StaleElementReferenceException error) {
			ReportLog.failed(error.getClass().getSimpleName() + " : " + error.getMessage());
			return false;
		}
	};
	
	public <Expected> Boolean verifyProperty(String property,Expected expected,Condition<Expected> validation){
		try { 
			return assertProperty(property, expected, validation);
		} catch (AssertionError e) {
			return false;
		} catch (NoSuchElementException|StaleElementReferenceException error) {
			ReportLog.failed(error.getClass().getSimpleName() + " : " + error.getMessage());
			return false;
		}
	};
	
	public <Expected> Boolean assertProperty(String property,Condition<Expected> validation){
		if(validation==null) return assertProperty(property, Conditions.isNull);
		Object actual = getProperty(property);
		Boolean success = validation.verify(null, actual);
		String message = "[" + name + "] Verify that "+property+" "+validation.name();
		try{
			ReportLog.assertTrue(success, message);
		} catch (AssertionError e) {
			ReportLog.addInfo("Actual: "+property+"="+actual);
			throw e;
		} catch (NoSuchElementException|StaleElementReferenceException error) {
			ReportLog.failed(error.getClass().getSimpleName() + " : " + error.getMessage());
			throw error;
		}
		return success;
	};
	
	public <Expected> Boolean assertProperty(String property,Expected expected,Condition<Expected> validation){
		Object actual = getProperty(property);
		Boolean success = validation.verify(expected, actual);
		String message = "[" + name + "] Verify that "+property+" "+validation.name()+" "+expected;
		try{
			ReportLog.assertTrue(success, message);
		} catch (AssertionError e) {
			ReportLog.addInfo("Actual: "+property+"="+actual);
			throw e;
		} catch (NoSuchElementException|StaleElementReferenceException error) {
			ReportLog.failed(error.getClass().getSimpleName() + " : " + error.getMessage());
			throw error;
		}
		return success;
	};
	
	public <Expected> Boolean waitForProperty(final String property,final Expected expected,long timeOutInSeconds){
		return waitForProperty(property, expected, timeOutInSeconds,Conditions.equals);
	}
	
	public <Expected> Boolean waitForProperty(final String property,final Expected expected,long timeOutInSeconds,final Condition<Expected> validation){
		WebDriverWait wait = new WebDriverWait(WebManager.getDriver(),timeOutInSeconds);
		Boolean success = true;
		
		//wait.ignoring(StaleElementReferenceException.class, NoSuchElementException.class);
		wait.ignoring(StaleElementReferenceException.class);
		try {
			ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driver) {
					return validation.verify(expected, getProperty(property));
				}
			};
			wait.until(expectedCondition);
		} catch(TimeoutException e){
			success = false;
		}
		return success;
	};
	
/*
 * Css Validations	
 */
	

	public <Expected> Boolean verifyCssValue(String property,String expected){
		return verifyCssValue(property, expected, Conditions.equalsIgnoreCase);
	};
	
	public <Expected> Boolean verifyCssValue(String property,Expected expected){
		return verifyCssValue(property, expected, Conditions.equals);
	};
	
	public <Expected> Boolean verifyCssValue(String property,Condition<Expected> validation){
		try { 
			return assertCssValue(property, validation);
		} catch (AssertionError e) {
			return false;
		}
	};
	
	public <Expected> Boolean verifyCssValue(String property,Expected expected,Condition<Expected> validation){
		try { 
			return assertCssValue(property, expected, validation);
		} catch (AssertionError e) {
			return false;
		}
	};
	
	public <Expected> Boolean assertCssValue(String property,Condition<Expected> validation){
//		if(validation==null) return assertCssValue(property, Conditions.isNull);
//		Boolean success = validation.verify(null, getCssValue(property));
//		String message = "[" + name + "] Verify that "+property+" "+validation.name();
//		ReportLog.assertTrue(success, message);
//		return success;
		if(validation==null) return assertCssValue(property, Conditions.isNull);
		Object actual = getCssValue(property);
		Boolean success = validation.verify(null, actual);
		String message = "[" + name + "] Verify that "+property+" "+validation.name();
		try{
			ReportLog.assertTrue(success, message);
		} catch (AssertionError e) {
			ReportLog.addInfo("Actual: "+property+"="+actual);
			throw e;
		} catch (NoSuchElementException|StaleElementReferenceException error) {
			ReportLog.failed(error.getClass().getSimpleName() + " : " + error.getMessage());
			throw error;
		}
		return success;
	};
	
	public <Expected> Boolean assertCssValue(String property,Expected expected,Condition<Expected> validation){
//		Boolean success = validation.verify(expected, getCssValue(property));
//		String message = "[" + name + "] Verify that "+property+" "+validation.name()+" "+expected;
//		ReportLog.assertTrue(success, message);
//		return success;
		Object actual = getCssValue(property);
		Boolean success = validation.verify(expected, actual);
		String message = "[" + name + "] Verify that "+property+" "+validation.name()+" "+expected;
		try{
			ReportLog.assertTrue(success, message);
		} catch (AssertionError e) {
			ReportLog.addInfo("Actual: "+property+"="+actual);
			throw e;
		} catch (NoSuchElementException|StaleElementReferenceException error) {
			ReportLog.failed(error.getClass().getSimpleName() + " : " + error.getMessage());
			throw error;
		}
		return success;
	};
	
	public <Expected> Boolean waitForCssValue(final String property,final String expected,long timeOutInSeconds){
		return waitForCssValue(property, expected, timeOutInSeconds,Conditions.equalsIgnoreCase);
	}
	
	public <Expected> Boolean waitForCssValue(final String property,final Expected expected,long timeOutInSeconds){
		return waitForCssValue(property, expected, timeOutInSeconds,Conditions.equals);
	}
	
	public <Expected> Boolean waitForCssValue(final String property,final Expected expected,long timeOutInSeconds,final Condition<Expected> validation){
		WebDriverWait wait = new WebDriverWait(WebManager.getDriver(),timeOutInSeconds);
		Boolean success = true;
		
		try {
			ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driver) {
					return validation.verify(expected, getCssValue(property));
				}
			};
			wait.until(expectedCondition);
		} catch(TimeoutException e){
			success = false;
		}
		return success;
	};
}
