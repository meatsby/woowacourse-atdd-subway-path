package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@JdbcTest
class SectionDaoTest {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    @Autowired
    public SectionDaoTest(NamedParameterJdbcTemplate jdbcTemplate) {
        this.stationDao = new StationDao(jdbcTemplate);
        this.lineDao = new LineDao(jdbcTemplate);
        this.sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("구간을 저장한다.")
    @Test
    void save() {
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("선릉역"));
        Line line = lineDao.save(new Line("2호선", "green"));

        Section section = new Section(line.getId(), upStation.getId(), downStation.getId(), 10);

        assertThat(sectionDao.save(section)).isNotNull();
    }

    @DisplayName("노선 id 로 구간을 찾아낸다.")
    @Test
    void findAllByLineId() {
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("선릉역"));
        Line line = lineDao.save(new Line("2호선", "green"));

        Section section = new Section(line.getId(), upStation.getId(), downStation.getId(), 10);

        sectionDao.save(section);

        assertThat(sectionDao.findAllByLineId(line.getId()).size()).isEqualTo(1);
    }

    @DisplayName("같은 상행 종점을 가진 구간을 찾아낸다.")
    @Test
    void findBySameUpStation() {
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("선릉역"));
        Line line = lineDao.save(new Line("2호선", "green"));

        Section section = new Section(line.getId(), upStation.getId(), downStation.getId(), 10);

        sectionDao.save(section);

        Section sameUpStation = sectionDao.findBy(section.getLineId(), upStation.getId(), 10L)
                .orElseThrow();

        assertThat(sameUpStation.getDownStationId()).isEqualTo(downStation.getId());
    }

    @DisplayName("같은 하행 종점을 가진 구간을 찾아낸다.")
    @Test
    void findBySameDownStation() {
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("선릉역"));
        Line line = lineDao.save(new Line("2호선", "green"));

        Section section = new Section(line.getId(), upStation.getId(), downStation.getId(), 10);

        sectionDao.save(section);

        Section sameUpStation = sectionDao.findBy(section.getLineId(), 10L, downStation.getId())
                .orElseThrow();

        assertThat(sameUpStation.getUpStationId()).isEqualTo(upStation.getId());
    }

    @DisplayName("같은 상행 또는 하행 종점을 가진 구간을 찾아내지 못할 경우 빈값을 반환한다.")
    @Test
    void findByNotExistingSection() {
        Optional<Section> section = sectionDao.findBy(1L, 9L, 10L);

        assertThat(section).isEmpty();
    }

    @Test
    @DisplayName("id로 노선을 삭제한다.")
    void deleteById() {
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("선릉역"));
        Line line = lineDao.save(new Line("2호선", "green"));

        Section section = new Section(line.getId(), upStation.getId(), downStation.getId(), 10);

        Section savedSection = sectionDao.save(section);

        sectionDao.deleteById(savedSection.getId());

        assertThat(sectionDao.findAllByLineId(line.getId()).size()).isZero();
    }

    @DisplayName("모든 구간을 찾아낸다.")
    @Test
    void findAll() {
        Station upStation1 = stationDao.save(new Station("강남역"));
        Station downStation1 = stationDao.save(new Station("선릉역"));
        Line line1 = lineDao.save(new Line("2호선", "green"));

        Section section1 = new Section(line1.getId(), upStation1.getId(), downStation1.getId(), 10);

        Station upStation2 = stationDao.save(new Station("수서역"));
        Station downStation2 = stationDao.save(new Station("가락시장역"));
        Line line2 = lineDao.save(new Line("3호선", "orange"));

        Section section2 = new Section(line2.getId(), upStation2.getId(), downStation2.getId(), 10);

        Section savedSection1 = sectionDao.save(section1);
        Section savedSection2 = sectionDao.save(section2);

        Sections expected = new Sections(List.of(savedSection1, savedSection2));

        assertThat(sectionDao.findAll()).isEqualTo(expected);
    }

    @DisplayName("모든 구간을 저장한다.")
    @Test
    void saveAll() {
        Station upStation1 = stationDao.save(new Station("강남역"));
        Station downStation1 = stationDao.save(new Station("선릉역"));
        Line line1 = lineDao.save(new Line("2호선", "green"));

        Section section1 = new Section(line1.getId(), upStation1.getId(), downStation1.getId(), 10);

        Station upStation2 = stationDao.save(new Station("수서역"));
        Station downStation2 = stationDao.save(new Station("가락시장역"));
        Line line2 = lineDao.save(new Line("3호선", "orange"));

        Section section2 = new Section(line2.getId(), upStation2.getId(), downStation2.getId(), 10);

        sectionDao.saveAll(List.of(section1, section2));

        assertThat(sectionDao.findAll().size()).isEqualTo(2);
    }
}
