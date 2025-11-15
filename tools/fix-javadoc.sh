#!/usr/bin/env bash
set -euo pipefail

# Must run from the repo root
echo "Repo root: $(pwd)"

echo "Finding occurrences of --no-module-directories..."
grep -R --line-number --include=pom.xml -- '-module-directories' . || true

echo "Removing the flag (backups as .bak)..."
find . -name pom.xml -print0 | xargs -0 sed -i.bak -E '/<additionalJOption>--no-module-directories<\/additionalJOption>/d'

echo "Done. Backups created: *.bak"
