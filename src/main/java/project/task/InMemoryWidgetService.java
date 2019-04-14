package project.task;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InMemoryWidgetService implements WidgetService {

    private final AtomicLong counter = new AtomicLong();

    private final Map<Long, Widget> widgets = new ConcurrentHashMap<>();
    private final NavigableMap<Integer, Widget> zSortedWidgets = new ConcurrentSkipListMap<>();

    @Override
    public synchronized Widget create(WidgetDto widgetDTO) {
        Widget widget = fill(widgetDTO, Widget.builder())
                .zIndex(widgetDTO.getZIndex() == null ? getAvailableZIndex() : widgetDTO.getZIndex())
                .id(counter.getAndIncrement())
                .build();

        moveWidgetsIfNeed(widget);
        insert(widget);

        return widget;
    }

    @Override
    public Optional<Widget> get(long id) {
        return Optional.ofNullable(widgets.get(id));
    }

    @Override
    public Collection<Widget> getPartially(int from, int count) {
        Collection<Widget> values = zSortedWidgets.values();
        if (values.size() <= from || count <= 0) {
            return Collections.emptyList();
        }

        return new ArrayList<>(values).subList(from, Math.min(from + count, values.size()));
    }

    @Override
    public synchronized Widget update(long id, WidgetDto widgetDTO) {
        Widget existing = widgets.get(id);
        if (existing == null) {
            throw new WidgetNotFoundException();
        }

        boolean zIndexChanged = widgetDTO.getZIndex() != null && existing.getZIndex() != widgetDTO.getZIndex();
        Widget updatedWidget = fill(widgetDTO, existing.toBuilder()).build();

        if (zIndexChanged) {
            zSortedWidgets.remove(existing.getZIndex());
            moveWidgetsIfNeed(updatedWidget);
        }
        insert(updatedWidget);

        return updatedWidget;
    }

    @Override
    public synchronized void delete(long id) {
        Optional.ofNullable(widgets.remove(id))
                .ifPresent(deleted -> zSortedWidgets.remove(deleted.getZIndex()));
    }

    @Override
    public Collection<Widget> getAll() {
        return zSortedWidgets.values();
    }

    @Override
    public int size() {
        return widgets.size();
    }

    private int getAvailableZIndex() {
        return zSortedWidgets.isEmpty() ? 0 : zSortedWidgets.lastEntry().getValue().getZIndex() + 1;
    }

    private void insert(Widget widget) {
        zSortedWidgets.put(widget.getZIndex(), widget);
        widgets.put(widget.getId(), widget);
    }

    private void moveWidgetsIfNeed(Widget start) {
        if (zSortedWidgets.containsKey(start.getZIndex())) {
            var widgetsToMoveForward = zSortedWidgets.tailMap(start.getZIndex(), true).descendingMap().values();

            widgetsToMoveForward.forEach(widget -> {
                widget = zSortedWidgets.remove(widget.getZIndex())
                        .toBuilder()
                        .zIndex(widget.getZIndex() + 1)
                        .build();
                insert(widget);
            });
        }
    }

    private Widget.WidgetBuilder fill(WidgetDto src, Widget.WidgetBuilder builder) {
        if (src.getX() != null) {
            builder.x(src.getX());
        }

        if (src.getY() != null) {
            builder.y(src.getY());
        }

        if (src.getWidth() != null) {
            builder.width(src.getWidth());
        }

        if (src.getHeight() != null) {
            builder.height(src.getHeight());
        }

        if (src.getZIndex() != null) {
            builder.zIndex(src.getZIndex());
        }

        return builder;
    }

}
