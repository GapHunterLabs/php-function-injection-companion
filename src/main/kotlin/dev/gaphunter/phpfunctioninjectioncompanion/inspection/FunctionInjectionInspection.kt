package dev.gaphunter.phpfunctioninjectioncompanion.inspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import dev.gaphunter.phpfunctioninjectioncompanion.detect.FunctionInjectionScanner
import dev.gaphunter.phpfunctioninjectioncompanion.review.ReviewPrompt

/**
 * Flags `call_user_func`/`call_user_func_array` calls whose
 * function-name argument directly references a PHP superglobal --
 * see [FunctionInjectionScanner] for the full reasoning. Runs via
 * [checkFile] (whole-file text scan) -- see `build.gradle.kts` for
 * why no PHP-language PSI dependency is taken.
 */
class FunctionInjectionInspection : LocalInspectionTool() {

    companion object {
        const val MAX_FILE_LENGTH = 500_000
        private val PHP_FILE_NAME = Regex("""^[^.]+\.php$""", RegexOption.IGNORE_CASE)
    }

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        val virtualFile = file.virtualFile ?: return null
        if (!PHP_FILE_NAME.matches(virtualFile.name)) return null

        val text = file.text
        if (text.length > MAX_FILE_LENGTH) return null

        val hits = FunctionInjectionScanner.scan(text)
        if (hits.isEmpty()) return null

        val document = file.viewProvider.document ?: return null
        val problems = mutableListOf<ProblemDescriptor>()

        for (hit in hits) {
            if (hit.lineNumber - 1 !in 0 until document.lineCount) continue
            val lineStartOffset = document.getLineStartOffset(hit.lineNumber - 1)
            val absoluteStart = lineStartOffset + hit.columnStart
            val absoluteEnd = lineStartOffset + hit.columnEnd
            val anchor = leafElementAt(file, absoluteStart) ?: continue
            val anchorStart = anchor.textRange.startOffset
            val relativeRange = TextRange(
                (absoluteStart - anchorStart).coerceAtLeast(0),
                (absoluteEnd - anchorStart).coerceAtMost(anchor.textLength),
            )
            if (relativeRange.startOffset >= relativeRange.endOffset) continue

            problems += manager.createProblemDescriptor(
                anchor,
                relativeRange,
                "${hit.callText}(...) calls a function whose name comes directly from a PHP superglobal -- if " +
                    "any part comes from untrusted input, this is Function Injection (an attacker can invoke " +
                    "system/exec/any reachable function). Validate against a strict allowlist of known callable " +
                    "names instead",
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                isOnTheFly,
            )

            ReviewPrompt.recordHit(file.project, "${virtualFile.path}:${hit.lineNumber}")
        }

        return if (problems.isEmpty()) null else problems.toTypedArray()
    }

    private fun leafElementAt(file: PsiFile, startOffset: Int): PsiElement? {
        if (startOffset < 0 || startOffset >= file.textLength) return null
        var element = file.findElementAt(startOffset) ?: return file
        while (element.firstChild != null) {
            element = element.firstChild
        }
        return element
    }
}
