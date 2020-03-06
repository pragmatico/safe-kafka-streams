package co.pragmati.kstreams.safe.stubs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleEvent {

  public static final Integer EXPECTED_ID = 1;
  public static final String EXPECTED_NAME = "name";

  private Integer id;
  private String name;

  public static SampleEvent sample() {
    return new SampleEvent(EXPECTED_ID, EXPECTED_NAME);
  }

  public static byte[] jsonSample() {
    return "{\"id\":1,\"name\":\"name\"}".getBytes();
  }

}
