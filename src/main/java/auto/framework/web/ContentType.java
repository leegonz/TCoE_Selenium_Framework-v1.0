package auto.framework.web;

public enum ContentType {
	APPLICATION_PDF("application/pdf"),
	APPLICATION_XLS("application/vnd.ms-excel"),
	APPLICATION_XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
	TEXT_HTML("text/html");
	
	private String value;
	private ContentType(String string){
		this.value = string;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
