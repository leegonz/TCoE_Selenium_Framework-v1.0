function FindProxyForURL(url,host){
	if(isPlainHostName(host)) return "DIRECT";
	else if(shExpMatch(host,"*.att.com"))
		return "PROXY one.proxy.att.com:8080; DIRECT";
	else return "DIRECT";
}