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

  private val timeZonesRandomRange = timeZones.length

  private val cultures = Array("cs-CZ", "en-GB", "en-US")

  private val culturesRandomRange = cultures.length

  /**
    * Returns random integer from 0 to give bound
    *
    * @param bound upper bound for random number
    * @return random number from 0 to bound exclusive
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
    * @return random boolean as its string representation - "1" or "0"
    */
  def randomBooleanAsBinary: String = if (randomBoolean) "1" else "0"

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
    * produce string since=yyyy-MM-dd&&until=yyyy-MM-dd
    * dates should be in interval <2015-01-01,2016-12-31>
    * @param numberOfDays how long the interval is
    * @return
    */
  def randomSinceUntil(numberOfDays: Int) = {
    val since = LocalDate.of(2015, 1, 1).plusDays(randomInt(600))
    val until = since.plusDays(numberOfDays)
    s"""since=${since.format(DateTimeFormatter.ISO_LOCAL_DATE)}&until=${until.format(DateTimeFormatter.ISO_LOCAL_DATE)}"""
  }

  /**
    * produce tuple of dates in a form 'yyyy-MM-dd'
    * dates should be in interval <2015-01-01,2016-12-31>
    * @param numberOfDays how long the random interval is
    * @return tuple of two strings, the first is "from" date, the second is "to" date
    */
  def randomValidFromAndTo(numberOfDays: Int) = {
    val since = LocalDate.of(2015, 1, 1).plusDays(randomInt(600))
    val until = since.plusDays(numberOfDays)

    (since.format(DateTimeFormatter.ISO_LOCAL_DATE), until.format(DateTimeFormatter.ISO_LOCAL_DATE))
  }

  def randomPropertyId = s"""990${"%03d".format(randomInt(999))}99-9999-4999-a999-999999999999"""

  def randomTripAdvisorPropertyId = s"""990${"%03d".format(randomInt(11))}99-9999-4999-a999-999999999999"""

  def getRandomLocationId = randomInt(100)
}
