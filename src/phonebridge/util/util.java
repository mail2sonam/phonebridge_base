package phonebridge.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;


public class util {
    public Date convertSQLStringToDate(String dateInString) throws Exception,NullPointerException,ParseException{
	Date dt = new Date();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        dt = formatter.parse(dateInString);
	return dt;
    }
    
    public String returnCollectionName(String serverNamePrefix,Date callDateTime_IST,String cdrType,String cdrDisposition){
        String collectionName = null;
        try{
            collectionName = this.getMMYYYYForMongoDate();
            collectionName = collectionName.concat("_").concat(serverNamePrefix);
            
            collectionName = collectionName.concat("_").concat(cdrType);
            if(cdrDisposition!=null)
                collectionName = collectionName.concat(cdrDisposition.replace(" ","").toLowerCase());
        }
        catch(Exception ex){
            LogClass.logMsg("util", "returnCollectionName", ex.getMessage());
        }
        return collectionName;
    }
    
    public String returnCollectionNameForCallReport(String serverNamePrefix,Date callDateTime_IST,String cdrTypeWithDisposition){
        String collectionName = null;
        try{
            collectionName = this.getMMYYYYForJavaDate(callDateTime_IST.getTime());
            collectionName = collectionName.concat("_").concat(serverNamePrefix);
            
            collectionName = collectionName.concat("_").concat(cdrTypeWithDisposition);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "returnCollectionNameForCallReport", ex.getMessage());
        }
        return collectionName;
    }
    
    public SimpleDateFormat getTimestamp(){
        SimpleDateFormat currentTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentTimeStamp.setTimeZone(TimeZone.getTimeZone("UTC"));
        return currentTimeStamp;
    }
    
    public ArrayList<String> returnCollectionNameForEventsProcessing(String serverNamePrefix,Date startDate,Date endDate){
        ArrayList<String> collectionNamesToRetrieveEvents = new ArrayList();
        try{
            collectionNamesToRetrieveEvents.add(
                    this.concatServerNamePrefixForCollection(serverNamePrefix).concat
                        (this.getMMYYYYForJavaDate(startDate.getTime())));
            if(endDate.getMonth()>startDate.getMonth()){
                collectionNamesToRetrieveEvents.add(
                    this.concatServerNamePrefixForCollection(serverNamePrefix).concat
                        (this.getMMYYYYForJavaDate(endDate.getTime())));
            }
        }
        catch(Exception ex){
            LogClass.logMsg("util", "returnCollectionNameForEventsProcessing", ex.getMessage());
        }
        return collectionNamesToRetrieveEvents;
    }
    
    public String generateCollectionNameForEventsLog(String serverNamePrefix){
        String eventCollectionName = null;
        try{
            
            eventCollectionName = this.concatServerNamePrefixForCollection(serverNamePrefix);
            //eventCollectionName = eventCollectionName.concat(this.getMMYYYYForMongoDate());
        }
        catch(Exception ex){
            LogClass.logMsg("util", "generateCollectionNameForEventsLog", ex.getMessage());
        }
        return eventCollectionName;
    }
    
    public String getMMYYYYForMongoDate(){
        String mmYYYY = "xxyyyy";
        try{
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH)+1;
            int year = calendar.get(Calendar.YEAR);
            String monthStr = Integer.toString(month);
            if(monthStr.length()==1)
                monthStr = "0"+monthStr;
            mmYYYY = monthStr.concat(Integer.toString(year));
        }
        catch(Exception ex){
            LogClass.logMsg("util", "getMMYYYYForMongoDate", ex.getMessage());
        }
        return mmYYYY;
    }
    
    public String getMMYYYYForJavaDate(Long dateProvided){
        String mmYYYY = "xxyyyy";
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateProvided);
            int month = calendar.get(Calendar.MONTH)+1;
            int year = calendar.get(Calendar.YEAR);
            String monthStr = Integer.toString(month);
            if(monthStr.length()==1)
                monthStr = "0"+monthStr;
            mmYYYY = monthStr.concat(Integer.toString(year));
        }
        catch(Exception ex){
            LogClass.logMsg("util", "getMMYYYYForJavaDate", ex.getMessage());
        }
        return mmYYYY;
    }
    
    public String getDDMMForJavaDate(Long dateProvided){
        String ddMM = "ddmm";
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateProvided);
            int date = calendar.get(Calendar.DATE);
            int month = calendar.get(Calendar.MONTH)+1;
            String monthStr = Integer.toString(month);
            String dateStr = Integer.toString(date);
            if(monthStr.length()==1)
                monthStr = "0"+monthStr;
            if(dateStr.length()==1)
                dateStr = "0"+dateStr;
            ddMM = dateStr.concat(monthStr);//monthStr.concat(Integer.toString(year));
        }
        catch(Exception ex){
            LogClass.logMsg("util", "getMMYYYYForJavaDate", ex.getMessage());
        }
        return ddMM;
    }
    
    public int returnIntegerValueFromObject(Object objectPassed){
        int intData = 0;
        try{
            switch(objectPassed.getClass().getName()){
                case "java.lang.Integer":
                    intData = (Integer)objectPassed;
                    break;
                case "java.lang.String":
                    intData = Integer.valueOf(objectPassed.toString());
                    break;
                case "java.lang.Double":
                    intData = ((Double)objectPassed).intValue();
                    break;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("util", "returnIntegerValueFromObject", ex.getMessage());
        }
        return intData;
    }
    
    public double returnDoubleValueFromObject(Object objectPassed){
        double doubleData = 0.00;
        try{
            switch(objectPassed.getClass().getName()){
                case "java.lang.Integer":
                    int ans=(int)objectPassed;
                    doubleData = (double)ans;
                    break;
                case "java.lang.String":
                    doubleData = Double.parseDouble(objectPassed.toString());
                    break;
                case "java.lang.Float":
                    doubleData = (double) objectPassed;
                    break;
                case "java.lang.Double":
                    doubleData = (double) objectPassed;
                    break;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("util", "returnDoubleValueFromObject", ex.getMessage());
        }
        return doubleData;
    }
    
    private String concatServerNamePrefixForCollection(String serverNamePrefix){
        String eventsCollectionName = null;
        try{
            eventsCollectionName = "events_".concat(serverNamePrefix);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "concatServerNamePrefixForCollection", ex.getMessage());
        }
        return eventsCollectionName;
    }
    
    public org.joda.time.DateTime convertJavaDateToMongoDate(Long javaDatePassed){
        org.joda.time.DateTime mongoDateTime = null;
        try{
            mongoDateTime = new DateTime(javaDatePassed,DateTimeZone.UTC);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "convertJavaDateToMongoDate", ex.getMessage());
        }
        return mongoDateTime;
    }
    
    public ArrayList<String> getCollNamesForRetrievingDataFromLog(String serverNamePrefix,Date startDate,Date endDate){
        ArrayList<String> collectionNames = new ArrayList<>();
        try{
            collectionNames = new util().returnCollectionNameForEventsProcessing(serverNamePrefix,startDate,endDate);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "getCollectionNamesForEventsLog", ex.getMessage());
        }
        return collectionNames;
    }
    
    public Date genDateInYYYYMMDDHHMMSSFormatWithHourDelay(){
        Date dt = null;
        try{
            String generatedDateString = null;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("IST"));
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.HOUR_OF_DAY, 1);
            generatedDateString = formatter.format(cal.getTime());
            dt = formatter.parse(generatedDateString);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "genDateInYYYYMMDDHHMMSSFormatWithHourDelay", ex.getMessage());
        }
        return dt;
    }
    
    public Date generateNewDateInYYYYMMDDFormat(){
        Date dt = null;
        try{
            String generatedDateString = null;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S");
            formatter.setTimeZone(TimeZone.getTimeZone("IST"));
            generatedDateString = formatter.format(new Date());
            dt = formatter.parse(generatedDateString);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "generateNewDateInYYYYMMDDFormat", ex.getMessage());
        }
        return dt;
    }
    
    public Date generateNewDateInYYYYMMDDFormat(String timeZoneToUse){
        Date dt=null;
        try{
            TimeZone destTimeZone = TimeZone.getTimeZone(timeZoneToUse);
            String generatedDateString = null;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S");
            formatter.setTimeZone(destTimeZone);
            generatedDateString = formatter.format(new Date());
            dt=formatter.parse(generatedDateString);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "generateNewDateInYYYYMMDDFormat", ex.getMessage());
        }
        return dt;
    }
    
    public String generateNewDateInYYYYMMDDFormat(Date dateToConvert,String timeZoneToUse){
        String generatedDateString = null;
        try{
            TimeZone destTimeZone = TimeZone.getTimeZone(timeZoneToUse);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            formatter.setTimeZone(destTimeZone);
            generatedDateString = formatter.format(dateToConvert);
            //dt = formatter.parse(generatedDateString);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "generateNewDateInYYYYMMDDFormat", ex.getMessage());
        }
        return generatedDateString;
    }
    
    public Date generateNewDate(String timeZone){
        Date dt=null;
        try{
            TimeZone destTimeZone = TimeZone.getTimeZone(timeZone);
            String generatedDateString = null;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setTimeZone(destTimeZone);
            generatedDateString = formatter.format(new Date());
            dt = formatter.parse(generatedDateString);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "generateNewDate", ex.getMessage());
        }
        return dt;
    }
    
    public String generateDateInHHMMSSFormat(Date dateToConvert,String timeZoneToUse){
        String generatedDateString = null;
        try{
            TimeZone destTimeZone = TimeZone.getTimeZone(timeZoneToUse);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(destTimeZone);
            generatedDateString = formatter.format(dateToConvert);
            //dt = formatter.parse(generatedDateString);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "generateNewDateInYYYYMMDDFormat", ex.getMessage());
        }
        return generatedDateString;
    }
    
    public String generateDateInDDMMYYYYFormat(Date dateToConvert,String timeZoneToUse){
        String generatedDateString = null;
        try{
            TimeZone destTimeZone = TimeZone.getTimeZone(timeZoneToUse);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            formatter.setTimeZone(destTimeZone);
            generatedDateString = formatter.format(dateToConvert);
            //dt = formatter.parse(generatedDateString);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "generateDateInDDMMYYYYFormat", ex.getMessage());
        }
        return generatedDateString;
    }
    
    public String generateDateInDDMMYYYYHHMMSSFormat(Date dateToConvert,String timeZoneToUse){
        String generatedDateString = null;
        try{
            TimeZone destTimeZone = TimeZone.getTimeZone(timeZoneToUse);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            formatter.setTimeZone(destTimeZone);
            generatedDateString = formatter.format(dateToConvert);
            //dt = formatter.parse(generatedDateString);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "generateDateInDDMMYYYYFormat", ex.getMessage());
        }
        return generatedDateString;
    }
    
    public ArrayList<String> getMMYYYYForTwoDate(Date fromDate,Date toDate){
        ArrayList<String> mmYYYYArr = new ArrayList<>();
        try{
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(fromDate.getYear(),fromDate.getMonth(),1));
            while(cal.getTime().getTime()<toDate.getTime()){
                mmYYYYArr.add(this.getMMYYYYForJavaDate(cal.getTime().getTime()));
                cal.add(Calendar.MONTH, 1);
            }
        }
        catch(Exception ex){
            LogClass.logMsg("util", "getMMYYYYForTwoDate", ex.getMessage());
        }
        return mmYYYYArr;
    }
    
    public String stripAdditionalInfoFromNumber(String phoneNumber){
        String number = phoneNumber;
        try{
            if("+".equals(number.substring(0, 1))){
                number = number.substring(1, number.length());
            }
            if("0".equals(number.substring(0, 1))){
                number = number.substring(1, number.length());
            }
            if(number.length()==12 && "91".equals(number.substring(0, 2))){
                number = number.substring(2,number.length());
            }
            if(!(number.startsWith("7") || number.startsWith("8") || number.startsWith("9"))){
                number = "0".concat(number);
            }
        }
        catch(Exception ex){
            LogClass.logMsg("util", "stripAdditionalInfoFromNumber", ex.getMessage());
        }
        return number;
    }
    
    public String concatServerNameAndExtension(String serverNamePrefix,String extension){
        String concatVal = null;
        try{
            concatVal = serverNamePrefix.concat("_").concat(extension);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "concatServerNameAndExtension", ex.getMessage());
        }
        return concatVal;
    }
    
    public String returnCollectionNameForPopup(String serverNamePrefix,String extension){
        String popupCollectionName = null;
        try{
            popupCollectionName = this.concatServerNameAndExtension(serverNamePrefix, extension).concat("_Popup");
        }
        catch(Exception ex){
            LogClass.logMsg("util", "returnCollectionNameForPopup", ex.getMessage());
        }
        return popupCollectionName;
    }
    
    public String returnCollNameForPerformanceRpt(String serverNamePrefix,String extension){
        String collectionName = null;
        try{
            collectionName = this.concatServerNameAndExtension(serverNamePrefix, extension).concat("_Performance");
        }
        catch(Exception ex){
            LogClass.logMsg("util", "returnCollNameForPerformanceRpt", ex.getMessage());
        }
        return collectionName;
    }
    
    public String returnCollectionNameForCurrentCalls(String mmYYYY,String serverNamePrefix){
        String currentCallsCollection = null;
        try{
            currentCallsCollection = mmYYYY.concat("_").concat(serverNamePrefix).concat("_currentCalls");
        }
        catch(Exception ex){
            LogClass.logMsg("util", "returnCollectionNameForCurrentCalls", ex.getMessage());
        }
        return currentCallsCollection;
    }
    
    public String returnCollNameToSearchContactInfo(String listID){
        String collName = null;
        try
        {
        int a=Integer.parseInt(listID);
        listID="lst_"+listID;
        }
        catch(NumberFormatException nu)
        {}
        return listID;
    }
    
    public double returnDiffBtwDateInSecsOrMinsOrHrs(Date firstDate,Date secondDate,String returnMinsOrSecs){
        double minsOrSecs = 0;
        try{
            double diffBetDates = firstDate.getTime() - secondDate.getTime();
            switch(returnMinsOrSecs){
                case "mins":
                    minsOrSecs = diffBetDates / (60 * 1000);
                    break;
                case "secs":
                    minsOrSecs = diffBetDates / 1000;
                    break;
                case "hour":
                    minsOrSecs = diffBetDates / (60 * 60 * 1000);
                    break;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("util", "returnDiffBtwDateInSecsOrMinsOrHrs", ex.getMessage());
        }
        return minsOrSecs;
    }
    
    public Document addDateConditionToWhereQry(String operator){
        Document whereQry = new Document();
        try{
            
            DateTime jodaDateTime = this.convertJavaDateToMongoDate(this.generateNewDate("IST").getTime());
            whereQry.append(operator, jodaDateTime.toDate());
        }
        catch(Exception ex){
            LogClass.logMsg("util", "addDateConditionToWhereQry", ex.getMessage());
        }
        return whereQry;
    }
    
    public String returnStringValueFromObject(Object objectPassed){
        String dependantData = null;
        try{
            switch(objectPassed.getClass().getName()){
                case "java.lang.Integer":
                    dependantData = String.valueOf(objectPassed);
                    break;
                case "java.lang.String":
                    dependantData = objectPassed.toString();
                    break;
                case "java.lang.Double":
                    dependantData = String.valueOf(objectPassed);
                    break;
                case "java.uitl.Date":
                    dependantData = this.generateDateInDDMMYYYYHHMMSSFormat((Date) objectPassed, "IST");
                    break;
                case "java.lang.Boolean":
                    boolean value = (boolean) objectPassed;
                    dependantData = String.valueOf(value);
                    break;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("util", "returnIntegerValueFromObject", ex.getMessage());
        }
        return dependantData;
    }
    
    public Date genDateInYYYYMMDDFormat(){
        Date dt = null;
        try{
            String generatedDateString = null;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setTimeZone(TimeZone.getTimeZone("IST"));
            generatedDateString = formatter.format(new Date());
            dt = formatter.parse(generatedDateString);
        }
        catch(Exception ex){
            LogClass.logMsg("util", "generateNewDateInYYYYMMDDFormat", ex.getMessage());
        }
        return dt;
    }
    
    public String chageDependantToString(Object dependant){
        String dependantField = "";
        Document objDoc = null;
        try{
            objDoc = (Document) dependant;
            for(Document.Entry<String,Object> e:objDoc.entrySet()){
                if(dependantField.length()>0)
                    dependantField = dependantField.concat(",");
                dependantField = dependantField.concat(e.getKey().toString());
                dependantField = dependantField.concat(":");
                try{
                    dependantField = dependantField.concat(e.getValue().toString());
                }
                catch(Exception ex){
                    System.out.println("ERROR IN chageDependantToString "+ex.getMessage());
                }
            }
        }
        catch(Exception ex){
            System.out.println("ERROR IN chageDependantToString "+ex.getMessage());
        }
        return dependantField;
    }
}
