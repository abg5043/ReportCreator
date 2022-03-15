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

  }

  public Clients(int clientNumber, String firstName, String lastName, String company, String address, String city, String country, String state, int postalCode, double riskFactor, String phone1, String phone2, String email, String website) {
    this.clientNumber = clientNumber;
    this.firstName = firstName;
    this.lastName = lastName;
    this.company = company;
    this.address = address;
    this.city = city;
    this.country = country;
    this.state = state;
    this.postalCode = postalCode;
    this.riskFactor = riskFactor;
    this.phone1 = phone1;
    this.phone2 = phone2;
    this.email = email;
    this.website = website;
  }
}
