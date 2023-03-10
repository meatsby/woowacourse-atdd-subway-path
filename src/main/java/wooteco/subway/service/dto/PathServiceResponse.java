package wooteco.subway.service.dto;

import java.util.List;
import wooteco.subway.domain.Station;

public class PathServiceResponse {

    private final List<Station> stations;
    private final int distance;
    private final int fare;

    public PathServiceResponse(List<Station> stations, int distance, int fare) {
        this.stations = stations;
        this.distance = distance;
        this.fare = fare;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public int getFare() {
        return fare;
    }
}
