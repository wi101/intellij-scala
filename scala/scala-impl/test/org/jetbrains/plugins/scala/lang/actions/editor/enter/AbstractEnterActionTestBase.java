/*
 * Copyright 2000-2008 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.scala.lang.actions.editor.enter;

import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.plugins.scala.lang.actions.ActionTestBase;

import static com.intellij.testFramework.EditorTestUtil.CARET_TAG;
import static org.jetbrains.plugins.scala.util.TestUtils.removeCaretMarker;


abstract public class AbstractEnterActionTestBase extends ActionTestBase {

    protected Editor myEditor;
    protected FileEditorManager fileEditorManager;
    protected String newDocumentText;
    protected PsiFile myFile;

    protected AbstractEnterActionTestBase(String... pathSegments) {
        super(pathSegments);
    }

    @Override
    protected void withSettings(CommonCodeStyleSettings settings) {
        super.withSettings(settings);
        CommonCodeStyleSettings.IndentOptions indentOptions = settings.getIndentOptions();
        indentOptions.INDENT_SIZE = 2;
        indentOptions.CONTINUATION_INDENT_SIZE = 2;
        indentOptions.TAB_SIZE = 2;
        settings.INDENT_CASE_FROM_SWITCH = true;
    }


    private String processFile(final PsiFile file) throws IncorrectOperationException, InvalidDataException {
        String result;
        String fileText = file.getText();
        int offset = fileText.indexOf(CARET_TAG);
        fileText = removeCaretMarker(fileText, offset);
        myFile = createScalaFileFromText(fileText);
        fileEditorManager = FileEditorManager.getInstance(myProject);
        myEditor = fileEditorManager.openTextEditor(new OpenFileDescriptor(myProject, myFile.getVirtualFile(), 0), false);
        assert myEditor != null;
        myEditor.getCaretModel().moveToOffset(offset);

        final myDataContext dataContext = getDataContext(myFile);
        final EditorActionHandler handler = EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_ENTER);

        try {
            performAction(myProject, new Runnable() {
                public void run() {
                    handler.execute(myEditor, myEditor.getCaretModel().getCurrentCaret(), dataContext);
                }
            });

            offset = myEditor.getCaretModel().getOffset();
            result = myEditor.getDocument().getText();
            result = result.substring(0, offset) + CARET_TAG + result.substring(offset);
        } finally {
            fileEditorManager.closeFile(myFile.getVirtualFile());
            myEditor = null;
        }

        return result;
    }

    public String transform(String testName, String[] data) {
        super.transform(testName, data);

        PsiFile psiFile = createScalaFileFrom(data);
        return processFile(psiFile);
    }
}
