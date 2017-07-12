package auto.framework.web;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import auto.framework.ReportLog;

public class ListBox extends FormElement {
	
	public ListBox(Element element) {
		super(element.name, element.by, element.parent);
	}
	
	public ListBox(By by) {
		super(by);
	}
	
	public ListBox(String name, By by) {
		super(name, by);
	}
	
	public ListBox(String name, By by, Element parent) {
		super(name, by, parent);
	}
	
	public ListBox(By by, Element parent) {
		super(by, parent);
	}
	
	protected Select getHandler(){
		return new Select(toWebElement());
	} 
	
	public void selectByVisibleText(Enum<?> E){
		selectByVisibleText(E.toString());
	}
	
	public void selectByVisibleText(String text){
		Boolean success = false;
		try {
			getHandler().selectByVisibleText(text);
			success = true;
		} catch(Error e){}
		finally {
			ReportLog.logEvent(success, "[" + name + "] Select '"+ text +"'");
		}
	}
	
	public void selectByIndex(Integer index){
		Boolean success = false;
		try {
			getHandler().selectByIndex(index);
			success = true;
		} catch(Error e){}
		finally {
			ReportLog.logEvent(success, "[" + name + "] Select {index: "+ index +"}");
		}
	}
	
	public void selectByValue(Enum<?> E){
		selectByValue(E.toString());
	}
	
	/**selectByValue should be no longer used - CHANGE TO selectByVisibleText*/
	@Deprecated
	public void selectByValue(String value) {
		Boolean success = false;
		try {
			getHandler().selectByValue(value);
			success = true;
		} catch(Error e){}
		finally {
			ReportLog.logEvent(success, "[" + name + "] Select {value: "+ value +"}");
		}
	}
	
	@Override
	public String getValue(){
		return getHandler().getFirstSelectedOption().getText();
	}
	
	public Element option(Enum<?> E){
		return option(E.toString());
	}
	
	public Element option(String text){
		return new Option(text,this);
	}
	
	public ArrayList<String> getList(){
		ArrayList<String> curlist = new ArrayList<String>();
		for(WebElement element:getHandler().getOptions()){
			curlist.add(element.getText().toString());
		}
		return curlist;
	}
	
	
	
	private static class Option extends Element {
		
		private String text;

		public Option(String text, ListBox parent) {
			//translate is for &nbsp;
			//super(parent.name+" {option: \""+text+"\"}", By.xpath(".//option[normalize-space(translate(.,' ',' ') = " + escapeQuotes(text) + "]"), parent);
			//super(parent.name+" {option: \""+text+"\"}", By.xpath(".//option[normalize-space(translate(.,'"+StringEscapeUtils.unescapeHtml4("&nbsp;")+"',' ') = " + escapeQuotes(text) + "]"), parent);
			super(parent.name+" {option: \""+text+"\"}",null,parent);
			this.text = text;
		}
		/*
		@Override
		public Boolean isDisplayed(){
			if( !this.parent.isDisplayed() ) return false;
			if( !this.parent.isEnabled() ){
				String value = ((ListBox) this.parent).getValue();
				//System.err.println("Test if match label: "+value +" vs " + label);
				return value.equals(label);
			} else {
				//System.err.println("Test if found: "+escapeQuotes(label));
				return this.parent.toWebElement().findElements(by).size()!=0;
			}
		}*/
		@Override
		public WebElement toWebElement(){
		    // try to find the option via XPATH ...
		  	WebElement element = parent.toWebElement();
		    List<WebElement> options =
		    	element.findElements(By.xpath(".//option[normalize-space(.) = " + escapeQuotes(text) + "]"));

		    for (WebElement option : options) {
		      return option;
		    }

		    if (options.isEmpty() && text.contains(" ")) {
		      String subStringWithoutSpace = getLongestSubstringWithoutSpace(text);
		      List<WebElement> candidates;
		      if ("".equals(subStringWithoutSpace)) {
		        // hmm, text is either empty or contains only spaces - get all options ...
		        candidates = element.findElements(By.tagName("option"));
		      } else {
		        // get candidates via XPATH ...
		        candidates =
		            element.findElements(By.xpath(".//option[contains(., " +
		                escapeQuotes(subStringWithoutSpace) + ")]"));
		      }
		      for (WebElement option : candidates) {
		        if (text.equals(option.getText())) {
		          return option;
		        }
		      }
		    }
		    throw new NoSuchElementException("Cannot locate element with text: " + text);
		  }

		  private String getLongestSubstringWithoutSpace(String s) {
		    String result = "";
		    StringTokenizer st = new StringTokenizer(s, " ");
		    while (st.hasMoreTokens()) {
		      String t = st.nextToken();
		      if (t.length() > result.length()) {
		        result = t;
		      }
		    }
		    return result;
		  }
		  
		  private static String escapeQuotes(String toEscape) {
		    // Convert strings with both quotes and ticks into: foo'"bar -> concat("foo'", '"', "bar")
		    if (toEscape.indexOf("\"") > -1 && toEscape.indexOf("'") > -1) {
		      boolean quoteIsLast = false;
		      if (toEscape.lastIndexOf("\"") == toEscape.length() - 1) {
		        quoteIsLast = true;
		      }
		      String[] substrings = toEscape.split("\"");

		      StringBuilder quoted = new StringBuilder("concat(");
		      for (int i = 0; i < substrings.length; i++) {
		        quoted.append("\"").append(substrings[i]).append("\"");
		        quoted
		            .append(((i == substrings.length - 1) ? (quoteIsLast ? ", '\"')" : ")") : ", '\"', "));
		      }
		      return quoted.toString();
		    }

		    // Escape string with just a quote into being single quoted: f"oo -> 'f"oo'
		    if (toEscape.indexOf("\"") > -1) {
		      return String.format("'%s'", toEscape);
		    }

		    // Otherwise return the quoted string
		    return String.format("\"%s\"", toEscape);
		  }
		
	}

}
