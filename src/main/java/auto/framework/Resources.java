package auto.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

public class Resources {
	
	public static String findResource(String resource){
		try {
		//Find Local Project
		File localFile = new File(resource);
		if(localFile.exists()) return localFile.getCanonicalPath();
		//Find in Framework
		return JarFileToLocal.findResource(resource);
		} catch (IOException e) {}
		//Found nothing
		return null;
	}
	
	//For Improvement
	protected static class JarFileToLocal {
    	
		public static String findResource(String resource) {
			InputStream stream = JarFileToLocal.class.getResourceAsStream(resource);
			if (stream!=null) {
				try {
					return copyTmp(resource).getCanonicalPath();
				} catch (IOException e) {
					return null;
				}
		    }
			return null;
		}
		
    	public static File copyTmp(String resource) throws IOException {
    		//String fileName = new File(resource).getName();
    		File tmpDir = FileUtils.getTempDirectory();
    		File file = new File(tmpDir,resource);
    		if(file.exists()) return file;
    		
    		File fileDir = file.getParentFile();
    		
    		if(fileDir.exists()||fileDir.mkdir()){
    			InputStream stream = JarFileToLocal.class.getResourceAsStream(resource);
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
    		        if(stream!=null) stream.close();
    		        if(resStreamOut!=null) resStreamOut.close();
    		    }
    		}
    		return file;
    	}
    	
    }

}
