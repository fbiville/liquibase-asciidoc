# Quick Start

If you are not familiar with Liquibase concepts, please start [here](https://docs.liquibase.com/concepts/home.html).

## Setup

The rest of this tutorial will assume a working [Liquibase CLI](https://docs.liquibase.com/start/install/home.html) and [Docker](https://www.docker.com/get-started/) installation. 

Please refer to the [download section](../download) to add the extension to Liquibase CLI.

Download the [MySQL driver]({{ maven_central }}/com/mysql/mysql-connector-j/{{ mysqlj_version }}/mysql-connector-j-{{ mysqlj_version }}.jar) and move it the `lib` folder of your Liquibase CLI installation.

!!! danger
    Check the integrity of the MySQL driver with one of these files:

    - [PGP]({{ maven_central }}/com/mysql/mysql-connector-j/{{ mysqlj_version }}/mysql-connector-j-{{ mysqlj_version }}.jar.asc)
    - [MD5]({{ maven_central }}/com/mysql/mysql-connector-j/{{ mysqlj_version }}/mysql-connector-j-{{ mysqlj_version }}.jar.md5)
    - [SHA-1]({{ maven_central }}/com/mysql/mysql-connector-j/{{ mysqlj_version }}/mysql-connector-j-{{ mysqlj_version }}.jar.sha1)

## Starting MySQL

Start a MySQL server for the rest of the tutorial:

```shell
docker run --name mysql -d \
    -p 3306:3306 \
    -e MYSQL_ROOT_PASSWORD='letmein!' \
    -e MYSQL_DATABASE=inventory \
    mysql:8
```


## Change Log

Here is a simplistic schema to manage an inventory, stored in `changeLog.adoc`.

```asciidoc
The nice thing about Asciidoc is that you can accompany your Liquibase
change logs with detailed schema documentation.

With added tools like https://kroki.io/[Kroki], you get powerful, rich 
and more importantly **executable** documentation!

Here comes the first change set.

[source,sql,id=inventory-creation,author=fbiville]
----
CREATE TABLE inventory
(
    id          MEDIUMINT  NOT NULL AUTO_INCREMENT,
    item_name   VARCHAR(255) UNIQUE NOT NULL,
    PRIMARY KEY (id)
);
----

Here comes the second one.

[source,sql,id=inventory-seed-data,author=fbiville]
----
INSERT INTO inventory(item_name) VALUES('A Thing'), ('Or Two')
----
```

## Run

The Liquibase execution is done with the `update` command.

```shell
liquibase --url jdbc:mysql://localhost/inventory \
          --username root \
          --password 'letmein!' \
          --changeLogFile changeLog.adoc \
          update
```

!!! tip
    You can run the `updateSQL` command first in order to make sure each change set is properly defined. This allows to catch obvious mistakes before modifying the target database.

The output should be similar to:
```
####################################################
##   _     _             _ _                      ##
##  | |   (_)           (_) |                     ##
##  | |    _  __ _ _   _ _| |__   __ _ ___  ___   ##
##  | |   | |/ _` | | | | | '_ \ / _` / __|/ _ \  ##
##  | |___| | (_| | |_| | | |_) | (_| \__ \  __/  ##
##  \_____/_|\__, |\__,_|_|_.__/ \__,_|___/\___|  ##
##              | |                               ##
##              |_|                               ##
##                                                ##
##  Get documentation at docs.liquibase.com       ##
##  Get certified courses at learn.liquibase.com  ##
##  Free schema change activity reports at        ##
##      https://hub.liquibase.com                 ##
##                                                ##
####################################################
Starting Liquibase at 15:12:16 (version 4.20.0 #7837 built at 2023-03-07 16:25+0000)
Liquibase Version: 4.20.0
Liquibase Open Source 4.20.0 by Liquibase
2023-04-07T15:12:16.864+02:00 [main] WARN FilenoUtil : Native subprocess control requires open access to the JDK IO subsystem
Pass '--add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED' to enable.
Running Changeset: changeLog.adoc::inventory-creation::fbiville
Running Changeset: changeLog.adoc::inventory-seed-data::fbiville
Liquibase command 'update' was executed successfully.
```

Check the content has been properly inserted:

```shell
# this will interactively ask for the password 'letmein!'
docker exec -it mysql mysql -uroot -p inventory -e 'SELECT * FROM inventory'
```

You should see an output similar to this:
```
Enter password:
+----+-----------+
| id | item_name |
+----+-----------+
|  1 | A Thing   |
|  2 | Or Two    |
+----+-----------+
```


{!includes/_abbreviations.md!}
