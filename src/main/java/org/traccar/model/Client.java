package org.traccar.model;


import org.traccar.storage.StorageName;

@StorageName("tc_clients")
public class Client extends ExtendedModel {
  private String name;
  private String email;
  private String rfc;

  public String getRfc() {
    return rfc;
  }

  public void setRfc(String rfc) {
    this.rfc = rfc;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
