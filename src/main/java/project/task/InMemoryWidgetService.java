package project.task;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InMemoryWidgetService implements WidgetService {

    private final AtomicLong counter = new AtomicLong();

    private final Map<Long, Widget> widgets = new ConcurrentHashMap<>();
    private final TreeSet<Widget> zSortedWidgets = new TreeSet<>(Comparator.comparingInt(Widget::getZIndex));

    @Override
    public Widget create(Widget.Patch patch) {
        Widget widget = patch.enrich(Widget.builder())
                .id(counter.getAndIncrement())
                .build();

        synchronized (this) {
            if (patch.getZIndex() == null) {
                widget.setZIndex(getAvailableZIndex());
            }

            moveWidgetsIfNeed(widget);
            insert(widget);

            return widget;
        }
    }

    @Override
    public Optional<Widget> get(long id) {
        return Optional.ofNullable(widgets.get(id));
    }

    @Override
    public synchronized Widget update(long id, Widget.Patch patch) {
        Widget existing = widgets.get(id);
        if (existing == null) {
            throw new WidgetNotFoundException();
        }

        boolean zIndexChanged = patch.getZIndex() != null && existing.getZIndex() != patch.getZIndex();
        Widget updatedWidget = patch.enrich(existing.toBuilder()).build();

        if (zIndexChanged) {
            zSortedWidgets.remove(existing);
            moveWidgetsIfNeed(updatedWidget);
        }
        insert(updatedWidget);

        return updatedWidget;
    }

    @Override
    public synchronized void delete(long id) {
        Optional.ofNullable(widgets.remove(id))
                .ifPresent(zSortedWidgets::remove);
    }

    @Override
    public Collection<Widget> getAll() {
        return zSortedWidgets;
    }

    @Override
    public int size() {
        return widgets.size();
    }

    private int getAvailableZIndex() {
        return zSortedWidgets.isEmpty() ? 0 : zSortedWidgets.last().getZIndex() + 1;
    }

    private void insert(Widget widget) {
        zSortedWidgets.add(widget);
        widgets.put(widget.getId(), widget);
    }

    private void moveWidgetsIfNeed(Widget start) {
        if (zSortedWidgets.contains(start)) {
            zSortedWidgets.tailSet(start, true).forEach(Widget::moveForward);
        }
    }

}
