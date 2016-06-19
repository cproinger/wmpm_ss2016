package at.ac.tuwien.wmpm.helper;

import at.ac.tuwien.wmpm.ss2016.VoteRequest;
import java.util.List;

public class TransformedRequest {

  private VoteRequest request;
  private List<ValidationObject> validationObjectList;

  public VoteRequest getRequest() {
    return request;
  }

  public void setRequest(VoteRequest request) {
    this.request = request;
  }

  public List<ValidationObject> getValidationObjectList() {
    return validationObjectList;
  }

  public void setValidationObjectList(List<ValidationObject> validationObjectList) {
    this.validationObjectList = validationObjectList;
  }
}
