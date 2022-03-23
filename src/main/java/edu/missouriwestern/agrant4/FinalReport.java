package edu.missouriwestern.agrant4;

public class FinalReport {

  private Client client;
  private String latitude;
  private String longitude;
  private String medianAge;
  private String population;
  private String currencyName;
  private String exchangeRate;
  private String ageOfExchangeRate;

  public FinalReport() {
    this(
        new Client(),
        "No info found",
        "No info found",
        "No info found",
        "No info found",
        "No info found",
        "No info found",
        "No info found"
    );
  }
  public FinalReport(
      Client client,
      String latitude,
      String longitude,
      String medianAge,
      String population,
      String currencyName,
      String exchangeRate,
      String ageOfExchangeRate
  ) {
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

  @Override
  public String toString() {
    return "FinalReport{" +
        "client=" + client +
        ", latitude='" + latitude + '\'' +
        ", longitude='" + longitude + '\'' +
        ", medianAge='" + medianAge + '\'' +
        ", population='" + population + '\'' +
        '}';
  }
}
