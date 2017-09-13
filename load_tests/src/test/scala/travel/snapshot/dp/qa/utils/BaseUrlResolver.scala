package travel.snapshot.dp.qa.utils

import travel.snapshot.dp.qa.utils.StagingUrlResolver.{host, protocol}

object LoadTestEnvironment extends Enumeration {
  type LoadTestEnvironment = Value

  val LOCAL = Value("local")
  val PRODUCTION = Value("production")
  val DEVELOPMENT = Value("development")
  val TESTING = Value("test")
  val DOCKER = Value("docker")
  val NGINX = Value("nginx")
  val VM12 = Value("vm12")
  val STABLE = Value("stable")
  val STAGING = Value("staging")
}

object LoadTestContext extends Enumeration {
  type LoadTestContext = LoadTestContextValue

  case class LoadTestContextValue(name: String, localContext: String, developmentContext: String, productionContext: String = "v1") extends Val(name)

  val CONFIGURATION = LoadTestContextInternalValue("configuration", "ConfigurationModule-1.0/api/", "ConfigurationModule-1.0/api/")
  val IDENTITY = LoadTestContextInternalValue("identity", "api/", "api/")
  val SOCIAL_COMMON = LoadTestContextInternalValue("social.common", "SocialMediaAnalyticsApi-1.0-SNAPSHOT/api/", "SocialMediaAnalyticsApi-1.0/api/")
  val SOCIAL_FACEBOOK = LoadTestContextInternalValue("social.facebook", "FacebookAnalyticsApi-1.0-SNAPSHOT/api/", "FacebookAnalyticsApi-1.0/api/")
  val SOCIAL_TWITTER = LoadTestContextInternalValue("social.twitter", "TwitterAnalyticsApi-1.0-SNAPSHOT/api/", "TwitterAnalyticsApi-1.0/api/")
  val SOCIAL_INSTAGRAM = LoadTestContextInternalValue("social.instagram", "InstagramAnalyticsApi-1.0-SNAPSHOT/api/", "InstagramAnalyticsApi-1.0/api/")
  val WEB_PERFORMANCE = LoadTestContextInternalValue("performance", "WebPerformance-1.0/api/", "WebPerformance-1.0/api/")
  val RATE_SHOPPER = LoadTestContextInternalValue("rateshopper", "RateShopper-SA/api/", "RateShopper-SA/api/")
  val TRIP_ADVISOR = LoadTestContextInternalValue("tripadvisor", "Review-1.0/api/", "Review-1.0/api/")
  val OAUTH = LoadTestContextInternalValue("oauth", "oauth/", "oauth/", null)

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

  val testEnvironmentProperties = Tuple5[String, String, String, String, LoadTestContext.LoadTestContextValue](
    System.getProperty("protocol", "https"),
    System.getProperty("host", "europewest-sso-test1.snapshot.technology"),
    System.getProperty("port", "8080"),
    System.getProperty("version", "v1"),
    resolveScenario
  )

  val productionEnvironmentProperties = Tuple5[String, String, String, String, LoadTestContext.LoadTestContextValue](
    System.getProperty("protocol", "https"),
    System.getProperty("host", "euf-api.snapshot.technology"),
    System.getProperty("port", "443"),
    System.getProperty("version", "v1"),
    resolveScenario
  )

  val dockerMachineEnvironmentProperties = Tuple4[String, String, String, LoadTestContext.LoadTestContextValue](
    System.getProperty("protocol", "http"),
    System.getProperty("host", "192.168.99.100"),
    System.getProperty("port", "8080"),
    resolveScenario
  )

  val nginxEnvironmentProperties = Tuple4[String, String, String, LoadTestContext.LoadTestContextValue](
    System.getProperty("protocol", "http"),
    System.getProperty("host", "192.168.99.100"),
    System.getProperty("port", "8899"),
    resolveScenario
  )

  val VM12EnvironmentProperties = Tuple4[String, String, String, LoadTestContext.LoadTestContextValue](
    System.getProperty("protocol", "http"),
    System.getProperty("host", "192.168.1.127"),
    System.getProperty("port", "8080"),
    resolveScenario
  )

  val StableEnvironmentProperties = Tuple5[String, String, String, String, LoadTestContext.LoadTestContextValue](
    System.getProperty("protocol", "https"),
    System.getProperty("host", "europewest-api-stable.snapshot.technology"),
    System.getProperty("port", "8080"),
    System.getProperty("version", "v1"),
    resolveScenario
  )
  val StagingEnvironmentProperties = Tuple5[String, String, String, String, LoadTestContext.LoadTestContextValue](
    System.getProperty("protocol", "https"),
    System.getProperty("host", "europewest-api-staging.snapshot.technology"),
    System.getProperty("version", "v1"),
    System.getProperty("port", "8080"),
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
    } else if (gatlingScenario.contains("performance")) {
      LoadTestContext.WEB_PERFORMANCE
    } else if (gatlingScenario.contains("rateshopper")) {
      LoadTestContext.RATE_SHOPPER
    } else if (gatlingScenario.contains("tripadvisor")) {
      LoadTestContext.TRIP_ADVISOR
    } else if (gatlingScenario.contains("oauth")) {
      LoadTestContext.OAUTH
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

  val (protocol, host, port, scenario) = developmentEnvironmentProperties

  def apply(): String = {
    s"$protocol://$host:$port/${scenario.developmentContext}"
  }

  def resolveOauthUrl(): String = {
    s"$protocol://$host/oauth/token/"
  }
}

object TestingUrlResolver extends SystemPropertiesGatherer {

  val (protocol, host, port, version, scenario) = testEnvironmentProperties

  def apply(): String = {
    s"$protocol://$host/$version/"
  }

  def resolveOauthUrl(): String = {
    s"$protocol://$host/oauth/token/"
  }
}

object ProductionUrlResolver extends SystemPropertiesGatherer {

  val (protocol, host, port, version, scenario) = productionEnvironmentProperties

  def apply(): String = {
//    s"$protocol://$host:$port/$version/${scenario.localContext}"
    s"$protocol://$host/$version"
  }

  def resolveOauthUrl(): String = {
    s"$protocol://$host/oauth/token/"
  }
}

object DockerUrlResolver extends SystemPropertiesGatherer {

  def apply(): String = {
    val (protocol, host, port, scenario) = dockerMachineEnvironmentProperties
    s"$protocol://$host:$port/${scenario.localContext}"
  }
}

object NginxUrlResolver extends SystemPropertiesGatherer {

  def apply(): String = {
    val (protocol, host, port, scenario) = nginxEnvironmentProperties
    s"$protocol://$host:$port/${scenario.localContext}"
  }
}

object VM12UrlResolver extends SystemPropertiesGatherer {

  def apply(): String = {
    val (protocol, host, port, scenario) = VM12EnvironmentProperties
    s"$protocol://$host:$port/${scenario.localContext}"
  }
}

object StableUrlResolver extends SystemPropertiesGatherer {

  val (protocol, host, port, version, scenario) = StableEnvironmentProperties

  def apply(): String = {
    s"$protocol://$host/$version/"
  }

  def resolveOauthUrl(): String = {
    s"$protocol://$host/oauth/token/"
  }
}

object StagingUrlResolver extends SystemPropertiesGatherer {

  val (protocol, host, version, port, scenario) = StagingEnvironmentProperties

  def apply(): String = {
    s"$protocol://$host/$version/"
  }

  def resolveOauthUrl(): String = {
    s"$protocol://$host/oauth/token/"
  }
}

object BaseUrlResolver {

  def apply(): String = {
    LoadTestEnvironment.withName(System.getProperty("environment", "local")) match {
      case LoadTestEnvironment.LOCAL => LocalUrlResolver()
      case LoadTestEnvironment.DEVELOPMENT => DevelopmentUrlResolver()
      case LoadTestEnvironment.TESTING => TestingUrlResolver()
      case LoadTestEnvironment.PRODUCTION => ProductionUrlResolver()
      case LoadTestEnvironment.DOCKER => DockerUrlResolver()
      case LoadTestEnvironment.NGINX => NginxUrlResolver()
      case LoadTestEnvironment.VM12 => VM12UrlResolver()
      case LoadTestEnvironment.STABLE => StableUrlResolver()
      case LoadTestEnvironment.STAGING => StagingUrlResolver()
    }
  }
}

object OAuthUrlResolver {

  def apply(): String = {
    LoadTestEnvironment.withName(System.getProperty("environment", "local")) match {
      case LoadTestEnvironment.DEVELOPMENT => DevelopmentUrlResolver.resolveOauthUrl()
      case LoadTestEnvironment.TESTING => TestingUrlResolver.resolveOauthUrl()
      case LoadTestEnvironment.STABLE => StableUrlResolver.resolveOauthUrl()
      case LoadTestEnvironment.STAGING => StagingUrlResolver.resolveOauthUrl()
//      case LoadTestEnvironment.PRODUCTION => ProductionUrlResolver().resolveOauthUrl()
      case LoadTestEnvironment.LOCAL => "no_oauth"
      case LoadTestEnvironment.VM12 => "no_oauth"
    }
  }
}
