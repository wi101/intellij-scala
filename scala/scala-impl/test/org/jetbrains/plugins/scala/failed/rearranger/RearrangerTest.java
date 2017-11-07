package org.jetbrains.plugins.scala.failed.rearranger;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.arrangement.engine.ArrangementEngine;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtilRt;
import junit.framework.Test;
import org.jetbrains.plugins.scala.testcases.ScalaFileSetTestCase;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

/**
 * @author Roman.Shein
 * Date: 26.07.13
 */
@RunWith(AllTests.class)
public abstract class RearrangerTest extends ScalaFileSetTestCase {

  public RearrangerTest() {
    super("rearranger", "failedData");
  }

  @Override
  public String transform(String testName, String[] data) {
    super.transform(testName, data);

    PsiFile file = createScalaFileFrom(data);
    CommandProcessor.getInstance().executeCommand(myProject, new Runnable() {
      public void run() {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
          public void run() {
            try {
              rearrange(file);
            } catch (IncorrectOperationException e) {
              e.printStackTrace();
            }
          }
        });
      }
    }, null, null);
    return file.getText();
  }

  private void rearrange(PsiFile file) {
    final ArrangementEngine engine = ServiceManager.getService(myProject, ArrangementEngine.class);
    engine.arrange(file, ContainerUtilRt.newArrayList(file.getTextRange()));
    PsiDocumentManager documentManager = PsiDocumentManager.getInstance(file.getProject());
    Document document = documentManager.getDocument(file);
    if (document != null) {
      documentManager.commitDocument(document);
    } else {
      throw new IllegalArgumentException("Wrong PsiFile type provided: the file has no document.");
    }
  }

  public static Test suite() {
    return new ScalaFailedRearrangerTest();
  }
}