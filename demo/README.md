# Demo data — PHP Function Injection Companion

For capturing the real Marketplace screenshot:

1. `./gradlew runIde`
2. Open `demo/dispatcher.php` as a scratch/standalone file (or drop it
   into any sandbox project) inside the sandbox IDE.
3. The `call_user_func($_GET['action'], $_GET['payload'])` call inside
   `dispatch_action` shows the warning — hover it for the tooltip.
   `dispatch_action_safely`'s allowlist-validated local variable stays
   clean, for contrast.
4. Enter Full Screen (`View > Appearance > Enter Full Screen`), capture
   with `Win+Shift+S`, save directly to `docs/screenshots/` in this
   repo.
