package org.cascomio.zioplayground

import java.util.UUID

import org.scalatest.{Matchers, WordSpec}
import scalaz.zio.DefaultRuntime

class UserRepositorySpec extends WordSpec with Matchers with DefaultRuntime {

  "UserRepository" should {

    "return a user by id" in {

      val userId = UUID.randomUUID.toString
      val user = User(userId, "john doe")
      val users = Seq(user)
      val userRepository = new DefaultUserRepository(users)

      val result = unsafeRun { userRepository.get(userId) }

      result shouldBe user
    }
  }

}
