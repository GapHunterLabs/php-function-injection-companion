package dev.gaphunter.phpfunctioninjectioncompanion.detect

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FunctionInjectionScannerTest {

    @Test
    fun `flags call_user_func with a GET superglobal function name`() {
        val code = "call_user_func(\$_GET['action'], \$data);"
        val hits = FunctionInjectionScanner.scan(code)
        assertEquals(1, hits.size)
        assertEquals("call_user_func", hits[0].callText)
    }

    @Test
    fun `flags call_user_func_array with a POST superglobal function name`() {
        val code = "call_user_func_array(\$_POST['handler'], \$args);"
        val hits = FunctionInjectionScanner.scan(code)
        assertEquals(1, hits.size)
        assertEquals("call_user_func_array", hits[0].callText)
    }

    @Test
    fun `does not flag call_user_func with a static literal callable`() {
        val code = "call_user_func('strtoupper', \$value);"
        assertTrue(FunctionInjectionScanner.scan(code).isEmpty())
    }

    @Test
    fun `does not flag call_user_func with a local variable callable`() {
        val code = "call_user_func(\$handler, \$data);"
        assertTrue(FunctionInjectionScanner.scan(code).isEmpty())
    }

    @Test
    fun `does not flag a commented-out line`() {
        val code = "// call_user_func(\$_GET['action']);"
        assertTrue(FunctionInjectionScanner.scan(code).isEmpty())
    }
}
