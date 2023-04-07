# Download

## Prerequisites

First install [Liquibase](https://www.liquibase.org/download).

## Installation

=== "CLI users"
    1. Locate the Liquibase installation folder (subsequently called `LIQUIBASE_HOME`)
    1. Download the extension JAR (from [GitHub]({{ github_repo }}/releases/
    or [Maven Central]({{ maven_central }}/{{ group_id_url }}/{{ artifact_id }}/{{ version }}/{{ artifact_id }}-{{ version }}-full.jar))
    1. Place the JAR in the `plugins` folder of `LIQUIBASE_HOME`
    1. Run `liquibase --version`, the extension should be listed

    !!! tip
        Maven Central remains the recommended download location.
        It includes [PGP]({{ maven_central }}/{{ group_id_url }}/{{ artifact_id }}/{{ version }}/{{ artifact_id }}-{{ version }}-full.jar.asc), [MD5]({{ maven_central }}/{{ group_id_url }}/{{ artifact_id }}/{{ version }}/{{ artifact_id }}-{{ version }}-full.jar.md5) and [SHA-1]({{ maven_central }}/{{ group_id_url }}/{{ artifact_id }}/{{ version }}/{{ artifact_id }}-{{ version }}-full.jar.sha1) files to validate the integrity of the downloadable artefact.

=== "Maven users"

    === "Regular users"

        Make sure to add the `{{ artifact_id }}` dependency as follows, alongside your other dependencies.

        ```xml
        <dependency>
            <groupId>{{ group_id }}</groupId>
            <artifactId>{{ artifact_id }}</artifactId>
            <version>{{ version }}</version>
        </dependency>
        ```
        !!! info
            The extension requires the `org:liquibase:liquibase-core` dependency to be included.
            If you are a Spring Framework or Spring Boot user for instance, `liquibase-core` is likely a transitive dependency and
            may not appear directly in your project file.
            Make sure `liquibase-core` is resolved in your dependency tree (run `mvn dependency:tree` to display the latter).

    === "Liquibase plugin users"

        Make sure to add the `{{ artifact_id }}` dependency to the plugin definition.

        ```xml
        <plugin>
            <groupId>org.liquibase</groupId>
            <artifactId>liquibase-maven-plugin</artifactId>
            <version>{{ liquibase_version }}</version>
            <dependencies>
                <dependency>
                    <groupId>{{ group_id }}</groupId>
                    <artifactId>{{ artifact_id }}</artifactId>
                    <version>{{ version }}</version>
                </dependency>
            </dependencies>
        </plugin>
        ```

=== "Gradle users"

    === "Regular users"

        Make sure to add the `{{ artifact_id }}` dependency as follows, alongside your other dependencies.

        ```groovy
        runtimeOnly '{{ group_id }}:{{ artifact_id }}:{{ version }}'
        ```

        !!! info
            The extension requires the `org:liquibase:liquibase-core` dependency to be included.
            If you are a Spring Framework or Spring Boot user for instance, `liquibase-core` is likely a transitive dependency and
            may not appear directly in your project file.
            Make sure `liquibase-core` is resolved in your dependency tree (run `gradle -q dependencies` to display the latter).

    === "Liquibase plugin users"

        Make sure to add the `{{ artifact_id }}` dependency as follows.

        ```groovy
        liquibaseRuntime '{{ group_id }}:{{ artifact_id }}:{{ version }}'
        ```

{!includes/_abbreviations.md!}
