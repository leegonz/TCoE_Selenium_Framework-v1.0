package auto.framework.web;

import org.openqa.selenium.By;

public interface ISearchContext {
	
	public ISearchContext getParent();
	public Element findElement(By by);
	public ISearchContext findElement(String name, By by);

}
