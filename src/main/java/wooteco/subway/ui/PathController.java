package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.PathDto;
import wooteco.subway.dto.PathResponse;
import wooteco.subway.service.PathService;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> findShortestPath(@RequestParam Long source,
                                                         @RequestParam Long target,
                                                         @RequestParam Integer age) {
        PathDto pathDto = pathService.findShortestPath(source, target);
        return ResponseEntity.ok(new PathResponse(pathDto));
    }
}
