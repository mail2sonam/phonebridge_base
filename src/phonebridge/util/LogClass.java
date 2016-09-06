/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.util;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bharath
 */
public class LogClass {
    private static final Logger logger = Logger.getLogger(LogClass.class.getName());
    
    public static void logMsg(String className,String methodName,String message){
        logger.logp(Level.INFO, className, methodName, message);
        
    }
}
