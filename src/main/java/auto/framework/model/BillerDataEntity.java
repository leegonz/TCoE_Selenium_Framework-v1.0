package auto.framework.model;

import java.io.Serializable;
//import java.sql.Timestamp;
import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;

//import Fillo.Recordset;
//import auto.framework.DataTable;

public class BillerDataEntity implements Serializable{

	/**
	 * GENERATED
	 */
	private static final long serialVersionUID = 5783878301207689365L;
	
	private long mId;
	private String mDataId;
	private String mBAN; // CONCRETE or VOLATILE	
	private List<String> mCnList;
	private String mName;
	private String mFan;					
	private String mScenCoverage;
	private String mRequestSet;
	private String mLiability;
	private String mAccountType ;
	private String mAccountSubType;
	private String mMarketType ;
	private String mMarketSubType;
	private String mFedTaxID;
	private String mSSNID;
	private String mAddressSet;
	private String mCTNCount;
	
	

	public String getSSNID() {
		return mSSNID;
	}
	public void setSSNID(String pSSNID) {
		mSSNID = pSSNID;
	}
	public String getFedTaxID() {
		return mFedTaxID;
	}
	public void setFedTaxID(String pFedTaxID) {
		mFedTaxID = pFedTaxID;
	}
	public String getMarketType() {
		return mMarketType;
	}
	public void setMarketType(String pMarketType) {
		mMarketType = pMarketType;
	}
	public String getMarketSubType() {
		return mMarketSubType;
	}
	public void setMarketSubType(String pMarketSubType) {
		mMarketSubType = pMarketSubType;
	}
	public String getLiability() {
		return mLiability;
	}
	public void setLiability(String pLiability) {
		mLiability = pLiability;
	}
	public String getAccountType() {
		return mAccountType;
	}
	public void setAccountType(String pAccountType) {
		mAccountType = pAccountType;
	}
	public String getAccountSubType() {
		return mAccountSubType;
	}
	public void setAccountSubType(String pAccountSubType) {
		mAccountSubType = pAccountSubType;
	}
	public String getAddressSet() {
		return mAddressSet;
	}
	public void setAddressSet(String pAddressSet) {
		mAddressSet = pAddressSet;
	}
	public String getCTNCount() {
		return mCTNCount;
	}
	public void setCTNCount(String pCTNCount) {
		mCTNCount = pCTNCount;
	}
	public long getId() {
		return mId;
	}
	public void setId(long pId) {
		mId = pId;
	}
	public String getDataId() {
		return mDataId;
	}
	public void setDataId(String pDataId) {
		mDataId = pDataId;
	}
	public String getRequestSet() {
		return mRequestSet;
	}
	public void setRequestSet(String pRequestSet) {
		mRequestSet = pRequestSet;
	}
	public String getFan() {
		return mFan;
	}
	
	public void setFan(String pFan) {
		mFan = pFan;
	}
	public String getScenCoverage() {
		return mScenCoverage;
	}
	public void setScenCoverage(String pScenCoverage) {
		mScenCoverage = pScenCoverage;
	}
	
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