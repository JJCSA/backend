# Contributing to JJCSA Portal Backend

## Reporting issues

To report an issue or make a request for a new feature please create a new feature for backend repository on [Zenhub](https://app.zenhub.com/).

| :exclamation:  Please be cautious while creating issues on Zenhub!   |
|----------------------------------------------------------------------|

1. Click the new issue button on from the Board menu or click [here](https://app.zenhub.com/workspaces/jjcsa-5ed1bcd022309703adc78079/issues/jjcsa/backend/new).
2. Make sure to select the backend repository while creating issues for the backend.


## The Contribution Process

For contributions, it is preferred to create a new branch with the same name as the issue on Zenhub. The new branch should be based off the `developer` branch. Once ready, make sure to submit a PR from your branch into the `developer` branch.

Ideally, these general steps should be followed to contribute.

1. Open your Git Terminal and switch to the `developer` branch of the [backend](https://github.com/JJCSA/backend) repo
```
git checkout developer
git pull --rebase
```
| :exclamation:  Always `rebase` instead of `pull`   |
|----------------------------------------------------|

2. Resolve any  merge conflicts or commit untracked code changes, if any.
3. Create a new branch with the same name as the Zenhub issue. For example, creating a branch for issue #1
```
git checkout -b issue-1
git push -u origin issue-1
```
4. Make all your code changes for issue-1 in the `issue-1` branch.
5. Once the code changes are ready, and the issue is tested, sync the branch with developer
```
git rebase developer issue-1
```
| :exclamation:  Always `rebase` instead of `merge`   |
|-----------------------------------------------------|

6. Resolve any merge conflicts before pushing the final code changes.
```
git push
```
7. Head over to GitHub to create a [new Pull Request](https://github.com/JJCSA/backend/compare/developer...) into the `developer` branch.
8. Make sure to choose the branch correctly and add description of the code changes for the reviewer.
   In case, your PR adds a new package (or library), mention so in the Pull Request with a justification of using the new package.
9. Once reviewed, merge the code changes and close the Pull Request.
10. Test the code changes on the `developer` branch
```
git checkout developer
git pull --rebase
```
11. If everything is okay, delete the `issue-1` branch and update the Zenhub issue as well.
