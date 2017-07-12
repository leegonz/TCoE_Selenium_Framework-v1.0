package auto.framework;

import auto.framework.services.DataLoggerService;

public class LocalDataManager {

	private static ThreadLocal<DataLoggerService> sDataLoggerService = new ThreadLocal<DataLoggerService>();
	

	/**
	 * @return Returns the DataLoggerHelper.
	 */
	public static DataLoggerService getDataLoggerHelper() {
		if(sDataLoggerService.get() == null){
			sDataLoggerService.set(new DataLoggerService());
		}
		return sDataLoggerService.get();
	}
	
}
