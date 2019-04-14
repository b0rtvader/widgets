package project.task;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class Widget {

    private final long id;

    private final long x;
    private final long y;

    private final long width;
    private final long height;

    private final int zIndex;

    private final LocalDateTime timestamp = LocalDateTime.now();
}
