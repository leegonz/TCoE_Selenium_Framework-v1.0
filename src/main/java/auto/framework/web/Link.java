package auto.framework.web;

import org.openqa.selenium.By;

public class Link extends Element {
	
	public Link(String linkText) {
		super(linkText+" link", By.linkText(linkText));
	}
	
	public Link(String linkText, Element parent) {
		super(linkText+" link", By.linkText(linkText),parent);
	}
	
	public Link(String name, By by, Element parent) {
		super(name, by, parent);
	}

	public Link(String name, By by) {
		super(name, by);
	}

	public Link(By by, Element parent) {
		super(by, parent);
	}

	public Link(By by) {
		super(by);
	}
	
	public static Link Partial(String linkText) {
		return new Link(linkText+" link", By.partialLinkText(linkText));
	}
	
	public static Link Partial(String linkText, Element parent) {
		return new Link(linkText+" link", By.partialLinkText(linkText),parent);
	}
	
	public static Link Partial(String name, By by, Element parent) {
		return new Link(name, by, parent);
	}

	public static Link Partial(String name, By by) {
		return new Link(name, by);
	}

	public static Link Partial(By by, Element parent) {
		return new Link(by, parent);
	}

	public static Link Partial(By by) {
		return new Link(by);
	}

}
