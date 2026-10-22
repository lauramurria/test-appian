#!/usr/bin/env bash
# Downloads the Appian Selenium API jar from Appian's public GitLab package
# registry and installs it into your local Maven repository.
#
# Appian distributes this library as a plain jar (not published to Maven
# Central), packaged inside a generic "FCS" release artifact. Project:
# https://gitlab.com/appian-oss/appian-selenium-api
#
# Usage:
#   ./pre-requisites/install-appian-selenium-api.sh [version]
#
#   [version] is a release tag from https://gitlab.com/appian-oss/appian-selenium-api/-/releases
#   (date-stamped, e.g. 20260910). Defaults to the version below, which must
#   match pom.xml's <appian.selenium.api.version>. To pick up a newer release,
#   check the releases page for the latest tag, pass it here, and update
#   pom.xml to match.
#
set -euo pipefail

PROJECT_ID=68602892
VERSION="${1:-20260910}"
JAR_NAME="appian-selenium-api.jar"
OUT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT_FILE="$OUT_DIR/appian-selenium-api-$VERSION.jar"

echo "Downloading $JAR_NAME @ $VERSION from GitLab package registry..."
curl -sL --fail \
    "https://gitlab.com/api/v4/projects/$PROJECT_ID/packages/generic/FCS/$VERSION/$JAR_NAME" \
    -o "$OUT_FILE"

if ! file "$OUT_FILE" | grep -q "Zip archive"; then
    echo "Download failed or returned something that isn't a jar. Check that"
    echo "version '$VERSION' exists at:"
    echo "  https://gitlab.com/appian-oss/appian-selenium-api/-/releases"
    rm -f "$OUT_FILE"
    exit 1
fi

mvn install:install-file \
    -Dfile="$OUT_FILE" \
    -DgroupId=appian \
    -DartifactId=appian-selenium-api \
    -Dversion="$VERSION" \
    -Dpackaging=jar

echo "Installed appian-selenium-api:$VERSION to your local Maven repository."
echo "Make sure pom.xml's <appian.selenium.api.version> matches: $VERSION"
