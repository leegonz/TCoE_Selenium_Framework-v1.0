package auto.framework.reportlog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import auto.framework.Resources;
import auto.framework.TestManager;

public class ReportLogConsolidator {
	
	protected ReportXmlFactory xmlFactory = new ReportXmlFactory();
	
	private static ArrayList<ReportLogInstance> reports = new ArrayList<ReportLogInstance>();
	
	public void addReport(ReportLogInstance report){
		if(!reports.contains(report)) reports.add(report);
	}
	
	synchronized public void save() {
		
		xmlFactory.refresh();
		Node rootElement = xmlFactory.createElement("root");
		xmlFactory.appendChild(rootElement);
		
		Iterator<ReportLogInstance> iter1 = reports.iterator();
																						
		while(iter1.hasNext()){
			NodeList nodes = iter1.next().rootElement.node().getChildNodes();
			for(int index=0; index<nodes.getLength(); index++){
				Node importedNode = xmlFactory.xmlStream.importNode(nodes.item(index), true);
				rootElement.appendChild(importedNode);
			}
		}	
		
		try {
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			File outputFile = new File("./src/test/resources/reports/consolidated/report.html");
			File rawFile = new File("./src/test/resources/reports/consolidated/report.xml");
			
			String styleSheetFile = TestManager.Preferences.getPreference("styleSheet", "/reportlog/stylesheet.xsl");
			
			String styleSheet = Resources.findResource(styleSheetFile);
			if(styleSheet==null){
				styleSheet = Resources.findResource("/reportlog/"+styleSheetFile);
			}
			
			//Source stylesheet = new StreamSource(WebManager.class.getClass().getResourceAsStream(styleSheet));
			Source stylesheet = new StreamSource(new File(styleSheet));
			Transformer xslTansform = transformerFactory.newTransformer(stylesheet);
			
		 	
			DOMSource source = new DOMSource(xmlFactory.xmlStream);
			
			outputFile.getParentFile().mkdirs();

			OutputStream rawXmlFile = new FileOutputStream(rawFile.getCanonicalPath());
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(source,new StreamResult(rawXmlFile));
            

			OutputStream htmlFile = new FileOutputStream(outputFile.getCanonicalPath());
            xslTansform.transform(source, new StreamResult(htmlFile));
            
            System.out.println(outputFile.getCanonicalPath());
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
}