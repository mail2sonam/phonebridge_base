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
public class SMSModel {
    private String smsID;
    private String phoneNumber;
    private String smsType;
    private String triggerOn;
    private String smsContent;
    private String smsTo;
    private String smsNumbers;
    private boolean isSLABreach;
    private String listID;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSmsType() {
        return smsType;
    }

    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }

    public String getTriggerOn() {
        return triggerOn;
    }

    public void setTriggerOn(String triggerOn) {
        this.triggerOn = triggerOn;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public String getSmsID() {
        return smsID;
    }

    public void setSmsID(String smsID) {
        this.smsID = smsID;
    }

    public String getSmsTo() {
        return smsTo;
    }

    public void setSmsTo(String smsTo) {
        this.smsTo = smsTo;
    }

    public String getSmsNumbers() {
        return smsNumbers;
    }

    public void setSmsNumbers(String smsNumbers) {
        this.smsNumbers = smsNumbers;
    }

    public boolean isIsSLABreach() {
        return isSLABreach;
    }

    public void setIsSLABreach(boolean isSLABreach) {
        this.isSLABreach = isSLABreach;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }
}
