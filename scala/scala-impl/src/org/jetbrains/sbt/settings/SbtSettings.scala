package org.jetbrains.sbt
package settings

import java.util

import com.intellij.openapi.components._
import com.intellij.openapi.externalSystem.settings.{AbstractExternalSystemSettings, ExternalSystemSettingsListener}
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiElement
import com.intellij.util.containers.ContainerUtilRt
import com.intellij.util.xmlb.annotations.XCollection
import org.jetbrains.annotations.NotNull
import org.jetbrains.sbt.project.settings.{SbtProjectSettings, SbtProjectSettingsListener, SbtProjectSettingsListenerAdapter, SbtTopic}
import org.jetbrains.sbt.settings.SbtSettings.defaultMaxHeapSize

import scala.beans.BeanProperty

/**
 * @author Pavel Fatin
 */

@State(
  name = "ScalaSbtSettings",
  storages = Array(new Storage("sbt.xml"))
)
class SbtSettings(project: Project)
  extends AbstractExternalSystemSettings[SbtSettings, SbtProjectSettings, SbtProjectSettingsListener](SbtTopic, project)
  with PersistentStateComponent[SbtSettings.State] {


  @BeanProperty var customLauncherEnabled: Boolean = false
  @BeanProperty var customLauncherPath: String = ""
  @BeanProperty var maximumHeapSize: String = defaultMaxHeapSize
  @BeanProperty var vmParameters: String = ""
  @BeanProperty var customVMEnabled: Boolean = false
  @BeanProperty var customVMPath: String = ""
  @BeanProperty var customSbtStructurePath: String = ""

  override def getState: SbtSettings.State = {
    val state = new SbtSettings.State
    fillState(state)

    state.customLauncherEnabled = customLauncherEnabled
    state.customLauncherPath = customLauncherPath
    state.maximumHeapSize = maximumHeapSize
    state.vmParameters = vmParameters
    state.customVMEnabled = customVMEnabled
    state.customVMPath = customVMPath
    state.customSbtStructurePath = customSbtStructurePath

    state
  }

  override def loadState(state: SbtSettings.State): Unit = {
    super[AbstractExternalSystemSettings].loadState(state)

    customLauncherEnabled = state.customLauncherEnabled
    customLauncherPath = state.customLauncherPath
    maximumHeapSize = state.maximumHeapSize
    vmParameters = state.vmParameters
    customVMEnabled = state.customVMEnabled
    customVMPath = state.customVMPath
    customSbtStructurePath = state.customSbtStructurePath
  }

  override def subscribe(listener: ExternalSystemSettingsListener[SbtProjectSettings]) {
    val adapter = new SbtProjectSettingsListenerAdapter(listener)
    getProject.getMessageBus.connect(getProject).subscribe(SbtTopic, adapter)
  }

  override def copyExtraSettingsFrom(settings: SbtSettings): Unit = {
    customLauncherEnabled = settings.customLauncherEnabled
    customLauncherPath = settings.customLauncherPath
    maximumHeapSize = settings.maximumHeapSize
    vmParameters = settings.vmParameters
    customVMEnabled = settings.customVMEnabled
    customVMPath = settings.customVMPath
    customSbtStructurePath = settings.customSbtStructurePath
  }

  def getLinkedProjectSettings(module: Module): Option[SbtProjectSettings] =
    Option(ExternalSystemApiUtil.getExternalRootProjectPath(module)).safeMap(getLinkedProjectSettings)

  def getLinkedProjectSettings(element: PsiElement): Option[SbtProjectSettings] =
    for {
      virtualFile <- Option(element.getContainingFile).safeMap(_.getVirtualFile)
      projectFileIndex = ProjectRootManager.getInstance(element.getProject).getFileIndex
      module <- Option(projectFileIndex.getModuleForFile(virtualFile))
      if project == element.getProject
      projectSettings <- getLinkedProjectSettings(module)
    } yield projectSettings

  override def getLinkedProjectSettings(linkedProjectPath: String): SbtProjectSettings =
    Option(super.getLinkedProjectSettings(linkedProjectPath))
      .getOrElse(super.getLinkedProjectSettings(ExternalSystemApiUtil.normalizePath(linkedProjectPath)))

  override def checkSettings(old: SbtProjectSettings, current: SbtProjectSettings): Unit = {}
}

object SbtSettings {
  def getInstance(@NotNull project: Project): SbtSettings = ServiceManager.getService(project, classOf[SbtSettings])

  val defaultMaxHeapSize: String = ""
  val hiddenDefaultMaxHeapSize: JvmMemorySize = JvmMemorySize.Megabytes(1536)

  class State extends AbstractExternalSystemSettings.State[SbtProjectSettings] {

    @BeanProperty
    var customLauncherEnabled: Boolean = false

    @BeanProperty
    var customLauncherPath: String = ""

    @BeanProperty
    var maximumHeapSize: String = defaultMaxHeapSize

    @BeanProperty
    var vmParameters: String = ""

    @BeanProperty
    var customVMEnabled: Boolean = false

    @BeanProperty
    var customVMPath: String = ""

    @BeanProperty
    var customSbtStructurePath: String = ""

    private val linkedProjectSettings: util.TreeSet[SbtProjectSettings] = ContainerUtilRt.newTreeSet[SbtProjectSettings]()

    @XCollection(style = XCollection.Style.v1, elementTypes = Array(classOf[SbtProjectSettings]))
    override def getLinkedExternalProjectsSettings: util.Set[SbtProjectSettings] =
      linkedProjectSettings

    override def setLinkedExternalProjectsSettings(settings: util.Set[SbtProjectSettings]): Unit = {
      linkedProjectSettings.addAll(settings)
    }
  }
}
