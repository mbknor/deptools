Maven deptools plugin
===========================================

**Supports Maven 3**


Latest version: **1.4**



* Now also as an enforcer-plugin rule *
  (See description at the bottom of this README)


---------------------------------------
Maven 2 and Maven 3 plugin which gives build error if maven resolves transient dependencies
in such a way that the none-newest version is chosen.

At work we have all kinds of different dependencies problems related to transient
dependencies.

Transient dependencies has many downsides, but in my opinion, the alternative is worse.

This plugin makes it possible to have the build process fail if not the newest
version of a referred dependency is selected by mavens dependency resolving.

It is possible to include/exclude which artifacts should be included in the check.

This will make it possible for us to check dependency resolving for all
inhouse APIs and projects..

The plugin is written in Scala


For example-maven-project showing the problem and the pluging, have a look here:

    http://github.com/mbknor/deptools/tree/master/test/

---------------------------------------

If you find a bug or have a feature request, report them here:

    http://github.com/mbknor/deptools/issues

---------------------------------------


The plugin can be found in this repository:

    <pluginRepositories>
        <pluginRepository>
            <id>mbk_mvn_repo</id>
            <name>mbk_mvn_repo</name>
            <url>https://raw.githubusercontent.com/mbknor/mbknor.github.com/master/m2repo/releases</url>
        </pluginRepository>
    </pluginRepositories>

(PS: the repository does not work from Nexus(http://nexus.sonatype.org/))

---------------------------------------

To run the plugin from the command line:

    mvn deptools.plugin:maven-deptools-plugin:version-checker

To include/exclude dependencies use one/both of these settings:

    -DexcludePattern=<regular expression>
    -DincludePattern=<regular expression>

---------------------------------------

If you want to automatically include the deptools plugin
in your build, so it can fail your build if it finds an error,
you can do it like this:

    <build>
        <plugins>
            <plugin>
                <groupId>deptools.plugin</groupId>
                <artifactId>maven-deptools-plugin</artifactId>
                <version>1.3</version>
                <!--
                    Use these optional settings to include and/or include
                    dependencies from the check:

                    <configuration>
                        <includePattern> a valid regular expression </includePattern>
                        <excludePattern> a valid regular expression </excludePattern>
                    </configuration>

                    The pattern is matched against the string "groupId:artifactId" for
                    all dependencies in the dependency-path.

                    Example:
                    <includePattern>com.kjetland:.*</includePattern>
                    The pattern above will only fail the build if an error is found
                    on any dependency linked to from a an artifact with groupId
                    equal to 'com.kjetland'

                -->
                <executions>
                    <execution>
                        <phase>compile</phase>
                        <goals>
                            <goal>version-checker</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

    <pluginRepositories>
        <pluginRepository>
            <id>mbk_mvn_repo</id>
            <name>mbk_mvn_repo</name>
            <url>https://raw.githubusercontent.com/mbknor/mbknor.github.com/master/m2repo/releases</url>
        </pluginRepository>
    </pluginRepositories>


--------------------------------------

The plugin can also be used as a maven-enforcer-plugin rule.

For example-maven-projects showing the problem and the plugin as enforcer-rule, have a look here:

    http://github.com/mbknor/deptools/tree/master/test/enforcer-tests/

This is done like like this:

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-enforcer-plugin</artifactId>
        <dependencies>
            <dependency>
                <groupId>deptools.plugin</groupId>
                <artifactId>enforcer-rule</artifactId>
                <version>1.3</version>
            </dependency>
        </dependencies>

        <configuration>
            <rules>
                <requireMavenVersion>
                    <version>2.0.6</version>
                </requireMavenVersion>
                <myCustomRule implementation="deptools.plugin.enforcerrule.DeptoolsEnforcerRule">
                    <includePattern>com.kjetland:.+</includePattern>
                    <excludePattern>.*commons-logging.*</excludePattern>
                </myCustomRule>

            </rules>
        </configuration>

    </plugin>


To invoke the deptools-plugin as an enforcer plugin, execute this on the command-line:

    mvn package enforcer:enforce


