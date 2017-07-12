package test.framework;

import auto.framework.WebManager;

public class SetupTest extends WebManager {

	//@Test
	public void setProxy(){
		WinRegistry.add(WinRegistry.HKEY_CURRENT_USER, "software\\microsoft\\windows\\currentversion\\internet settings", "ProxyEnable", WinRegistry.REG_DWORD, 0);
		WinRegistry.add(WinRegistry.HKEY_CURRENT_USER, "software\\policies\\microsoft\\internet explorer\\control panel", "Proxy", WinRegistry.REG_DWORD, 0);
	}

}
