package liquibase.ext.asciidoc

import liquibase.integration.commandline.LiquibaseCommandLine
import liquibase.integration.commandline.Main
import org.testcontainers.containers.MySQLContainer
import spock.lang.Shared
import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager

class AsciidocChangeLogIT extends Specification {

    @Shared
    def mysql = new MySQLContainer<>("mysql:8.0.32")

    @Shared
    Connection connection

    def setupSpec() {
        mysql.start()
        connection = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword())
    }

    def cleanupSpec() {
        connection.close()
        mysql.stop()
    }

    def cleanup() {
        connection.createStatement().withCloseable {statement -> {
            statement.execute("DROP TABLE year")
        }}
    }

    def "runs top-level Asciidoc change log"() {
        given:
        String[] arguments = [
                "--url", mysql.getJdbcUrl(),
                "--username", mysql.getUsername(),
                "--password", mysql.getPassword(),
                "--changeLogFile", "/changelog.adoc",
                "update"
        ]

        when:
        Main.run(arguments)

        then:
        connection.createStatement()
                .withCloseable { statement ->
                    {
                        statement.executeQuery("SELECT year FROM year ORDER BY year ASC")
                                .withCloseable { resultSet ->
                                    {
                                        assert resultSet.next()
                                        assert resultSet.getLong("year") == 2022
                                        assert resultSet.next()
                                        assert resultSet.getLong("year") == 2023
                                        assert !resultSet.next()
                                    }
                                }
                        return true
                    }
                }
    }

    def "runs nested Asciidoc change log"() {
        given:
        String[] arguments = [
                "--url", mysql.getJdbcUrl(),
                "--username", mysql.getUsername(),
                "--password", mysql.getPassword(),
                "--changeLogFile", "/changelog.xml",
                "update"
        ]

        when:
        Main.run(arguments)

        then:
        connection.createStatement()
                .withCloseable { statement ->
                    {
                        statement.executeQuery("SELECT year FROM year ORDER BY year ASC")
                                .withCloseable { resultSet ->
                                    {
                                        assert resultSet.next()
                                        assert resultSet.getLong("year") == 2022
                                        assert resultSet.next()
                                        assert resultSet.getLong("year") == 2023
                                        assert !resultSet.next()
                                    }
                                }
                        return true
                    }
                }
    }
}
