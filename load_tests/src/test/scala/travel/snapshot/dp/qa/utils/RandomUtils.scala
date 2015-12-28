package travel.snapshot.dp.qa.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.{TimeZone, UUID}
import java.util.concurrent.ThreadLocalRandom

class RandomUtils {

  def randomGranularity: Granularity.Granularity = Granularity.getRandom

  def randomTwitterMetric: Metrics.TwitterMetric.TwitterMetric = Metrics.TwitterMetric.getRandom

  def randomFacebookMetric: Metrics.FacebookMetric.FacebookMetric = Metrics.FacebookMetric.getRandom

  def randomInstagramMetric: Metrics.InstagramMetric.InstagramMetric = Metrics.InstagramMetric.getRandom

  private val timeZones = TimeZone.getAvailableIDs.filter {
    z => z.startsWith("Europe") || z.startsWith("America") || z.startsWith("Africa") || z.startsWith("Asia")
  }

  private val timeZonesRandomRange = timeZones.length - 1

  private val cultures = Array("cs_CZ", "en_GB", "en_US")

  private val culturesRandomRange = cultures.length - 1

  /**
    * Returns random integer from 0 to give bound
    *
    * @param bound upper bound for random number
    * @return random number from 0 to bound
    */
  def randomInt(bound: Int): Int = ThreadLocalRandom.current.nextInt(bound)

  /**
    * Returns random boolean
    *
    * @return random boolean
    */
  def randomBoolean: Boolean = ThreadLocalRandom.current().nextBoolean()

  /**
    *
    * @return random UUID as a String
    */
  def randomUUIDAsString: String = this.randomUUID.toString

  /**
    *
    * @return random UUID
    */
  def randomUUID: UUID = java.util.UUID.randomUUID

  /**
    * Returns random timezone in Europe, Asia, Africa or America
    *
    * @return random timezone
    */
  def randomTimezone: String = timeZones(randomInt(timeZonesRandomRange))

  /**
    * Returns random culture
    *
    * @return random culture
    */
  def randomCulture: String = cultures(randomInt(culturesRandomRange))

  /**
    * produce string &since=yyyy-MM-dd&&until=yyyy-MM-dd
    * dates should be in interval <2014-01-01,2015-12-31>
    * @param numberOfDays how long the interval is
    * @return
    */
  def randomSinceUntil(numberOfDays: Int) = {
    val since = LocalDate.of(2014, 1, 1).plusDays(randomInt(600))
    val until = since.plusDays(numberOfDays)
    s"""&since=${since.format(DateTimeFormatter.ISO_LOCAL_DATE)}&until=${until.format(DateTimeFormatter.ISO_LOCAL_DATE)}"""
  }

  def randomPropertyId = s"""990${"%03d".format(randomInt(999))}99-9999-4999-a999-999999999999"""
}
