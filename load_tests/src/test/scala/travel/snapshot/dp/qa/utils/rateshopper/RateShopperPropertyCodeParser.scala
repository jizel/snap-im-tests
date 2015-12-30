package travel.snapshot.dp.qa.utils.rateshopper

import scala.io.Source

class RateShopperPropertyCodeParser(val propertyCodesFile: String = "tti_hotel_ids") {

  def parse(): RateShopperPropertyCodes = {
    val src = Source.fromFile(propertyCodesFile).getLines()
    val propertyCodes: RateShopperPropertyCodes = new RateShopperPropertyCodes

    for (line <- src) {
      propertyCodes.add(new PropertyRecord(line))
    }

    propertyCodes
  }
}
