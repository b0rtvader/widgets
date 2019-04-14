package project.task;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class Widget {

    private final long id;

    private final long x;
    private final long y;

    private final long width;
    private final long height;

    @Setter
    private volatile int zIndex;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public synchronized void moveForward() {
        zIndex++;
    }

    @RequiredArgsConstructor
    public static class Patch {

        @Getter
        private final Integer zIndex;
        private final Long x;
        private final Long y;
        private final Long width;
        private final Long height;

        public Widget.WidgetBuilder enrich(Widget.WidgetBuilder builder) {
            if (x != null) {
                builder.x(x);
            }

            if (y != null) {
                builder.y(y);
            }

            if (width != null) {
                builder.width(width);
            }

            if (height != null) {
                builder.height(height);
            }

            if (zIndex != null) {
                builder.zIndex(zIndex);
            }

            return builder;
        }
    }

}
