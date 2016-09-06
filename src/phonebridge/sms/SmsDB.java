/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.sms;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.settings.AdditionalSettings;
import com.settings.AdditionalSettingsDao;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bson.Document;
import org.bson.types.ObjectId;
import singleton.db.DBClass;


/**
 *
 * @author theyva
 */
public class SmsDB{
    public void sendSMSTONums(SMSModel sms,String serverNamePrefix){
        try{
            String data = null;
            data = URLEncoder.encode(sms.getSmsContent(),"UTF-8");
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            AdditionalSettings settings = new AdditionalSettingsDao().getCurrentSettings();
            String smsUrl = settings.getMessageURL();
            String mobileField = settings.getMobileNumber();
            
            String tempURL = smsUrl+"&"+mobileField+"="+sms.getPhoneNumber()+"&"+settings.getMessage()+"="+data;
            URL url = new URL(tempURL);
            System.out.println(url);
            boolean smsSent = false;
            URLConnection urlConn = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String inputLine;
            String smsResponse = "";
            while((inputLine = bufferedReader.readLine())!=null){
                if(inputLine.contains("<messageid>")){
                    Pattern ptr = Pattern.compile("(?<=\\>)\\d+(?=\\<)");
                    Matcher mch = ptr.matcher(inputLine);
                    if(mch.find())
                        smsSent = true;
                }
                smsResponse = smsResponse.concat(inputLine).concat(" ");
            }
            String smsSentStatus = null;
            if(smsSent)
                smsSentStatus="Success";
            else
                smsSentStatus = "failed";
            MongoCollection smsColl = mongoDB.getCollection(serverNamePrefix+"_smsLog");
            Document inSMSLogs = new Document("phoneNumber",sms.getPhoneNumber());
            inSMSLogs.append("message", sms.getSmsContent());
            inSMSLogs.append("status", inputLine);
            inSMSLogs.append("eventDate", new Date());
            inSMSLogs.append("smsType", sms.getSmsType());                   
            inSMSLogs.append("smsSentStatus", smsSentStatus);
            inSMSLogs.append("smsResponse", smsResponse);
            inSMSLogs.append("smsURL", tempURL);
            smsColl.insertOne(inSMSLogs);
            //if(smsSentStatus!=null && smsSentStatus.equals("Success"))
            this.updateSMSSentTime(sms.getSmsID(),serverNamePrefix);    
        }
        catch(Exception exe){
            exe.getMessage();
        }        
    }
    
    public void updateSMSURL(String smsID,String smsURL,String serverNamePrefix){
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix+"_sendsmsqueue");
            Document whereQry = new Document("_id",new ObjectId(smsID));
            Document updateQry = new Document("smsURL",smsURL);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(Exception e){
            e.getMessage();
        }
    }
    
    public void updateSMSSentTime(String smsID,String serverNamePrefix){
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix+"_sendsmsqueue");
            Document whereQry = new Document("_id",new ObjectId(smsID));
            whereQry.append("smsSentOn", null);
            Document updateQry = new Document("smsSentOn",new Date());
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(Exception e){
            e.getMessage();
        }
    }
    
    public void insertSMSDetails(String phoneNumber,String smsContent,String smsType,String serverNamePrefix){
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix+"_sendsmsqueue");
            Document insertQry = new Document();
            insertQry.append("phoneNumber", phoneNumber);
            insertQry.append("smsContent", smsContent);
            insertQry.append("smsType", smsType);
            insertQry.append("insertedOn", new Date());
            insertQry.append("smsSentOn", null);
            collection.insertOne(insertQry);
        }
        catch(Exception e){
            e.getMessage();
        }
    }
    
    public ArrayList<SMSModel> getSMSDetails(String serverNamePrefix){
        ArrayList<SMSModel> smsReqs = new ArrayList<>();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix+"_sendsmsqueue");
            Document whereQry = new Document("smsSentOn", null);
            MongoCursor cursor = collection.find(whereQry).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                SMSModel smsReq = new SMSModel();
                smsReq.setPhoneNumber(dc.getString("phoneNumber"));
                smsReq.setSmsContent(dc.getString("smsContent"));
                smsReq.setSmsType(dc.getString("smsType"));
                smsReq.setSmsID(dc.getObjectId("_id").toString());
                smsReqs.add(smsReq);
            }
        }
        catch(Exception e){
            e.getMessage();
        }
        return smsReqs;
    }
    
    public void insertMissedCallSMSData(String phoneNumber,String listID,boolean isSLA,SMSModel smsTemplate,String serverNamePrefix)
    {           
        try
        { 
            /*System.out.println("collection Name:"+collectionName);   
            String collectionNme="lst_"+collectionName;
            MongoClient mongoconn=DBClass.getInstance().getConnection();
            MongoDatabase mongoDB=mongoconn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection=mongoDB.getCollection("campaign");
            Document wherequery=new Document("deleted",0);
            wherequery.append("_id", new ObjectId(campaignID));            
            Document selectqry=new Document("sms",1);
            MongoCursor cursor=collection.find(wherequery).projection(selectqry).limit(1).iterator();            
            while(cursor.hasNext()){
                Document smsDoc=(Document) cursor.next();
                Object ob=smsDoc.get("sms");
                ArrayList<Object> smsDetails = (ArrayList<Object>) ob;
                 for(Object sms:smsDetails){
                    Document singleSMSDoc=(Document) sms;
                    String smsvalue=singleSMSDoc.getString("smsText");*/
                    if(smsTemplate.getSmsContent().length()>0)
                    {
                        String smsValue= smsTemplate.getSmsContent();
                        Pattern ptn=Pattern.compile("\\{#([^}]*)\\}");
                        Matcher m=ptn.matcher(smsValue);
                        System.out.println(m);                    
                        while(m.find())
                        {
                            System.out.println(m.group(1)+"and group 0 is:"+m.group(0));
                            MongoClient mongoConn = DBClass.getInstance().getConnection();
                            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
                            MongoCollection coll=mongoDB.getCollection("lst_"+listID);
                            Document whreqry=new Document("phoneNumber",phoneNumber);
                            Document selqry=new Document(m.group(1),1);
                            MongoCursor cur=coll.find(whreqry).projection(selqry).limit(1).iterator();
                            while(cur.hasNext())
                            {
                                Document d=(Document) cur.next();
                                if(smsValue.contains(m.group(0)) && d.getString(m.group(1))!=null)
                                {
                                    smsValue=smsValue.replace(m.group(0),d.getString(m.group(1))); 
                                    System.out.println(smsValue);
                                }
                                else
                                {
                                    smsValue=smsValue.replace(m.group(0),""); 
                                    System.out.println(smsValue);
                                }
                            }                       
                        } 
                        if(smsTemplate.getSmsTo().equals("custom"))
                        {            
                            String phNums=smsTemplate.getSmsNumbers();
                            String[] phNumAry=phNums.split(",");
                            for(String nums:phNumAry)
                            {
                                String phoneNum=nums.trim();
                                this.insertMissedCallSMSDetails(phoneNum, smsValue,"InnerCustom",isSLA,listID,serverNamePrefix);                              
                            }
                        }
                        else
                        {
                            this.insertMissedCallSMSDetails(phoneNumber, smsValue,"Customer",isSLA,listID,serverNamePrefix);
                        }
                }          
            }
        catch(Exception exec){            
        }
    }
    
    public void insertMissedCallSMSDetails(String phoneNumber,String smsContent,String smsType,boolean isSLA,String listID,String serverNamePrefix){
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix+"_sendsmsqueue");
            Document insertQry = new Document();
            insertQry.append("phoneNumber", phoneNumber);
            insertQry.append("smsContent", smsContent);
            insertQry.append("smsType", smsType);
            insertQry.append("listID", listID);
            insertQry.append("isSLABreach", isSLA);
            insertQry.append("insertedOn", new Date());
            insertQry.append("smsSentOn", null);
            collection.insertOne(insertQry);
        }
        catch(Exception e){
            e.getMessage();
        }
    }
    
    public ArrayList<SMSModel> getMissedCallsmsDetails(String serverNamePrefix){
        ArrayList<SMSModel> smsReqs = new ArrayList<>();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix+"_sendsmsqueue");
            Document whereQry = new Document("smsSentOn", null);
            MongoCursor cursor = collection.find(whereQry).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                SMSModel smsReq = new SMSModel();
                smsReq.setPhoneNumber(dc.getString("phoneNumber"));
                smsReq.setSmsContent(dc.getString("smsContent"));
                smsReq.setSmsType(dc.getString("smsType"));
                smsReq.setListID(dc.getString("listID"));
                try{
                    smsReq.setIsSLABreach(dc.getBoolean("isSLABreach"));
                }catch(Exception e){}
                smsReq.setSmsID(dc.getObjectId("_id").toString());
                smsReqs.add(smsReq);
            }
        }
        catch(Exception e){
            e.getMessage();
        }
        return smsReqs;
    }
    
    public ArrayList<SMSModel> getAllSMSTemplates(String campaignID){
        ArrayList<SMSModel> smsTemps = new ArrayList<>();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQry = new Document("_id",new ObjectId(campaignID));
            Document selectQry = new Document("sms",1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                ArrayList<Document> smsTemplates = (ArrayList<Document>) dc.get("sms");
                for(Document smsTemplate:smsTemplates)
                {
                    SMSModel sm = new SMSModel();
                    try{
                        sm.setSmsContent(smsTemplate.getString("smsText"));
                    }catch(Exception e){}
                    try{
                        sm.setTriggerOn(smsTemplate.getString("triggerOn"));
                    }catch(Exception e){}
                    try{
                        sm.setSmsTo(smsTemplate.getString("smsTo"));
                    }catch(Exception e){}
                    try{
                        sm.setSmsNumbers(smsTemplate.getString("customNums"));
                    }catch(Exception e){}
                    smsTemps.add(sm);
                }
            }
        }
        catch(Exception e){
            e.getMessage();
        }
        return smsTemps;
    }
    
    public void sendMissedCallSMSTONums(String phoneNumber,String message,String smsType,String collectionName,
        boolean isSLA,String smsID,String serverNamePrefix){
        try{
            String data = null;
            data = URLEncoder.encode(message,"UTF-8");
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("settings");
            //Document wherequery1=new Document("_id",new ObjectId("5600f04615cde3203617145f"));
            Document selectQry = new Document("url", 1);
            selectQry.append("mobileNumber", 1);
            selectQry.append("message", 1);
            Document sortQry = new Document("dateEntered",-1);
            MongoCursor cursor = collection.find().projection(selectQry).sort(sortQry).limit(1).iterator();
            while(cursor.hasNext()){
                Document dt = (Document) cursor.next();
                System.out.println(dt);
                String docURL = dt.getString("url");
                String mobileNumber = dt.getString("mobileNumber");
                String msg = dt.getString("message");
                String tempURL = docURL+"&"+mobileNumber+"="+phoneNumber+"&"+msg+"="+data;
                URL url = new URL(tempURL);
                System.out.println(url);
                boolean smsSent = false;
                URLConnection urlConn = url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String inputLine;
                String smsResponse = "";
                while((inputLine = bufferedReader.readLine())!=null){
                    if(inputLine.contains("<messageid>")){
                        Pattern ptr = Pattern.compile("(?<=\\>)\\d+(?=\\<)");
                        Matcher mch = ptr.matcher(inputLine);
                        if(mch.find())
                            smsSent = true;
                    }
                    smsResponse = smsResponse.concat(inputLine).concat(" ");
                }
                String smsSentStatus = null;
                if(smsSent)
                    smsSentStatus="Success";
                else
                    smsSentStatus = "failed";
                MongoCollection smsColl = mongoDB.getCollection(serverNamePrefix+"_smsLog");
                Document inSMSLogs = new Document("phoneNumber",phoneNumber);
                inSMSLogs.append("message", message);
                inSMSLogs.append("status", inputLine);
                inSMSLogs.append("eventDate", new Date());
                inSMSLogs.append("smsType", smsType);                   
                inSMSLogs.append("smsSentStatus", smsSentStatus);
                inSMSLogs.append("smsResponse", smsResponse);
                inSMSLogs.append("smsURL", tempURL);
                smsColl.insertOne(inSMSLogs);
                //if(smsSentStatus!=null && smsSentStatus.equals("Success"))
                this.updateSMSSentTime(smsID,serverNamePrefix);
                if(isSLA){
                    MongoCollection monColl = mongoDB.getCollection("lst_"+collectionName);
                    Document whrQry = new Document("phoneNumber",phoneNumber);
                    whrQry.append("calledBack", false);
                    Document updateQry = new Document("$set",new Document("isEscalated",true));
                    monColl.updateOne(whrQry, updateQry);
                }
            }    
        }
        catch(Exception exe){
            exe.getMessage();
        }        
    }
}
