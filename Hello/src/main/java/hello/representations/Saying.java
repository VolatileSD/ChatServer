package hello.representations;

import com.fasterxml.jackson.annotation.*;

public class Saying {
    public final long id;
    public final String content;
    @JsonCreator
    public Saying(@JsonProperty("id") long id, @JsonProperty("content") String content) {
      this.id = id;
      this.content = content;
    }
}

