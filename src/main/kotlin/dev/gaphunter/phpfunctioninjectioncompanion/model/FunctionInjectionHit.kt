package dev.gaphunter.phpfunctioninjectioncompanion.model

data class FunctionInjectionHit(val callText: String, val lineNumber: Int, val columnStart: Int, val columnEnd: Int)
