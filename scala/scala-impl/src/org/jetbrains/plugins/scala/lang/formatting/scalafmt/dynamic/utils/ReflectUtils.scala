package org.jetbrains.plugins.scala.lang.formatting.scalafmt.dynamic.utils

import scala.reflect.ClassTag

object ReflectUtils {

  implicit class ObjectReflectOps[T](private val target: T) extends AnyVal {
    def invokeAs[R](methodName: String, args: (Class[_], Object)*): R = {
      invoke(methodName, args: _*).asInstanceOf[R]
    }

    def invoke(methodName: String, args: (Class[_], Object)*): Object = {
      val clazz = target.getClass
      val method = clazz.getMethod(methodName, args.map(_._1): _*)
      method.invoke(target, args.map(_._2): _*)
    }

    def asParam(implicit classTag: ClassTag[T]): (Class[_], T) = {
      (classTag.runtimeClass, target)
    }
  }

  implicit class ClassReflectOps(private val clazz: Class[_]) extends AnyVal {
    def invokeStaticAs[T](methodName: String, args: (Class[_], Object)*): T = {
      invokeStatic(methodName, args: _*).asInstanceOf[T]
    }

    def invokeStatic(methodName: String, args: (Class[_], Object)*): Object = {
      val method = clazz.getMethod(methodName, args.map(_._1): _*)
      method.invoke(null, args.map(_._2): _*)
    }
  }

}
