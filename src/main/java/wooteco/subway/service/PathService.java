package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Fare;
import wooteco.subway.domain.Path;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.discountpolicy.DiscountPolicyFactory;
import wooteco.subway.domain.farepolicy.FarePolicyFactory;
import wooteco.subway.service.dto.LineServiceResponse;
import wooteco.subway.service.dto.PathServiceRequest;
import wooteco.subway.service.dto.PathServiceResponse;

@Service
public class PathService {

    private final StationService stationService;
    private final SectionService sectionService;
    private final LineService lineService;

    public PathService(StationService stationService, SectionService sectionService, LineService lineService) {
        this.stationService = stationService;
        this.sectionService = sectionService;
        this.lineService = lineService;
    }

    public PathServiceResponse findShortestPath(PathServiceRequest pathServiceRequest) {
        Long departureId = pathServiceRequest.getDepartureId();
        Long arrivalId = pathServiceRequest.getArrivalId();
        int age = pathServiceRequest.getAge();

        Sections sections = sectionService.findAll();
        Path path = new Path(sections);

        List<Station> stations = getStations(path, departureId, arrivalId);
        int distance = path.getShortestPathDistance(departureId, arrivalId);
        int fare = getFare(age, sections, distance);

        return new PathServiceResponse(stations, distance, fare);
    }

    private List<Station> getStations(Path path, Long departureId, Long arrivalId) {
        List<Long> shortestPathStationIds = path.getShortestPathStationIds(departureId, arrivalId);
        return shortestPathStationIds.stream()
                .map(stationService::findById)
                .collect(Collectors.toList());
    }

    private int getFare(int age, Sections sections, int distance) {
        int extraFare = sections.getAllLindIds()
                .stream()
                .map(lineService::findById)
                .mapToInt(LineServiceResponse::getExtraFare)
                .max()
                .orElse(0);

        Fare fare = new Fare(FarePolicyFactory.from(distance), DiscountPolicyFactory.from(age));
        return fare.calculate(distance, extraFare);
    }
}
