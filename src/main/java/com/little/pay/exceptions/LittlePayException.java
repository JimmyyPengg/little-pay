package com.little.pay.exceptions;

public class LittlePayException extends RuntimeException {
  public LittlePayException(final String message) {
    super(message);
  }

  public LittlePayException(final String message, final Exception exception) {
    super(message, exception);
  }
}
