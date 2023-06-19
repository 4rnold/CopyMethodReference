package com.github.arnold.copymethodreference;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class CopyMethodReference extends AnAction {

    private PsiMethod findMethod(@NotNull PsiElement element) {
        return element instanceof PsiMethod ? (PsiMethod)element : null;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {

        // TODO: insert action logic here
        PsiElement element = (PsiElement)event.getDataContext().getData("psi.Element");
        Editor editor = (Editor)event.getDataContext().getData("editor");
        PsiFile file = (PsiFile)event.getDataContext().getData("psi.File");
        Project project = (Project)event.getDataContext().getData("project");
        if (element != null) {
            PsiMethod method = this.findMethod(element);
            if (method != null) {
                PsiClass containingClass = method.getContainingClass();
                if (containingClass != null && containingClass.getName() != null) {
                    String className = containingClass.getName();
                    String methodName = method.getName();
                    PsiType returnType = method.getReturnType();

                    String returnTypeText = "";
                    if (returnType != null) {
                        returnTypeText = returnType.getPresentableText() + " ";
                    }
                    String parameterTypeNames = this.concatenateParameterTypes(method);
                    String classDotMethod = returnTypeText + className + "#" + methodName + "(" + parameterTypeNames + ")";
                    //LOG.debug("Copying Class.Method: " + classDotMethod);
                    CopyPasteManager.getInstance().setContents(new StringSelection(classDotMethod));
//                    if (editor != null && file != null && project != null) {
//                        this.highlightIdentifier(editor, file, project);
//                    }

                    WindowManager windowManager = WindowManager.getInstance();
                    StatusBar statusBar = windowManager.getStatusBar(project);
                    statusBar.setInfo(MessageFormat.format("Method header ''{0}'' has been copied", classDotMethod));
                }
            }
        }
    }

    private String concatenateParameterTypes(PsiMethod inMethod) {
        List<String> parameterTypeNamesWithParemeterName = new ArrayList();
        PsiParameter[] parameters = inMethod.getParameterList().getParameters();
        PsiParameter[] arr$ = parameters;
        int len$ = parameters.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            PsiParameter parameter = arr$[i$];
            PsiType parameterType = parameter.getType();
            String parameterTypeName = parameterType.getPresentableText();
            String parameterName = parameter.getName();
            parameterTypeNamesWithParemeterName.add(parameterTypeName + " " + parameterName);
        }

        return StringUtil.join(parameterTypeNamesWithParemeterName, ", ");
    }
}
