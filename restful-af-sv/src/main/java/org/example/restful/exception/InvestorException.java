package org.example.restful.exception;

public class InvestorException extends RuntimeException {

  private static final long serialVersionUID = -8538212400867405969L;

  public InvestorException(final String message) {
    super(message);
  }
}
