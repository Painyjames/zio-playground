package org.cascomio.zioplayground

import java.util.UUID

import com.codahale.metrics.MetricRegistry
import org.coursera.metrics.datadog.DatadogReporter
import org.coursera.metrics.datadog.transport.HttpTransport
import scalaz.zio.{DefaultRuntime, ZIO}

object Main extends App with DefaultRuntime {

  val userPreferences = (1 to 1000).map{i =>
    val userId = UUID.randomUUID.toString
    val user = User(userId, s"john doe $userId")
    val preferences = UserPreferences(userId, math.random > 0.5)
    GetUserResponse(user, preferences)
  }
  val httpTransport = new HttpTransport.Builder().withApiKey("c34dc09b2d2bce85d08c65dbd5331d0c").build()
  val registry = new MetricRegistry
  val reporter = DatadogReporter.forRegistry(registry)
    .withTransport(httpTransport)
    .build()
  val userRepository = new DefaultUserRepository(userPreferences.filterNot(_.user.userId.startsWith("1")).map(_.user))
  val preferencesRepository = new DefaultPreferencesRepository(userPreferences.filterNot(_.user.userId.startsWith("4")).map(_.preferences))
  val userService = new DefaultUserService(userRepository, preferencesRepository, registry)

  val result = {
    userPreferences.map(up => unsafeRun(userService.get(up.user.userId).catchAll(_ => ZIO.succeed())))
  }

  println(result)
  println(result.collect{ case u: GetUserResponse => u }.partition(_.preferences.emailAlerts) match {
    case (t, f) => {
      val tru = t.size.toDouble
      val fal = f.size.toDouble
      val fail = (result.size - tru - fal)
      s"""
         |${tru / result.size}: true ${fal / result.size}: false failed: ${fail / result.size}
         |""".stripMargin
    }
  })
}
