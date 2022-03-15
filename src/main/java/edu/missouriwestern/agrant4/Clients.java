package edu.missouriwestern.agrant4;

public class Clients {
  private int clientNumber;
  private String firstName;
  private String lastName;
  private String company;
  private String address;
  private String city;
  private String country;
  private String state;
  private int postalCode;
  private double riskFactor;
  private String phone1;
  private String phone2;
  private String email;
  private String website;


  public Clients() {
    //TODO: fix this
  }

  public Clients(int clientNumber, String firstName, String lastName, String company, String address, String city, String country, String state, int postalCode, double riskFactor, String phone1, String phone2, String email, String website) {
    setClientNumber(clientNumber);
    setFirstName(firstName);
    setLastName(lastName);
    setCompany(company);
    setAddress(address);
    setCity(city);
    setCountry(country);
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

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
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
    return "Clients{" +
        "clientNumber=" + clientNumber +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", company='" + company + '\'' +
        ", address='" + address + '\'' +
        ", city='" + city + '\'' +
        ", country='" + country + '\'' +
        ", state='" + state + '\'' +
        ", postalCode=" + postalCode +
        ", riskFactor=" + riskFactor +
        ", phone1='" + phone1 + '\'' +
        ", phone2='" + phone2 + '\'' +
        ", email='" + email + '\'' +
        ", website='" + website + '\'' +
        '}';
  }
}
