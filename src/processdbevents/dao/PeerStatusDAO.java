/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import org.bson.Document;
import processdbevents.model.PeerStatusModel;

/**
 *
 * @author venky
 */
public class PeerStatusDAO {
    public PeerStatusModel createPeerStatusObject(Document peerStatusEvent){
        PeerStatusModel peerStatus = new PeerStatusModel();
        try{
            peerStatus.setEventName(peerStatusEvent.getString("event"));
        }catch(Exception e){}
        try{
            peerStatus.setPrivilege(peerStatusEvent.getString("privilege"));
        }catch(Exception e){}
        try{
            peerStatus.setChannelType(peerStatusEvent.getString("channeltype"));
        }catch(Exception e){}
        try{
            peerStatus.setPeer(peerStatusEvent.getString("peer"));
        }catch(Exception e){}
        try{
            peerStatus.setPeerStatus(peerStatusEvent.getString("peerstatus"));
        }catch(Exception e){}
        try{
            peerStatus.setAddress(peerStatusEvent.getString("address"));
        }catch(Exception e){}
        try{
            peerStatus.setEventTime(peerStatusEvent.getDate("eventtime"));
        }catch(Exception e){}
        try{
            peerStatus.setCause(peerStatusEvent.getString("cause"));
        }catch(Exception e){}
        return peerStatus;
    }
    
    public String getExtensionFromPeer(String peer){
        String extension = null;
        try{
            String[] extensionSourceArr = peer.split("/");
            extension = extensionSourceArr[1];
        }catch(Exception e){
            e.printStackTrace();
        }
        return extension;
    }
    
    public String getRegisteredIpAddress(String address){
        String ipAddress = null;
        try{
            String[] ipAddressArr = address.split(":");
            ipAddress = ipAddressArr[0];
        }catch(Exception e){
            e.printStackTrace();
        }
        return ipAddress;
    }
}
