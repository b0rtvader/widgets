package project.task;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WidgetDto {

    private final Integer zIndex;
    private final Long x;
    private final Long y;
    private final Long width;
    private final Long height;

    public static WidgetDto empty() {
        return WidgetDto.builder().build();
    }
}
