/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.sms;

/**
 *
 * @author venky
 */
public class sendMissedCallSMS{
    private String phoneNumber;
    private String smsContent;
    private String smsType;
    private String listID;
    private boolean isSLABreach;
    private String smsID;
    private String serverNamePrefix;
    
    public sendMissedCallSMS(String phoneNumber,String smsContent,String smsType,String listID,boolean isSLA,String smsID,String serverNamePrefix){
        this.phoneNumber = phoneNumber;
        this.smsContent = smsContent;
        this.smsType = smsType;
        this.listID = listID;
        this.isSLABreach = isSLA;
        this.smsID = smsID;
        this.serverNamePrefix = serverNamePrefix;
    }
    
    public void sendsms(){
        new SmsDB().sendMissedCallSMSTONums(phoneNumber, smsContent, smsType, listID, isSLABreach,smsID,serverNamePrefix);
    }
    
    public void run() {
        this.sendsms();
    }
    
}
