package org.cascomio.zioplayground

import com.codahale.metrics.MetricRegistry
import scalaz.zio.clock.Clock
import scalaz.zio.{Schedule, ZIO}

trait UserService {

  def get(userId: String): ZIO[Any with Clock, Any, GetUserResponse]
}

final class DefaultUserService(userRepository: UserRepository, preferencesRepository: PreferencesRepository, registry: MetricRegistry = new MetricRegistry) extends UserService {

  private val userCounter = registry.counter("user error")
  private val prefCounter = registry.counter("pref error")
  override def get(userId: String): ZIO[Any with Clock, Any, GetUserResponse] =
    (userRepository.get(userId).bimap(e => { userCounter.inc; e }, identity).retry(Schedule.recurs(3))
      <*> preferencesRepository.get(userId).bimap(e => { prefCounter.inc; e }, identity).retry(Schedule.recurs(3)))
      .map { case (user, preferences) =>
        println(user)
        println(preferences)
        GetUserResponse(user, preferences)
      }
}
