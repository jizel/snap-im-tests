package travel.snapshot.dp.qa.utils

import io.gatling.core.Predef._

object SessionUtils {

  /** Retrieves value of specified key from session */
  def getValue[A](implicit session: Session, key: String): A = session.get(key).as[A]
}
