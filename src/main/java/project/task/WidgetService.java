package project.task;

import java.util.Collection;
import java.util.Optional;

public interface WidgetService {

    Widget create(WidgetDto widgetDTO);

    Optional<Widget> get(long id);

    Widget update(long id, WidgetDto widgetDTO);

    void delete(long id);

    Collection<Widget> getAll();

    Collection<Widget> getPartially(int from, int count);

    int size();
}
