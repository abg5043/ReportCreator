package edu.missouriwestern.agrant4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.noynaert.sqlCredentials.SqlCredentials;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * INSERT DESCRIPTION
 *
 * @since March 2022
 * @author Aaron Grant
 */
public class App {
  // Create logger instance
  public static final Logger LOG = LogManager.getLogger(App.class);

  public static void main(String[] args) {
    LOG.info("Program started");
    LOG.trace("Inside main method");

    try {
      String action = args[0];
      LOG.info("Step 1: Type of action: " + action);
      String reportFileName = args[1];
      LOG.info("Step 1: Name of report file: " + reportFileName);

      if (action.equals("query")) {
        // this is a query

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

        // Store clients in a list
        List<Client> clients =
            new CsvToBeanBuilder(new FileReader(clientFile)).withType(Client.class).build().parse();
        LOG.info("Step 3: Records read: " + clients.size());
        LOG.info("Step 3: First record: " + clients.get(0).toString());

        // Dump the entire client file to clients.json
        dumpListToJson(clients, "clients.json");
        LOG.info("Step 4: clients.json file created");

        // pull out the specified client number
        Client queriedClient = getSpecifiedClient(clients, clientNumber);
        LOG.info("Step 5: Requested client is: " + queriedClient.toString());

        // get information about latitude and longitude for client
        String queriedClientZip = String.format("%05d", queriedClient.getPostalCode());
        LOG.info("Step 6: client zip is " + queriedClientZip);

        // create a json object from url
        JsonObject locationDataJson =
            getJsonFromUrl("https://api.zippopotam.us/us/" + queriedClientZip);

        // get latitude and longitude from JsonObject
        JsonObject placeInfo = (JsonObject) locationDataJson.getAsJsonArray("places").get(0);
        String latitude = placeInfo.get("latitude").getAsString();
        String longitude = placeInfo.get("longitude").getAsString();
        LOG.info("Step 6: client latitude is " + latitude);
        LOG.info("Step 6: client longitude is " + longitude);

        // get sql credentials
        SqlCredentials credentials = new SqlCredentials("woz.xml");
        String pass = credentials.getPassword();
        String user = credentials.getUser();
        String host = credentials.getHost();

        // Do the prepared query to get median age and population of a city
        HashMap<String, String> cityData =
            getMedianAgeAndPopulation(
                pass, user, host, queriedClient.getCity(), queriedClient.getState());
        String medianAge = cityData.get("medianAge");
        String population = cityData.get("population");
        LOG.info("Step 7: Population is: " + population);
        LOG.info("Step 7: median age is: " + medianAge);

        // Read current exchange rates from website
        JsonObject currencyDataJson = getJsonFromUrl("http://www.floatrates.com/daily/usd.json");
        String currencyName =
            currencyDataJson.getAsJsonObject(currency).getAsJsonPrimitive("name").getAsString();
        String currencyRate =
            currencyDataJson.getAsJsonObject(currency).getAsJsonPrimitive("rate").getAsString();
        String currencyDate =
            currencyDataJson.getAsJsonObject(currency).getAsJsonPrimitive("date").getAsString();

        // Parse rate date as LocalDateTime object to determine age of rate
        LocalDateTime date =
            LocalDateTime.parse(currencyDate, DateTimeFormatter.RFC_1123_DATE_TIME);
        String ageOfRate =
            String.valueOf(Duration.between(date, LocalDateTime.now(ZoneId.of("GMT"))).toHours());
        String ageOfRateMessage = "The exchange rate is " + ageOfRate + " hour(s) old.";
        LOG.info("Step 8: currency name is: " + currencyName);
        LOG.info("Step 8: exchange rate is: " + currencyRate);
        LOG.info("Step 8: " + ageOfRateMessage);

        /*
         * send results of query to file from args[1] by first creating a final report
         * object then using that to build a Json object
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
        dumpObjectToJson(finalReport, reportFileName);
        LOG.info("Step 9: json file \"" + reportFileName + "\" created.");

      } else {
        // this is a report
        Report report = jsonToObject(reportFileName);
        LOG.info("Report read. Report includes: " + report.toString());
        String newFileName = reportFileName.split("\\.")[0] + ".md";
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

  private static HashMap<String, String> getMedianAgeAndPopulation(
      String pass, String user, String host, String city, String state) {
    LOG.trace("Inside getMedianAgeAndPopulation method");
    String connectionString = String.format("jdbc:mysql://%s:3306/misc", host);
    HashMap<String, String> cityData = new HashMap<>();
    try {
      String query =
          "SELECT * FROM usCityDemographics WHERE city LIKE ? AND state LIKE ? ORDER BY city";

      Connection con = DriverManager.getConnection(connectionString, user, pass);
      PreparedStatement stmt = con.prepareStatement(query);
      LOG.info("Connection established to database");

      // set the "?" in the prepared query to our city variable
      stmt.setString(1, city);
      stmt.setString(2, abbreviatedStateToString(state));

      // execute the query
      ResultSet results = stmt.executeQuery();

      // Code to figure out if the query returned a result
      boolean foundRecord = results.next();
      if (foundRecord) {
        LOG.info("At least one record was found for client's city");
        // this is a badly normalized database, so we only need the top row if one was found
        String medianAge = results.getString("medianage");
        String population = results.getString("total_population");
        cityData.put("medianAge", medianAge);
        cityData.put("population", population);
      } else {
        LOG.info("No records found for client's city");
        // if no data found, log a default string
        cityData.put("medianAge", "No city data found");
        cityData.put("population", "No city data found");
      }

      con.close();
      LOG.info("Connection to database closed.");
    } catch (SQLException e) {
      LOG.error("Error in getMedianAgeAndPopulation method: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }

    return cityData;
  }

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

  private static JsonObject getJsonFromUrl(String url) throws IOException {
    LOG.trace("Inside getJsonFromUrl method");
    InputStream inputStream = new URL(url).openStream();
    JsonObject json = null;
    try {
      BufferedReader input =
          new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
      String jsonText = input.readLine();
      json = JsonParser.parseString(jsonText).getAsJsonObject();
      inputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error("IOException inside readJsonFromUrl: " + e.getMessage());
      System.exit(1);
    }
    return json;
  }

  private static Client getSpecifiedClient(List<Client> clients, int queriedClientNumber) {
    LOG.trace("Inside getSpecifiedClient method");
    Client queriedClient = null;
    for (Client client : clients) {
      if (client.getClientNumber() == queriedClientNumber) {
        queriedClient = client;
      }
    }
    return queriedClient;
  }

  private static Report jsonToObject(String fileName) {
    LOG.trace("Inside jsonToObject method");
    Report report = null;

    try {
      Gson gson = new Gson();
      report = gson.fromJson(new FileReader(fileName), Report.class);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      LOG.error("FileNotFoundException inside jsonToObject: " + e.getMessage());
      System.exit(1);
    }
    return report;
  }

  private static void dumpListToJson(List<Client> clientsList, String fileName) {
    LOG.trace("Inside dumpClients method");
    try {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      FileWriter writer = new FileWriter(fileName);
      gson.toJson(clientsList, writer);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
      LOG.error("IOException inside dumpClients: " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Prints an object to a json file.
   *
   * @param fileName -- The name of the file to be written to
   * @param object -- ????
   */
  private static void dumpObjectToJson(Object object, String fileName) {
    LOG.trace("Inside dumpObjectToJson method");
    try {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      FileWriter writer = new FileWriter(fileName);
      gson.toJson(object, writer);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
      LOG.error("IOException inside dumpObjectToJson: " + e.getMessage());
      System.exit(1);
    }
  }
}
