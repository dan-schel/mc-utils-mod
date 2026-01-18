#!/bin/bash
set -e

# Exit with code 0 if current branch is master
if git rev-parse --abbrev-ref HEAD | grep -q "^master$"; then
  echo "✅ On master branch"
  exit 0
fi

# Extract current version from gradle.properties
current="v$(sed -n 's/^mod_version=//p' gradle.properties)"

# Extract master version from git
git show origin/master:gradle.properties > /tmp/master-gradle.properties || echo "mod_version=0.0.0" > /tmp/master-gradle.properties
master="v$(sed -n 's/^mod_version=//p' /tmp/master-gradle.properties)"

echo "Current version: $current"
echo "Master version: $master"

# Check if version was changed
if [ "$current" == "$master" ]; then
  echo "❌ Version not changed"
  exit 1
fi

# Check if version is higher (not decreased)
if [ "$(printf '%s\n' "${master#v}" "${current#v}" | sort -V | head -n1)" == "${current#v}" ] && [ "$master" != "$current" ]; then
  echo "❌ Version decreased or invalid"
  exit 1
fi

echo "✅ Version correctly bumped from $master to $current"
