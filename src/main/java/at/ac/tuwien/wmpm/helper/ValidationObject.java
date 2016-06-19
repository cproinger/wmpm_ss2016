package at.ac.tuwien.wmpm.helper;

public class ValidationObject {

  private ValidationType type;

  private String value;

  public ValidationObject(ValidationType type, String value) {
    this.type = type;
    this.value = value;
  }

  public ValidationType getType() {
    return type;
  }

  public String getValue() {
    return value;
  }
}
