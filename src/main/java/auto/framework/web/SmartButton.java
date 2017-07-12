package auto.framework.web;

import org.openqa.selenium.By;

public class SmartButton extends Element {
	
	public SmartButton(String name,Element parent) {
		super(name+" button", By.xpath(getButtonXpath(name)),parent);
	}
	
	public SmartButton(String name) {
		super(name+" button", By.xpath(getButtonXpath(name)));
	}
	
	private final static String getButtonXpath(String name){
		name = name.replace(" ", "").replace("&", "And").toLowerCase();
		String src = "translate(@src,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";
		String start = "(contains("+src+",'/"+name+"') or contains("+src+",'_"+name+"'))";
		String end = "(contains("+src+",'"+name+".') or contains("+src+",'"+name+"_'))";
		String alt = "translate(@alt,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";
		return ".//*[("+start+" and "+end+") or " +alt+ "='" + name + "']";
	}
	
	@Override
	public Boolean isEnabled() {
		return isDisplayed() && (getAttribute("src").matches(".*(disabled|inactive).*")==false);
	}
}
