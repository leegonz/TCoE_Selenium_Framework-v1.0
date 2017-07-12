package auto.framework;

public enum UserAgentList {
	iphone_OS_3_0("Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16"),
	iphone_OS_4_3("Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3 like Mac OS X; de-de) AppleWebKit/533.17.9 (KHTML, like Gecko) Mobile/8F190"),
	iphone_OS_5_1_1("Mozilla/5.0 (iPhone; U; CPU iPhone OS 5_1_1 like Mac OS X; da-dk) AppleWebKit/534.46.0 (KHTML, like Gecko) CriOS/19.0.1084.60 Mobile/9B206 Safari/7534.48.3"),
	iphone_OS_6("UCWEB/8.8 (iPhone; CPU OS_6; en-US)AppleWebKit/534.1 U3/3.0.0 Mobile"),
	android_4_4("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/BuildID) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36"),
	iphoneRelease("Mozilla/5.0 (Windows NT 6.1; rv:38.0) Gecko/20100101 Firefox/38.0 UACLDT2543635");

	UserAgentList() {
		
	}
	private String userAgent;
	private UserAgentList(String string){
		this.userAgent = string;
	}
	public String getUserAgentt() {
		return userAgent;
	}
	@Override
	public String toString() {
		return userAgent;
	}
		
	}