# Cat Fact Service

[release-badge]: https://github.com/yonatankarp/cat-fact-service/actions/workflows/release.yml/badge.svg
[release-state]: https://github.com/yonatankarp/cat-fact-service/actions/workflows/release.yml
[gradle-upgrade-badge]: https://github.com/yonatankarp/cat-fact-service/actions/workflows/update-gradle-wrapper.yml/badge.svg
[gradle-upgrade-state]: https://github.com/yonatankarp/cat-fact-service/actions/workflows/update-gradle-wrapper.yml
[linters-badge]: https://github.com/yonatankarp/cat-fact-service/actions/workflows/linting.yml/badge.svg
[linters-state]: https://github.com/yonatankarp/cat-fact-service/actions/workflows/linting.yml

| **Type**     | **Status**                                                                                                                                            |
|--------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| CI pipelines | [![Release][release-badge]][release-state] [![Gradle Upgrade][gradle-upgrade-badge]][gradle-upgrade-state] [![Linters][linters-badge]][linters-state] |


A simple cat fact service application, service a JSON endpoint and using the 
[cat-fact client library](https://github.com/yonatankarp/cat-fact-client)
to get the facts about cats.

This is the example application described in the article [LINK TO ARTICLE]()

## Running the project

To build the project, run the following commands:

```bash
$ ./gradlew build
$ docker compose up
```

You can now hit the endpoint

```shell
$ curl http://localhost:8080/api/v1/cat/facts
```

You can set the number of facts (1 to 10) using the query parameter `max` as follows:

```shell
$ curl http://localhost:8080/api/v1/cat/facts?max=3
```

If the value is higher than 10, it would be clamped to 10, and if it is lower
than 1, it would be clamped to 1.

Alternatively, you can pull the final docker image from GHCR:

```shell
$ docker pull ghcr.io/yonatankarp/cat-fact-service:latest
```

## Authors

- **Yonatan Karp-Rudin** - *Initial work* - [yonatankarp](https://github.com/yonatankarp)
