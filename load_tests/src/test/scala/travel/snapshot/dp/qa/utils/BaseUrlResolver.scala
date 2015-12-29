package travel.snapshot.dp.qa.utils

object LoadTestEnvironment extends Enumeration {
  type LoadTestEnvironment = Value

  val LOCAL = Value("local")
  val PRODUCTION = Value("production")
  val DEVELOPMENT = Value("development")
  val TESTING = Value("testing")
}

object LoadTestContext extends Enumeration {
  type LoadTestContext = LoadTestContextValue

  case class LoadTestContextValue(name: String, localContext: String, developmentContext: String, productionContext: String = "v1") extends Val(name)

  val CONFIGURATION = LoadTestContextInternalValue("configuration", "ConfigurationModule-1.0/api/", "ConfigurationModule-1.0/api/")
  val IDENTITY = LoadTestContextInternalValue("identity", "IdentityModule-1.0/api/", "IdentityModule-1.0/api/")
  val SOCIAL_COMMON = LoadTestContextInternalValue("social.common", "SocialMediaAnalyticsApi-1.0-SNAPSHOT/api/", "SocialMediaAnalyticsApi-1.0/api/")
  val SOCIAL_FACEBOOK = LoadTestContextInternalValue("social.facebook", "FacebookAnalyticsApi-1.0-SNAPSHOT/api/", "FacebookAnalyticsApi-1.0/api/")
  val SOCIAL_TWITTER = LoadTestContextInternalValue("social.twitter", "TwitterAnalyticsApi-1.0-SNAPSHOT/api/", "TwitterAnalyticsApi-1.0/api/")
  val SOCIAL_INSTAGRAM = LoadTestContextInternalValue("social.instagram", "InstagramAnalyticsApi-1.0-SNAPSHOT/api/", "InstagramAnalyticsApi-1.0/api/")

  protected final def LoadTestContextInternalValue(name: String,
                                                   localContext: String,
                                                   developmentContext: String,
                                                   productionContext: String = "v1"): LoadTestContextValue = {
    LoadTestContextValue(name, localContext, developmentContext, productionContext)
  }
}

trait SystemPropertiesGatherer {

  val localEnvironmentProperties = Tuple4[String, String, String, LoadTestContext.LoadTestContextValue](
    System.getProperty("protocol", "http"),
    System.getProperty("host", "localhost"),
    System.getProperty("port", "8080"),
    resolveScenario
  )

  val developmentEnvironmentProperties = Tuple4[String, String, String, LoadTestContext.LoadTestContextValue](
    System.getProperty("protocol", "http"),
    System.getProperty("host", "rg01we-dp-dev-tomcat1.westeurope.cloudapp.azure.com"),
    System.getProperty("port", "8080"),
    resolveScenario
  )

  val testEnvironmentProperties = Tuple4[String, String, String, LoadTestContext.LoadTestContextValue](
    System.getProperty("protocol", "http"),
    System.getProperty("host", "rg01we-dp-test-tomcat1.westeurope.cloudapp.azure.com"),
    System.getProperty("port", "8080"),
    resolveScenario
  )

  val productionEnvironmentProperties = Tuple4[String, String, String, LoadTestContext.LoadTestContextValue](
    System.getProperty("protocol", "https"),
    System.getProperty("host", "de.api.snapshot.technology"),
    System.getProperty("port", "443"),
    resolveScenario
  )

  private def resolveScenario: LoadTestContext.LoadTestContextValue = {
    val gatlingScenario: String = System.getProperty("gatling.simulationClass")

    if (gatlingScenario.contains("configuration")) {
      LoadTestContext.CONFIGURATION
    } else if (gatlingScenario.contains("identity")) {
      LoadTestContext.IDENTITY
    } else if (gatlingScenario.contains("social.common")) {
      LoadTestContext.SOCIAL_COMMON
    } else if (gatlingScenario.contains("social.facebook")) {
      LoadTestContext.SOCIAL_FACEBOOK
    } else if (gatlingScenario.contains("social.instagram")) {
      LoadTestContext.SOCIAL_INSTAGRAM
    } else if (gatlingScenario.contains("social.twitter")) {
      LoadTestContext.SOCIAL_TWITTER
    } else {
      throw new IllegalStateException("Unable to recognize what type of scenario you are going to execute!")
    }
  }
}

object LocalUrlResolver extends SystemPropertiesGatherer {

  def apply(): String = {
    val (protocol, host, port, scenario) = localEnvironmentProperties
    s"$protocol://$host:$port/${scenario.localContext}"
  }
}

object DevelopmentUrlResolver extends SystemPropertiesGatherer {

  def apply(): String = {
    val (protocol, host, port, scenario) = developmentEnvironmentProperties
    s"$protocol://$host:$port/${scenario.developmentContext}"
  }
}

object TestingUrlResolver extends SystemPropertiesGatherer {

  def apply(): String = {
    val (protocol, host, port, scenario) = testEnvironmentProperties
    s"$protocol://$host:$port/${scenario.developmentContext}"
  }
}

object ProductionUrlResolver extends SystemPropertiesGatherer {

  def apply(): String = {
    val (protocol, host, port, scenario) = productionEnvironmentProperties
    s"$protocol://$host:$port/${scenario.productionContext}"
  }
}

object BaseUrlResolver {

  def apply(): String = {
    LoadTestEnvironment.withName(System.getProperty("environment", "local")) match {
      case LoadTestEnvironment.LOCAL => LocalUrlResolver()
      case LoadTestEnvironment.DEVELOPMENT => DevelopmentUrlResolver()
      case LoadTestEnvironment.TESTING => TestingUrlResolver()
      case LoadTestEnvironment.PRODUCTION => ProductionUrlResolver()
    }
  }
}