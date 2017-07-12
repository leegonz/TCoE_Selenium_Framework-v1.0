package auto.framework;

import auto.framework.web.Page;

public class PageBase {
	
	public static Page page = new Page("Page Object",".*");
	
	/**
	 * Content and other validations
	 */
	public static class tests {
		
		public static void noDefinedTests() throws Exception {
			throw new NoSuchMethodException();
		}
		
	}

	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}
	
}



