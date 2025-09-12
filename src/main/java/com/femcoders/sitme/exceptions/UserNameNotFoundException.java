package com.femcoders.sitme.exceptions;

public class UserNameNotFoundException extends RuntimeException{
  public UserNameNotFoundException(String username) {
    super("Username " + username + " not found");
  }
}
