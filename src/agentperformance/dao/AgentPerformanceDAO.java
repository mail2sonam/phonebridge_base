/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentperformance.dao;

import com.mongodb.MongoClient;
import phonebridge.util.LogClass;
import agentperformance.db.AgentPerformanceDB;
import campaignconfig.model.UserModel;
import com.phonebridgecti.dao.UserDAO;
import java.util.Date;
import phonebridge.util.util;

/**
 *
 * @author bharath
 */
public class AgentPerformanceDAO implements Runnable{
    private String updateField;
    private double answerTimeInSecs;
    private String extension;
    private String campaignID;
    private String serverNamePrefix;
    private Date dateToUpdate;
    private String conversionDisposition;
    private MongoClient mongoConn;
    
    public AgentPerformanceDAO(String updateField,double answerTimeInSecs,String extension,String campaignID,
            String serverNamePrefix,Date dateToUpdate,String conversionDisposition,MongoClient mongoConn){
        this.updateField = updateField;
        this.answerTimeInSecs = answerTimeInSecs;
        this.extension = extension;
        this.campaignID = campaignID;
        this.serverNamePrefix = serverNamePrefix;
        this.dateToUpdate = dateToUpdate;
        this.conversionDisposition = conversionDisposition;
        this.mongoConn = mongoConn;
    }
    
    public String checkIfDocExists(){
        String docID = null;
        int intValue = 0;
        double doubleValue = 0;
        AgentPerformanceDB agentPerfmDb = new AgentPerformanceDB();
        try{
            UserModel user = new UserDAO().checkExtensionExistInUsers(extension, mongoConn);
            if(user!=null)
            {
                docID = agentPerfmDb.checkIfDocExists(extension, campaignID, serverNamePrefix, mongoConn);
                if(docID==null){
                    /*
                    Insert New Doc
                    */
                    docID = agentPerfmDb.insertNewDoc(extension, campaignID, serverNamePrefix, mongoConn);
                }
                switch(updateField){
                    case "callInitiateTime":
                        agentPerfmDb.updateTimeInDoc(docID, dateToUpdate, updateField, extension, serverNamePrefix, mongoConn);
                        break;
                    case "totalCallCnt":
                        agentPerfmDb.incrementFieldInDoc(updateField, docID, extension, campaignID, serverNamePrefix, null, mongoConn);
                        break;
                    case "totalAnswered":
                        agentPerfmDb.incrementFieldInDoc(updateField, docID, extension, campaignID, serverNamePrefix, null,mongoConn);
                        agentPerfmDb.updateTimeInDoc(docID, dateToUpdate, "callAnswerTime", extension, serverNamePrefix, mongoConn);
                        Date callInitiateTime = agentPerfmDb.getDateDataFromDoc("callInitiateTime", docID, extension, campaignID, serverNamePrefix, mongoConn);
                        intValue = agentPerfmDb.getIntDataFromDoc(updateField, docID, extension, campaignID, serverNamePrefix, mongoConn);
                        doubleValue = agentPerfmDb.getDoubleDataFromDoc("totalConnectTime", docID, extension, campaignID, serverNamePrefix, mongoConn);
                        double getCallConnectTime = new util().returnDiffBtwDateInSecsOrMinsOrHrs(dateToUpdate, callInitiateTime, "secs");
                        double tempTotalConnectTime = doubleValue + getCallConnectTime;
                        agentPerfmDb.updateFieldInDoc("totalConnectTime", docID, tempTotalConnectTime, extension, campaignID, serverNamePrefix, mongoConn);
                        double avgCallConnectTime = tempTotalConnectTime / intValue;
                        agentPerfmDb.updateFieldInDoc("avgConnectTime", docID, avgCallConnectTime, extension, campaignID, serverNamePrefix, mongoConn);
                        break;
                    case "totalTalkTime":
                        doubleValue = agentPerfmDb.getDoubleDataFromDoc(updateField, docID, extension, campaignID, serverNamePrefix, mongoConn);
                        double tempTotalTalkTime = doubleValue + answerTimeInSecs;
                        agentPerfmDb.updateFieldInDoc(updateField, docID, tempTotalTalkTime, extension, campaignID, serverNamePrefix, mongoConn);
                        int totalAnsweredCall = agentPerfmDb.getIntDataFromDoc("totalAnswered", docID, extension, campaignID, serverNamePrefix, mongoConn);
                        double avgCallTime = tempTotalTalkTime / totalAnsweredCall;
                        agentPerfmDb.updateFieldInDoc("avgTalkTime", docID, avgCallTime, extension, campaignID, serverNamePrefix, mongoConn);
                        break;
                    case "noOfBreaks":

                        break;
                    case "totalWrapTime":
                        doubleValue = agentPerfmDb.getDoubleDataFromDoc(updateField, docID, extension, campaignID, serverNamePrefix, mongoConn);
                        double tempTotalWrapTime = doubleValue + answerTimeInSecs;
                        agentPerfmDb.updateFieldInDoc(updateField, docID, tempTotalWrapTime, extension, campaignID, serverNamePrefix, mongoConn);
                        int totalCalls = agentPerfmDb.getIntDataFromDoc("totalCallCnt", docID, extension, campaignID, serverNamePrefix, mongoConn);
                        double avgWrapTime = tempTotalWrapTime / totalCalls;
                        agentPerfmDb.updateFieldInDoc("avgWrapTime", docID, avgWrapTime, extension, campaignID, serverNamePrefix, mongoConn);
                        break;
                    case "conversionDisposition":
                        agentPerfmDb.incrementFieldInDoc(conversionDisposition, docID, extension, campaignID, serverNamePrefix, "conversionDisposition.",mongoConn);
                        break;
                    case "conversionDependent":
                        agentPerfmDb.incrementFieldInDoc(conversionDisposition, docID, extension, campaignID, serverNamePrefix, "conversionDependent.",mongoConn);
                        break;
                }
            }
        }
        catch(Exception ex){
            LogClass.logMsg("AgentPerformanceDAO", "checkIfDocExists", ex.getMessage());
        }
        return docID;
    }

    @Override
    public void run() {
        this.checkIfDocExists();
    }
}
