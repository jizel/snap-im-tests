package travel.snapshot.dp.qa.utils

class CustomerPropertyFilterBuilder(filterCount: Integer = 3) {

  private val EMPTY_FILTER = ""

  private val EQUALS_TO = "=="

  private val randomUtils = new RandomUtils

  private val (validFrom, validTo) = randomUtils.randomValidFromAndTo(20)

  private var initialFilters: Array[CustomerPropertyFilter.Value] = Array()

  def init(initialFilters: Array[CustomerPropertyFilter.Value]): CustomerPropertyFilterBuilder = {
    this.initialFilters = initialFilters
    this
  }

  def init(initialFilters: CustomerPropertyFilter.Value*): CustomerPropertyFilterBuilder = {
    init(initialFilters.toArray)
  }

  def build(): String = {

    if (filterCount == null || filterCount <= 0) {
      return EMPTY_FILTER
    }

    val filters: Array[CustomerPropertyFilter.Value] = if (initialFilters.isEmpty) CustomerPropertyFilter.getRandom(filterCount) else initialFilters

    var sb: StringBuilder = new StringBuilder

    filters.foreach { filterEnum =>

      sb.append(filterEnum.toString).append(EQUALS_TO)

      filterEnum match {
        case CustomerPropertyFilter.RELATIONSHIP_ID => sb.append(randomUtils.randomUUIDAsString)
        case CustomerPropertyFilter.PROPERTY_NAME => sb.append(randomUtils.randomUUIDAsString)
        case CustomerPropertyFilter.PROPERTY_ID => sb.append(randomUtils.randomPropertyId)
        case CustomerPropertyFilter.TYPE => sb.append("anchor")
        case CustomerPropertyFilter.VALID_FROM => sb.append(validFrom)
        case CustomerPropertyFilter.VALID_TO => sb.append(validTo)
      }

      sb.append(";")
    }

    val builtFilter = sb.toString

    sb = null

    builtFilter
  }
}
