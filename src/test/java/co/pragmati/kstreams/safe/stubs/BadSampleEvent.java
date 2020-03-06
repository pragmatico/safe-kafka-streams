package co.pragmati.kstreams.safe.stubs;

public class BadSampleEvent extends SampleEvent {

  @Override
  public Integer getId() {
    throw new UnsupportedOperationException("blah blah");
  }

  public static BadSampleEvent sample() {
    return new BadSampleEvent();
  }
}