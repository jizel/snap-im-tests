package travel.snapshot.dp.qa.nonpms.jms.test.etl.extra;

import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.jms.messages.Provider.SOCIALMEDIA_FACEBOOK;
import static travel.snapshot.dp.qa.nonpms.jms.util.DateUtils.toDateId;
import static travel.snapshot.dp.qa.nonpms.jms.util.Helpers.createSchedulerMessageJson;
import static travel.snapshot.dp.qa.nonpms.jms.util.Helpers.extractTimestamp;

import lombok.Getter;
import org.junit.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;
import travel.snapshot.dp.qa.nonpms.jms.messages.Provider;
import travel.snapshot.dp.qa.nonpms.jms.test.etl.AbstractEtlTest;
import travel.snapshot.dp.qa.nonpms.jms.test.etl.extra.dal.FacebookDwhDao;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;

@Getter
@Transactional
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@MapperScan("travel.snapshot.dp.qa.nonpms.jms.test.etl.extra.dal")
public class AdvancedFacebookEtlTest extends AbstractEtlTest {

	Provider provider = SOCIALMEDIA_FACEBOOK;

	@Value("${integration.facebook.affected.date}")
	String affectedDate;
	@Value("${integration.facebook.start.queue}")
	String startQueue;
	@Value("${integration.facebook.start.firetime}")
	String fireTime;
	@Value("#{'${integration.facebook.affected.properties}'.split(',')}")
	Set<String> affectedProperties;
	//	@Value("${integration.facebook.end-date}")
//	String endDate;
	@Value("${integration.facebook.timezone}")
	String timezone;

	private Instant timestamp = Instant.now();

	@Autowired
	FacebookDwhDao dwhDao;

	// Workaround for DPNP-127
	@Override
	protected String getEndDate() {
		return LocalDate.now(ZoneId.of(timezone)).toString();
	}

	@Test(timeout = 60000)
	public void testEtlWithOverrideData() throws Exception {
		LocalDateTime start = LocalDateTime.now(Clock.systemUTC());

		jms.send(getStartQueue(), createSchedulerMessageJson(getFireTime(), true));

		checkNotifications();

		affectedProperties.forEach(propertyId -> {
			Map<String, Object> data = dwhDao.getData(propertyId, toDateId(affectedDate));
			assertThat(extractTimestamp(data, "inserted_time_stamp")).isAfter(start);
		});
	}

}
