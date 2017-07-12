package auto.framework.model;

import java.io.Serializable;
//import java.sql.Timestamp;
import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;


public class BillerRequestEntity implements Serializable{

	/**
	 * GENERATED
	 */
	private static final long serialVersionUID = 5783878301207689365L;
	
//	private long mId;	
	private String mBAN; // CONCRETE or VOLATILE	
	private List<String> mCnList;
	private String mName;
	public String getName() {
		return mName;
	}
	public void setName(String pName) {
		mName = pName;
	}
	public String getScenarioCoverage() {
		return mScenarioCoverage;
	}
	public void setScenarioCoverage(String pScenarioCoverage) {
		mScenarioCoverage = pScenarioCoverage;
	}
	private String mScenarioCoverage;
	
	
	public String getBAN() {
		return mBAN;
	}
	public void setBAN(String pBAN) {
		mBAN = pBAN;
	}
	public List<String> getCnList() {
		return mCnList;
	}
	public void setCnList(List<String> pCnList) {
		mCnList = pCnList;
	}
	
	
		
}