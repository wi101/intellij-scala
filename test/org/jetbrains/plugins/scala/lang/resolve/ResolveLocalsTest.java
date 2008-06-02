package org.jetbrains.plugins.scala.lang.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.plugins.scala.lang.psi.api.base.patterns.ScReferencePattern;
import org.jetbrains.plugins.scala.util.TestUtils;

/**
 * @author ilyas
 */
public class ResolveLocalsTest extends ScalaResolveTestCase{

  protected String getTestDataPath() {
    return TestUtils.getTestDataPath() + "/resolve/local/";
  }

  public void testValueDef() throws Exception {
    PsiReference ref = configureByFile("local1/local1.scala");
    PsiElement resolved = ref.resolve();
    assertTrue(resolved instanceof ScReferencePattern);
    assertEquals(((ScReferencePattern) resolved).name(), "aaa");
  }
}
