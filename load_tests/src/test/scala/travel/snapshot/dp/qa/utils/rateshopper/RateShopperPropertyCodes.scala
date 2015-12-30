package travel.snapshot.dp.qa.utils.rateshopper

import java.util.concurrent.ThreadLocalRandom

import scala.collection.mutable.MutableList

class RateShopperPropertyCodes {

  private val records: MutableList[PropertyRecord] = MutableList()

  private var size: Integer = 0

  def add(propertyRecord: PropertyRecord) = {
    records += propertyRecord
    size += 1
  }

  def getRandomPropertyCode(): String = {
    if (records.isEmpty) {
      throw new IllegalStateException("Property list is empty")
    }

    records(ThreadLocalRandom.current().nextInt(size)).id
  }
}
