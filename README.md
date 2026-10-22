# test-appian

UI test automation for an Appian application, built on Appian's own
[Appian Selenium API](https://gitlab.com/appian-oss/appian-selenium-api) (an
Appian-maintained wrapper around Selenium that understands Appian-specific
behavior like SAIL's async progress bar), with Selenium + TestNG + Extent
Reports around it. Target: Appian Cloud, Sites interface.

Structure and conventions in this project are adapted from a public
reference implementation of the same stack:
[arjityadavv/APPIAN-SNAP-TEST-AUTOMATION](https://github.com/arjityadavv/APPIAN-SNAP-TEST-AUTOMATION).
(That reference used an older, AppMarket-distributed jar version-stamped to
match an Appian release, e.g. `24.2`. The current GitLab OSS releases are
date-stamped instead, e.g. `20260910` — see the setup step below.)

## Project layout

```
pre-requisites/                      one-time local setup (Appian Selenium API jar)
src/main/java/com/testappian/
  config/ConfigReader.java           loads config.properties + credentials
  driver/DriverFactory.java          builds WebDriver + Appian SitesFixture
  reporting/                         Extent Reports + screenshot-on-failure
src/test/java/com/testappian/
  base/BaseTest.java                 @BeforeMethod/@AfterMethod setup/teardown
  pages/                             Page Object Model classes
  tests/                             TestNG test classes
src/test/resources/
  config.properties                  site URL, Appian version, browser, timeouts
  credentials.properties.example     template -> copy to credentials.properties
testng.xml                           test suite / grouping definition
```

## One-time setup

1. **Install the Appian Selenium API jar locally.** Appian distributes it as a
   plain jar rather than publishing to Maven Central, so it must be installed
   into your local `~/.m2` repository once. This is fully scripted — it
   downloads the jar straight from Appian's public GitLab package registry
   and runs `mvn install:install-file`:

   ```bash
   ./pre-requisites/install-appian-selenium-api.sh
   ```

   This installs the version pinned in the script (currently `20260910`,
   matching `<appian.selenium.api.version>` in `pom.xml`). To pick up a newer
   release, find the latest tag at
   https://gitlab.com/appian-oss/appian-selenium-api/-/releases, then run
   `./pre-requisites/install-appian-selenium-api.sh <version>` and update
   `pom.xml` to match.

   **Verified:** `mvn clean compile test-compile` succeeds against this jar,
   and `mvn test` runs the full stack end-to-end (WebDriverManager → real
   Chrome → TestNG → `SitesFixture.loginIntoWithUsernameAndPassword`) — it
   only fails at the point of navigating to the placeholder site URL below,
   which is expected until you configure a real one.

2. **Point the project at your Appian site.** Edit
   `src/test/resources/config.properties`:

   ```properties
   appian.site.url=https://<your-company>.appiancloud.com/suite/sites/<your-site-name>
   appian.version=25.2
   ```

3. **Set credentials.** Either:
   - export env vars (preferred, especially for CI): `APPIAN_USERNAME`, `APPIAN_PASSWORD`, or
   - copy `src/test/resources/credentials.properties.example` to
     `credentials.properties` (already gitignored) and fill in a real test
     user's username/password.

## Running tests

```bash
mvn clean test                 # runs the suite defined in testng.xml
mvn test -Dgroups=login         # run only the "login" group
```

Reports land in `reports/` (Extent HTML report), failure screenshots in
`screenshots/`.

## Extending this framework

- Add a new page object under `src/test/java/com/testappian/pages/`.
- Add a new `@Test` class under `src/test/java/com/testappian/tests/`, extending `BaseTest`.
- Register new test groups/classes in `testng.xml`.
- `HomePage.isLoaded()` currently does a placeholder URL check — replace it
  with an assertion specific to your app's actual landing page once you've
  logged in once and seen what it looks like.
- If your Appian instance uses SSO instead of native username/password login,
  replace `LoginPage.login()`'s call to
  `SitesFixture#loginIntoWithUsernameAndPassword` with your IdP's flow; that
  is the one piece of this framework that's inherently environment-specific.

## Why this stack

Appian renders its UI from SAIL definitions through a dynamic, script-heavy
DOM (async component refresh, a progress-bar overlay during saves/navigation),
which trips up plain Selenium/Playwright scripts with flaky waits. Appian's
own Appian Selenium API wraps Selenium with helpers that already account for
this (`waitForProgressBar()`, version-aware locators, etc.), and is also the
engine underneath Appian's official `Cucumber for Appian` and
`FitNesse for Appian` AppMarket plugins — so it's the best-supported path for
UI-level testing of an Appian app from outside Appian Designer.
