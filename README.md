# PHP Function Injection Companion

Warning on `call_user_func(` or `call_user_func_array(` whose
function-name argument directly references a PHP superglobal
(`$_GET`, `$_POST`, `$_REQUEST`, `$_COOKIE`). This is the textbook
Function Injection anti-pattern: OWASP's own guidance documents that
"when an attacker passes a function name with user input to
call_user_func, it can lead to remote code execution by passing the
system function with arbitrary commands" — an attacker who controls
the callable name can invoke any built-in function reachable by that
call.

Confirmed real gap: "PHP Inspections (EA Extended)" (one of the most
widely used PHP inspection plugins on Marketplace) does not cover
function injection detection anywhere in its documented security
feature list — confirmed by reading it before building this.

## Why it exists

```php
call_user_func($_GET['action'], $data);
```

compiles and runs fine — until someone requests `?action=system` with
the right follow-up parameters, at which point it's a full remote
code execution vulnerability.

## Why built this way

- **100% static text analysis** — a regex-based line scanner, not a
  real PHP parser, so it works whether the PHP plugin is installed or
  not.

## v0.1 scope — stated honestly, not exhaustively

Only flags a superglobal referenced within the call's own parentheses
on the same line. A function name built from an intermediate variable
assigned from a superglobal several lines earlier isn't traced (real
data-flow analysis, out of scope for a text scanner).

## Usage

Open any `.php` file. A `call_user_func`/`call_user_func_array` call
whose function name is built from a superglobal shows a warning.

## Enterprise / Team Licensing

Need enterprise features, custom rules, or team licensing? Contact us at
**gaphunterlabs@gmail.com**.

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
