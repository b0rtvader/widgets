package project.task;

import java.util.Collection;
import java.util.Optional;

public interface WidgetService {

    Widget create(Widget.Patch patch);

    Optional<Widget> get(long id);

    Widget update(long id, Widget.Patch patch);

    void delete(long id);

    Collection<Widget> getAll();

    int size();
}
