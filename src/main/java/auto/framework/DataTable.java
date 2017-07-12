package auto.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import Exception.FilloException;
import Fillo.Connection;
import Fillo.Fillo;
import Fillo.Recordset;

public class DataTable {
	
	private static ThreadLocal<DataHolder> dataHolder = new InheritableThreadLocal<DataHolder>(){
        @Override
        protected DataHolder initialValue() { return new DataHolder(); }
	};
	
	public static DataUpdate setRowData(String sheet, String where){
		return new DataUpdate(sheet, where);
	}
	
    public static Recordset getRowData(String sheet, String... where){
    	return dataHolder.get().executeQuery(sheet, where);
    }
    
    public static DataTableInstance Load(String table){
    	String resource = Resources.findResource(table);
    	//System.err.println("load: "+resource);
    	return new DataTableInstance(resource);
    }
    
    @Deprecated public static void loadTable(String table){ //experimental
    	System.err.println("load: "+table);
    	dataHolder.get().table = table;
    }
    
   public static class  DataUpdate {
		//ADDED FOR DATA UPDATE	
    	private final Fillo fillo = new Fillo();
		private String SHEET;
		private String WHERE;
		
		public DataUpdate(String sheet, String where){
			SHEET = sheet;
			WHERE = where;
		}
		
		public DataUpdate update(String column, String value) throws IOException, InterruptedException {
			Connection connection = null;		
			try {
				String dataTable = "./src/test/resources/data/DataTable.xls"; 
				
				String dataPath = TestManager.Preferences.getPreference("data","DataTable.xls");
				if(dataPath!=null){
					if(new File("./src/test/resources/data/"+dataPath).exists()){
						dataTable = "./src/test/resources/data/"+dataPath;
					} else {
						dataTable = dataPath;
					}
				}
				
				connection = fillo.getConnection((new File(dataTable)).getAbsolutePath());
				String query = "Update "+SHEET+" Set "+column+"='"+value+"' where "+WHERE; //"Select * from " + sheet;				
				connection.executeUpdate(query);
				Thread.sleep(1000);
							
			} catch (FilloException e) {				
				ReportLog.failed("Error Updating Spreadsheet: ");				
				
			} finally {
				if(connection!=null) connection.close();
			}
			return null;		
		}
	}
   
   public static ArrayList<String> getPreference(String field, String name, String tab) throws Exception{
		ArrayList<String> columndata = null;
		boolean found = false;
		try {
			String dataTable = "./src/test/resources/data/DataTable.xls"; 
			
			String dataPath = TestManager.Preferences.getPreference("data","DataTable.xls");
			if(dataPath!=null){
				if(new File("./src/test/resources/data/"+dataPath).exists()){
					dataTable = "./src/test/resources/data/"+dataPath;
				} else {
					dataTable = dataPath;
				}
			}
			
	        POIFSFileSystem file = new POIFSFileSystem(new FileInputStream(dataTable));
	        FileOutputStream fileo = new FileOutputStream(new File(dataTable));
	
	        //Create Workbook instance holding reference to .xls file
	        HSSFWorkbook workbook = new HSSFWorkbook(file);
	
	        //Get first/desired sheet from the workbook
	        HSSFSheet sheet = workbook.getSheet(tab);
	        
	        //Iterate through each rows one by one
	        Iterator<Row> rowIterator = sheet.iterator();
	        columndata = new ArrayList<>();
	        Row row = rowIterator.next();
	        
	        int colNum = getColNumber(row, field);
	        
	        try{
		        while (rowIterator.hasNext()){
		        	row = rowIterator.next();
		        	String value = row.getCell(colNum).toString();
		        	if(name.equalsIgnoreCase(value)) {
		        		found = true;
		        	} else if(found == true && value.equalsIgnoreCase("End of Line")) {
		        		break;
		        	} else if(found == true) {
		        		columndata.add(value);
		        	} else {
		        		
		        	}
		        }
	        }catch(NullPointerException e) {
	        	ReportLog.addInfo(name + " Preference Not Found in Data Table");
	        }

	        workbook.write(fileo);
	        fileo.flush();
	        fileo.close();
	        
		} catch (Exception e) {
           e.printStackTrace();
		}
		return columndata;
   }
	
	private static int getColNumber(Row row, String field) {
		int colNumber = 0;
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
          Cell cell = cellIterator.next();
          if(cell.getStringCellValue().equalsIgnoreCase(field)) {
        	  colNumber = cell.getColumnIndex();
        	  break;
          }
		}
		
		return colNumber;
	}
	
	public static class DataTableInstance {
		
		private DataHolder dataHolder = new DataHolder();
		
		public DataTableInstance(String table){
			dataHolder.table = table;
		}
		
	    public Recordset getRowData(String sheet, String... where){
	    	return dataHolder.executeQuery(sheet, where);
	    }
		
	}
    
}

class DataHolder {
	
	private final Fillo fillo = new Fillo();
		
	protected String table=null; //"DataTable.xls";
	
	protected Recordset executeQuery(String sheet, String... where) {
		Connection connection = null;
		Recordset recordset = null;
		try {
			
			String dataTable = "./src/test/resources/data/DataTable.xls"; 
			
			String dataPath = table!=null? table : TestManager.Preferences.getPreference("data","DataTable.xls");
			if(dataPath!=null){
				if(new File("./src/test/resources/data/"+dataPath).exists()){
					dataTable = "./src/test/resources/data/"+dataPath;
				} else {
					dataTable = dataPath;
				}
			}
			
			connection = fillo.getConnection((new File(dataTable)).getAbsolutePath());
			String query = "Select * from " + sheet;
			recordset = connection.executeQuery(query);
			for(int i=0; i<where.length; i++){
				recordset = recordset.where(where[i]);
			}
			recordset.next();
			return recordset;
		} catch (FilloException e) {
			//e.printStackTrace();;
//		} catch (IOException e) {
//			//e.printStackTrace();
		} finally {
			if(connection!=null) connection.close();
		}
		return null;
	}
}	
    