package auto.framework.model;

import java.io.Serializable;
//import java.sql.Timestamp;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;

public class ProfileDataEntity implements Serializable{

	/**
	 * GENERATED
	 */
	private static final long serialVersionUID = 5783878301207689365L;
	
//	private long mId;
	private String mDataId;	
	private String mContractType;
	private String BaseEnterpriseType;
	private String mName;
	private String mAccountGrouop;
	private String mLogin1;
	private String mLogin1Prefix; // CONCRETE or VOLATILE
	private String mLogin2;
	private String mLogin2Prefix; 
	private String mLiability; // Data Entity's name
	private String mProfileConfi; // Data Entity's name
	private String mScenCoverage; // Data Entity's name
	private String mFan; // LIVE or BURNT
	private String mFanName; // LIVE or BURNT
	private String mFanBilling;
	private String mMarketType;
	private String mMarketSubType;	
	private String mEmailAddress;
	
	public String getFanName() {
		return mFanName;
	}
	public void setFanName(String pFanName) {
		mFanName = pFanName;
	}
	public String getEmailAddress() {
		return mEmailAddress;
	}
	public void setEmailAddress(String pEmailAddress) {
		mEmailAddress = pEmailAddress;
	}
	public String getFanBilling() {
		return mFanBilling;
	}
	public void setFanBilling(String pFanBilling) {
		mFanBilling = pFanBilling;
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
	public String getContractType() {
		return mContractType;
	}
	public void setContractType(String pContractType) {
		mContractType = pContractType;
	}
	public String getBaseEnterpriseType() {
		return BaseEnterpriseType;
	}
	public void setBaseEnterpriseType(String pBaseEnterpriseType) {
		BaseEnterpriseType = pBaseEnterpriseType;
	}
	public String getName() {
		return mName;
	}
	public void setName(String pName) {
		mName = pName;
	}
	public String getAccountGrouop() {
		return mAccountGrouop;
	}
	public void setAccountGrouop(String pAccountGrouop) {
		mAccountGrouop = pAccountGrouop;
	}
	public String getLogin1() {
		return mLogin1;
	}
	public void setLogin(String pLogin1) {
		mLogin1 = pLogin1;
	}
	public String getDataId() {
		return mDataId;
	}
	public void setDataId(String pDataId) {
		mDataId = pDataId;
	}
	public String getLogin1Prefix() {
		return mLogin1Prefix;
	}
	public void setLogin1Prefix(String pLogin1Prefix) {
		mLogin1Prefix = pLogin1Prefix;
	}
	public String getLogin2() {
		return mLogin2;
	}
	public void setLogin2(String pLogin2) {
		mLogin2 = pLogin2;
	}
	public String getLogin2Prefix() {
		return mLogin2Prefix;
	}
	public void setLogin2Prefix(String pLogin2Prefix) {
		mLogin2Prefix = pLogin2Prefix;
	}
	public String getProfileConfi() {
		return mProfileConfi;
	}
	public void setProfileConfi(String pProfileConfi) {
		mProfileConfi = pProfileConfi;
	}
	public String getScenCoverage() {
		return mScenCoverage;
	}
	public void setScenCoverage(String pScenCoverage) {
		mScenCoverage = pScenCoverage;
	}
	public String getLiability() {
		return mLiability;
	}
	public void setLiability(String pLiability) {
		mLiability = pLiability;
	}
	public String getFan() {
		return mFan;
	}
	public void setFan(String pFan) {
		mFan = pFan;
	}
	
	public String getScenarioCoverage() {
		return mScenCoverage;
	}
	public void setScenarioCoverage(String pScen) {
		mScenCoverage = pScen;
	}
	
}