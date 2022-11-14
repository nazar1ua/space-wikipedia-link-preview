# Wikipedia Link Previews

<p align="right">
  <a href="https://github.com/nazar1ua/space-wikipedia-link-preview/blob/main/README_UK.md"><img src="https://twemoji.maxcdn.com/v/latest/svg/1f1fa-1f1e6.svg" height="24" width="24" alt="UK" /></a>
</p>

[Plugin page in JetBrains Marketplace](https://plugins.jetbrains.com/plugin/20371-wikipedia-link-previews)

## How to run the plugin:

1. Fill out `.env`:
    ```shell
    cp .env.example .env
    ```

2. Change data in `application.conf`, version in `build.gradle.kts`
and `docker_ci.sh`

3. Run:
    ```shell
    bash docker_ci.sh
    ```
