package auto.framework;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.openqa.selenium.safari.SafariDriver;

public class WebManager {

    private static ThreadLocal<WebDriver> webDriver = new InheritableThreadLocal<WebDriver>();
    private static Boolean debugMode = false;
    public static String browser= "";
    public static WebDriver getDriver() {
        return webDriver.get();
    }
    
    public static void endDriver() throws InterruptedException{
    	if(!debugMode){
	    	WebDriver driver = WebManager.getDriver();
	    	if(driver!=null)
	    	{				    		    	
	    					driver.close();
	    					driver.quit();
	    	}}
    }
    
    public static String getBrowserName() {
    	WebDriver driver = WebManager.getDriver();
		Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
		String browserName = caps.getBrowserName();
    	return browserName;
    }
    
    public static WebDriver startDriver() throws IOException{
    	return startDriver(getBrowserName());
    }
    
    public static WebDriver startDriver(String browserName) throws IOException {
    	
    	if(browserName==null) {
    		browserName="firefox";
    	}
    	browser = browserName;
    	System.out.println("Create Driver: "+browserName);
    	System.setProperty("webdriver.reap_profile", "true");

    	//browserName = TestManager.Preferences.getPreference("browser.override",browserName);
    	String forceBrowser = TestManager.Preferences.getPreference("browser.override","false");
    	if(!forceBrowser.trim().equalsIgnoreCase("false")){
    		browserName = forceBrowser;
    	}
    	
    	WebDriver driver = null;
    	
    	try {
	    	switch(browserName.trim().toLowerCase()){
	    		case "chrome":
	    			driver = newChromeDriver();
	    			break;
	    		case "internet explorer":
	    		case "ie":
	    			driver = newInternetExplorerDriver();
	    			break;
//	    		case "htmlunit":
//	    			driver = newHtmlUnitDriver();
//	    			break;
//	    		case "safari":
//	    			driver = newSafariDriver();
//	    			break;
	    		case "firefox":
	    			driver = newFirefoxDriver();
	    			break;
	    		case "firefox -debug":
	    			driver = newFirefoxDebugDriver();
	    			break;
	    		default:
	    			driver = newFirefoxDriver();
	    			break;
	    	}
    	} catch(Error error){
    		System.out.println("Error: "+error.getMessage());
    		throw error;
    	}

    	Timeouts timeouts = driver.manage().timeouts();
    	Window window = driver.manage().window();
    	try {
    		//System.out.println("pageLoadTimeout: "+Preferences.pageLoadTimeOut());
    		//System.out.println("implicitlyWait: "+Preferences.implicitlyWait());
	    	timeouts.pageLoadTimeout(Preferences.pageLoadTimeOut(), TimeUnit.SECONDS);
	    	timeouts.implicitlyWait(Preferences.implicitlyWait(), TimeUnit.SECONDS);
	    	timeouts.setScriptTimeout(Preferences.setScriptTimeout(), TimeUnit.SECONDS);
    	} catch(WebDriverException e){}
    	try {
    		driver.switchTo().activeElement().sendKeys(Keys.chord(Keys.CONTROL,"0")); //zoom to 100%
    	} catch(WebDriverException e){}
    	try {
    		System.out.println("maximize: "+Preferences.maximize());
    		window.maximize();
    		if(Preferences.maximize()) window.maximize();
    	} catch(WebDriverException e){System.out.println(e);}
    	return setDriver(driver);
    }
    
    public static WebDriver setDriver(String browserName, String profile) throws IOException {
    	return startDriver(browserName);
    }
 
    public static WebDriver setDriver(WebDriver driver) {
    	try {
    		assert(driver!=null);
    		webDriver.set(driver);
    	} catch(Error error){
    		throw new Error("Driver is null");
    	}
        
        return getDriver();
    }

    private static WebDriver newFirefoxDriver(){
    	
    	System.setProperty("webdriver.firefox.useExisting", "false");
    	System.setProperty("webdriver.reap_profile", "false");
    	
//    	System.setProperty("webdriver.firefox.marionette", System.getProperty("user.dir") + "/webdriver/geckodriver.exe");
    	String userAgent= TestManager.Preferences.getPreference("UserAgent");
    	
    	ProfilesIni profile = new ProfilesIni();
    	FirefoxProfile myprofile = profile.getProfile("default");
    	//myprofile.setPreference("webdriver.reap_profile", "false");
    	
    	if (userAgent!=null) {
    		myprofile.setPreference("general.useragent.override", UserAgentList.valueOf(userAgent).toString()); // here, the user-agent is 'Yahoo Slurp'
    	}
    	
    	File ffPath = new File("C:\\Mozilla Firefox\\firefox.exe");
		FirefoxBinary binary = new FirefoxBinary();
    	if(ffPath.exists()){//test
        	System.out.println("Loading custom driver for Jenkins..");
    		binary = new FirefoxBinary(ffPath);
    	}
    	DesiredCapabilities capabilities = DesiredCapabilities.firefox();
    	//capabilities.setCapability(FirefoxDriver.PROFILE, myprofile);
    	Proxy proxy = getProxySettings();
    	String noProxy = proxy.getNoProxy();
    	proxy.setNoProxy(noProxy!=null ? noProxy.replace(";", ",") : noProxy);
    	capabilities.setCapability(CapabilityType.PROXY, proxy);
    	return new FirefoxDriver(binary,myprofile,capabilities);
    	//return new FirefoxDriver(myprofile);
		//return new FirefoxDriver();
	}
    
    protected static Proxy getProxySettings(){
    	Proxy proxy = new org.openqa.selenium.Proxy();
    	
    	String proxyType = TestManager.Preferences.getPreference("webdriver.proxy.proxyType", "unspecified").toLowerCase();
    	
    	switch(proxyType){
    	case "direct":
    		proxy.setProxyType(ProxyType.DIRECT);
        	break;
    	case "pac":
    		proxy.setProxyType(ProxyType.PAC)
    			.setProxyAutoconfigUrl(TestManager.Preferences.getPreference("webdriver.proxy.proxyAutoconfigUrl"));
    		break;
    	case "manual":
    		String allProxy, ftpProxy, httpProxy, sslProxy, socksProxy, socksUsername, socksPassword, noProxy;
    		allProxy = TestManager.Preferences.getPreference("webdriver.proxy.allProxy",null);
    		if(allProxy!=null){
    			ftpProxy = httpProxy = sslProxy = socksProxy = allProxy;
    		} else {
	    		ftpProxy = TestManager.Preferences.getPreference("webdriver.proxy.ftpProxy","");
	    		httpProxy = TestManager.Preferences.getPreference("webdriver.proxy.httpProxy","");
	    		sslProxy = TestManager.Preferences.getPreference("webdriver.proxy.sslProxy","");
	    		socksProxy = TestManager.Preferences.getPreference("webdriver.proxy.socksProxy","");
    		}
    		socksUsername = TestManager.Preferences.getPreference("webdriver.proxy.socksUsername","");
    		socksPassword = TestManager.Preferences.getPreference("webdriver.proxy.socksPassword","");
    		noProxy = TestManager.Preferences.getPreference("webdriver.proxy.noProxy","");
        	proxy.setProxyType(ProxyType.MANUAL)
        		.setNoProxy(noProxy)
        		.setHttpProxy(httpProxy)
	    	    .setFtpProxy(ftpProxy)
	    	    .setSslProxy(sslProxy);
        	proxy.setSocksProxy(socksProxy)
	        	.setSocksUsername(socksUsername).setSocksPassword(socksPassword);
        	break;
    	case "system":
    		proxy.setProxyType(ProxyType.SYSTEM);
    		break;
    	default:
    		proxy.setProxyType(ProxyType.UNSPECIFIED);
    		break;
    	}
    	return proxy;
    }
    
    private static WebDriver newFirefoxDebugDriver(){
    	debugMode=true;
    	
    	System.setProperty("webdriver.firefox.useExisting", "true");
    	System.setProperty("webdriver.reap_profile", "false");
    	Boolean isRunning = false;
    	try {
    		final Socket socket = new Socket();
    		socket.connect(new InetSocketAddress("localhost",7055));
    		socket.close();
    		isRunning = true;
    	} catch(final IOException io){
    		//io.printStackTrace();
    	}
    	if(!isRunning){
//    		ProfilesIni profile = new ProfilesIni();
//        	final FirefoxProfile myprofile = profile.getProfile("default");
//        	return new FirefoxDriver(myprofile);
    		return newFirefoxDriver();
    	} else {
    		try {
    			final WebDriver existingWebDriver = new RemoteWebDriver(new URL("http://localhost:7055/hub"), DesiredCapabilities.firefox());
				System.out.println("Using existing..");
    			return existingWebDriver;
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		return null;
	}
    
    private static WebDriver newChromeDriver() throws IOException{
    	
    	String chromeDriverPath;
    	
    	try {
    		chromeDriverPath = "C:/Workspace/TCoE_Selenium_Framework v1.0/src/main/resources/webdriver/chromedriver.exe";//JarFileToLocal.copyTmp("/webdriver/chromedriver.exe").getCanonicalPath();//WebManager.class.getClass().getResource("/webdriver/chromedriver.exe").getFile().replace("%20", " ");
    		//JarFileToLocal.copyTmp("/webdriver/chromedriver.exe").getCanonicalPath();
    		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
    		System.out.println("Chrome Driver: "+chromeDriverPath);
    	} catch(Error error){
    		throw new Error("Chrome Driver not found");
    	}
    	
    	//return new ChromeDriver();
    	
    	System.setProperty("webdriver.chrome.driver", chromeDriverPath);
    	

		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		
		
		ChromeOptions options = new ChromeOptions();
	    options.addArguments("test-type");
	    options.addArguments("start-maximized"); 
	    //TODO 07/18/2016 remove comment in the script once familiar with chrome settings
	    //------------------------------------------------------------------------
		//capabilities.setCapability(CapabilityType.PROXY, getProxySettings());
	    //------------------------------------------------------------------------
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
    	
		return new ChromeDriver(capabilities);
    	
    	/*ChromeOptions options = new ChromeOptions();
    	//options.addExtensions(new File("/path/to/extension.crx"));
    	options.setBinary(new File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe"));
    	
    	DesiredCapabilities capabilities = DesiredCapabilities.chrome();
    	capabilities.setCapability(ChromeOptions.CAPABILITY, options);
    	//capabilities.setCapability("chrome.binary", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
    	//capabilities.setCapability(RemoteDriver, value);
    	
    	try {
			return new RemoteWebDriver(new URL("http://localhost:7055/hub"), capabilities);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
*/    	
		//return new ChromeDriver(capabilities);	
	}
    
    //Chrome Debug Test
    private static WebDriver newChromeDebugDriver() throws IOException{
    	debugMode=true;
    	String baseUrl;
    
    	System.setProperty("webdriver.chrome.useExisting", "true");
    	System.setProperty("webdriver.reap_profile", "false");
    	Boolean isRunning = false;
    	
    	try {
    		final Socket socket = new Socket();
    		socket.connect(new InetSocketAddress("localhost", 9515));
    		socket.close();
    		//var driver = new webdriver.Builder().withCapabilities(webdriver.Capabilities.chrome()).usingServer("http://localhost:9515")  // <- this.build();
    		isRunning = true;
    	} catch(final IOException io){
    		//io.printStackTrace();
    	}
    	
    	//String webDriverURL = "http://" + environmentData.getHubIP() + ":" + environmentData.getHubPort() + "/wd/hub";
    	if(!isRunning){
//    		ProfilesIni profile = new ProfilesIni();
//        	final FirefoxProfile myprofile = profile.getProfile("default");
//        	return new FirefoxDriver(myprofile);
    		return newChromeDriver();
    	} else {
    		try {
    			final WebDriver existingWebDriver = new RemoteWebDriver(new URL("http://localhost:19914"), DesiredCapabilities.chrome());
    			System.out.println("Using existing..");
    			return existingWebDriver;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		return null;
    	
    	/*ChromeOptions options = new ChromeOptions();
    	//options.addExtensions(new File("/path/to/extension.crx"));
    	options.setBinary(new File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe"));
    	
    	DesiredCapabilities capabilities = DesiredCapabilities.chrome();
    	capabilities.setCapability(ChromeOptions.CAPABILITY, options);
    	//capabilities.setCapability("chrome.binary", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
    	//capabilities.setCapability(RemoteDriver, value);
    	
    	try {
			return new RemoteWebDriver(new URL("http://localhost:7055/hub"), capabilities);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
*/    	
		//return new ChromeDriver(capabilities);	
	}
    
    private static WebDriver newInternetExplorerDriver() throws IOException{
    	try {
    		String arch = System.getenv("PROCESSOR_ARCHITECTURE");
    		String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

    		String realArch = arch.endsWith("64")
    		                  || wow64Arch != null && wow64Arch.endsWith("64")
    		                      ? "64" : "32";
    		
    		String ieDriverPath;
    	
    		switch(realArch){
    		case "64":
    			ieDriverPath = JarFileToLocal.copyTmp("/webdriver/IEDriverServer_64.exe").getCanonicalPath();
    			break;
    		case "32":
    		default:
    			ieDriverPath = JarFileToLocal.copyTmp("/webdriver/IEDriverServer_32.exe").getCanonicalPath();
    			break;
    		}
    		
    		//String ieDriverPath = JarFileToLocal.copyTmp("/webdriver/IEDriverServer.exe").getCanonicalPath();//WebManager.class.getClass().getResource("/webdriver/IEDriverServer.exe").getFile().replace("%20", " ");
    		System.setProperty("webdriver.ie.driver",ieDriverPath);
    		System.out.println("IE Driver: " + ieDriverPath);
    	} catch(Error error){
    		throw new Error("IE Driver not found");
    	}
		
    	DesiredCapabilities capabilities = getIECapabilities();
    	
    	//capabilities.setCapability(InternetExplorerDriver.LOG_LEVEL, "FATAL");
    
    	//HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Internet Explorer\Main\FeatureControl\FEATURE_BFCACHE
    	WinRegistry.add(WinRegistry.HKEY_CURRENT_USER, "software\\microsoft\\internet explorer\\main\\FeatureControl\\FEATURE_BFCACHE", "iexplore.exe", WinRegistry.REG_DWORD, 0);
    	//HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Microsoft\Internet Explorer\Main\FeatureControl\FEATURE_BFCACHE
    	WinRegistry.add(WinRegistry.HKEY_CURRENT_USER, "software\\wow6432node\\microsoft\\internet explorer\\main\\FeatureControl\\FEATURE_BFCACHE", "iexplore.exe", WinRegistry.REG_DWORD, 0);
    	
    	Proxy proxy = getProxySettings();
		
    	//TODO 07/18/2016 remove comment in the script once familiar with ie settings
	    //------------------------------------------------------------------------
		//capabilities.setCapability(CapabilityType.PROXY, proxy);
    	//------------------------------------------------------------------------
    	
		WebDriver driver;
		
    	try {
    		driver = new InternetExplorerDriver(capabilities);
    	} catch(SessionNotFoundException e){
    		if(e.getMessage().contains("TabProcGrowth")){
    			System.err.println("Setting TabProcGrowth to 0");
    			//reg add "hkcu\software\microsoft\internet explorer\main" /v TabProcGrowth /t reg_dword /d 0 /f
    			WinRegistry.add(WinRegistry.HKEY_CURRENT_USER, "software\\microsoft\\internet explorer\\main", "TabProcGrowth", WinRegistry.REG_DWORD, 0);
    		}
    		driver = new InternetExplorerDriver(capabilities);
    	}
    	WinRegistry.add(WinRegistry.HKEY_CURRENT_USER, "software\\microsoft\\windows\\currentversion\\internet settings", "ProxyOverride", WinRegistry.REG_SZ, proxy.getNoProxy());
//    	driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    	return driver;
	}
    
    private static DesiredCapabilities getIECapabilities(){
    	DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
    	//capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true); //should be manual
    	capabilities.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, true);
    	capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false); //test (orig=false)
    	capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false); //test
    	capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false); //test
    	//capabilities.setCapability(InternetExplorerDriver.IE_USE_PRE_PROCESS_PROXY, true); //test
    	//capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
    	capabilities.setCapability(InternetExplorerDriver.IE_SWITCHES, "-private");
    	capabilities.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, "about:blank");
    	capabilities.setCapability(InternetExplorerDriver.SILENT, true);
    	capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
    	return capabilities;
    }
    
    /*
    private static DesiredCapabilities getIECapabilities(){
    	DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
    	//capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true); //should be manual
    	capabilities.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, true);
    	capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, true); //test (orig=false)
    	capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true); //test
    	capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true); //test
    	//capabilities.setCapability(InternetExplorerDriver.IE_USE_PRE_PROCESS_PROXY, true); //test
    	//capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
    	capabilities.setCapability(InternetExplorerDriver.IE_SWITCHES, "-private");
    	capabilities.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, "about:blank");
    	capabilities.setCapability(InternetExplorerDriver.SILENT, true);
    	capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
    	return capabilities;
    } */
    
    private static WebDriver newHtmlUnitDriver(){
    	DesiredCapabilities capabilities = DesiredCapabilities.htmlUnitWithJs();
    	HtmlUnitDriver driver = new HtmlUnitDriver(capabilities);
		return driver;
	}
    
    private static WebDriver newSafariDriver(){
    	DesiredCapabilities capabilities = DesiredCapabilities.safari();
    	SafariDriver driver = new SafariDriver(capabilities);
    	//Timeouts timeouts = driver.manage().timeouts();
    	//timeouts.pageLoadTimeout(-1, TimeUnit.SECONDS);
    	//timeouts.implicitlyWait(2, TimeUnit.SECONDS);
		return driver;
	}
    
    //will make a separate class for this
    protected static class Configuration {
    	
    	public static String readProperty(String property) {
    		String configPath = "./src/test/resources/config/defaults.properties";
    		File configFile = new File( configPath );
    		if(configFile.exists()){
    			try {
    				FileInputStream fileInput = new FileInputStream(configFile);
        			Properties properties = new Properties();
					properties.load(fileInput);
					return properties.getProperty(property);
				} catch (Exception e) {
				}
    		}
    		return null;
    	}
    	
    }
    
    protected static class Preferences {
    	
    	public static long pageLoadTimeOut(){
    		return Long.valueOf(
    			TestManager.Preferences.getPreference("webdriver.timeouts.pageLoadTimeOut","-1")
    		);
    	}
    	
    	public static long implicitlyWait(){
    		return Long.valueOf(
    			TestManager.Preferences.getPreference("webdriver.timeouts.implicitlyWait","2")
    		);
    	}
    	
    	public static long setScriptTimeout(){
    		return Long.valueOf(
    			TestManager.Preferences.getPreference("webdriver.timeouts.setScriptTimeout","-1")
    		);
    	}
    	
    	
    	
    	public static boolean maximize(){
    		return Boolean.valueOf(
    			TestManager.Preferences.getPreference("webdriver.window.maximize","false")
    		);
    	}
    	
    }
    
    protected static class JarFileToLocal {
    	
    	public static File copyTmp(String jarPath) throws IOException {
    		//String fileName = new File(jarPath).getName();
    		File tmpDir = FileUtils.getTempDirectory();
    		File file = new File(tmpDir,jarPath);
    		if(file.exists()) return file;
    		
    		File fileDir = file.getParentFile();
    		
    		if(fileDir.exists()||fileDir.mkdir()){
    			InputStream stream = JarFileToLocal.class.getResourceAsStream(jarPath);
    			if (stream == null) {
    		        //warning
    		    }
    		    OutputStream resStreamOut = null;
    		    int readBytes;
    		    byte[] buffer = new byte[4096];
    		    try {
    		        resStreamOut = new FileOutputStream(file);
    		        while ((readBytes = stream.read(buffer)) > 0) {
    		            resStreamOut.write(buffer, 0, readBytes);
    		        }
    		    } catch (IOException e1) {
    		        e1.printStackTrace();
    		    } finally {
    		        stream.close();
    		        if(resStreamOut!=null) resStreamOut.close();
    		    }
    		}
    		return file;
    	}
    	
    }
    
    
	public static class WinRegistry {
		
		//public static void 
		
		public static final String HKEY_CLASSES_ROOT = "hkcr";
		public static final String HKEY_LOCAL_MACHINE = "hklm";
		public static final String HKEY_CURRENT_USER = "hkcu";
		public static final String HKEY_USERS = "hku";
		public static final String HKEY_CURRENT_CONFIG = "hkcc";
		
		public static final String REG_DWORD = "reg_dword";
		public static final String REG_SZ = "reg_sz";
		
		//reg add "hkcu\software\microsoft\internet explorer\main" /v TabProcGrowth /t reg_dword /d 0 /f
		
		//reg add KeyName [/v EntryName|/ve] [/t DataType] [/s separator] [/d value] [/f]
		public static String add(String subtree, String keyName, String entryName, String dataType, Object value){
			try{
				
				String command = "reg add " + 
	                    '"' + subtree+ "\\"+ keyName + "\" /v " + entryName + " /t " + dataType + " /d " + value.toString() + " /f";
				
				//System.err.println(command);
				
				Process process = Runtime.getRuntime().exec(command);
				StreamReader reader = new StreamReader(process.getInputStream());
	            reader.start();
	            process.waitFor();
	            reader.join();
	            String output = reader.getResult();
	
	            // Output has the following format:
	            // \n<Version information>\n\n<key>\t<registry type>\t<value>
	            if( ! output.contains("\t")){
	                    return null;
	            }
	
	            // Parse out the value
	            //String[] parsed = output.split("\t");
	            return String.valueOf(process.exitValue());
	            //return parsed[parsed.length-1];	
				//return command;
	        }
	        catch (Exception e) {
	            return null;
	        }
		}
		
		static class StreamReader extends Thread {
	        private InputStream is;
	        private StringWriter sw= new StringWriter();

	        public StreamReader(InputStream is) {
	            this.is = is;
	        }

	        public void run() {
	            try {
	                int c;
	                while ((c = is.read()) != -1)
	                    sw.write(c);
	            }
	            catch (IOException e) { 
	        }
	        }

	        public String getResult() {
	            return sw.toString();
	        }
	    }
		
	}
    
}



	
