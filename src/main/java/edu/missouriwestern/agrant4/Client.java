package edu.missouriwestern.agrant4;

import com.opencsv.bean.CsvBindByName;

/** This is an object that represent a client for the company. */
public class Client {
  @CsvBindByName(column = "number")
  private int clientNumber;

  @CsvBindByName(column = "first_name")
  private String firstName;

  @CsvBindByName(column = "last_name")
  private String lastName;

  @CsvBindByName private String company;
  @CsvBindByName private String address;
  @CsvBindByName private String city;
  @CsvBindByName private String county;
  @CsvBindByName private String state;

  @CsvBindByName(column = "postal_code")
  private int postalCode;

  @CsvBindByName(column = "risk_factor")
  private double riskFactor;

  @CsvBindByName private String phone1;

  @CsvBindByName(column = "phon2")
  private String phone2;

  @CsvBindByName private String email;

  @CsvBindByName(column = "web")
  private String website;

  /** Default no-arg constructor for client. Fills client with default values */
  public Client() {
    this(
        -1,
        "?",
        "?",
        "?",
        "?",
        "?",
        "?",
        "?",
        -1,
        -1.0,
        "???-???-????",
        "???-???-????",
        "?@?.com",
        "?");
  }

  /**
   * Full constructor for client.
   *
   * @param clientNumber -- client's number
   * @param firstName -- client's first name
   * @param lastName -- client's last name
   * @param company -- client's company
   * @param address -- client's address
   * @param city -- client's city
   * @param county -- client's county
   * @param state -- client's state
   * @param postalCode -- client's postal code
   * @param riskFactor -- client's risk factor
   * @param phone1 -- client's phone number
   * @param phone2 -- client's secondary number
   * @param email -- client's email
   * @param website -- client's website
   */
  public Client(
      int clientNumber,
      String firstName,
      String lastName,
      String company,
      String address,
      String city,
      String county,
      String state,
      int postalCode,
      double riskFactor,
      String phone1,
      String phone2,
      String email,
      String website) {
    setClientNumber(clientNumber);
    setFirstName(firstName);
    setLastName(lastName);
    setCompany(company);
    setAddress(address);
    setCity(city);
    setCounty(county);
    setState(state);
    setPostalCode(postalCode);
    setRiskFactor(riskFactor);
    setPhone1(phone1);
    setPhone2(phone2);
    setEmail(email);
    setWebsite(website);
  }

  public int getClientNumber() {
    return clientNumber;
  }

  public void setClientNumber(int clientNumber) {
    this.clientNumber = clientNumber;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCounty() {
    return county;
  }

  public void setCounty(String county) {
    this.county = county;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public int getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(int postalCode) {
    this.postalCode = postalCode;
  }

  public double getRiskFactor() {
    return riskFactor;
  }

  public void setRiskFactor(double riskFactor) {
    this.riskFactor = riskFactor;
  }

  public String getPhone1() {
    return phone1;
  }

  public void setPhone1(String phone1) {
    this.phone1 = phone1;
  }

  public String getPhone2() {
    return phone2;
  }

  public void setPhone2(String phone2) {
    this.phone2 = phone2;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  @Override
  public String toString() {
    return "Clients{"
        + "clientNumber="
        + clientNumber
        + ", firstName='"
        + firstName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + ", company='"
        + company
        + '\''
        + ", address='"
        + address
        + '\''
        + ", city='"
        + city
        + '\''
        + ", county='"
        + county
        + '\''
        + ", state='"
        + state
        + '\''
        + ", postalCode="
        + postalCode
        + ", riskFactor="
        + riskFactor
        + ", phone1='"
        + phone1
        + '\''
        + ", phone2='"
        + phone2
        + '\''
        + ", email='"
        + email
        + '\''
        + ", website='"
        + website
        + '\''
        + '}';
  }
}
