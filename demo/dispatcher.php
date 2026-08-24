<?php
// Demo data for PHP Function Injection Companion — used with
// `./gradlew runIde` to capture the real Marketplace screenshot. Open
// this file, the warning should appear on the call_user_func() line.

function dispatch_action() {
    // Function name built directly from a superglobal -- FLAGGED.
    call_user_func($_GET['action'], $_GET['payload']);
}

function dispatch_action_safely() {
    $allowed = ['refresh_cache', 'send_report', 'ping'];
    $action = $_GET['action'] ?? '';
    if (!in_array($action, $allowed, true)) {
        return;
    }
    // Validated against a strict allowlist first -- NOT flagged
    // (the call_user_func argument here is a local variable, not a
    // superglobal reference).
    call_user_func($action);
}
