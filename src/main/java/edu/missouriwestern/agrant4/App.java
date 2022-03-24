package edu.missouriwestern.agrant4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.noynaert.sqlCredentials.SqlCredentials;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a program that handles queries and reports for a company. The program can either print a
 * report from a json file or query a csv file for information about a client given a client number
 * depending on command-line arguments. The output of a report is a formatted markdown file, and the
 * output for a query is a .json file for a given client that can be printed with the report
 * function.
 *
 * @since March 2022
 * @author Aaron Grant
 */
public class App {
  // Create logger instance
  public static final Logger LOG = LogManager.getLogger(App.class);

  /**
   * Main method
   *
   * <p>args[0] contains either the word "query" or "report" * args[1] contains the name of the
   * report file to be written if args[0] is a report * or read from if args[0] is a query. This
   * file should be a json file. For example, "jim.json."
   *
   * <p>If this is a query, it must have more command-line arguments: * args[2] contains the name of
   * the clients csv file (for example, "clients.csv"). * args[3] containing the client id number
   * (for example, 220). * args[4] contains the currency the client works with (for example, eur). *
   * If any argument is missing or does not match the database, an error will be thrown.
   */
  public static void main(String[] args) {
    LOG.info("Program started");
    LOG.trace("Inside main method");

    try {
      /*
       * start by storing command line arguments in readable variables.
       *
       */
      String action = args[0];
      LOG.info("Step 1: Type of action: " + action);
      String reportFileName = args[1];
      LOG.info("Step 1: Name of report file: " + reportFileName);

      if (action.equals("query")) {
        /*
         * this is a query.
         */
        String clientFile = args[2];
        int clientNumber = Integer.parseInt(args[3]);
        String currency = args[4];
        LOG.info("Step 1: Client file: " + clientFile);
        LOG.info("Step 1: Client number: " + clientNumber);
        LOG.info("Step 1: Currency type: " + currency);
        LOG.debug(
            "Step 2: Test Client data = "
                + (new Client(
                    0,
                    "test",
                    "test",
                    "test",
                    "test",
                    "test",
                    "test",
                    "test",
                    12345,
                    0.0,
                    "999-999-9999",
                    "999-999-9999",
                    "test@test.com",
                    "www.test.com")));

        // parse clients from csv file as objects and store them in a list
        List<Client> clients =
            new CsvToBeanBuilder(new FileReader(clientFile)).withType(Client.class).build().parse();
        LOG.info("Step 3: Records read: " + clients.size());
        LOG.info("Step 3: First record: " + clients.get(0).toString());

        // dump the entire client file to clients.json for backup
        dumpClientsToJson(clients, "clients.json");
        LOG.info("Step 4: clients.json file created");

        // pull out the specified client from the provided client number
        Client queriedClient = getSpecifiedClient(clients, clientNumber);
        LOG.info("Step 5: Requested client is: " + queriedClient.toString());

        // get information about geography for client's area; this takes a few steps...
        // start by formatting the zipcode to include any missing leading zeros
        String queriedClientZip = String.format("%05d", queriedClient.getPostalCode());
        LOG.info("Step 6: client zip is " + queriedClientZip);

        // then download location data for zip from a url and store it in a json object
        JsonObject locationDataJson =
            getJsonFromUrl("https://api.zippopotam.us/us/" + queriedClientZip);

        // finally, extract latitude and longitude from the created JsonObject
        JsonObject placeInfo = (JsonObject) locationDataJson.getAsJsonArray("places").get(0);
        String latitude = placeInfo.get("latitude").getAsString();
        String longitude = placeInfo.get("longitude").getAsString();
        LOG.info("Step 6: client latitude is " + latitude);
        LOG.info("Step 6: client longitude is " + longitude);

        // get information about population for the client's area; this also takes a few steps...
        // first get sql credentials from xml file
        SqlCredentials credentials = new SqlCredentials("woz.xml");
        String pass = credentials.getPassword();
        String user = credentials.getUser();
        String host = credentials.getHost();

        // then do the prepared query of database to get median age and population of a city
        HashMap<String, String> cityData =
            getMedianAgeAndPopulation(
                pass, user, host, queriedClient.getCity(), queriedClient.getState());
        String medianAge = cityData.get("medianAge");
        String population = cityData.get("population");
        LOG.info("Step 7: Population is: " + population);
        LOG.info("Step 7: median age is: " + medianAge);

        // finally, get information about currency that the client works with
        // start by downloading current exchange rates from website as json
        JsonObject currencyDataJson = getJsonFromUrl("http://www.floatrates.com/daily/usd.json");

        // then extract data from the created json
        String currencyName =
            currencyDataJson.getAsJsonObject(currency).getAsJsonPrimitive("name").getAsString();
        String currencyRate =
            currencyDataJson.getAsJsonObject(currency).getAsJsonPrimitive("rate").getAsString();
        String currencyDate =
            currencyDataJson.getAsJsonObject(currency).getAsJsonPrimitive("date").getAsString();

        // finally, parse rate date as LocalDateTime object to determine age of rate
        LocalDateTime date =
            LocalDateTime.parse(currencyDate, DateTimeFormatter.RFC_1123_DATE_TIME);
        String ageOfRate =
            String.valueOf(Duration.between(date, LocalDateTime.now(ZoneId.of("GMT"))).toHours());
        String ageOfRateMessage = "The exchange rate is " + ageOfRate + " hour(s) old.";
        LOG.info("Step 8: currency name is: " + currencyName);
        LOG.info("Step 8: exchange rate is: " + currencyRate);
        LOG.info("Step 8: " + ageOfRateMessage);

        /*
         * With all the information, we can now send results of the
         * query to a file name given in args[1] by first creating a final report
         * object then using that object to create a Json file
         */
        Report finalReport =
            new Report(
                reportFileName,
                queriedClient,
                latitude,
                longitude,
                medianAge,
                population,
                currencyName,
                currencyRate,
                ageOfRateMessage);
        objectToJson(finalReport, reportFileName);
        LOG.info("Step 9: json file \"" + reportFileName + "\" created.");

      } else {

        // this is a report
        Report report = jsonFileToReport(reportFileName);
        LOG.info("Report read. Report includes: " + report.toString());
        // create the new file name from the report file name arg from command line
        String newFileName = reportFileName.split("\\.")[0] + ".md";
        // print report
        report.printReport(newFileName);
        LOG.info("Report printed to file: " + newFileName);
      }
    } catch (Exception e) {
      LOG.error("Error within main: " + e.getMessage());
      System.err.println("Error within main: " + e.getMessage());
      System.exit(1);
    }

    LOG.info("Exiting Program");
  }

  /**
   * Extracts the median age and population of a given city/state combo from a database. Returns a
   * hashmap of: "medianAge" --> Given median age "population" --> Given population
   *
   * @param pass -- the password for the database
   * @param user -- the username for the database
   * @param host -- the database host name
   * @param city -- the city we want to get information about
   * @param state -- the state we want to get information about
   */
  private static HashMap<String, String> getMedianAgeAndPopulation(
      String pass, String user, String host, String city, String state) {
    LOG.trace("Inside getMedianAgeAndPopulation method");
    String connectionString = String.format("jdbc:mysql://%s:3306/misc", host);
    HashMap<String, String> cityData = new HashMap<>();
    try {
      // create prepared query
      String query =
          "SELECT * FROM usCityDemographics WHERE city LIKE ? AND state LIKE ? ORDER BY city";

      // connect to the database
      Connection con = DriverManager.getConnection(connectionString, user, pass);
      PreparedStatement stmt = con.prepareStatement(query);
      LOG.info("Connection established to database");

      // set the "?" in the prepared query to our city and state variables. The two
      stmt.setString(1, city);
      stmt.setString(2, abbreviatedStateToString(state));

      // execute the query
      ResultSet results = stmt.executeQuery();

      // Code to figure out if the query returned a result
      boolean foundRecord = results.next();

      if (foundRecord) {
        LOG.info("At least one record was found for client's city");
        /*
         * If a record was found, put that information into the hashmap.
         * NOTE: city/state act as a composite key. Even so, multiple records
         * may be returned because this is a badly normalized database. We can ignore all
         * but the top row since population and median age do not vary when multiple
         * rows are returned.
         */
        cityData.put("medianAge", results.getString("medianage"));
        cityData.put("population", results.getString("total_population"));
      } else {
        LOG.info("No records found for client's city");
        // if no data found, put a default string into the hashmap
        cityData.put("medianAge", "No city data found");
        cityData.put("population", "No city data found");
      }
      con.close();
      LOG.info("Connection to database closed.");
    } catch (SQLException e) {
      LOG.error("Error in getMedianAgeAndPopulation method: " + e.getMessage());
      System.err.println("Error in getMedianAgeAndPopulation method: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }

    return cityData;
  }

  /**
   * Converts a state abbreviation to the full-length version of the state.
   *
   * @param stateAbbreviation -- the state abbreviation we want to convert
   */
  private static String abbreviatedStateToString(String stateAbbreviation) {
    LOG.trace("Inside abbreviatedStateToString method");

    HashMap<String, String> states = new HashMap<>();
    states.put("AL", "Alabama");
    states.put("AK", "Alaska");
    states.put("AB", "Alberta");
    states.put("AS", "American Samoa");
    states.put("AZ", "Arizona");
    states.put("AR", "Arkansas");
    states.put("AE", "Armed Forces (AE)");
    states.put("AA", "Armed Forces Americas");
    states.put("AP", "Armed Forces Pacific");
    states.put("BC", "British Columbia");
    states.put("CA", "California");
    states.put("CO", "Colorado");
    states.put("CT", "Connecticut");
    states.put("DE", "Delaware");
    states.put("DC", "District Of Columbia");
    states.put("FL", "Florida");
    states.put("GA", "Georgia");
    states.put("GU", "Guam");
    states.put("HI", "Hawaii");
    states.put("ID", "Idaho");
    states.put("IL", "Illinois");
    states.put("IN", "Indiana");
    states.put("IA", "Iowa");
    states.put("KS", "Kansas");
    states.put("KY", "Kentucky");
    states.put("LA", "Louisiana");
    states.put("ME", "Maine");
    states.put("MB", "Manitoba");
    states.put("MD", "Maryland");
    states.put("MA", "Massachusetts");
    states.put("MI", "Michigan");
    states.put("MN", "Minnesota");
    states.put("MS", "Mississippi");
    states.put("MO", "Missouri");
    states.put("MT", "Montana");
    states.put("NE", "Nebraska");
    states.put("NV", "Nevada");
    states.put("NB", "New Brunswick");
    states.put("NH", "New Hampshire");
    states.put("NJ", "New Jersey");
    states.put("NM", "New Mexico");
    states.put("NY", "New York");
    states.put("NF", "Newfoundland");
    states.put("NC", "North Carolina");
    states.put("ND", "North Dakota");
    states.put("NT", "Northwest Territories");
    states.put("NS", "Nova Scotia");
    states.put("NU", "Nunavut");
    states.put("OH", "Ohio");
    states.put("OK", "Oklahoma");
    states.put("ON", "Ontario");
    states.put("OR", "Oregon");
    states.put("PA", "Pennsylvania");
    states.put("PE", "Prince Edward Island");
    states.put("PR", "Puerto Rico");
    states.put("QC", "Quebec");
    states.put("RI", "Rhode Island");
    states.put("SK", "Saskatchewan");
    states.put("SC", "South Carolina");
    states.put("SD", "South Dakota");
    states.put("TN", "Tennessee");
    states.put("TX", "Texas");
    states.put("UT", "Utah");
    states.put("VT", "Vermont");
    states.put("VI", "Virgin Islands");
    states.put("VA", "Virginia");
    states.put("WA", "Washington");
    states.put("WV", "West Virginia");
    states.put("WI", "Wisconsin");
    states.put("WY", "Wyoming");
    states.put("YT", "Yukon Territory");

    return states.get(stateAbbreviation);
  }

  /**
   * Reads a json string from a url and converts it to a Gson JsonObject.
   *
   * @param url -- the url with the json string
   */
  private static JsonObject getJsonFromUrl(String url) {
    LOG.trace("Inside getJsonFromUrl method");
    JsonObject json = null;
    try {
      InputStream inputStream = new URL(url).openStream();
      BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
      // the json data is one big string, so we can just read the first line to get the entire thing
      String jsonText = input.readLine();
      // Note: JsonParser is depreciated, so must use static method to parse
      json = JsonParser.parseString(jsonText).getAsJsonObject();
      inputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error("IOException inside readJsonFromUrl: " + e.getMessage());
      System.err.println("IOException inside readJsonFromUrl: " + e.getMessage());
      System.exit(1);
    }
    return json;
  }

  /**
   * Finds a specified client within a list of clients given a particular client number. Throws an
   * NoSuchElement exception if no client is found
   *
   * @param clients -- a list of client objects
   * @param queriedClientNumber -- the client number for the client we want to return
   */
  private static Client getSpecifiedClient(List<Client> clients, int queriedClientNumber) {
    LOG.trace("Inside getSpecifiedClient method");
    Client queriedClient = null;
    try {
      for (Client client : clients) {
        if (client.getClientNumber() == queriedClientNumber) {
          queriedClient = client;
        }
      }
      // if there is no client, throw a NoSuchElementException
      if (queriedClient == null) {
        throw new NoSuchElementException();
      }

    } catch (NoSuchElementException e) {
      e.printStackTrace();
      System.err.println("No client found with specified client number: " + e.getMessage());
      LOG.error("No client found with specified client number: " + e.getMessage());
      System.exit(1);
    }

    return queriedClient;
  }

  /**
   * Converts a json file to a report object.
   *
   * @param fileName -- the file name of the json file we want to convert
   */
  private static Report jsonFileToReport(String fileName) {
    LOG.trace("Inside jsonFileToReport method");
    Report report = null;
    try {
      Gson gson = new Gson();
      report = gson.fromJson(new FileReader(fileName), Report.class);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.err.println("FileNotFoundException inside jsonFileToReport: " + e.getMessage());
      LOG.error("FileNotFoundException inside jsonFileToReport: " + e.getMessage());
      System.exit(1);
    }
    return report;
  }

  /**
   * Takes a client list and converts it to a json file.
   *
   * @param clientsList -- the list of client objects we want to convert
   * @param fileName -- the file name of the json file we want to create
   */
  private static void dumpClientsToJson(List<Client> clientsList, String fileName) {
    LOG.trace("Inside dumpClientsToJson method");
    try {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      FileWriter writer = new FileWriter(fileName);
      gson.toJson(clientsList, writer);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
      LOG.error("IOException inside dumpClientsToJson: " + e.getMessage());
      System.err.println("IOException inside dumpClientsToJson: " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Prints an object to a json file.
   *
   * @param fileName -- The name of the file to be written to
   * @param object -- the object we want to convert to json
   */
  private static void objectToJson(Object object, String fileName) {
    LOG.trace("Inside objectToJson method");
    try {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      FileWriter writer = new FileWriter(fileName);
      gson.toJson(object, writer);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
      LOG.error("IOException inside objectToJson: " + e.getMessage());
      System.err.println("IOException inside objectToJson: " + e.getMessage());
      System.exit(1);
    }
  }
}
