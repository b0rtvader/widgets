import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.task.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class WidgetServiceTest {

    private WidgetService widgetService;

    @BeforeEach
    void init() {
        widgetService = new InMemoryWidgetService();
    }

    @Test
    void create_withZIndex_returnExpected() {
        WidgetDto widgetDTO = WidgetDto.builder()
                .zIndex(1)
                .x(50L)
                .y(100L)
                .width(200L)
                .height(300L)
                .build();
        Widget actual = widgetService.create(widgetDTO);

        Widget expected = Widget.builder()
                .id(0)
                .x(50)
                .y(100)
                .width(200)
                .height(300)
                .zIndex(1)
                .build();

        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getX(), expected.getX());
        assertEquals(actual.getY(), expected.getY());
        assertEquals(actual.getWidth(), actual.getWidth());
        assertEquals(actual.getHeight(), expected.getHeight());
        assertEquals(actual.getZIndex(), expected.getZIndex());
    }

    @Test
    void create_firstWidgetWithoutZIndex_returnWithZeroIndex() {
        Widget created = widgetService.create(WidgetDto.empty());

        assertEquals(0, created.getZIndex());
    }

    @Test
    void create_nonFirstWidgetWithoutZIndex_returnWithNextZIndex() {
        int index = 5;
        widgetService.create(WidgetDto.builder().zIndex(index).build());

        Widget second = widgetService.create(WidgetDto.empty());

        assertEquals(index + 1, second.getZIndex());
    }

    @Test
    void create_withLowerZIndex_moveWidgetsForward() {
        Widget first = widgetService.create(WidgetDto.builder().zIndex(2).build());
        Widget second = widgetService.create(WidgetDto.builder().zIndex(1).build());
        Widget third = widgetService.create(WidgetDto.builder().zIndex(3).build());
        Widget fourth = widgetService.create(WidgetDto.builder().zIndex(2).build());

        assertEquals(4, widgetService.size());
        assertEquals(3, widgetService.get(first.getId()).get().getZIndex());
        assertEquals(1, widgetService.get(second.getId()).get().getZIndex());
        assertEquals(4, widgetService.get(third.getId()).get().getZIndex());
        assertEquals(2, widgetService.get(fourth.getId()).get().getZIndex());
    }

    @Test
    void get_nonCreatedWidget_returnEmpty() {
        Optional<Widget> widget = widgetService.get(10);

        assertTrue(widget.isEmpty());
    }

    @Test
    void getAll_returnOrderedByZIndex() {
        Widget frontWidget = widgetService.create(WidgetDto.builder().zIndex(10).build());
        Widget backWidget = widgetService.create(WidgetDto.builder().zIndex(5).build());

        Collection<Widget> all = widgetService.getAll();
        Iterator<Widget> iterator = all.iterator();

        Widget first = iterator.next();
        Widget second = iterator.next();

        assertEquals(first.getId(), backWidget.getId());
        assertEquals(second.getId(), frontWidget.getId());
    }

    @Test
    void getPartially_checkReturnedCount() {
        WidgetDto dto = WidgetDto.empty();
        for(int i = 0; i < 5; i ++) {
            widgetService.create(dto);
        }

        assertEquals(2, widgetService.getPartially(3, 2).size());
        assertEquals(2, widgetService.getPartially(3, 4).size());
        assertEquals(0, widgetService.getPartially(3, 0).size());
        assertEquals(0, widgetService.getPartially(5, 1).size());
    }

    @Test
    void update_notFoundWidget_exceptionThrown() {
        assertThrows(WidgetNotFoundException.class,
                () -> widgetService.update(1, WidgetDto.empty()));
    }

    @Test
    void update_zIndexChanged_moveWidgetsForward() {
        Widget first = widgetService.create(WidgetDto.builder()
                .zIndex(5)
                .build());
        Widget second = widgetService.create(WidgetDto.builder()
                .zIndex(6)
                .build());
        Widget third = widgetService.create(WidgetDto.builder()
                .zIndex(7)
                .build());

        widgetService.update(first.getId(), WidgetDto.builder()
                .zIndex(second.getZIndex())
                .build());

        assertEquals(7, widgetService.get(second.getId()).get().getZIndex());
        assertEquals(8, widgetService.get(third.getId()).get().getZIndex());
    }

    @Test
    void delete() {
        Widget created = widgetService.create(WidgetDto.empty());

        widgetService.delete(created.getId());

        assertEquals(widgetService.size(), 0);
        assertTrue(widgetService.get(created.getId()).isEmpty());
        assertTrue(widgetService.getAll().isEmpty());
    }
}
