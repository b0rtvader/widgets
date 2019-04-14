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
        return widgetService.getAll();
    }

    @GetMapping
    public Collection<Widget> getWidgets(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int count) {
        return widgetService.getPartially(offset, count);
    }

    @PostMapping
    public Widget create(@RequestBody WidgetDto widgetDTO) {
        return widgetService.create(widgetDTO);
    }

    @PatchMapping("/{id}")
    public Widget update(@PathVariable long id, @RequestBody WidgetDto widgetDTO) {
        return widgetService.update(id, widgetDTO);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        widgetService.delete(id);
    }
}
