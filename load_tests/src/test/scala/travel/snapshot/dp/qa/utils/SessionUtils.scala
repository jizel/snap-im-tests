package travel.snapshot.dp.qa.utils

import io.gatling.core.Predef._

object SessionUtils {

  /** Retrieves value of specified key from session */
  def getValue[A](implicit session: Session, key: String): A = session.get(key).as[A]

  def containsValue[A](implicit session: Session, key: String): Boolean = session.contains(key)

  object EntityType extends Enumeration {
    type EntityType = Value
    val Customer, Property, User, PropertySet, Partner = Value
  }
}
