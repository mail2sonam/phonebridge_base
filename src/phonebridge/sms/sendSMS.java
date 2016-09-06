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
public class sendSMS{
    private String phoneNumber;
    private String smsContent;
    private String smsType;
    private String smsID;
    private String serverNamePrefix;
    private SMSModel smsModel;
    
    public sendSMS(SMSModel smsModel,String serverNamePrefix){
        this.smsModel = smsModel;
        this.serverNamePrefix = serverNamePrefix;
    }
    
    public void sendsms(){
        new SmsDB().sendSMSTONums(smsModel,serverNamePrefix);
    }
    
    public void run() {
        this.sendsms();
    }
    
}
