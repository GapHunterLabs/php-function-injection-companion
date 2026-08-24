package dev.gaphunter.phpfunctioninjectioncompanion.detect

import dev.gaphunter.phpfunctioninjectioncompanion.model.FunctionInjectionHit

/**
 * Plain-text line scanner for a PHP file -- flags `call_user_func(`
 * or `call_user_func_array(` whose function-name argument directly
 * references a PHP superglobal (`$_GET`, `$_POST`, `$_REQUEST`,
 * `$_COOKIE`). This is the textbook Function Injection anti-pattern:
 * OWASP's own guidance documents that "when an attacker passes a
 * function name with user input to call_user_func, it can lead to
 * remote code execution by passing the system function with
 * arbitrary commands" -- an attacker who controls the callable name
 * can invoke any built-in function reachable by that call, including
 * `system`/`exec`/`passthru`.
 *
 * Confirmed real gap: "PHP Inspections (EA Extended)" (one of the
 * most widely used PHP inspection plugins on Marketplace) does not
 * cover function injection detection for `call_user_func`/
 * `call_user_func_array` anywhere in its documented security feature
 * list -- confirmed by reading it before building this.
 *
 * **v0.1 scope, stated honestly:** plain-text regex matching, not
 * real PHP PSI -- only flags a superglobal referenced within the
 * call's own parentheses on the same line. A function name built from
 * an intermediate variable assigned from a superglobal several lines
 * earlier isn't traced (real data-flow analysis, out of scope for a
 * text scanner).
 */
object FunctionInjectionScanner {

    private val DANGEROUS_CALL = Regex("""\b(call_user_func_array|call_user_func)\s*\(""")
    private val SUPERGLOBAL = Regex("\\\$_(GET|POST|REQUEST|COOKIE)\\b")

    fun scan(text: String): List<FunctionInjectionHit> {
        val hits = mutableListOf<FunctionInjectionHit>()
        text.lines().forEachIndexed { index, rawLine ->
            val trimmed = rawLine.trimStart()
            if (trimmed.startsWith("//") || trimmed.startsWith("#") || trimmed.startsWith("*")) return@forEachIndexed

            val callMatch = DANGEROUS_CALL.find(rawLine) ?: return@forEachIndexed
            val afterCall = rawLine.substring(callMatch.range.last + 1)
            if (!SUPERGLOBAL.containsMatchIn(afterCall)) return@forEachIndexed

            hits += FunctionInjectionHit(callMatch.groupValues[1], index + 1, callMatch.range.first, callMatch.range.last + 1)
        }
        return hits
    }
}
