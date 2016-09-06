package processdbevents.model;

import phonebridgelogger.model.Trunk;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import org.bson.Document;

public class CdrModel implements Serializable{
    private String eventName;
    private String amaFlags;
    private String destinationChannel;
    private String userField;
    private String channel;
    private String destinationContext;
    private String privilege;
    private String callDirection;
    private String recordingFile;
    private String sipExtension;
    private String phoneNumber;
    private String uniqueID;
    private String disposition;
    private String callerID;
    private String source;
    private String accountCode;
    private String destination;
    private String lastApplication;
    private String lastData;
    private int billableSeconds;
    private int duration;
    private Date answerTime;
    private Date endTime;
    private Date startTime;
    private String userDisposition;
    private String userMemo;
    private String location;
    private String serverName;
    private String department;
    private String cdrOriginatedFrom;
    private Trunk trunkUsed;
    private HashMap cdrProcessedLogic;
    private boolean transferedCall;
    private String transferedFrom;
    private Date eventTime;
    
    
    public String getSipExtension() {
            return sipExtension;
    }

    public void setSipExtension(String sipExtension) {
            this.sipExtension = sipExtension;
    }

    public String getPhoneNumber() {
            return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
    }

    public String getRecordingFile() {
            return recordingFile;
    }

    public void setRecordingFile(String recordingFile) {
            this.recordingFile = recordingFile;
    }

    public String getCallDirection() {
            return callDirection;
    }

    public void setCallDirection(String callDirection) {
            this.callDirection = callDirection;
    }

    public String getAmaFlags() {
            return amaFlags;
    }

    public void setAmaFlags(String amaFlags) {
            this.amaFlags = amaFlags;
    }

    public String getDestinationChannel() {
            return destinationChannel;
    }

    public void setDestinationChannel(String destinationChannel) {
            this.destinationChannel = destinationChannel;
    }

    public String getUserField() {
            return userField;
    }

    public void setUserField(String userField) {
            this.userField = userField;
    }

    public String getChannel() {
            return channel;
    }

    public void setChannel(String channel) {
            this.channel = channel;
    }

    public String getDestinationContext() {
            return destinationContext;
    }

    public void setDestinationContext(String destinationContext) {
            this.destinationContext = destinationContext;
    }

    public String getPrivilege() {
            return privilege;
    }

    public void setPrivilege(String privilege) {
            this.privilege = privilege;
    }

    public String getUniqueID() {
            return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
            this.uniqueID = uniqueID;
    }

    public String getDisposition() {
            return disposition;
    }

    public void setDisposition(String disposition) {
            this.disposition = disposition;
    }

    public String getCallerID() {
            return callerID;
    }

    public void setCallerID(String callerID) {
            this.callerID = callerID;
    }

    public String getSource() {
            return source;
    }

    public void setSource(String source) {
            this.source = source;
    }

    public String getAccountCode() {
            return accountCode;
    }

    public void setAccountCode(String accountCode) {
            this.accountCode = accountCode;
    }

    public String getDestination() {
            return destination;
    }

    public void setDestination(String destination) {
            this.destination = destination;
    }

    public String getLastApplication() {
            return lastApplication;
    }

    public void setLastApplication(String lastApplication) {
            this.lastApplication = lastApplication;
    }

    public String getLastData() {
            return lastData;
    }

    public void setLastData(String lastData) {
            this.lastData = lastData;
    }

    public int getBillableSeconds() {
            return billableSeconds;
    }

    public void setBillableSeconds(int billableSeconds) {
            this.billableSeconds = billableSeconds;
    }

    public int getDuration() {
            return duration;
    }

    public void setDuration(int duration) {
            this.duration = duration;
    }

    public Date getAnswerTime() {
            return answerTime;
    }

    public void setAnswerTime(Date answerTime) {
            this.answerTime = answerTime;
    }

    public Date getEndTime() {
            return endTime;
    }

    public void setEndTime(Date endTime) {
            this.endTime = endTime;
    }

    public Date getStartTime() {
            return startTime;
    }

    public void setStartTime(Date startTime) {
            this.startTime = startTime;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * @param department the department to set
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * @return the cdrCategory
     */
    public String getCdrOriginatedFrom() {
        return cdrOriginatedFrom;
    }

    /**
     * @param cdrOriginatedFrom the cdrCategory to set
     */
    public void setCdrOriginatedFrom(String cdrOriginatedFrom) {
        this.cdrOriginatedFrom = cdrOriginatedFrom;
    }

    

    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * @return the userDisposition
     */
    public String getUserDisposition() {
        return userDisposition;
    }

    /**
     * @param userDisposition the userDisposition to set
     */
    public void setUserDisposition(String userDisposition) {
        this.userDisposition = userDisposition;
    }

    /**
     * @return the userMemo
     */
    public String getUserMemo() {
        return userMemo;
    }

    /**
     * @param userMemo the userMemo to set
     */
    public void setUserMemo(String userMemo) {
        this.userMemo = userMemo;
    }

    /**
     * @return the trunkUsed
     */
    public Trunk getTrunkUsed() {
        return trunkUsed;
    }

    /**
     * @param trunkUsed the trunkUsed to set
     */
    public void setTrunkUsed(Trunk trunkUsed) {
        this.trunkUsed = trunkUsed;
    }

    /**
     * @return the cdrProcessedLogic
     */
    public HashMap getCdrProcessedLogic() {
        return cdrProcessedLogic;
    }

    /**
     * @param cdrProcessedLogic the cdrProcessedLogic to set
     */
    public void setCdrProcessedLogic(HashMap cdrProcessedLogic) {
        this.cdrProcessedLogic = cdrProcessedLogic;
    }

    /**
     * @return the eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * @param eventName the eventName to set
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * @return the transferedCall
     */
    public boolean isTransferedCall() {
        return transferedCall;
    }

    /**
     * @param transferedCall the transferedCall to set
     */
    public void setTransferedCall(boolean transferedCall) {
        this.transferedCall = transferedCall;
    }

    /**
     * @return the transferedFrom
     */
    public String getTransferedFrom() {
        return transferedFrom;
    }

    /**
     * @param transferedFrom the transferedFrom to set
     */
    public void setTransferedFrom(String transferedFrom) {
        this.transferedFrom = transferedFrom;
    }

    /**
     * @return the eventTime
     */
    public Date getEventTime() {
        return eventTime;
    }

    /**
     * @param eventTime the eventTime to set
     */
    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }
    
    public Document getDocument(){
        Document doc = new Document();
        doc.append("eventName",this.eventName);
        doc.append("privilege",this.privilege);
        doc.append("accountCode",this.accountCode);
        doc.append("source",this.source);
        doc.append("destination",this.destination);
        doc.append("destinationContext",this.destinationContext);
        doc.append("callerID",this.callerID);
        doc.append("channel",this.channel);
        doc.append("destinationChannel",this.destinationChannel);
        doc.append("lastApplication",this.lastApplication);
        doc.append("lastData",this.lastData);
        doc.append("startTime",this.startTime);
        doc.append("answerTime",this.answerTime);
        doc.append("endTime",this.endTime);
        doc.append("duration",this.duration);
        doc.append("billableSeconds",this.billableSeconds);
        doc.append("disposition",this.disposition);
        doc.append("amaFlags",this.amaFlags);
        doc.append("uniqueID",this.uniqueID);
        doc.append("userField",this.userField);
        doc.append("callDirection",this.callDirection);
        doc.append("sipExtension",this.sipExtension);
        doc.append("phoneNumber",this.phoneNumber);
        doc.append("trunkUsed",new Trunk().getDocument(trunkUsed));
        doc.append("recordingFile", this.recordingFile);
        doc.append("userDisposition",this.userDisposition);
        doc.append("userMemo",this.userMemo);
        doc.append("serverName",this.serverName);
        doc.append("location",this.location);
        doc.append("department",this.department);
        doc.append("cdrOriginatedFrom",this.cdrOriginatedFrom);
        doc.append("cdrProccesedLogic",this.cdrProcessedLogic);
        doc.append("isTransferedCall",this.transferedCall);
        doc.append("transferedFrom",this.transferedFrom);
        doc.append("eventTime", this.eventTime);
        return doc;
    }

}
