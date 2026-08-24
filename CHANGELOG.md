<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# PHP Function Injection Companion Changelog

## [Unreleased]

## [0.1.0]

### Added

- Warning on `call_user_func`/`call_user_func_array` whose function
  name is built directly from a PHP superglobal -- Function
  Injection, not covered by "PHP Inspections (EA Extended)".
- 100% static text analysis, no PHP plugin dependency, no network
  calls, no telemetry. Free.

[Unreleased]: https://github.com/GapHunterLabs/php-function-injection-companion/compare/0.1.0...HEAD
[0.1.0]: https://github.com/GapHunterLabs/php-function-injection-companion/commits/0.1.0
