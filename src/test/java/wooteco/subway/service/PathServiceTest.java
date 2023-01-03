package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.LineServiceRequest;
import wooteco.subway.service.dto.LineServiceResponse;
import wooteco.subway.service.dto.PathServiceRequest;
import wooteco.subway.service.dto.PathServiceResponse;

@SpringBootTest
class PathServiceTest extends ServiceTest {

    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineService lineService;
    private final PathService pathService;

    @Autowired
    public PathServiceTest(StationDao stationDao, SectionDao sectionDao, LineService lineService,
                           PathService pathService) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.lineService = lineService;
        this.pathService = pathService;
    }

    @DisplayName("최단 경로의 경유역들과 거리, 운임비용을 반환한다.")
    @Test
    void findShortestPath() {
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("선릉역"));
        Station station3 = stationDao.save(new Station("수서역"));
        Station station4 = stationDao.save(new Station("가락시장역"));

        LineServiceResponse line = lineService.save(
                new LineServiceRequest("2호선", "green", station1.getId(), station2.getId(), 2, 0));
        sectionDao.save(new Section(line.getId(), station2.getId(), station3.getId(), 3));
        lineService.save(new LineServiceRequest("3호선", "orange", station2.getId(), station4.getId(), 4, 0));

        PathServiceRequest pathServiceRequest = new PathServiceRequest(station1.getId(), station4.getId(), 10);

        PathServiceResponse pathServiceResponse = pathService.findShortestPath(pathServiceRequest);

        assertAll(
                () -> assertThat(pathServiceResponse.getStations()).containsExactly(station1, station2, station4),
                () -> assertThat(pathServiceResponse.getDistance()).isEqualTo(6),
                () -> assertThat(pathServiceResponse.getFare()).isEqualTo(1250)
        );
    }

    @DisplayName("구간에 등록되지않은 지하철역으로 최단 경로 조회시 예외가 발생한다.")
    @Test
    void findShortestPath_exceptionNotSavedInSection() {
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("선릉역"));
        Station station3 = stationDao.save(new Station("수서역"));
        Station station4 = stationDao.save(new Station("가락시장역"));
        Station station5 = stationDao.save(new Station("천호역"));

        LineServiceResponse line = lineService.save(
                new LineServiceRequest("2호선", "green", station1.getId(), station2.getId(), 2, 0));
        sectionDao.save(new Section(line.getId(), station2.getId(), station3.getId(), 3));
        lineService.save(new LineServiceRequest("3호선", "orange", station2.getId(), station4.getId(), 4, 0));

        PathServiceRequest pathServiceRequest = new PathServiceRequest(station1.getId(), station5.getId(), 10);

        assertThatThrownBy(() -> pathService.findShortestPath(pathServiceRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간에 등록 되지 않은 역입니다.");
    }

    @DisplayName("연결되지 않은 구간의 최단 경로 조회시 예외가 발생한다.")
    @Test
    void findShortestPath_exceptionInvalidPath() {
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("선릉역"));
        Station station3 = stationDao.save(new Station("수서역"));
        Station station4 = stationDao.save(new Station("가락시장역"));
        Station station5 = stationDao.save(new Station("천호역"));

        LineServiceResponse line = lineService.save(
                new LineServiceRequest("2호선", "green", station1.getId(), station2.getId(), 2, 0));
        sectionDao.save(new Section(line.getId(), station2.getId(), station3.getId(), 3));
        lineService.save(new LineServiceRequest("3호선", "orange", station5.getId(), station4.getId(), 4, 0));

        PathServiceRequest pathServiceRequest = new PathServiceRequest(station1.getId(), station5.getId(), 10);

        assertThatThrownBy(() -> pathService.findShortestPath(pathServiceRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("연결되지 않은 구간입니다.");
    }
}
