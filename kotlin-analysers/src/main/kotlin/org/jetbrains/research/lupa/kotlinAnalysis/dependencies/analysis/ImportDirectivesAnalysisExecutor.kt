package org.jetbrains.research.lupa.kotlinAnalysis.dependencies.analysis

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty
import org.jetbrains.research.lupa.kotlinAnalysis.AnalysisExecutor
import org.jetbrains.research.lupa.kotlinAnalysis.ExecutorHelper
import org.jetbrains.research.lupa.kotlinAnalysis.PrintWriterResourceManager
import org.jetbrains.research.lupa.kotlinAnalysis.ResourceManager
import org.jetbrains.research.lupa.kotlinAnalysis.psi.extentions.extractKtElementsOfType
import java.nio.file.Path

/**
 * Executor for import directives analysis which collects full qualified names of all import directives in projects
 * and stores them to file in [output directory][outputDir].
 */
class ImportDirectivesAnalysisExecutor(
    outputDir: Path,
    executorHelper: ExecutorHelper? = null,
    filename: String = "import_directives_data.csv"
) :
    AnalysisExecutor(executorHelper) {

    private val dependenciesDataWriter = PrintWriterResourceManager(
        outputDir, filename,
        header = listOf("project_name", "import").joinToString(separator = ",")
    )

    override val controlledResourceManagers: Set<ResourceManager> = setOf(dependenciesDataWriter)

    override fun analyse(project: Project) {
        val packageDirectives = project.extractKtElementsOfType(KtPackageDirective::class.java)
            .filter { !it.isRoot }
        val projectPackageFqNames = packageDirectives.map { PackageDirectivePsiAnalyzer.analyze(it) }.toSet()
        val importDirectives = project.extractKtElementsOfType(KtImportDirective::class.java)
        val results = importDirectives
            .map { ImportDirectivePsiAnalyzer.analyze(it) }
            .filter { importDirective -> !projectPackageFqNames.any { importDirective.startsWith(it) } }
        results.ifNotEmpty {
            dependenciesDataWriter.writer.println(joinToString(separator = System.getProperty("line.separator")) {
                listOf(project.name, it).joinToString(separator = ",")
            })
        }
    }
}
