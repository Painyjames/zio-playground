package org.cascomio.zioplayground

import scalaz.zio.ZIO

trait UserRepository {

  def get(userId: String): ZIO[Any, Unit, User]
}

final class DefaultUserRepository(users: Seq[User]) extends UserRepository {

  override def get(userId: String): ZIO[Any, Unit, User] =
    ZIO.fromOption(users.find(_.userId == userId))
}

