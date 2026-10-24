# Repository size : plan to make clones lighter

A clone of `jzy3d-api` downloads about 450 MB, and checks out 350 MB of files. Almost all of it is made of
the baseline images of the integration tests (`jzy3d-tester-integration/src/test/resources/<platform>/`).

## Measures (October 2026)

Measured on a mirror of the repository (branches and tags), after `git gc --aggressive`.

| What                                                      | Size    |
|-----------------------------------------------------------|---------|
| Full clone today                                          | 438 MB  |
| PNG files in history                                      | 420 MB  |
| Baselines of the integration tests in history             | 399 MB  |
| Baselines in the current tree (1100 PNG, on disk)         | 295 MB  |
| Files only in history (older versions, removed files)     | 260 MB  |
| Everything except PNG                                     | 45 MB   |

Baselines per platform in the current tree (on disk) : macOS 26.6 M3 70 MB, macOS 10.15 Intel 56 MB,
macOS 14.4 M3 55 MB, macOS 10.12 NVIDIA 50 MB, macOS 11.4 M1 35 MB, Linux 5.13 Intel 30 MB. The macOS 10.12
baselines were regenerated many times : all their versions weigh 182 MB in the history.

Simulated rewrites of the history (`git filter-repo` on a copy, then `git gc --aggressive`) :

| Scenario                                                                | Clone size |
|-------------------------------------------------------------------------|------------|
| Today                                                                   | 438 MB     |
| B. Keep the current baselines, drop their older versions                | 243 MB     |
| A. Move the baselines out of the repository and of its history          | 66 MB      |
| C. A, and drop files above 100 KB no longer in the tree (old jars, `dragon.obj`...) | 30 MB |

## Step 1 : without changing the history (done)

* **CI** checks out without the baselines, which it does not use (it builds its own baselines in
  `target/ittest-baselines`) : sparse and blobless checkout in `.github/workflows/ci.yml`.
* **Developers who do not run the integration tests** can clone the same way (24 MB of history, 47 MB of
  files) :

```
git clone --filter=blob:none --no-checkout https://github.com/jzy3d/jzy3d-api.git
cd jzy3d-api
git sparse-checkout set --no-cone '/*' '!/jzy3d-tester-integration/src/test/resources/*_*/'
git checkout master
```

`git sparse-checkout disable` brings back the baselines later. `git clone --depth 1` is another option (current
tree only, with the baselines).

## Step 2 : remove obsolete baselines (no history change, done)

Baselines of computers that are no longer used can not be verified anymore : macOS 10.12, 10.15, 11.4,
14.4, Linux 5.13, and the manual tests of macOS 10.12. They have been removed, which saves about 230 MB of
checkout, but nothing of clone until the history is rewritten. Only the baselines of macOS 26.6 M3 and
macOS 27.0.1 M1 remain.

Baselines of `HiDPI.OFF` have also been removed for the tests whose rendering does not depend on the pixel
scale (surface, scatter, axis flip), see the README of `jzy3d-tester-integration`. Baselines can also be compressed losslessly (e.g.
`zopflipng -y in.png out.png` saved 18% and 34% on two baselines, at several seconds per image).

## Step 3 : store baselines out of this repository

Baselines change with each OS, GPU and driver, and are useless to most contributors. Options :

| Option | Pros | Cons |
|---|---|---|
| **Separate repository** `jzy3d/jzy3d-tester-baselines`, as a git submodule in `jzy3d-tester-integration/src/test/resources/baselines` (or cloned next to `jzy3d-api` and given with `-Djzy3d.ittest.baselines`) | No quota, no extra tool, a clone of `jzy3d-api` stays small, the baselines repository can have its history squashed whenever it grows | Two repositories to commit to when a rendering change updates baselines |
| **Git LFS** (`git lfs migrate import --everything --include='jzy3d-tester-integration/src/test/resources/**/*.png'`) | Single repository, images are downloaded only for the checked-out commit | GitHub LFS storage and bandwidth quotas (check the current ones on the billing page) are consumed by every clone of a contributor ; every contributor must install `git lfs` ; CI must set `lfs: false` |
| **Archive downloaded by the tests** (e.g. from maven.jzy3d.org) | Nothing in git | Versioning of the archive by hand |

**Recommendation** : a separate repository. Baselines are only needed by the few computers running the
integration tests, which is what a separate repository gives, without the quotas and the extra tool of LFS.

## Step 4 : rewrite the history (scenario C, 30 MB)

Only a rewrite of the history shrinks clones. It changes the hash of every commit : it is a one-shot
operation, to plan when no pull request is open.

```
# 1. On a fresh bare clone (branches and tags)
git clone --bare https://github.com/jzy3d/jzy3d-api.git && cd jzy3d-api.git

# 2. Remove baselines, under both names of the module
git filter-repo --invert-paths \
  --path-glob 'jzy3d-tests-java9/src/test/resources/*.png' \
  --path-glob 'jzy3d-tester-integration/src/test/resources/*.png'

# 3. Remove files above 100 KB that are not in master anymore (review strip.txt first)
git rev-list --all --objects | git cat-file --batch-check='%(objecttype) %(objectname) %(objectsize) %(rest)' \
  | awk '$1=="blob" && $3>100000 {print $2}' | sort -u > big.txt
git ls-tree -r master | awk '{print $3}' | sort -u > kept.txt
comm -23 big.txt kept.txt > strip.txt
git filter-repo --strip-blobs-with-ids strip.txt

# 4. Push all branches and tags (force). filter-repo removes the origin remote on purpose
git remote add origin https://github.com/jzy3d/jzy3d-api.git
git push --force --all origin && git push --force --tags origin
```

Consequences :

* **Every commit hash changes** : contributors must clone again (a `pull` would merge both histories back).
  Open pull requests must be merged or closed before, branches of forks keep the old history.
* Tags are rewritten : released Maven artifacts are not affected, but links to old commits break.
* GitHub keeps the old objects referenced by `refs/pull/*` : they are not downloaded by clones, and GitHub
  support can be asked to garbage collect them.
* The baselines must be pushed to their new storage (step 3) before the rewrite.
