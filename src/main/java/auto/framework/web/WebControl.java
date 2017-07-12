package auto.framework.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import auto.framework.ReportLog;
import auto.framework.WebManager;

public class WebControl {
	
	public static class Window {
		
		private final String handle;
		
		public Window(String handle) {
			this.handle = handle;
		}
		
		public Boolean isActive(){
			WebDriver driver = WebManager.getDriver();
			return driver.getWindowHandle().equals(handle);
		}
		
		public Boolean isOpen(){
			WebDriver driver = WebManager.getDriver();
			return driver.getWindowHandles().contains(handle);
		}
		
		protected Boolean switchWith() {
			if(isActive()) return true;
			if(!isOpen()) return false;
			try {
				WebDriver driver = WebManager.getDriver();
				driver.switchTo().window(handle);
			} catch(Throwable e) {}
			return isActive();
		}
		
		public Window open(String url){
			if(switchWith()){
				WebControl.open(url);
			}
			return this;
		}
		
		public Window close(){
			if(switchWith()){
				WebControl.close();
			}
			return this;
		}
		
		public Window refresh(){
			if(switchWith()){
				WebControl.refresh();
			}
			return this;
		}
		
		public Window waitForPageToLoad(long timeout) {
			WebDriver driver = WebManager.getDriver();
		    ExpectedCondition<Boolean> pageLoadCondition = new
		        ExpectedCondition<Boolean>() {
		            public Boolean apply(WebDriver driver) {
		                return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
		            }
		        };
		    WebDriverWait wait = new WebDriverWait(driver, timeout);
		    wait.until(pageLoadCondition);
		    return this;
		}
		
	}
	
	public static class WebFile {
		
		private final String url;
		private String localCopy;
		private BufferedOutputStream bufferedOutputStream;
		private String cookies;
		
		public WebFile(String url) {
			this.url = url;
			cookies = WebControl.getCookieString();
		}
		
		protected Boolean exists(){
			return new File(localCopy).exists();
		}
		
		public File getLocalCopy() {
			return new File(localCopy);
		}
		
		public String getFileName(){
			return FilenameUtils.getName(this.localCopy);
		}
		
		public WebFile verifyFileName(String expected){
			String fileName = getFileName();
			ReportLog.verifyTrue(fileName.equals(expected)||fileName.matches(expected), "File name matches \""+expected+"\"");
			return this;
		}
		
		public String getContentType() {
			String type="";
			try {
			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
			URL url = new URL(this.url);
			URLConnection con = url.openConnection();
			con.setRequestProperty("Cookie",cookies);	
			
			//type = con.getContentType();
			type = con.getHeaderField("Content-Type");
			
			} catch(Exception e){e.printStackTrace();}
			return type.split(";")[0];
		}
		
		public WebFile verifyContentType(ContentType type){
			ReportLog.verifyTrue(type.toString().equals(getContentType()), "Content-Type is \""+type.toString()+"\"");
			return this;
		}
		
		protected WebFile download() {
			if( this.localCopy==null || !new File(localCopy).exists() ){
				try {
					java.net.Proxy javaProxy = getNetProxy();
					//connect
					CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
					URL url = new URL(this.url);
					URLConnection con = null;
					if(javaProxy!=null){
						con = url.openConnection(javaProxy);
					} else {
						con = url.openConnection();
					}
					con.setRequestProperty("Cookie",cookies);	
					//get file name
					String filename;
					String raw = con.getHeaderField("Content-Disposition");
					if(raw != null && raw.indexOf("filename=") != -1) {
					    filename = raw.split("filename=")[1].replace("\"", "");
					} else {
						filename = FilenameUtils.getName(url.getPath());
					} 
					//create temp file
					File tmpDir = FileUtils.getTempDirectory();
					File sessionDir = new File(tmpDir, ((RemoteWebDriver) WebManager.getDriver()).getSessionId().toString() );
					if(sessionDir.exists() || sessionDir.mkdir()){
						tmpDir = sessionDir;
					}
					File tmpFile = new File(tmpDir,filename);
					System.err.println("File saved to: "+tmpFile.getCanonicalPath());
					//read / write
					InputStream stream = con.getInputStream();
					BufferedInputStream in = new BufferedInputStream(stream);
					FileOutputStream file = new FileOutputStream(tmpFile);
					bufferedOutputStream = new BufferedOutputStream(file);
					int i;
					while ((i = in.read()) != -1) {
						bufferedOutputStream.write(i);
					}
					bufferedOutputStream.flush();
					//save path of local copy
					this.localCopy = tmpFile.getCanonicalPath();
				} catch(Exception e){
					e.printStackTrace();
				}
				
				/*
				InputStream input = con.getInputStream();
				byte[] buffer = new byte[4096];
				int n = - 1;

				OutputStream output = new FileOutputStream( file );
				while ( (n = input.read(buffer)) != -1)
				{
				    if (n > 0)
				    {
				        output.write(buffer, 0, n);
				    }
				}
				output.close();
				*/
			}
			return this;
		}
		
	}
	
	private static void takeScreenshot64(){
		RemoteWebDriver driver = (RemoteWebDriver) WebManager.getDriver();
		/*WebDriver augmentedDriver = new Augmenter().augment(driver);
        File screenshot = ((TakesScreenshot)augmentedDriver).
                            getScreenshotAs(OutputType.FILE);*/
        if(!(driver instanceof TakesScreenshot)) {
        	driver = (RemoteWebDriver) new Augmenter().augment(driver);
        }
        try {
        	if (!(Boolean) driver.getCapabilities().getCapability(CapabilityType.TAKES_SCREENSHOT)) {
        		return;
        	}
        	 String screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        	 ReportLog.attachScreenshot("screenshot", screenshot);
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void takeScreenshot() {
		
		/*if( Boolean.valueOf(TestManager.Preferences.getPreference("reporter.screenshots.embed", "false")) ){
			takeScreenshot64();
			return;
		}*/
		
		RemoteWebDriver driver = (RemoteWebDriver) WebManager.getDriver();
		/*WebDriver augmentedDriver = new Augmenter().augment(driver);
        File screenshot = ((TakesScreenshot)augmentedDriver).
                            getScreenshotAs(OutputType.FILE);*/
        if(!(driver instanceof TakesScreenshot)) {
        	driver = (RemoteWebDriver) new Augmenter().augment(driver);
        }
        try {
        	if (!(Boolean) driver.getCapabilities().getCapability(CapabilityType.TAKES_SCREENSHOT)) {
        		return;
        	}
        	File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        	DateFormat dateFormatTimeStamp = new SimpleDateFormat("MM-dd-yyyy HH-mm-ss ");
        	String timestamp = dateFormatTimeStamp.format(new Date());
        	String filename = timestamp+ FilenameUtils.getName(screenshot.getCanonicalPath());
        	File tmpDir = new File("./src/test/resources/screenshots");//FileUtils.getTempDirectory();
			File sessionDir = new File(tmpDir, ((RemoteWebDriver) driver).getSessionId().toString() );
			if(sessionDir.exists() || sessionDir.mkdir()){
				tmpDir = sessionDir;
			}
			File tmpFile = new File(tmpDir,filename);
        	
        	FileUtils.copyFile(screenshot, tmpFile);
        	
        	ReportLog.attachFile("screenshot", tmpFile.toURI().toURL().toString());
        	
			//System.err.println(tmpFile.getCanonicalPath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //System.err.println(screenshot.getCanonicalPath());
	}
	
	public static WebFile download(Element src){
		String href=getLink(src);
		WebFile file = new WebFile(href);
		file.download();
		ReportLog.logEvent(file.exists(), "[" +src.getName()+ "] Download file");
		return file;
	}
	
	public static Window activeWindow(){
		WebDriver driver = WebManager.getDriver();
		String handle = driver.getWindowHandle();
	//	driver.switchTo().window(handle);
		return new Window(handle);
	}
	
	private static String fixURL(String url) {
		return url;
	}
	
	private static java.net.Proxy getNetProxy(){
		Proxy proxy = (Proxy) ((RemoteWebDriver) WebManager.getDriver()).getCapabilities().getCapability(CapabilityType.PROXY);
		java.net.Proxy javaProxy = null;
		if(proxy!=null){
			switch(proxy.getProxyType()){
			case AUTODETECT:
				break;
			case DIRECT:
				break;
			case MANUAL:
				String[] proxyAddress = proxy.getHttpProxy().split(":");
				javaProxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyAddress[0], Integer.valueOf(proxyAddress[1])));
				break;
			case PAC:
				break;
			case RESERVED_1:
				break;
			case SYSTEM:
				break;
			case UNSPECIFIED:
				break;
			default:
				break;
			}
		}
		return javaProxy;
	}
	
//	static final String COOKIES_HEADER = "Set-Cookie";
//	static java.net.CookieManager msCookieManager = new java.net.CookieManager();
//	
//	public static Window openAuth(String url, String username,String password){
//		
//		url = fixURL(url);
//		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
//		try {
//			URL urlObj = new URL(url);
//			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection(getNetProxy());
//			con.setRequestProperty("Cookie",getCookieString());	
//			
//			byte[] credBytes = (username + ":" + password).getBytes();
//	        @SuppressWarnings("restriction")
//			String credEncodedString = "Basic " + new BASE64Encoder().encode(credBytes);
//	        con.setRequestProperty("Authorization",credEncodedString);
//	        con.connect();
//	        
//	        Map<String, List<String>> headerFields = con.getHeaderFields();
//	        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
//
//	        if(cookiesHeader != null)
//	        {
//	            for (String cookie : cookiesHeader) 
//	            {
//	            	try {
//		            	HttpCookie httpCookie = HttpCookie.parse(cookie).get(0);
//		            	Cookie selCookie = new Cookie(httpCookie.getName(), httpCookie.getValue(), httpCookie.getDomain(), httpCookie.getPath(), null);
//		            	WebManager.getDriver().manage().addCookie(selCookie);
//	            	} catch(IllegalArgumentException e){}
//	            }
//	        }
//	        return open(url);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return activeWindow();
//	}
	
	public static Window openAuth(String url, String username, String password){
		
		url = fixURL(url);
		try {
			URL urlObj = new URL(url);
			String authority = username + ":"+password+"@"+urlObj.getAuthority().replaceAll(".*@", "");
			if(url.contains(urlObj.getProtocol())){
				url = url.replace(urlObj.getProtocol()+"://"+urlObj.getAuthority(),urlObj.getProtocol()+"://"+authority);
			} else {
				url = url.replace(urlObj.getAuthority(),authority);
			}
		} catch (MalformedURLException e) {
		} return open(url);
	}

	public static Window open(String url) {
		
		try {
			URL absoluteURL;
			try {
					absoluteURL = new URL(url);
				} catch (MalformedURLException e){
					
					absoluteURL = new URL(WebManager.getDriver().getCurrentUrl());
					
					return open(absoluteURL,url);
						}	
				return open(null,url);
			} catch (MalformedURLException e) {
				return open(null,url);
		}		
	}
	
public static Window navigateTo(String url) {
		
		try {
			URL absoluteURL;
		try {
				absoluteURL = new URL(url);
			} catch (MalformedURLException e){
				
				absoluteURL = new URL(WebManager.getDriver().getCurrentUrl());
				
				return navigateTo(absoluteURL,url);
					}	
			return navigateTo(null,url);
		} catch (MalformedURLException e) {
			return navigateTo(null,url);
		}		
	}

public static Window navigateTo(URL baseUrl, String relativeUrl) {
	Window activeWin = activeWindow();
	try{
		URL newURL;
		try {
			newURL = new URL(baseUrl,relativeUrl);
		} catch(MalformedURLException e){
			newURL = new URL(relativeUrl);
		}
		
		try {
			WebManager.getDriver().navigate().to(newURL.toString());
		}catch(TimeoutException e){

		}
	} catch(Error|MalformedURLException error){
		
		error.printStackTrace();
	}
	return activeWin;
}
	
	public static Window open(Element linkedElement){
		String href=getLink(linkedElement);
		return open(href);
	}
	
	public static Window openNew(Element linkedElement){
		String href=getLink(linkedElement);
		return openNew(href);
	}
	
	public static String getLink(Element linkedElement){
		String href=linkedElement.findElement(By.xpath("./descendant-or-self::*[@href]|.//ancestor::a")).getAttribute("href");
		return href;
	}
	
	public static Window open(URL baseUrl, String relativeUrl) {
		Window activeWin = activeWindow();
		try{
			URL newURL;
			try {
				newURL = new URL(baseUrl,relativeUrl);
			} catch(MalformedURLException e){
				newURL = new URL(relativeUrl);
			}
			
			try {
				WebManager.getDriver().navigate().to(newURL.toString());
				//ReportLog.setTestStep("Navigate to page and check that page has loaded");
				ReportLog.logEvent(true, "Navigate to " + relativeUrl);
				//ReportLog.passed( relativeUrl+ " Page has loaded");
			}catch(TimeoutException e){
				ReportLog.logEvent(false, "Navigate to " + relativeUrl);
				//ReportLog.failed(relativeUrl + " has not loaded");
			}
		} catch(Error|MalformedURLException error){
			
			error.printStackTrace();
		}
		return activeWin;
	}
	
	//new open method
	  public static Window openPage(String url, String PageName) { 
		//pageLoadTimeout();
          try { 
                  URL absoluteURL; 
                  try { 
                          absoluteURL = new URL(url); 
                  } catch (MalformedURLException e){ 
                          absoluteURL = new URL(WebManager.getDriver().getCurrentUrl()); 
                          return openPageName(absoluteURL,url,PageName); 
                  } 
                  return openPageName(null,url, PageName); 
          } catch (MalformedURLException e) { 
                  return openPageName(null,url,PageName); 
          } 
  } 
	  
	  //copy of open(URL, string) this has been modified.
	public static Window openPageName(URL baseUrl, String relativeUrl, String PageName) {
		Window activeWin = activeWindow();
		try{
			URL newURL;
			try {
				newURL = new URL(baseUrl,relativeUrl);
			} catch(MalformedURLException e){
				newURL = new URL(relativeUrl);
			}
			
			try {
				WebManager.getDriver().navigate().to(newURL.toString());
				//ReportLog.setTestStep("Navigate to page and check that page has loaded");
				ReportLog.logEvent(true, "Navigate to " + PageName);
				//ReportLog.passed( PageName+ " Page has loaded");
			}catch(TimeoutException e){
				ReportLog.logEvent(false, "Navigate to " + PageName);
				//ReportLog.passed( PageName + " has not loaded");
			}
		} catch(Error|MalformedURLException error){
			
			error.printStackTrace();
		}
		return activeWin;
	}
	

	
	public static Window openNew(String url){
		String newHandle=null;
		try{
			WebDriver driver = WebManager.getDriver();
			((JavascriptExecutor)driver).executeScript("window.open();");
			Set<String> winHandles = driver.getWindowHandles();
			newHandle = (String) winHandles.toArray()[winHandles.size()-1];
			driver.switchTo().window(newHandle);
			driver.navigate().to(url);
			ReportLog.logEvent(true, "Navigate to " + url);
		} catch(Error error){
			ReportLog.logEvent(false, "Navigate to " + url);
			error.printStackTrace();
		}
		return new Window(newHandle);
	}
	
	public static Window switchWithTop(Integer index){
		index = Math.max(1,index);
		WebDriver driver = WebManager.getDriver();
		Set<String> winHandles = driver.getWindowHandles();
		String newHandle = (String) winHandles.toArray()[Math.max(winHandles.size()-index,0)];
		driver.switchTo().window(newHandle);
		return new Window(newHandle);
	}	
	
	public static Window waitForPageToLoad(long timeout){
		return activeWindow().waitForPageToLoad(timeout);
	}
	
	public static Window back(){
		Window activeWin = activeWindow();
		try{
			WebManager.getDriver().navigate().back();
			ReportLog.logEvent(true, "Navigate back");
		} catch(Error error){
			ReportLog.logEvent(false, "Navigate back");
		}
		return activeWin;
	}
	
	public static Window refresh(){
		Window activeWin = activeWindow();
		try{
			WebManager.getDriver().navigate().refresh();
			ReportLog.logEvent(true, "Refresh page");
		} catch(Error error){
			ReportLog.logEvent(false, "Refresh page");
		}
		return activeWin;
	}
	
	public static Window clearData(){
		Window activeWin = activeWindow();
		try{
			WebManager.getDriver().manage().deleteAllCookies();
			ReportLog.logEvent(true, "Clear data");
		} catch(Error error){
			ReportLog.logEvent(false, "Clear data");
		}
		return activeWin;
	}
	
	public static Window close(){
		Window activeWin = activeWindow();
		try{
			WebDriver driver = WebManager.getDriver();
			driver.close();
			Set<String> winHandles = driver.getWindowHandles();
			if(winHandles.size()>0){
				String newHandle = (String) winHandles.toArray()[winHandles.size()-1];
				driver.switchTo().window(newHandle);
			}
			ReportLog.logEvent(true, "Close page");
		} catch(Error error){
			ReportLog.logEvent(false, "Close page");
		}
		return activeWin;
	}
	
	private static HashMap<String,WebData> getWinHash(){
		WebDriver driver = WebManager.getDriver();
		String origHandle = driver.getWindowHandle(); 
		HashMap<String,WebData> winHash = new HashMap<String, WebData>();
		for(String winHandle : driver.getWindowHandles()){
			driver.switchTo().window(winHandle);
			WebData data = new WebData();
			data.title = driver.getTitle();
			data.url = driver.getCurrentUrl();
			winHash.put(winHandle, data);
		}
		driver.switchTo().window(origHandle);
		return winHash;
	}
	
	public static Window switchWithUrl(String url){
		String handle=null;
		try{
			WebDriver driver = WebManager.getDriver();
			HashMap<String,WebData> winHash = getWinHash();
			for(Entry<String, WebData> hash : winHash.entrySet()){
				WebData winData = hash.getValue();
				if(winData.url.equals(url)||winData.url.matches(url)){
					handle = hash.getKey();
					driver.switchTo().window(handle);
					ReportLog.logEvent(true, "Switch to Page {url:"+url+"}");
					return new Window(handle);
				}
			}
			return new Window(handle);
		} catch(Error error){
			ReportLog.logEvent(false, "Switch to Page {url:"+url+"}");
			return new Window(handle);
		}
	}
	
	public static Window switchWithTitle(String title){
		String handle=null;
		try{
			WebDriver driver = WebManager.getDriver();
			HashMap<String,WebData> winHash = getWinHash();
			for(Entry<String, WebData> hash : winHash.entrySet()){
				WebData winData = hash.getValue();
				if(winData.title.equals(title)||winData.title.matches(title)){
					handle = hash.getKey();
					driver.switchTo().window(handle);
				}
			}
			ReportLog.logEvent(true, "Switch to Page {title:"+title+"}");
		} catch(Error error){
			ReportLog.logEvent(false, "Switch to Page {title:"+title+"}");
		}
		return new Window(handle);
	}
	
	@Deprecated
	public static void waitForPageToLoad(){
		//TODO
	}
	
	public static String getContentType() {
	/*	HttpClient client = new DefaultHttpClient();
		String currentURL = WebManager.getDriver().getCurrentUrl();
		HttpHead httphead = new HttpHead(currentURL);
		org.apache.http.HttpResponse response = null;
		try {
			response = client.execute(httphead);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Header contenttypeheader = response.getFirstHeader("Content-Type");
		return contenttypeheader.getValue();	*/
		String type="";
		try {
		String cookies = WebControl.getCookieString();
		
		URL url = new URL(WebManager.getDriver().getCurrentUrl());
		URLConnection con = url.openConnection();
		con.setRequestProperty("Cookie",cookies);		
		type = con.getHeaderField("Content-Type");
		
		} catch(Exception e){e.printStackTrace();}
		return type.split(";")[0];
	}
	
	public static void verifyContentType(ContentType type){
		ReportLog.verifyTrue(type.toString().equals(getContentType()), "Content-Type is \""+type.toString()+"\"");
	}
	
	private static String getCookieString(){
		RemoteWebDriver driver = (RemoteWebDriver) WebManager.getDriver();
		Iterator<Cookie> cookies = driver.manage().getCookies().iterator();
		String cookieString="";
		while(cookies.hasNext()){
			Cookie cookie = cookies.next();
			cookieString+=cookie.getName()+"="+cookie.getValue()+";";
		}
		return cookieString;
	}
	
	public static String getCurrentUrl(){
		WebDriver driver = WebManager.getDriver();
		return driver.getCurrentUrl();
	}
	
	public static void verifyURL(String url){
		String currentURL = getCurrentUrl();
		ReportLog.verifyTrue(currentURL.equalsIgnoreCase(url) || currentURL.matches(url), "Verify that url matches \""+url+"\"");
	}

   public static void pageLoadTimeout(){
		WebDriver driver = WebManager.getDriver();
		driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
//		ReportLog.addInfo("Page Timeout set to 15 seconds.");
		}
   
   public static void acceptAlert() {
	    try {
	    	WebDriver driver = WebManager.getDriver();
	        WebDriverWait wait = new WebDriverWait(driver, 2);
	        wait.until(ExpectedConditions.alertIsPresent());
	        Alert alert = driver.switchTo().alert();
	        alert.accept();
	    } catch (Exception e) {
	        //exception handling
	    }
	}
   
   public static void dismissAlert() {
	    try {
	    	WebDriver driver = WebManager.getDriver();
	        WebDriverWait wait = new WebDriverWait(driver, 2);
	        wait.until(ExpectedConditions.alertIsPresent());
	        Alert alert = driver.switchTo().alert();
	        alert.dismiss();
	    } catch (Exception e) {
	        //exception handling
	    }
	}
  

   //Authentication not yet tested
//   
   
   //only can switch tab for Firefox browser
    public static void switchTab(){
    	WebDriver driver = WebManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, 4);
        Actions action= new Actions(driver);
        action.keyDown(Keys.CONTROL).sendKeys(Keys.TAB).build().perform();
    }
//    
//    public static void switchTabLeft(){
//    	WebDriver driver = WebManager.getDriver();
//        WebDriverWait wait = new WebDriverWait(driver, 4);
//        Actions action= new Actions(driver);
//        action.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).sendKeys(Keys.TAB).build().perform();
//    }
/*	public static String download(Element src) throws IOException, URISyntaxException{
		RemoteWebDriver driver = (RemoteWebDriver) WebManager.getDriver();
		Iterator<Cookie> cookies = driver.manage().getCookies().iterator();

		
		String href=src.getAttribute("href");
		String filename="";
		SessionId session=driver.getSessionId();
		
		// instantiate CookieManager
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
        CookieStore cookieJar =  manager.getCookieStore();

        // add cookie to CookieStore for a
        // particular URL
        
		URL url = new URL(new URL(driver.getCurrentUrl()),href);
		URI uri = new URI(url.getProtocol()+"://"+url.getHost()+":"+url.getPort());
        
        while(cookies.hasNext()){
        	
			Cookie seleniumCookie = cookies.next();
			
			System.err.println("Cookie");
			System.out.println(seleniumCookie.getName()+"="+seleniumCookie.getValue());
			cookieJar.add(uri, new HttpCookie(seleniumCookie.getName(), seleniumCookie.getValue()));
			
		}
		

		
		File tmpDir = FileUtils.getTempDirectory();
		File file = new File(tmpDir,filename);
		
		HttpDownloadUtility.downloadFile(url.toExternalForm(), file.getCanonicalPath(), session.toString());
		
		return file.getCanonicalPath();
	}	*/
	/*
	
	private static String downloadAs(String href, String filename) throws IOException {
		URL url = new URL(new URL(WebManager.getDriver().getCurrentUrl()),href);
		URLConnection con = url.openConnection();
		
		String raw = con.getHeaderField("Content-Disposition");
		
		if(raw != null && raw.indexOf("filename=") != -1) {
		    filename = raw.split("filename=")[1];
		} else {
			filename = FilenameUtils.getName(url.getPath());
		}
		
		File tmpDir = FileUtils.getTempDirectory();
		
		File file = new File(tmpDir,filename);
		
		FileUtils.copyURLToFile(url,file);
		return file.getCanonicalPath();
	}	*/
  
}

class WebData {
	
	protected String url;
	protected String title;
	
}
