package org.jetbrains.plugins.scala.lang.psi.controlFlow.cfg

import org.jetbrains.plugins.scala.dfa.DfEntity
import org.jetbrains.plugins.scala.lang.psi.controlFlow.AbstractInstructionVisitor

/**
 * Pushes an entity to the stack
 *
 * @param entity to push to the stack
 */
class Push private[controlFlow](entity: DfEntity) extends Instruction {
  override def pushCount: Int = 1
  override def asmString: String = s"push $entity"
  override def info: Instruction.Info = Push
  override def accept(visitor: AbstractInstructionVisitor): Unit = visitor.visitPush(this)
}

object Push extends Instruction.Info(name = "Push")