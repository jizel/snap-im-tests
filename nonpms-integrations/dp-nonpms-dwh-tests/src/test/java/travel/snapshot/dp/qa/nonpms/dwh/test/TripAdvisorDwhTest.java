package travel.snapshot.dp.qa.nonpms.dwh.test;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import travel.snapshot.dp.qa.nonpms.dwh.test.dal.FacebookDwhDao;
import travel.snapshot.dp.qa.nonpms.dwh.test.dal.TripAdvisorDwhDao;

@Getter
public class TripAdvisorDwhTest extends AbstractDwhTest {

    @Getter
    @Autowired
    TripAdvisorDwhDao dataGapProvider;

}
