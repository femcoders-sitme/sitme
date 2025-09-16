package com.femcoders.sitme.user.exceptions;

import com.femcoders.sitme.shared.exceptions.ErrorCode;

public class UserNameNotFoundException extends RuntimeException{
  public UserNameNotFoundException(String username) {
    super("Username " + username + " not found");
  }
  public ErrorCode getErrorCode() {
    return ErrorCode.NOT_FOUND;
  }
}
