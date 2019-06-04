package org.cascomio.zioplayground

import java.util.UUID

import org.scalatest.{Matchers, WordSpec}
import scalaz.zio.{DefaultRuntime, Ref, ZIO}

class UserServiceSpec extends WordSpec with Matchers with DefaultRuntime {

  "DefaultUerService" should {

    "return a user by id" in {

      val userId = UUID.randomUUID.toString
      val user = User(userId, "john doe")
      val preferences = UserPreferences(userId, true)
      val getUserResponse = GetUserResponse(user, preferences)
      val userRepository = new DefaultUserRepository(Seq(user))
      val preferencesRepository = new DefaultPreferencesRepository(Seq(preferences))
      val userService = new DefaultUserService(userRepository, preferencesRepository)

      val result = unsafeRun {
        userService.get(userId)
      }

      result shouldBe getUserResponse
    }

    "return a user by id after failures in the user preferences service" in {

      val userId = UUID.randomUUID.toString
      val user = User(userId, "john doe")
      val preferences = UserPreferences(userId, true)
      val getUserResponse = GetUserResponse(user, preferences)
      val userRepository = new DefaultUserRepository(Seq(user))

      val res =
        for {
          c <- Ref.make(0)
          preferencesRepository = new PreferencesRepository {
            private val counter: Ref[Int] = c

            override def get(userId: String): ZIO[Any, Unit, UserPreferences] =
              for {
                i <- counter.update(_ + 1)
                x <- if (i <= 3) ZIO.fail(()) else ZIO.succeed(preferences)
              } yield x
          }
          userService = new DefaultUserService(userRepository, preferencesRepository)
          result <- userService.get(userId)
        } yield result

      unsafeRun{
        res.map(
          _ shouldBe getUserResponse
        )
      }
    }
  }

}
