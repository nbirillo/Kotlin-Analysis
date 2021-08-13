package org.jetbrains.research.ml.kotlinAnalysis.psi.extentions

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex

/** Finds [PsiFile] in module by given name. */
fun Module.findPsiFileByName(name: String): PsiFile? {
    return FilenameIndex.getFilesByName(project, name, moduleContentScope).firstOrNull()
}
