package travel.snapshot.dp.qa

/**
  * Common simulation class for integrations. Integrations should extend this instead of AbstractSimulation.
  */
abstract class AbstractIntegrationsSimulation extends AbstractSimulation{

  val integrationsPropertyId: String = if (propertyId.isEmpty) randomUtils.randomPropertyId else propertyId

}
