/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.main;

import agentperformance.dao.AgentPerformanceDAO;
import campaignconfig.db.TwoLegDialingDB;
import campaignconfig.model.BranchModel;
import campaignconfig.model.CampaignModel;
import campaignconfig.model.DependantForSave;
import campaignconfig.model.TwoLegDialingModel;
import campaignconfig.model.UserModel;
import com.mongodb.MongoClient;
import com.phonebridgecti.dao.BranchDAO;
import com.phonebridgecti.dao.CampaignDAO;
import phonebridgelogger.dao.ServerDAO;
import singleton.db.DBClass;
import phonebridgelogger.db.ServerDB;
import phonebridgelogger.model.Server;
import phonebridge.util.util;
import com.phonebridgecti.dao.CtiDAO;
import com.phonebridgecti.dao.UserDAO;
import com.phonebridgecti.db.CtiDB;
import com.settings.AdditionalSettings;
import com.settings.AdditionalSettingsDao;
import phonebridge.postdata.CurlDataToURL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import phonebridge.originate.OriginateCall;
import phonebridge.missedcall.dao.MissedCallDAO;
import phonebridge.missedcall.db.MissedCallDB;
import processdbevents.dao.ProcessEvents;
import phonebridge.missedcall.model.MissedCallRequestModel;
import phonebridge.originate.OriginateRequestModel;
import phonebridge.originate.TwoLegOriginateCall;
import phonebridge.sms.SMSModel;
import phonebridge.sms.SmsDB;
import phonebridge.sms.sendMissedCallSMS;
import phonebridge.sms.sendSMS;
import processdbevents.db.ProcessAMIDB;

/**
 *
 * @author bharath
 */
public class ProcessDBEvents {
    private Server serverDetails;
    private MongoClient mongoConn;
    private boolean stopRequest;
    private ServerDAO serverDao;
    private CtiDAO ctiDao;
    private CtiDB ctiDb;
    private MissedCallDAO missedCallDao;
    private MissedCallDB missedCallDb;
    private AdditionalSettings settings;
    
    /**
     * @param args the command line arguments
     */
    
    public ProcessDBEvents(String serverID){
        serverDetails = new ServerDAO().getServerByID(serverID);
        mongoConn = DBClass.getInstance().getConnection();
        stopRequest = false;
        serverDao = new ServerDAO();
        ctiDao = new CtiDAO();
        ctiDb = new CtiDB();
        missedCallDao = new MissedCallDAO();
        missedCallDb = new MissedCallDB();
        settings = new AdditionalSettingsDao().getCurrentSettings();
    }
    
    TimerTask checkProcessStopReq = new TimerTask(){
        public void run(){
            
            String stopRequestID = serverDao.checkForStopRequest(serverDetails.getServerID(), new ServerDB(), "callprocess",mongoConn);
            if(stopRequestID!=null){
                serverDao.updateRequestStartedOn(stopRequestID, mongoConn);
                serverDao.updateRequestCompletedOn(stopRequestID, mongoConn);
                serverDao.updateCallProcessStatus("stopped",serverDetails.getServerID(), mongoConn);
                stopRequest = true; 
            }
        }
    };
    
    TimerTask checkAutoWrapExceededPopup = new TimerTask(){
        public void run(){
            ArrayList<OriginateRequestModel> numbersToOriginate = ctiDao.getNumbersToOriginate(serverDetails.getServerNamePrefix(), ctiDb, mongoConn);
            for(OriginateRequestModel eachNum : numbersToOriginate){
                new OriginateCall(serverDetails, eachNum.getPrefix(),eachNum.getPhoneNumber(), 
                    eachNum.getUserExtension(), eachNum.getTypeOfDialer(),eachNum.getCallContext(),
                    eachNum.getOriginateReqID(),eachNum.isFollowMeExists(),eachNum.getExtensionType(),eachNum.getFollowMeNumber(),
                        mongoConn).run();
                new AgentPerformanceDAO("callInitiateTime", 0,eachNum.getUserExtension() , eachNum.getCampaignID(),
                    serverDetails.getServerNamePrefix(), new util().generateNewDateInYYYYMMDDFormat("UTC"),null,mongoConn).run();
                ctiDao.updateCallInitiated(eachNum.getOriginateReqID(), serverDetails.getServerNamePrefix(), ctiDb, mongoConn);
            }
            
            ArrayList<HashMap> numbersNotAttended = ctiDao.getNumbersNotAttendedByAgent(serverDetails.getServerNamePrefix(), ctiDb, mongoConn);
            for(HashMap<String,String> numHash : numbersNotAttended){
                ctiDao.updateLeadStatusInList(numHash.get("listID"), numHash.get("custID"), ctiDb, mongoConn);
                ctiDao.updateHasAgentAnsweredInOriginateCall(numHash.get("extension"),numHash.get("phoneNumber"),"Closed Since Agent Did Not Answer", serverDetails.getServerNamePrefix(), ctiDb, mongoConn);
                ctiDb.deletePopupRecord(numHash.get("extension"), serverDetails.getServerNamePrefix(),numHash.get("popupID"), mongoConn);
                ctiDb.deleteDocFromOpenCalls(numHash.get("popupID"), serverDetails.getServerNamePrefix(),mongoConn);
                CampaignModel campaignDetails = new CampaignDAO().getCampaignDetailsForID(numHash.get("campaignID"));
                if(!campaignDetails.getCampaignSource().equals("apicampaign")){
                    new CurlDataToURL(settings.getPopupURL(),numHash.get("extension"), "XYZAGENTNORESPONSE","popup","","","","").run();
                    new CurlDataToURL(settings.getPopupURL(),numHash.get("extension"), "XYZAGENTNORESPONSE","livemonitoring","","","","").run();
                }
            }
            
            ArrayList<HashMap> closePopupDetails = ctiDao.getClosePopupDetails(serverDetails.getServerID(),ctiDb ,mongoConn);
            for(HashMap<String,String> eachHash : closePopupDetails){
                ctiDao.saveCti(eachHash.get("callDocID"), "Wrap Up Time Exceeded", new ArrayList<DependantForSave>(),"", eachHash.get("mmYYYY"),
                    eachHash.get("serverNamePrefix"), eachHash.get("campaignID"),eachHash.get("listID"),eachHash.get("campaignName"), eachHash.get("custID"), 
                    "Closed Due To WrapUp", eachHash.get("extension"), eachHash.get("serverID"), eachHash.get("popupID"), null,
                    null,null,null,null,null,null,null,false,mongoConn);
                CampaignModel campaignDetails = new CampaignDAO().getCampaignDetailsForID(eachHash.get("campaignID"));
                if(!campaignDetails.getCampaignSource().equals("apicampaign")){
                    new CurlDataToURL(settings.getPopupURL(),eachHash.get("extension"), "XYZPOPUPCLOSED","popup","","","","").run();
                    new CurlDataToURL(settings.getPopupURL(),eachHash.get("extension"), "XYZPOPUPCLOSED","livemonitoring","","","","").run();
                }
            }
        }
    };
    
    TimerTask callBackMissedCall = new TimerTask() {
        public void run() {
            ArrayList<CampaignModel> missedCallCampaigns = new CampaignDAO().getAllMissedCallCampaigns(mongoConn);
            for(CampaignModel campaign:missedCallCampaigns){
                if(campaign.isBranchWise()){
                    ArrayList<BranchModel> branches = new BranchDAO().getAllBranches();
                    for(BranchModel branch:branches)
                        processMissedCallRequestForBranch(campaign,branch.getBranchID());
                }
                else
                    processMissedCallRequestForBranch(campaign,null);
            }
        }
    };
    
    TimerTask SLATime = new TimerTask() {
        public void run() {
            ArrayList<CampaignModel> missedCallCampaigns = new ProcessAMIDB().getAllMissedCallCampaigns(mongoConn);
            for(CampaignModel campaign:missedCallCampaigns){
                if(campaign.getSlaTime()!=0)
                {
                    ArrayList<MissedCallRequestModel> missedCallSLABreachRequests = new ProcessAMIDB().
                            getmissedCallSLABreachRequests(campaign.getListID(),missedCallDb,campaign.getSlaTime(),mongoConn);
                    for(MissedCallRequestModel slaNumber:missedCallSLABreachRequests)
                    {
                        ArrayList<SMSModel> allSMSTemplates = new SmsDB().getAllSMSTemplates(campaign.getCampaignID());
                        for(SMSModel smstemplate:allSMSTemplates)
                        {
                            if(smstemplate.getTriggerOn().equals("SLABreach"))
                                new phonebridge.sms.SmsDB().insertMissedCallSMSData(slaNumber.getPhoneNumber(),campaign.getListID(),true,smstemplate,serverDetails.getServerNamePrefix());
                        }
                    }
                }
            }
        }
    };
            
    TimerTask sendMissedCallSMS = new TimerTask() {
        public void run() {
            ArrayList<SMSModel> smsDetails = new SmsDB().getMissedCallsmsDetails(serverDetails.getServerNamePrefix());
            for(SMSModel eachSMS : smsDetails){
                new sendMissedCallSMS(eachSMS.getPhoneNumber(), eachSMS.getSmsContent(), eachSMS.getSmsType(), eachSMS.getListID(), 
                    eachSMS.isIsSLABreach(),eachSMS.getSmsID(),serverDetails.getServerNamePrefix()).run();
            }
        }
    };
    
    TimerTask sendSMS = new TimerTask() {
        public void run() {
            ArrayList<SMSModel> smsDetails = new SmsDB().getSMSDetails(serverDetails.getServerNamePrefix());
            for(SMSModel eachSMS : smsDetails){
                new sendSMS(eachSMS,serverDetails.getServerNamePrefix()).run();
            }
        }
    };
    
    TimerTask twoLegDialing = new TimerTask() {
        @Override
        public void run() {
            ArrayList<TwoLegDialingModel> alltwolegDialingRequests = new TwoLegDialingDB().getAllTwolegDialingRequests();
            for(TwoLegDialingModel eachReq:alltwolegDialingRequests){
                new TwoLegOriginateCall(serverDetails, eachReq.getPrefix(), eachReq.getPhone1(), eachReq.getPhone2(),
                        eachReq.getCallContext(),eachReq.getCampaignID(), mongoConn).run();
                //new TwoLegDialingDB().updateusingPhoneAndCampaignID(eachReq.getPhone1(), eachReq.getPhone2(), eachReq.getCampaignID(), "processed");
            }
        }
    };
            
    public void processMissedCallRequestForBranch(CampaignModel campaign,String branchID){
        ArrayList<MissedCallRequestModel> missedCallReqs = null;
        
        missedCallReqs = missedCallDao.getAllRetryMissedCallReq(campaign.getListID(), missedCallDb, branchID, mongoConn);
        if(missedCallReqs.size()<1)
            missedCallReqs = missedCallDao.getAllMissedCallReq(campaign.getListID(),missedCallDb,branchID, mongoConn);
        if(missedCallReqs.size()<1)
            return;
        //if(new MissedCallDAO().checkIfAlreadyCalled(mis, branchID, branchID, mongoConn))
        ArrayList<String> mappedExtensions = missedCallDao.getTotalExtensionsForMissedCallCampaign(campaign.getCampaignID(),branchID, mongoConn);
        if(mappedExtensions.size()<1)
            return;
        
        //Added By Venky
        ArrayList<String> extensions = new ArrayList<>();
        for(String ext:mappedExtensions){
            UserModel userDetails = new UserDAO().getUserNameAndIDForExtensionAndServerID(ext, serverDetails.getServerID(), mongoConn);
            if((!userDetails.isIsFollowMe() && userDetails.getExtensionStatus().equals("0")) || 
               (userDetails.isIsFollowMe() && missedCallDao.checkIfExtensionFreeFromCurrentCalls(ext, serverDetails.getServerNamePrefix(), mongoConn)))
                extensions.add(ext);
        }
        if(extensions.size()<1)
            return;
        //End
        
        ArrayList<String> sortedExtensionFromDb = missedCallDao.getExtensionBasedOnNoOfCallsAnswered(campaign.getListID(),branchID,extensions,mongoConn);
        /*
            1) Removing extension in arrayList obtained from userCampaignMapping by using sorted extension which is 
                obtained from the aggregate result
        */
        /*
            1) To remove unmapped extension from MissedCall
        */
        for(String ext:sortedExtensionFromDb)
            if(!extensions.contains(ext))
                sortedExtensionFromDb.remove(ext);
        
        extensions.removeAll(sortedExtensionFromDb);
        
        if(extensions.isEmpty())
            extensions = sortedExtensionFromDb;
        else
            extensions.addAll(sortedExtensionFromDb);
        int extensionCounter=0;
        int missedCallCounter=0;
        //get all mapped extensions
        while(extensionCounter<extensions.size() && missedCallCounter<missedCallReqs.size()){
            String currentExtension=extensions.get(extensionCounter);
            extensionCounter++;
            MissedCallRequestModel missedCallReq = missedCallReqs.get(missedCallCounter);
            if(serverDetails.getServerType().equalsIgnoreCase("elastix"))
                if(new MissedCallDAO().checkIfAlreadyCalled(missedCallReq.getDateEntered(), serverDetails.getServerNamePrefix(), missedCallReq.getPhoneNumber(), mongoConn)){
                    new MissedCallDB().updateMissedCallReqStatus(campaign.getListID(), missedCallReq.getMissedCallReqID(), null, "Completed", "Already Spoken", null,mongoConn);
                    break;
                }
            if(missedCallReq.getLastExtensionTried()!=null && missedCallReq.getLastExtensionTried().equals(currentExtension)){
                if(extensions.size()>extensionCounter){
                    currentExtension=extensions.get(extensionCounter);
                    extensionCounter++;
                }
            }
            UserModel userDetails = new UserDAO().getUserNameAndIDForExtensionAndServerID(currentExtension, serverDetails.getServerID(), mongoConn);
            //Commented By Venky
            /*if(
                (!userDetails.isIsFollowMe() && userDetails.getExtensionStatus().equals("0")) || 
                (userDetails.isIsFollowMe() && missedCallDao.checkIfExtensionFreeFromCurrentCalls(currentExtension, serverDetails.getServerNamePrefix(), mongoConn))
            ){ */               
                missedCallReq.setLastExtensionTried(currentExtension);
                missedCallDao.deleteRecFromOriginateCallWithID(missedCallReq.getMissedCallReqID(), serverDetails.getServerNamePrefix(), mongoConn);
                missedCallDao.originateCall(userDetails.getPrefix(), missedCallReq.getPhoneNumber(),
                    currentExtension , "Missed Call CB",serverDetails.getServerNamePrefix(),0,null, 
                    campaign.getCampaignID(), serverDetails.getDialOutContext(), campaign.getCampaignName(), 
                    null, userDetails.isIsFollowMe(),missedCallReq.getMissedCallReqID(),missedCallDb,
                    userDetails.getExtensionType(),userDetails.getFollowNumber());
                missedCallDb.updateMissedCallReqStatus(campaign.getListID(),missedCallReq.getMissedCallReqID(), currentExtension, "processing"
                    , null,null,mongoConn);
                missedCallCounter++;
            /*}
            else{
                new MissedCallDAO().updateLastExtensionTriedInMissedCall(campaign.getListID(), missedCallReq.getMissedCallReqID(), currentExtension, mongoConn);
                //missedCallDb.updateMissedCallReqStatus(campaign.getListID(),missedCallReq.getMissedCallReqID(), currentExtension, "Not Initiated"
                    //, null,null,mongoConn);
            }*/
        }
    }
    
    public static void main(String[] args) throws Exception {
        ProcessDBEvents processDbEvents = new ProcessDBEvents(args[0]);
        processDbEvents.processEvents();
    }
    
    private void processEvents(){
        try{
            Timer timer_checkstopcallprocess = new Timer();
            timer_checkstopcallprocess.schedule( checkProcessStopReq, 1000,10*1000 );
            Timer timer_checkopenpopup = new Timer();
            timer_checkopenpopup.schedule( checkAutoWrapExceededPopup, 1000,1*1000 );
            Timer timer_processmissedcall = new Timer();
            timer_processmissedcall.schedule(callBackMissedCall, 1000,1*1000);
            Timer timer_sms = new Timer();
            timer_sms.schedule( sendSMS, 1000,1*1000 );
            //Timer timer_slaTime = new Timer();
            //timer_slaTime.schedule(SLATime, 1000,1*1000);
            Timer timer_sendMissedCallsms = new Timer();
            timer_sendMissedCallsms.schedule(sendMissedCallSMS, 1000,1*1000);
            Timer timer_twoLegDialing = new Timer();
            timer_twoLegDialing.schedule( twoLegDialing, 1000,1*1000 );
            
            String collectionName = new util().generateCollectionNameForEventsLog(serverDetails.getServerNamePrefix());
            ProcessEvents processEvents = new ProcessEvents(serverDetails,mongoConn);
            serverDao.updateCallProcessStatus("started",serverDetails.getServerID(), mongoConn);
            while(!stopRequest){
                processEvents.processDBEvents(serverDetails,collectionName);
                Thread.sleep(100);
            }
            mongoConn.close();
            System.exit(0);
        }
        catch(Exception ex){
            
        }
    }
    
}