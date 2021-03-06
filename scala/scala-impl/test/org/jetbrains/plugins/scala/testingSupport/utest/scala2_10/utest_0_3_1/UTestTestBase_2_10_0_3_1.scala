package org.jetbrains.plugins.scala
package testingSupport
package utest
package scala2_10.utest_0_3_1

import org.jetbrains.plugins.scala.DependencyManagerBase._
import org.jetbrains.plugins.scala.base.libraryLoaders.{IvyManagedLoader, LibraryLoader}

abstract class UTestTestBase_2_10_0_3_1 extends UTestTestCase {

  override protected def supportedIn(version: ScalaVersion): Boolean = version == Scala_2_10

  override protected def additionalLibraries: Seq[LibraryLoader] = IvyManagedLoader(
      "com.lihaoyi"     %% "utest"        % "0.3.1",
      "org.scalamacros" %% "quasiquotes"  % "2.0.0"
    ) :: Nil
}
