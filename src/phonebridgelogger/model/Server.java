/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridgelogger.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author sonamuthu
 */
public class Server implements Serializable{
    private String serverName;
    private String serverIP;
    private String amiUserName;
    private String amiPassword;
    private String sshUsername;
    private String sshPassword;
    private String remoteRecordingLocation;
    private String currentRecordingLocation;
    private String serverType;
    private String queueContext;
    private String companyName;
    private String location;
    private String serverRecordingLocation;
    private boolean syncEnabled;
    private boolean autoStart;
    private String serverID;
    private String serverNamePrefix;
    private String transferContext;
    private String ivrContext;
    private String cdrQueueLastApplication;
    private ArrayList<Trunk> trunk;
    private Integer amiPort,sshPort;
    private String crmContext;
    private String dialerContext;
    private String dialOutContext;
    private String currentStatus;
    private Date lastEventTime;
    private String queueName;
    private int lifeTime;
    private String callProcessStatus;
    private String knownIncomingContext;
    private String missedCallTrunk;
    private String prefix;
    /**
     * @return the location
     */
    
    

    /**
     * @return the serverIP
     */
    public String getServerIP() {
        return serverIP;
    }

    /**
     * @param serverIP the serverIP to set
     */
    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    /**
     * @return the amiUserName
     */
    public String getAmiUserName() {
        return amiUserName;
    }

    /**
     * @param amiUserName the amiUserName to set
     */
    public void setAmiUserName(String amiUserName) {
        this.amiUserName = amiUserName;
    }

    /**
     * @return the amiPassword
     */
    public String getAmiPassword() {
        return amiPassword;
    }

    /**
     * @param amiPassword the amiPassword to set
     */
    public void setAmiPassword(String amiPassword) {
        this.amiPassword = amiPassword;
    }

    /**
     * @return the amiPort
     */
    public Integer getAmiPort() {
        return amiPort;
    }

    /**
     * @param amiPort the amiPort to set
     */
    public void setAmiPort(Integer amiPort) {
        this.amiPort = amiPort;
    }

    /**
     * @return the sshUsername
     */
    public String getSshUsername() {
        return sshUsername;
    }

    /**
     * @param sshUsername the sshUsername to set
     */
    public void setSshUsername(String sshUsername) {
        this.sshUsername = sshUsername;
    }

    /**
     * @return the sshPassword
     */
    public String getSshPassword() {
        return sshPassword;
    }

    /**
     * @param sshPassword the sshPassword to set
     */
    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    /**
     * @return the remoteRecordingLocation
     */
    public String getRemoteRecordingLocation() {
        return remoteRecordingLocation;
    }

    /**
     * @param remoteRecordingLocation the remoteRecordingLocation to set
     */
    public void setRemoteRecordingLocation(String remoteRecordingLocation) {
        this.remoteRecordingLocation = remoteRecordingLocation;
    }

    /**
     * @return the currentRecordingLocation
     */
    public String getCurrentRecordingLocation() {
        return currentRecordingLocation;
    }

    /**
     * @param currentRecordingLocation the currentRecordingLocation to set
     */
    public void setCurrentRecordingLocation(String currentRecordingLocation) {
        this.currentRecordingLocation = currentRecordingLocation;
    }

    /**
     * @return the serverType
     */
    public String getServerType() {
        return serverType;
    }

    /**
     * @param serverType the serverType to set
     */
    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getMissedCallTrunk() {
        return missedCallTrunk;
    }

    public void setMissedCallTrunk(String missedCallTrunk) {
        this.missedCallTrunk = missedCallTrunk;
    }

    /**
     * @return the queueContext
     */
    public String getQueueContext() {
        return queueContext;
    }

    /**
     * @param queueContext the queueContext to set
     */
    public void setQueueContext(String queueContext) {
        this.queueContext = queueContext;
    }

    /**
     * @return the syncEnabled
     */
    public boolean isSyncEnabled() {
        return syncEnabled;
    }

    /**
     * @param syncEnabled the syncEnabled to set
     */
    public void setSyncEnabled(boolean syncEnabled) {
        this.syncEnabled = syncEnabled;
    }
 
    public Server(String serverName,String serverIP,String amiUserName,String amiPassword,
            Integer amiPort,String sshUsername,String sshPassword,Integer sshPort,String remoteRecordingLocation,
            String currentRecordingLocation,String serverRecordingLocation,String serverType,String queueContext,
            boolean syncEnabled,String serverID,String location,String serverNamePrefix,String transferContext,
            String ivrContext,String cdrQueueContext,boolean autoStart,String crmContext,String dialerContext,
            String dialOutContext,String currentStatus,Date lastEventTime,String queueName,int lifeTime,
            String callProcessStatus,String knownIncomingContext,String missedCallTrunk,String prefix)
    {
        this.serverName = serverName;
        this.serverIP = serverIP;
        this.amiUserName = amiUserName;
        this.amiPassword = amiPassword;
        this.amiPort = amiPort;
        this.sshUsername = sshUsername;
        this.sshPassword = sshPassword;
        this.sshPort = sshPort;
        this.remoteRecordingLocation = remoteRecordingLocation;
        this.currentRecordingLocation = currentRecordingLocation;
        this.serverRecordingLocation = serverRecordingLocation;
        this.serverType = serverType;
        this.queueContext = queueContext;
        this.syncEnabled = syncEnabled;
        this.serverID = serverID;
        this.location = location;
        this.serverNamePrefix = serverNamePrefix;
        this.transferContext = transferContext;
        this.ivrContext = ivrContext;
        this.cdrQueueLastApplication = cdrQueueContext;
        this.autoStart = autoStart;
        this.crmContext = crmContext;
        this.dialerContext = dialerContext;
        this.dialOutContext = dialOutContext;
        this.currentStatus = currentStatus;
        this.lastEventTime = lastEventTime;
        this.queueName = queueName;
        this.lifeTime = lifeTime;
        this.callProcessStatus = callProcessStatus;
        this.knownIncomingContext = knownIncomingContext;
        this.missedCallTrunk = missedCallTrunk;
        this.prefix = prefix;
    }
    
    /**
     * @return the serverID
     */
    public String getServerID() {
        return serverID;
    }

    /**
     * @param serverID the serverID to set
     */
    public void setServerID(String serverID) {
        this.serverID = serverID;
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
     * @return the trunk
     */
    public ArrayList<Trunk> getTrunk() {
        return trunk;
    }

    /**
     * @param trunk the trunk to set
     */
    public void setTrunk(ArrayList<Trunk> trunk) {
        this.trunk = trunk;
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
     * @return the serverNamePrefix
     */
    public String getServerNamePrefix() {
        return serverNamePrefix;
    }

    /**
     * @param serverNamePrefix the serverNamePrefix to set
     */
    public void setServerNamePrefix(String serverNamePrefix) {
        this.serverNamePrefix = serverNamePrefix;
    }

    /**
     * @return the transferContext
     */
    public String getTransferContext() {
        return transferContext;
    }

    /**
     * @param transferContext the transferContext to set
     */
    public void setTransferContext(String transferContext) {
        this.transferContext = transferContext;
    }

    /**
     * @return the ivrContext
     */
    public String getIvrContext() {
        return ivrContext;
    }

    /**
     * @param ivrContext the ivrContext to set
     */
    public void setIvrContext(String ivrContext) {
        this.ivrContext = ivrContext;
    }

    /**
     * @return the cdrQueueLastApplication
     */
    public String getCdrQueueLastApplication() {
        return cdrQueueLastApplication;
    }

    /**
     * @param cdrQueueLastApplication the cdrQueueLastApplication to set
     */
    public void setCdrQueueLastApplication(String cdrQueueLastApplication) {
        this.cdrQueueLastApplication = cdrQueueLastApplication;
    }

    /**
     * @return the sshPort
     */
    public Integer getSshPort() {
        return sshPort;
    }

    /**
     * @param sshPort the sshPort to set
     */
    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    /**
     * @return the autoStart
     */
    public boolean isAutoStart() {
        return autoStart;
    }

    /**
     * @param autoStart the autoStart to set
     */
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    /**
     * @return the serverRecordingLocation
     */
    public String getServerRecordingLocation() {
        return serverRecordingLocation;
    }

    /**
     * @param serverRecordingLocation the serverRecordingLocation to set
     */
    public void setServerRecordingLocation(String serverRecordingLocation) {
        this.serverRecordingLocation = serverRecordingLocation;
    }

    /**
     * @return the crmContext
     */
    public String getCrmContext() {
        return crmContext;
    }

    /**
     * @param crmContext the crmContext to set
     */
    public void setCrmContext(String crmContext) {
        this.crmContext = crmContext;
    }

    /**
     * @return the dialerContext
     */
    public String getDialerContext() {
        return dialerContext;
    }

    /**
     * @param dialerContext the dialerContext to set
     */
    public void setDialerContext(String dialerContext) {
        this.dialerContext = dialerContext;
    }

    /**
     * @return the dialOutContext
     */
    public String getDialOutContext() {
        return dialOutContext;
    }

    /**
     * @param dialOutContext the dialOutContext to set
     */
    public void setDialOutContext(String dialOutContext) {
        this.dialOutContext = dialOutContext;
    }
    
    public Server(){
        
    }

    /**
     * @return the currentStatus
     */
    public String getCurrentStatus() {
        return currentStatus;
    }

    /**
     * @param currentStatus the currentStatus to set
     */
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    /**
     * @return the lastEventTime
     */
    public Date getLastEventTime() {
        return lastEventTime;
    }

    /**
     * @param lastEventTime the lastEventTime to set
     */
    public void setLastEventTime(Date lastEventTime) {
        this.lastEventTime = lastEventTime;
    }

    /**
     * @return the queueName
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * @param queueName the queueName to set
     */
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    /**
     * @return the lifeTime
     */
    public int getLifeTime() {
        return lifeTime;
    }

    /**
     * @param lifeTime the lifeTime to set
     */
    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }

    /**
     * @return the callProcessStatus
     */
    public String getCallProcessStatus() {
        return callProcessStatus;
    }

    /**
     * @param callProcessStatus the callProcessStatus to set
     */
    public void setCallProcessStatus(String callProcessStatus) {
        this.callProcessStatus = callProcessStatus;
    }

    /**
     * @return the knownIncomingContext
     */
    public String getKnownIncomingContext() {
        return knownIncomingContext;
    }

    /**
     * @param knownIncomingContext the knownIncomingContext to set
     */
    public void setKnownIncomingContext(String knownIncomingContext) {
        this.knownIncomingContext = knownIncomingContext;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
