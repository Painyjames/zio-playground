package org.cascomio.zioplayground

import scalaz.zio.ZIO

trait PreferencesRepository {

  def get(userId: String): ZIO[Any, Unit, UserPreferences]
}

final class DefaultPreferencesRepository(preferences: Seq[UserPreferences]) extends PreferencesRepository {
  override def get(userId: String): ZIO[Any, Unit, UserPreferences] =
    ZIO.fromOption(preferences.find(_.userId == userId))
}