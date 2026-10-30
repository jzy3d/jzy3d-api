#!/usr/bin/env bash
# Lists the Jzy3d version used by a project, the published Jzy3d versions, and the sources describing
# what is new in each version after the one in use.
#
# Usage : jzy3d-versions.sh <project-dir> [jzy3d-api-clone]
#
# Without a clone of jzy3d-api, tags and branches are read from GitHub with the gh CLI.

PROJECT="${1:-.}"
CLONE="$2"

MAVEN=https://maven.jzy3d.org
SITE=http://www.jzy3d.org   # https does not answer
GITHUB=https://github.com/jzy3d/jzy3d-api
JAVADOC=https://doc.jzy3d.org/javadoc

fetch() { curl -sL --max-time 20 "$1"; }
status() { curl -s --max-time 20 -o /dev/null -w '%{http_code}' "$1"; }
versions_of() { fetch "$MAVEN/$1/org/jzy3d/$2/maven-metadata.xml" | grep -oE '<version>[^<]+' | sed 's/<version>//'; }
# Version comparison on numbers only, a SNAPSHOT counting as its release
newer_than() { [ "$(printf '%s\n%s\n' "${1%-SNAPSHOT}" "${2%-SNAPSHOT}" | sort -V | tail -1)" = "${1%-SNAPSHOT}" ] && [ "${1%-SNAPSHOT}" != "${2%-SNAPSHOT}" ]; }

echo "# Jzy3d dependencies of $PROJECT"
grep -rnE "org\.jzy3d|jzy3d-[a-z]" "$PROJECT" --include=pom.xml --include='*.gradle' --include='*.gradle.kts' 2>/dev/null \
  | grep -v '/target/' | grep -E "artifactId|groupId|version|jzy3d" | head -40
find "$PROJECT" -name 'jzy3d-*.jar' -not -path '*/target/*' 2>/dev/null | head -20

USED=$(grep -rhE -A2 "<artifactId>jzy3d-(api|core|all|native-jogl-awt|emul-gl-awt)</artifactId>" "$PROJECT" --include=pom.xml 2>/dev/null \
  | grep -oE '<version>[^<$]+' | sed 's/<version>//' | sort -V | tail -1)
[ -z "$USED" ] && [ -f "$PROJECT/pom.xml" ] && command -v mvn >/dev/null \
  && USED=$(cd "$PROJECT" && mvn -q dependency:list -Dincludes=org.jzy3d 2>/dev/null | grep -oE 'org\.jzy3d:jzy3d-[a-z-]+:jar:[^:]+' | head -1 | awk -F: '{print $4}')
echo
echo "Version in use : ${USED:-unknown, read the dependencies above (properties, parent pom, jar names)}"

echo
echo "# Published versions"
echo "Releases 2.x+ (jzy3d-all)    : $(versions_of releases jzy3d-all | tr '\n' ' ')"
echo "Releases 0.9-1.0 (jzy3d-api) : $(versions_of releases jzy3d-api | tr '\n' ' ')"
echo "Snapshots (jzy3d-core)       : $(versions_of snapshots jzy3d-core | tr '\n' ' ')"
echo "Maven Central                : $(status https://repo1.maven.org/maven2/org/jzy3d/jzy3d-core/maven-metadata.xml | sed 's/404/not published/')"
echo "Javadoc                      : $(fetch $JAVADOC/ | grep -oE 'href="[0-9][^"/]*/"' | sed -E 's/href="|\/"//g' | tr '\n' ' ')"

echo
echo "# Tags and release branches"
if [ -n "$CLONE" ] && [ -d "$CLONE/.git" ]; then
  git -C "$CLONE" fetch -q --tags origin 2>/dev/null
  git -C "$CLONE" for-each-ref refs/tags --sort=creatordate --format='tag    %(refname:short) %(creatordate:short)'
  git -C "$CLONE" for-each-ref refs/remotes/origin --format='%(refname:short) %(committerdate:short)' \
    | grep -E 'origin/[0-9]' | sed 's/^/branch /'
  echo "master version : $(git -C "$CLONE" show origin/master:pom.xml | grep -m1 -oE '<version>[^<]+' | sed 's/<version>//')"
elif command -v gh >/dev/null; then
  gh api repos/jzy3d/jzy3d-api/tags --jq '.[].name' | sed 's/^/tag    /'
  gh api repos/jzy3d/jzy3d-api/branches --paginate --jq '.[].name' | grep -E '^[0-9]' | sed 's/^/branch /'
else
  echo "See $GITHUB/tags and $GITHUB/branches/all"
fi

echo
echo "# Website download pages"
ALL_VERSIONS=$( (versions_of releases jzy3d-api; versions_of releases jzy3d-all; versions_of snapshots jzy3d-core | sed 's/-SNAPSHOT//'; echo 0.9; echo 0.9.1) | sort -uV)
for v in $ALL_VERSIONS; do
  [ "$(status "$SITE/download-$v.php")" = 200 ] && echo "$SITE/download-$v.php"
done

[ -z "$USED" ] && exit 0

echo
echo "# What is new since $USED"
DATE=""
if [ -n "$CLONE" ] && [ -d "$CLONE/.git" ]; then
  REF=$(git -C "$CLONE" rev-parse -q --verify "jzy3d-all-${USED%-SNAPSHOT}" >/dev/null && echo "jzy3d-all-${USED%-SNAPSHOT}" || echo "origin/${USED%-SNAPSHOT}")
  DATE=$(git -C "$CLONE" log -1 --format=%cs "$REF" 2>/dev/null)
  echo "Commits since $REF ($DATE) :"
  echo "  git -C $CLONE log --oneline --no-merges $REF..origin/master"
  echo "  git -C $CLONE log --oneline --merges $REF..origin/master   # merged pull requests"
  echo "  $(git -C "$CLONE" rev-list --count --no-merges "$REF..origin/master" 2>/dev/null) commits"
  for f in $(git -C "$CLONE" ls-tree --name-only origin/master doc/ | grep -E 'release-[0-9.]+\.md'); do
    v=$(echo "$f" | grep -oE '[0-9]+\.[0-9]+\.[0-9]+')
    newer_than "$v" "$USED" && echo "Release notes : $f (git -C $CLONE show origin/master:$f)"
  done
else
  echo "Release notes : $GITHUB/tree/master/doc (files release-X.Y.Z.md)"
fi
for v in $ALL_VERSIONS; do
  newer_than "$v" "$USED" && [ "$(status "$SITE/download-$v.php")" = 200 ] && echo "Website       : $SITE/download-$v.php"
done
if [ -n "$CLONE" ] && [ -d "$CLONE/.git" ]; then
  TAGS=$(git -C "$CLONE" tag -l 'jzy3d-all-*')
elif command -v gh >/dev/null; then
  TAGS=$(gh api repos/jzy3d/jzy3d-api/tags --jq '.[].name' | grep '^jzy3d-all-')
fi
# Before 2.0.0, versions are release branches, not tags
FROM=$(echo "$TAGS" | grep -qx "jzy3d-all-${USED%-SNAPSHOT}" && echo "jzy3d-all-${USED%-SNAPSHOT}" || echo "${USED%-SNAPSHOT}")
for t in $(echo "$TAGS" | sort -V); do
  newer_than "${t#jzy3d-all-}" "$USED" && echo "Compare       : $GITHUB/compare/$FROM...$t"
done
echo "Compare       : $GITHUB/compare/$FROM...master"
if [ -n "$DATE" ]; then
  echo "Pull requests : gh pr list -R jzy3d/jzy3d-api --state merged --search \"merged:>$DATE\" --limit 200"
  echo "Issues        : gh issue list -R jzy3d/jzy3d-api --state closed --search \"closed:>$DATE\" --limit 200"
fi
