package travel.snapshot.dp.qa

import io.gatling.core.Predef._

object Utils {

  /** Retrieves value of specified key from session */
  def getValue[A](implicit session: Session, key: String): A = session.get(key).as[A]

  /** Generates random UUID and returns its string representation */
  def randomUUID : String = java.util.UUID.randomUUID().toString

}
