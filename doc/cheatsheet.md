# Merge PR locally

## Get it

Documented [here](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/reviewing-changes-in-pull-requests/checking-out-pull-requests-locally)

```
git fetch origin pull/ID/head:BRANCHNAME
```

Example

```
git fetch origin pull/160/head:pomStructure
```

## Rebase it

Good how-to in french [here](https://www.miximum.fr/blog/git-rebase/)

```
git rebase master pomStructure
```
