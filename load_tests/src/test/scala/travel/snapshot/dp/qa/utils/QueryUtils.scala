package travel.snapshot.dp.qa.utils

class QueryUtils {

  def buildAdditionalQueries(filter: String, sort: AnyRef, cursor: Integer = -1, limit: Integer = -1): String = {
    var sb: StringBuilder = new StringBuilder

    if (filter != null && !filter.isEmpty) {
      sb.append(s"&filter=$filter")
    }

    if (sort != null) {
      sb.append(s"&sort=$sort")
    }

    if (cursor > 0) {
      sb.append(s"&cursor=$cursor")
    }

    if (limit > 0) {
      sb.append(s"&limit=$limit")
    }

    val additionalQuery = sb.toString

    sb = null

    additionalQuery
  }
}
