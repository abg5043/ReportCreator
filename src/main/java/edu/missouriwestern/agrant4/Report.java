package edu.missouriwestern.agrant4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Report {

  private String fileName;
  private Client client;
  private String latitude;
  private String longitude;
  private String medianAge;
  private String population;
  private String currencyName;
  private String exchangeRate;
  private String ageOfExchangeRate;

  public Report() {
    this(
        "No info found",
        new Client(),
        "No info found",
        "No info found",
        "No info found",
        "No info found",
        "No info found",
        "No info found",
        "No info found");
  }

  public Report(
      String fileName,
      Client client,
      String latitude,
      String longitude,
      String medianAge,
      String population,
      String currencyName,
      String exchangeRate,
      String ageOfExchangeRate) {
    setFileName(fileName);
    setClient(client);
    setLatitude(latitude);
    setLongitude(longitude);
    setMedianAge(medianAge);
    setPopulation(population);
    setCurrencyName(currencyName);
    setExchangeRate(exchangeRate);
    setAgeOfExchangeRate(ageOfExchangeRate);
  }

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public String getMedianAge() {
    return medianAge;
  }

  public void setMedianAge(String medianAge) {
    this.medianAge = medianAge;
  }

  public String getPopulation() {
    return population;
  }

  public void setPopulation(String population) {
    this.population = population;
  }

  public String getCurrencyName() {
    return currencyName;
  }

  public void setCurrencyName(String currencyName) {
    this.currencyName = currencyName;
  }

  public String getExchangeRate() {
    return exchangeRate;
  }

  public void setExchangeRate(String exchangeRate) {
    this.exchangeRate = exchangeRate;
  }

  public String getAgeOfExchangeRate() {
    return ageOfExchangeRate;
  }

  public void setAgeOfExchangeRate(String ageOfExchangeRate) {
    this.ageOfExchangeRate = ageOfExchangeRate;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public String toString() {
    return "Report{"
        + "fileName='"
        + fileName
        + '\''
        + ", client="
        + client
        + ", latitude='"
        + latitude
        + '\''
        + ", longitude='"
        + longitude
        + '\''
        + ", medianAge='"
        + medianAge
        + '\''
        + ", population='"
        + population
        + '\''
        + ", currencyName='"
        + currencyName
        + '\''
        + ", exchangeRate='"
        + exchangeRate
        + '\''
        + ", ageOfExchangeRate='"
        + ageOfExchangeRate
        + '\''
        + '}';
  }

  public void printReport(String fileName) {
    try {
      PrintWriter output = new PrintWriter(new File(fileName));
      output.println("# Client report for " + client.getFirstName() + " " + client.getLastName());

      output.println("## Client ID " + client.getClientNumber());

      output.println("```");
      output.println(client.getAddress());
      output.println(
          client.getCity()
              + ", "
              + client.getState()
              + " "
              + String.format("%05d", client.getPostalCode()));
      output.println("```");

      output.println("### Client Information");

      output.println("* Company: " + client.getCompany());
      output.println("* Risk Factor: " + client.getRiskFactor());
      output.println("* Phone 1: " + client.getPhone1());
      output.println("* Phone 2: " + client.getPhone2());
      output.println("* Email: " + client.getEmail());
      output.println("* Website: " + client.getWebsite());

      output.println("### Geography Information");
      output.println("* Latitude: " + latitude);
      output.println("* Longitude: " + longitude);

      output.println("### Population Information");
      output.println("* Median Age: " + medianAge);
      output.println("* Total Population: " + population);

      output.println("### Currency Information");
      output.println("* Currency Name: " + currencyName);
      output.println("* Exchange Rate: " + exchangeRate);
      output.println("* (NOTE: " + ageOfExchangeRate + ")");

      output.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
