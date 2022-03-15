package edu.missouriwestern.agrant4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * INSERT DESCRIPTION
 *
 * @since March 2022
 * @author Aaron Grant
 */
public class App 
{
    //Create logger instance
    public final static Logger LOG = LogManager.getLogger(App.class);

    public static void main( String[] args ) {
        LOG.info("Program started");
        LOG.trace("Inside main function");

        try {
            String action = args[0];
            String reportName = args[1];
            LOG.info("Step 1: Type of action: " + action);
            LOG.info("Step 1: Name of report: " + reportName);


            if(action.equals("query")) {
                //this is a query
                String clientFile = args[2];
                String clientNumber = args[3];
                String currency = args[4];
                LOG.info("Step 1: Client file: " + clientFile);
                LOG.info("Step 1: Client number: " + clientNumber);
                LOG.info("Step 1: Currency type: " + currency);

            } else {
                //this is a report
                //TODO: Finish this
            }
        } catch (Exception e) {
            LOG.error("Error reading from command line: " + e.getMessage());
            System.err.println("Error reading url from command line: " + e.getMessage());
            System.exit(1);
        }


        LOG.info("Map has been printed.");
        LOG.info("Exiting Program");
    }
}
