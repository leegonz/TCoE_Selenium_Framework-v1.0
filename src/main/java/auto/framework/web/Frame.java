package auto.framework.web;

import org.openqa.selenium.By;

import auto.framework.WebManager;

public class Frame extends Element {

	public Frame(Element element){
		super(element.name,element.by,element.parent);
	}
	
	public Frame(By by) {
		super(by);
	}
	
	public Frame(String name, By by) {
		super(name, by);
	}
	
	public Frame(String name, By by, Element parent) {
		super(name, by, parent);
	}
	
	public Frame(By by, Element parent) {
		super(by, parent);
	}
	
	@Override
	protected void switchFrame(){
		super.switchFrame();
		WebManager.getDriver().switchTo().frame(toWebElement());
		//System.out.println("Switch to Frame" + toWebElement().toString());
	}

}
