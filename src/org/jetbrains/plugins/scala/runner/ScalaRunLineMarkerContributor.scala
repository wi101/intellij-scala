package org.jetbrains.plugins.scala.runner

import com.intellij.execution.application.ApplicationConfigurationType
import com.intellij.execution.lineMarker.RunLineMarkerContributor.Info
import com.intellij.execution.lineMarker.{ExecutorAction, RunLineMarkerContributor}
import com.intellij.psi.{PsiClass, PsiElement}
import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import org.jetbrains.plugins.scala.lang.psi.api.statements.ScFunctionDefinition
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.ScObject

/**
 * User: Dmitry.Naydanov
 * Date: 23.10.15.
 */
class ScalaRunLineMarkerContributor extends RunLineMarkerContributor {
  override def getInfo(element: PsiElement): Info = {
    val isIdentifier = element.getNode.getElementType == ScalaTokenTypes.tIDENTIFIER
    val hasMain = element.getParent match {
      case fun: ScFunctionDefinition => ScalaMainMethodUtil.isMainMethod(fun)
      case obj: ScObject if ScalaMainMethodUtil.hasMainMethod(obj) => true
      case c: PsiClass => ScalaMainMethodUtil.hasMainMethodFromProviders(c)
      case _ => false
    }
    if (isIdentifier && hasMain)
      new Info(ApplicationConfigurationType.getInstance.getIcon, null, ExecutorAction.getActions(0): _*)
    else null
  }
}