package processdbevents.dao;

import processdbevents.model.CdrModel;
import com.mongodb.MongoClient;
import phonebridgelogger.model.Server;
import phonebridgelogger.model.Trunk;
import processdbevents.db.CdrDB;
import phonebridge.util.LogClass;
import phonebridge.util.util;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bson.Document;

public class CdrDAO{
    public void processCdr(Server serverDetails,CdrModel cdrDetails,util utilObj,MongoClient mongoConn){
        try{
            CdrDB cdrDBObj = new CdrDB();
            cdrDetails.setServerName(serverDetails.getServerName());
            cdrDetails.setRecordingFile(cdrDetails.getUserField());
            HashMap processedLogicReason = new HashMap();
            String userExtension = null;
            String phoneNumber = null;
            String cdrChannel = cdrDetails.getChannel();
            String cdrDestinationChannel = cdrDetails.getDestinationChannel();
            String cdrDestinationContext = cdrDetails.getDestinationContext();
            String collectionNameToInsertData = null;
            String transferedFrom = null;
            boolean transferedCall = false;
            String[] isInternalOrExternalCall = {"externalcall","Reason: Did Not Satisfy InternalCall Condition"};
            Trunk trunkUsedInCall = null;
            trunkUsedInCall = getTrunkUsedInCall(cdrChannel,cdrDestinationChannel,serverDetails.getTrunk());
            if(trunkUsedInCall==null && !this.checkIfQueueContextExistsInChannel(cdrChannel, serverDetails.getQueueContext()))
                isInternalOrExternalCall = this.checkIfInternalOrExternalCall(cdrChannel,cdrDestinationChannel,serverDetails.getQueueContext());
            processedLogicReason.put("IsInternalOrExternalLogic",isInternalOrExternalCall[1]);
            switch(isInternalOrExternalCall[0]){
                case "internalcall":
                    cdrDetails.setSipExtension(userExtension);
                    cdrDetails.setPhoneNumber(phoneNumber);
                    cdrDetails.setTrunkUsed(trunkUsedInCall);
                    cdrDetails.setCdrProcessedLogic(processedLogicReason);
                    collectionNameToInsertData = utilObj.returnCollectionName(serverDetails.getServerNamePrefix(),
                        cdrDetails.getStartTime(),isInternalOrExternalCall[0],null);
                    cdrDBObj.insertProcessedCDR(cdrDetails.getDocument(), collectionNameToInsertData, mongoConn);
                    break;
                case "externalcall":
                    String[] cdrType = this.getCdrType(cdrDetails,serverDetails);
                    processedLogicReason.put("CdrTypeLogic",cdrType[1]);
                    switch(cdrType[0]){
                        case "ivrcdr":
                            cdrDetails.setSipExtension(userExtension);
                            cdrDetails.setPhoneNumber(phoneNumber);
                            cdrDetails.setTrunkUsed(trunkUsedInCall);
                            cdrDetails.setCdrProcessedLogic(processedLogicReason);
                            collectionNameToInsertData = utilObj.returnCollectionName(serverDetails.getServerNamePrefix(),
                                cdrDetails.getStartTime(),cdrType[0],null);
                            cdrDBObj.insertProcessedCDR(cdrDetails.getDocument(), collectionNameToInsertData, mongoConn);
                            break;
                        case "queuecdr":
                            cdrDetails.setSipExtension(userExtension);
                            cdrDetails.setPhoneNumber(phoneNumber);
                            cdrDetails.setTrunkUsed(trunkUsedInCall);
                            cdrDetails.setCdrProcessedLogic(processedLogicReason);
                            collectionNameToInsertData = utilObj.returnCollectionName(serverDetails.getServerNamePrefix(),
                                cdrDetails.getStartTime(),cdrType[0],null);
                            cdrDBObj.insertProcessedCDR(cdrDetails.getDocument(), collectionNameToInsertData, mongoConn);
                            break;
                        case "callcdr":
                            String[] callDirectionInfo =  this.getCallDirection(cdrChannel, cdrDestinationChannel,cdrDestinationContext, trunkUsedInCall, serverDetails);
                            processedLogicReason.put("callDirectionLogic",callDirectionInfo[1]);
                            cdrDetails.setCallDirection(callDirectionInfo[0]);
                            switch(callDirectionInfo[0]){
                                case "incoming":
                                    if(this.checkIfTransferedCall(cdrDetails.getDestinationContext(),serverDetails.getTransferContext())){
                                        transferedCall = true;
                                        transferedFrom = this.getTransferedFrom(cdrDetails.getUniqueID(), serverDetails.getServerNamePrefix(), utilObj, mongoConn);
                                    }
                                    cdrDetails.setTransferedCall(transferedCall);
                                    cdrDetails.setTransferedFrom(transferedFrom);
                                    userExtension = this.getUserExtensionBasedOnCallDirection(cdrDetails,callDirectionInfo[0],transferedCall);
                                    phoneNumber = this.getPhoneNumberBasedOnCallDirection(cdrDetails, callDirectionInfo[0],transferedCall);
                                    cdrDetails.setSipExtension(userExtension);
                                    cdrDetails.setPhoneNumber(phoneNumber);
                                    cdrDetails.setTrunkUsed(trunkUsedInCall);
                                    cdrDetails.setCdrProcessedLogic(processedLogicReason);
                                    String callDisposition = cdrDetails.getDisposition();
                                    if(cdrDetails.getLastApplication().equalsIgnoreCase("Busy") || 
                                            cdrDetails.getLastApplication().equalsIgnoreCase("Congestion")){
                                        callDisposition = "BUSY";
                                    }
                                    collectionNameToInsertData = utilObj.returnCollectionName(serverDetails.getServerNamePrefix(),
                                            cdrDetails.getStartTime(),callDirectionInfo[0],callDisposition);
                                    cdrDBObj.insertProcessedCDR(cdrDetails.getDocument(), collectionNameToInsertData, mongoConn);
                                    break;
                                case "outgoing":
                                    cdrDetails.setTrunkUsed(trunkUsedInCall);
                                    if(this.checkIfTransferedCall(cdrDetails.getDestinationContext(),serverDetails.getTransferContext())){
                                        transferedCall = true;
                                        transferedFrom = this.getTransferedFrom(cdrDetails.getUniqueID(), serverDetails.getServerNamePrefix(), utilObj, mongoConn);
                                    }
                                    cdrDetails.setTransferedCall(transferedCall);
                                    cdrDetails.setTransferedFrom(transferedFrom);
                                    userExtension = this.getUserExtensionBasedOnCallDirection(cdrDetails,callDirectionInfo[0],transferedCall);
                                    phoneNumber = this.getPhoneNumberBasedOnCallDirection(cdrDetails, callDirectionInfo[0],transferedCall);
                                    phoneNumber = this.removePrefixFromPhoneNumber(phoneNumber, trunkUsedInCall);
                                    cdrDetails.setSipExtension(userExtension);
                                    cdrDetails.setPhoneNumber(phoneNumber);
                                    cdrDetails.setCdrProcessedLogic(processedLogicReason);
                                    collectionNameToInsertData = utilObj.returnCollectionName(serverDetails.getServerNamePrefix(),
                                            cdrDetails.getStartTime(),callDirectionInfo[0],cdrDetails.getDisposition());
                                    cdrDBObj.insertProcessedCDR(cdrDetails.getDocument(), collectionNameToInsertData, mongoConn);
                                    break;
                                case "unknown":
                                    cdrDetails.setSipExtension(userExtension);
                                    cdrDetails.setPhoneNumber(phoneNumber);
                                    cdrDetails.setTrunkUsed(trunkUsedInCall);
                                    cdrDetails.setCdrProcessedLogic(processedLogicReason);
                                    collectionNameToInsertData = utilObj.returnCollectionName(serverDetails.getServerNamePrefix(),
                                            cdrDetails.getStartTime(),callDirectionInfo[0],cdrDetails.getDisposition());
                                    cdrDBObj.insertProcessedCDR(cdrDetails.getDocument(), collectionNameToInsertData, mongoConn);
                                    break;
                            }
                            break;
                    }
                    break;
                case "unknowncall":
                    cdrDetails.setSipExtension(userExtension);
                    cdrDetails.setPhoneNumber(phoneNumber);
                    cdrDetails.setTrunkUsed(trunkUsedInCall);
                    cdrDetails.setCdrProcessedLogic(processedLogicReason);
                    collectionNameToInsertData = utilObj.returnCollectionName(serverDetails.getServerNamePrefix(),
                        cdrDetails.getStartTime(),isInternalOrExternalCall[0],null);
                    cdrDBObj.insertProcessedCDR(cdrDetails.getDocument(), collectionNameToInsertData, mongoConn);
                    break;
            }   
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "processCdr", ex.getMessage());
        }
    }
    
    private String[] getCdrType(CdrModel cdrDetails,Server serverDetails){
        String[] cdrType = new String[2];
        try{
            if(serverDetails.getIvrContext().length()>0 && cdrDetails.getDestinationContext().contains(serverDetails.getIvrContext())){
                cdrType[0] = "ivrcdr";
                cdrType[1] = "Reason: Destination Context Is "+cdrDetails.getDestinationContext();
            }
            else if(cdrDetails.getLastApplication().equalsIgnoreCase(serverDetails.getCdrQueueLastApplication())){
                cdrType[0] = "queuecdr";
                cdrType[1] = "Reason: Last Application Is "+cdrDetails.getLastApplication();
            }
            else{
                cdrType[0] = "callcdr";
                cdrType[1] = "Reason: Did Not Match With IVR and Queue Cdr Condition : "+cdrDetails.getDestinationContext()+
                        " : "+cdrDetails.getLastApplication();
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "getCdrType", ex.getMessage());
        }
        return cdrType;
    }
    
    public String[] checkIfInternalOrExternalCall(String channel,String destinationChannel,String queueContext){
        String[] isInternalOrExternal = new String[2];
        try{
            if(channel.contains("SIP/") && destinationChannel.contains("SIP/")){
                isInternalOrExternal[0] = "internalcall";
                isInternalOrExternal[1] = "Reason: Internal Call Because Channel Contains "+
                        channel+" and DestinationChannel Contains "+destinationChannel;
            }
            else if(channel.contains("Local/") && destinationChannel.contains("SIP/") 
                && (queueContext.length()>0 && !channel.contains(queueContext) && !destinationChannel.contains(queueContext))){
                isInternalOrExternal[0] = "internalcall";
                isInternalOrExternal[1] = "Reason: Internal Call Because Channel Contains "+
                        channel+" and DestinationChannel Contains "+destinationChannel;
            }
            else if(channel.contains("SIP/") && destinationChannel.contains("Local/")
                && (queueContext.length()>0 && !channel.contains(queueContext) && !destinationChannel.contains(queueContext))){
                isInternalOrExternal[0] = "internalcall";
                isInternalOrExternal[1] = "Reason: Internal Call Because Channel Contains "+
                        channel+" and DestinationChannel Contains "+destinationChannel;
            }
            else{
                isInternalOrExternal[0] = "unknowncall";
                isInternalOrExternal[1] = "Reason: Unknown Call Because Channel Contains "+
                        channel+" and DestinationChannel Contains "+destinationChannel+
                        " and did not match any Internal Call Condition";
            }
            
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfInternalOrExternalCall", ex.getMessage());
        }
        return isInternalOrExternal;
    }
    
    public String[] getCallDirection(String cdrChannel,String cdrDestinationChannel,String cdrDestinationContext,Trunk trunkUsed,Server serverDetails){
        String[] callDirectionInformation = new String[2];
        try{
            if(this.checkIfTransferedIncoming(cdrDestinationChannel, trunkUsed, cdrDestinationContext,serverDetails.getTransferContext())){
                callDirectionInformation[0] = "incoming";
                callDirectionInformation[1] = "DestinationChannel "+cdrDestinationChannel+
                        " Trunk "+trunkUsed.getTrunkValue()+" And DestinationContext Has "+serverDetails.getTransferContext()+
                        " so direction is Incoming";
            }
            else if(this.checkIfTransferedOutgoing(cdrChannel, trunkUsed, cdrDestinationContext,serverDetails.getTransferContext())){
                callDirectionInformation[0] = "outgoing";
                callDirectionInformation[1] = " Channel "+cdrChannel+
                        " has Trunk "+trunkUsed+" And DestinationContext Has "+serverDetails.getTransferContext()+
                        " so direction is Outgoing";
            }
            else if(this.checkIfQueueContextExistsInChannel(cdrChannel,serverDetails.getQueueContext())){
                callDirectionInformation[0] = "incoming";
                callDirectionInformation[1] = "Channel Contains "+cdrChannel+" Queue Context"+
                        serverDetails.getQueueContext()+" so direction is Incoming";
            }
            else if(this.checkIfChannelHasTrunk(cdrChannel, trunkUsed)){
                callDirectionInformation[0] = "incoming";
                callDirectionInformation[1] = "Channel "+cdrChannel+" Contains Trunk "+
                        trunkUsed.getTrunkValue()+" so direction is Incoming";
            }
            else if(this.checkIfDestinationChannelHasTrunk(cdrDestinationChannel, trunkUsed)){
                callDirectionInformation[0] = "outgoing";
                callDirectionInformation[1] =  "DestinationChannel "+cdrDestinationChannel+
                        " Contains Trunk "+trunkUsed.getTrunkValue()+" so direction is Outgoing";
            }
            else{
                callDirectionInformation[0] = "unknown";
                callDirectionInformation[1] = "Did Not Satisfy Any Incoming or Outgoing Call Conditions";
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "getCallDirection", ex.getMessage());
        }
        return callDirectionInformation;
    }
    
    /*
    public String[] getCallDirection(String cdrChannel,String cdrDestinationChannel,String cdrDestinationContext,Trunk trunkUsed,
            Server serverDetails,String destNumber,String extFromFollowMeChannel,String connectedLineNo,
            String mobileNoFromFollowMeChannel,String mobileNoFromDialString,ArrayList<CampaignModel> missedCallCampaigns){
        String[] callDirectionInformation = new String[3];
        try{
            if(this.checkIfTransferedIncoming(cdrDestinationChannel, trunkUsed, cdrDestinationContext,serverDetails.getTransferContext())){
                callDirectionInformation[0] = "incoming";
                callDirectionInformation[1] = "DestinationChannel "+cdrDestinationChannel+
                        " Trunk "+trunkUsed.getTrunkValue()+" And DestinationContext Has "+serverDetails.getTransferContext()+
                        " so direction is Incoming";
                callDirectionInformation[2] = "transferred Incoming Call";
            }
            else if(this.checkIfTransferedOutgoing(cdrChannel, trunkUsed, cdrDestinationContext,serverDetails.getTransferContext())){
                callDirectionInformation[0] = "outgoing";
                callDirectionInformation[1] = " Channel "+cdrChannel+
                        " has Trunk "+trunkUsed+" And DestinationContext Has "+serverDetails.getTransferContext()+
                        " so direction is Outgoing";
                callDirectionInformation[2] = "transferred Outgoing Call";
            }
            else if(this.checkIfQueueContextExistsInChannel(cdrChannel,cdrDestinationChannel,serverDetails,destNumber,connectedLineNo)){
                callDirectionInformation[0] = "incoming";
                callDirectionInformation[1] = "Channel Contains "+cdrChannel+" Queue Context"+
                        serverDetails.getQueueContext()+" so direction is Incoming";
                callDirectionInformation[2] = "Queue Incoming Call";
            }
            else if(this.checkIfChannelHasTrunk(cdrChannel, trunkUsed, serverDetails)){
                callDirectionInformation[0] = "incoming";
                callDirectionInformation[1] = "Channel "+cdrChannel+" Contains Trunk "+
                        trunkUsed.getTrunkValue()+" so direction is Incoming";
                callDirectionInformation[2] = "normal Incoming Call";
            }
            else if(this.checkIfDestinationChannelHasTrunk(cdrDestinationChannel, cdrChannel, trunkUsed ,serverDetails)){
                callDirectionInformation[0] = "outgoing";
                callDirectionInformation[1] =  "DestinationChannel "+cdrDestinationChannel+
                        " Contains Trunk "+trunkUsed.getTrunkValue()+"and not Contains DialOutContext "
                        +serverDetails.getDialOutContext()+" so direction is Outgoing";
                callDirectionInformation[2] = "normal Outgoing Call";
            }
            else if(this.checkIfDestinationChannelHasDialOutContext(cdrDestinationChannel, cdrChannel, serverDetails)){
                callDirectionInformation[0] = "incoming";
                callDirectionInformation[1] =  "Channel "+cdrChannel+ "Contains QueueContext"+serverDetails.getQueueContext()+
                        " And DestinationChannel "+cdrDestinationChannel+
                        " Contains DialOutContext "+serverDetails.getDialOutContext()+" so direction is Incoming";
                callDirectionInformation[2] = "FollowMe to mobile";
            }
            else if(this.checkIfChannelHasDialOutContext(cdrDestinationChannel, cdrChannel, trunkUsed, serverDetails, 
                    mobileNoFromFollowMeChannel,mobileNoFromDialString,connectedLineNo)){
                callDirectionInformation[0] = "incoming";
                callDirectionInformation[1] =  "Channel "+cdrChannel+
                        " Contains DialOutContext "+serverDetails.getDialOutContext()+"and DestinationChannel"
                        +cdrDestinationChannel+"contains trunk "+trunkUsed.getTrunkValue()+" so direction is Incoming";
                callDirectionInformation[2] = "FollowMe to Trunk";
            }
            else if(this.checkIfDestinationAndConnectedLineSame(destNumber,connectedLineNo)){
                callDirectionInformation[0] = "incoming";
                callDirectionInformation[1] = "destNumber Contains "+destNumber+" and ConnectedLineNo"+
                        connectedLineNo+"are Same so direction is Incoming";
                callDirectionInformation[2] = "FollowMe to Extension";
            }
            else if(this.checkIfChannelAndDestinationHasDialOutContext(cdrDestinationChannel, cdrChannel ,serverDetails)){
                callDirectionInformation[0] = "outgoing";
                callDirectionInformation[1] =  "DestinationChannel "+cdrDestinationChannel+
                        " Contains DialOutContext "+serverDetails.getDialOutContext()+"and Channel Contains "
                        +serverDetails.getDialOutContext()+" so direction is Outgoing";
                callDirectionInformation[2] = "FollowMe Outgoing Call";
            }
            else if(this.checkIfChannelAndDestinationHasSameExtension(cdrChannel ,serverDetails,destNumber,extFromFollowMeChannel)){
                callDirectionInformation[0] = "outgoing";
                callDirectionInformation[1] =  "Channel "+cdrChannel+
                        " Contains DialOutContext "+serverDetails.getDialOutContext()+"and Channel and Destination has same Extension "
                        +extFromFollowMeChannel+" so direction is Outgoing";
                callDirectionInformation[2] = "FollowMe Outgoing Call";
            }
            else if(this.checkIfChannelHasDialOutContextAndConnLineNoHasFMGL(cdrDestinationChannel, cdrChannel, trunkUsed, serverDetails, 
                    mobileNoFromFollowMeChannel,mobileNoFromDialString,connectedLineNo)){
                callDirectionInformation[0] = "outgoing";
                callDirectionInformation[1] =  "Channel "+cdrChannel+
                        " Contains DialOutContext "+serverDetails.getDialOutContext()+"and Destination Channel"+cdrDestinationChannel+
                        " Contains Trunk and mobileNo From Channel "+mobileNoFromFollowMeChannel+" and Dial String "+mobileNoFromDialString+
                        "and Connected Line No Contains FMGL so direction is Outgoing";
                callDirectionInformation[2] = "FollowMe Outgoing Call";
            }
            
            else if(this.checkIfChannelHasDialOutContextAndChannelAndDialStringNotSame(cdrDestinationChannel, cdrChannel, trunkUsed, serverDetails, 
                    mobileNoFromFollowMeChannel,mobileNoFromDialString)){
                callDirectionInformation[0] = "outgoing";
                callDirectionInformation[1] =  "Channel "+cdrChannel+
                        " Contains DialOutContext "+serverDetails.getDialOutContext()+"and Destination Channel"+cdrDestinationChannel+
                        " Contains Trunk and mobileNo From Channel "+mobileNoFromFollowMeChannel+" and Dial String "+mobileNoFromDialString+
                        "are Not Same so direction is Outgoing";
                callDirectionInformation[2] = "FollowMe OutgoingCall to Customer Number";
            }
            
            else if(this.checkIfChannelHasMissedCallTrunk(cdrChannel,missedCallCampaigns)!=null){
                callDirectionInformation[0] = "missedCall";
                callDirectionInformation[1] =  "Channel "+cdrChannel+
                        " Contains missedCall Trunk "+serverDetails.getMissedCallTrunk()+"so Call is missedCall";
                callDirectionInformation[2] = "missedCall";
            }
            
            else{
                callDirectionInformation[0] = "unknown";
                callDirectionInformation[1] = "Did Not Satisfy Any Incoming or Outgoing Call Conditions";
                callDirectionInformation[2] = "unknown";
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "getCallDirection", ex.getMessage());
        }
        return callDirectionInformation;
    }*/
    
    private boolean checkIfQueueContextExistsInChannel(String channel,String queueContext){
        boolean isExist = false;
        try{
            if(queueContext.length()>0 && channel.contains(queueContext)){
                isExist = true;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfQueueContextExistsInChannel", ex.getMessage());
        }
        return isExist;
    }
    
    public boolean checkIfChannelHasTrunk(String channel,Trunk trunkUsed){
        boolean isExist = false;
        try{
            if(channel.contains(trunkUsed.getTrunkValue())){
                isExist = true;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfChannelHasTrunk", ex.getMessage());
        }
        return isExist;
    }
    
    private boolean checkIfTransferedIncoming(String destinationChannel,Trunk trunkUsed,String destinationContext,String transferContext){
        boolean isExist = false;
        try{
            if(transferContext.length()>0 && destinationChannel.contains(trunkUsed.getTrunkValue()) && destinationContext.contains(transferContext)){
                isExist = true;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfTransferedIncoming", ex.getMessage());
        }
        return isExist;
    }
    
    private boolean checkIfTransferedOutgoing(String channel,Trunk trunkUsed,String destinationContext,String transferContext){
        boolean isExist = false;
        try{
            if(channel.contains(trunkUsed.getTrunkValue()) && transferContext.length()>0 && destinationContext.contains(transferContext)){
                isExist = true;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfTransferedOutgoing", ex.getMessage());
        }
        return isExist;
    }
    
    private boolean checkIfDestinationChannelHasTrunk(String destinationChannel,Trunk trunkUsed){
        boolean isExist = false;
        try{
            if(destinationChannel.contains(trunkUsed.getTrunkValue())){
                isExist = true;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfDestinationChannelHasTrunk", ex.getMessage());
        }
        return isExist;
    }
    
    private boolean checkIfDestinationChannelHasDialOutContext(String destinationChannel,String channel, Server serverDetails){
        boolean isExist = false;
        try{
            if(channel.contains(serverDetails.getQueueContext()) && destinationChannel.contains(serverDetails.getDialOutContext())){
                isExist = true;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfDestinationChannelHasDialOutContext", ex.getMessage());
        }
        return isExist;
    }
    
    private boolean checkIfChannelHasDialOutContext(String destinationChannel,String channel, Trunk trunkused, 
            Server serverDetails,String mobileNoFromFollowMeChannel,String mobileNoFromDialString,String connectedLineNo){
        boolean isExist = false;
        try{
            if(channel.contains(serverDetails.getDialOutContext()) && destinationChannel.contains(trunkused.getCdrTrunkValue())
                    && mobileNoFromDialString.equals(mobileNoFromFollowMeChannel) && !connectedLineNo.contains("FMGL")){
                isExist = true;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfChannelHasDialOutContext", ex.getMessage());
        }
        return isExist;
    }
    
    private boolean checkIfChannelAndDestinationHasDialOutContext(String destinationChannel,String channel, Server serverDetails){
        boolean isExist = false;
        try{
            if(channel.contains(serverDetails.getDialOutContext()) && destinationChannel.contains(serverDetails.getDialOutContext())){
                isExist = true;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfChannelAndDestinationHasDialOutContext", ex.getMessage());
        }
        return isExist;
    }
    
    private boolean checkIfChannelAndDestinationHasSameExtension(String channel,Server serverDetails, String destinationNumber,String extFromFollowMeChannel){
        boolean isExist = false;
        try{
            if(channel.contains(serverDetails.getDialOutContext()) && extFromFollowMeChannel.equals(destinationNumber)){
                isExist = true;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfChannelAndDestinationHasSameExtension", ex.getMessage());
        }
        return isExist;
    }
    
    private boolean checkIfChannelHasDialOutContextAndConnLineNoHasFMGL(String destinationChannel,String channel, Trunk trunkused, 
            Server serverDetails,String mobileNoFromFollowMeChannel,String mobileNoFromDialString,String connectedLineNo){
        boolean isExist = false;
        try{
            if(channel.contains(serverDetails.getDialOutContext()) && destinationChannel.contains(trunkused.getCdrTrunkValue())
                    && mobileNoFromDialString.equals(mobileNoFromFollowMeChannel) && connectedLineNo.contains("FMGL")){
                isExist = true;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfChannelHasDialOutContextAndConnLineNoHasFMGL", ex.getMessage());
        }
        return isExist;
    }
    
    private boolean checkIfChannelHasDialOutContextAndChannelAndDialStringNotSame(String destinationChannel,String channel, Trunk trunkused, 
            Server serverDetails,String mobileNoFromFollowMeChannel,String mobileNoFromDialString){
        boolean isExist = false;
        try{
            if(channel.contains(serverDetails.getDialOutContext()) && destinationChannel.contains(trunkused.getCdrTrunkValue())
                    && !mobileNoFromDialString.equals(mobileNoFromFollowMeChannel)){
                isExist = true;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfChannelHasDialOutContextAndConnLineNoHasFMGL", ex.getMessage());
        }
        return isExist;
    }
    
    /*private CampaignModel checkIfChannelHasMissedCallTrunk(String channel,ArrayList<CampaignModel> missedCallCampaignModels){
        CampaignModel result = null;
        try{
            for(CampaignModel cmp:missedCallCampaignModels)
            {
                if(channel.contains(cmp.getTrunkValue())){
                    result = cmp;
                    break;
                }
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfChannelHasMissedCallTrunk", ex.getMessage());
        }
        return result;
    }*/
    
    private boolean checkIfDestinationAndConnectedLineSame(String destinationNumber,String connectedLineNo){
        boolean isExist = false;
        try{
            if(destinationNumber.contains(connectedLineNo)){
                isExist = true;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfDestinationAndConnectedLineSame", ex.getMessage());
        }
        return isExist;
    }
    
    public Trunk getTrunkUsedInCall(String channel,String destinationChannel,ArrayList<Trunk> trunkDetails){
        Trunk trunkUsed = null;
            String tempChannel = "";
            String tempDestinationChannel = "";
            if(channel!=null)
                tempChannel = channel;
            if(destinationChannel!=null)
                tempDestinationChannel = destinationChannel;
        try{          
            for(Trunk eachTrunk:trunkDetails){
                if(channel.contains(eachTrunk.getTrunkValue())){
                    trunkUsed = eachTrunk;
                }
                else
                {
                    if(destinationChannel!=null)
                        if(destinationChannel.contains(eachTrunk.getTrunkValue()))
                            trunkUsed = eachTrunk;
                }
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "getTrunkUsedInCall", ex.getMessage());
        }
        return trunkUsed;
    }
    
    private String getUserExtensionBasedOnCallDirection(CdrModel cdrDetails,String callDirection,boolean transferedCall){
        String userExtension = null;
        try{
            switch(callDirection){
                case "outgoing":
                    userExtension = (transferedCall)?cdrDetails.getDestination():cdrDetails.getSource();
                    if(userExtension.length()==0 && !transferedCall){
                        userExtension = new DialBeginDAO().getExtensionFromChannelOrDestination(cdrDetails.getChannel());
                    }
                    break;
                case "incoming":
                    userExtension = (transferedCall)?cdrDetails.getSource():cdrDetails.getDestination();
                    if(userExtension=="s"){
                        String[] lastDataSplitUp = cdrDetails.getLastData().split("/");
                        userExtension = lastDataSplitUp[1];
                    }
                    break;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "getUserExtensionBasedOnCallDirection", ex.getMessage());
        }
        return userExtension;
    }
    
    
    

    private String getPhoneNumberBasedOnCallDirection(CdrModel cdrDetails,String callDirection,boolean transferedCall){
        String phoneNumber = null;
        try{
            switch(callDirection){
                case "outgoing":
                    phoneNumber = (transferedCall)?cdrDetails.getSource():cdrDetails.getDestination();
                    break;
                case "incoming":
                    phoneNumber = (transferedCall)?cdrDetails.getDestination():cdrDetails.getSource();
                    break;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "getPhoneNumberBasedOnCallDirection", ex.getMessage());
        }
        return phoneNumber;
    }
    
    private String removePrefixFromPhoneNumber(String numberToRemovePrefix,Trunk trunkDetails){
        String result=numberToRemovePrefix;
        try{
            if(trunkDetails.getPrefix().length()>0)
                if(numberToRemovePrefix.substring(0,trunkDetails.getPrefix().length()).equalsIgnoreCase(trunkDetails.getPrefix()))
                    result = numberToRemovePrefix.substring(trunkDetails.getPrefix().length(),(numberToRemovePrefix.length()-trunkDetails.getPrefix().length())+1);
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "removePrefixFromPhoneNumber", ex.getMessage());
        }
        return result;
    }
    
    private boolean checkIfTransferedCall(String destinationContext,String transferContext){
        boolean transferedCall = false;
        try{
            if(transferContext.length()>0 && destinationContext.contains(transferContext)){
                transferedCall = true;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "checkIfTransferedCall", ex.getMessage());
        }
        return transferedCall;
    }
    
    private String getTransferedFrom(String uniqueID,String serverNamePrefix,util utilObj,MongoClient mongoConn){
        String transferedFrom = null;
        try{
            String eventCollectionName = utilObj.generateCollectionNameForEventsLog(serverNamePrefix);
            String channel = new CdrDB().getTransferedFrom(uniqueID, eventCollectionName, mongoConn);
            transferedFrom = this.getExtensionFromChannel(channel);
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "getTransferedFrom", ex.getMessage());
        }
        return transferedFrom;
    }
    
    private String getExtensionFromChannel(String channel){
        String extension = null;
        try{
            
            String[] tempChannel = channel.split("-");
            tempChannel = tempChannel[0].split("/");
            extension = tempChannel[1];
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "getExtensionFromChannel", ex.getMessage());
        }
        return extension;
    }
    
    public CdrModel createCdrObject(Document cdrEvents){
        CdrModel cdr = new CdrModel();
        try{
            util utilObj = new util();
            cdr.setEventName(cdrEvents.getString("event"));
            cdr.setPrivilege(cdrEvents.getString("privilege"));
            cdr.setAccountCode(cdrEvents.getString("accountcode"));
            cdr.setSource(cdrEvents.getString("source"));
            cdr.setDestination(cdrEvents.getString("destination"));
            cdr.setDestinationContext(cdrEvents.getString("destinationcontext"));
            cdr.setCallerID(cdrEvents.getString("callerid"));
            cdr.setChannel(cdrEvents.getString("channel"));
            cdr.setDestinationChannel(cdrEvents.getString("destinationchannel"));
            cdr.setLastApplication(cdrEvents.getString("lastapplication"));
            cdr.setLastData(cdrEvents.getString("lastdata"));
            try{
                cdr.setStartTime(utilObj.convertSQLStringToDate(cdrEvents.getString("starttime")));
            }
            catch(NullPointerException nexc){
                LogClass.logMsg("CdrDAO", "createCdrObject", nexc.getMessage());
                cdr.setStartTime(null);
            }
            catch(ParseException pexc){
                LogClass.logMsg("CdrDAO", "createCdrObject", pexc.getMessage());
                cdr.setStartTime(null);
            }
            catch(Exception ex){
                LogClass.logMsg("CdrDAO", "createCdrObject", ex.getMessage());
                cdr.setStartTime(null);
            }
            try{
                cdr.setAnswerTime(utilObj.convertSQLStringToDate(cdrEvents.getString("answertime")));
            }
            catch(NullPointerException nexc){
                LogClass.logMsg("CdrDAO", "createCdrObject", nexc.getMessage());
                cdr.setAnswerTime(null);
            }
            catch(ParseException pexc){
                LogClass.logMsg("CdrDAO", "createCdrObject", pexc.getMessage());
                cdr.setAnswerTime(null);
            }
            catch(Exception ex){
                LogClass.logMsg("CdrDAO", "createCdrObject", ex.getMessage());
                cdr.setAnswerTime(null);
            }
            try{
                cdr.setEndTime(utilObj.convertSQLStringToDate(cdrEvents.getString("endtime")));
            }
            catch(NullPointerException nexc){
                LogClass.logMsg("CdrDAO", "createCdrObject", nexc.getMessage());
                cdr.setEndTime(null);
            }
            catch(ParseException pexc){
                LogClass.logMsg("CdrDAO", "createCdrObject", pexc.getMessage());
                cdr.setEndTime(null);
            }
            catch(Exception ex){
                LogClass.logMsg("CdrDAO", "createCdrObject", ex.getMessage());
                cdr.setEndTime(null);
            }
            
            cdr.setDuration(new util().returnIntegerValueFromObject(cdrEvents.getString("duration")));
            cdr.setBillableSeconds(new util().returnIntegerValueFromObject(cdrEvents.getString("billableseconds")));
            cdr.setDisposition(cdrEvents.getString("disposition"));
            cdr.setAmaFlags(cdrEvents.getString("amaflags"));
            cdr.setUniqueID(cdrEvents.getString("uniqueid"));
            cdr.setUserField(cdrEvents.getString("userfield"));
            cdr.setEventTime(cdrEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "createCdrObject", ex.getMessage());
        }
        return cdr;
    }
    
    public CdrModel getCdrDataFromDB(String docID,String uniqueID,String serverNamePrefix,MongoClient mongoConn){
        CdrModel cdr = null;
        try{
            cdr = new CdrDB().getRecordingFileNameForUniqueID(docID, uniqueID, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "getCdrData", ex.getMessage());
        }
        return cdr;
    }
    
    public String getExtensionFromDestination(String destination){
        String extension = null;
        try{
            if(destination.contains("SIP/"))
            {
                Pattern ptr =Pattern.compile("(?<=\\/)\\d+(?=\\-)");
                Matcher mch = ptr.matcher(destination);
                if(mch.find()){
                    extension = mch.group();
                }
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "getExtensionFromDestination", ex.getMessage());
        }
        return extension;
    }
    
    public String getExtensionFromFollowMeChannel(String channel){
        String extension = null;
        try{
            if(channel.contains("Local/FMPR"))
            {
                Pattern ptr =Pattern.compile("(?<=\\-)\\d+(?=\\@)");
                Matcher mch = ptr.matcher(channel);
                if(mch.find()){
                    extension = mch.group();
                }
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "getExtensionFromDestination", ex.getMessage());
        }
        return extension;
    }
    
    public String getNumberFromChannel(String channel){
        String mobileNumber = null;
        try{
            if(channel.contains("Local/"))
            {
                Pattern ptr = Pattern.compile("(?<=\\/)\\d+(?=\\@)");
                Matcher mch = ptr.matcher(channel);
                if(mch.find()){
                    mobileNumber = mch.group();
                }
            }
            else if(channel.contains("SIP/"))
            {
                Pattern ptr = Pattern.compile("(?<=\\/)\\d+(?=\\-)");
                Matcher mch = ptr.matcher(channel);
                if(mch.find()){
                    mobileNumber = mch.group();
                }
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "getExtensionFromDestination", ex.getMessage());
        }
        return mobileNumber;
    }
    
    public CdrModel createCdrObjectFromCurrentCalls(Document cdrEvents){
        CdrModel cdr = new CdrModel();
        try{
            util utilObj = new util();
            cdr.setEventName(cdrEvents.getString("eventName"));
            cdr.setPrivilege(cdrEvents.getString("privilege"));
            cdr.setAccountCode(cdrEvents.getString("accountCode"));
            cdr.setSource(cdrEvents.getString("source"));
            cdr.setDestination(cdrEvents.getString("destination"));
            cdr.setDestinationContext(cdrEvents.getString("destinationContext"));
            cdr.setCallerID(cdrEvents.getString("callerID"));
            cdr.setChannel(cdrEvents.getString("channel"));
            cdr.setDestinationChannel(cdrEvents.getString("destinationChannel"));
            cdr.setLastApplication(cdrEvents.getString("lastApplication"));
            cdr.setLastData(cdrEvents.getString("lastData"));
            try{
                cdr.setStartTime(cdrEvents.getDate("startTime"));
            }
            catch(Exception ex){
                LogClass.logMsg("CdrDAO", "createCdrObject", ex.getMessage());
                cdr.setStartTime(null);
            }
            try{
                cdr.setAnswerTime(cdrEvents.getDate("answerTime"));
            }
            catch(Exception ex){
                LogClass.logMsg("CdrDAO", "createCdrObject", ex.getMessage());
                cdr.setAnswerTime(null);
            }
            try{
                cdr.setEndTime(cdrEvents.getDate("endTime"));
            }
            catch(Exception ex){
                LogClass.logMsg("CdrDAO", "createCdrObject", ex.getMessage());
                cdr.setEndTime(null);
            }
            
            cdr.setDuration(cdrEvents.getInteger("duration"));
            cdr.setBillableSeconds(cdrEvents.getInteger("billableseconds"));
            cdr.setDisposition(cdrEvents.getString("disposition"));
            cdr.setAmaFlags(cdrEvents.getString("amaFlags"));
            cdr.setUniqueID(cdrEvents.getString("uniqueID"));
            cdr.setUserField(cdrEvents.getString("userField"));
            cdr.setEventTime(cdrEvents.getDate("eventTime"));
        }
        catch(Exception ex){
            LogClass.logMsg("CdrDAO", "createCdrObject", ex.getMessage());
        }
        return cdr;
    }
}
