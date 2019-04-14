package project.task;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/widgets")
@AllArgsConstructor
public class WidgetController {

    private final WidgetService widgetService;

    @GetMapping("/{id}")
    public Widget get(@PathVariable long id) {
        return widgetService.get(id)
                .orElseThrow(WidgetNotFoundException::new);
    }

    @GetMapping("/all")
    public Collection<Widget> getAll() {
        // TODO постраничный вывод ?
        return widgetService.getAll();
    }

    @PostMapping
    public Widget create(@RequestBody Widget.Patch patch) {
        return widgetService.create(patch);
    }

    @PatchMapping("/{id}")
    public void update(@PathVariable long id, @RequestBody Widget.Patch patch) {
        widgetService.update(id, patch);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        widgetService.delete(id);
    }
}
