package auto.framework.web;

import org.openqa.selenium.By;

@Deprecated
public class PartialLink extends Link {
	
	public PartialLink(String linkText) {
		super(linkText+" link", By.partialLinkText(linkText));
	}
	
	public PartialLink(String linkText, Element parent) {
		super(linkText+" link", By.partialLinkText(linkText),parent);
	}
	
	public PartialLink(String name, By by, Element parent) {
		super(name, by, parent);
	}

	public PartialLink(String name, By by) {
		super(name, by);
	}

	public PartialLink(By by, Element parent) {
		super(by, parent);
	}

	public PartialLink(By by) {
		super(by);
	}

}
