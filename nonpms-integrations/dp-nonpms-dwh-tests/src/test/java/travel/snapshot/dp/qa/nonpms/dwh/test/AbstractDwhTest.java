package travel.snapshot.dp.qa.nonpms.dwh.test;

import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.dwh.util.DateUtils.localDateToDbDateId;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import travel.snapshot.dp.qa.nonpms.dwh.config.TestConfig;
import travel.snapshot.dp.qa.nonpms.dwh.test.dal.DataGapProvider;

import java.time.LocalDate;
import java.time.ZoneId;

@Transactional
@RunWith(SpringRunner.class)
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = { TestConfig.class })
public abstract class AbstractDwhTest {

    @Value("${data-gaps.history.days}")
    int dataGapsHistoryDays;

    static LocalDate LAST_COMPLETED_DATE = LocalDate.now(ZoneId.of("GMT-12")).minusDays(1);

    protected abstract DataGapProvider getDataGapProvider();

    @Test
    public void testDataGaps() {
        int until = localDateToDbDateId(LAST_COMPLETED_DATE);
        int since = localDateToDbDateId(LAST_COMPLETED_DATE.minusDays(dataGapsHistoryDays));

        assertThat(getDataGapProvider().getDataGaps(since, until)).isEmpty();
    }

}
