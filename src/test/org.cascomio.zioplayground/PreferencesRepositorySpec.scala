package org.cascomio.zioplayground

import java.util.UUID

import org.scalatest.{Matchers, WordSpec}
import scalaz.zio.DefaultRuntime

class PreferencesRepositorySpec extends WordSpec with Matchers with DefaultRuntime {

  "PreferencesRepository" should {

    "return user's preferences" in {

      val userId = UUID.randomUUID.toString
      val userPreferences = UserPreferences(userId, true)
      val preferencesRepository = new DefaultPreferencesRepository(Seq(userPreferences))

      val result = unsafeRun{ preferencesRepository.get(userId) }

      result shouldBe userPreferences
    }
  }

}
