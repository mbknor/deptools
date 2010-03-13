package deptools.plugin;

import deptools.plugin.scala.ScalaMojo;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.project.MavenProject;

/**
 * Goal which touches a timestamp file.
 * <p/>
 * set aggregator-annotation to make our plugin only execute once when building
 * project with modules..
 *
 * @aggregator true
 * @goal check2
 * @phase process-sources
 */
public class MyMojo2
        extends ScalaMojo {
    /**
     * Ok - so here is how I got this to work..
     * The mojo is implemented in scala, but the plugin:descriptor cannot
     * parse the javadoc annotations from the scala file..
     *
     * therefor I have this java class that extends the scala class.
     *
     * the scala class have the methods setTestString() and getTestString()
     *
     * this java class has a protected property testString, which we
     * can write the mojo annotation on.
     *
     * but when the plugin is launched, and maven uses reflection to set the value
     * of the property, the scala setter is called..
     *
     * short: we annotate on fake/unused property in java, but the code is in scala
     */

    /**
     * The Maven Project Object
     *
     * @parameter expression="${testString}" default-value=""
     * @required
     * @readonly
     */
    protected String testString;

    /**
     * The Maven Project Object
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * The Maven Session Object
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    protected MavenSession session;

    /**
     * The Maven PluginManager Object
     *
     * @component
     * @required
     */
    protected PluginManager pluginManager;

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }



    /**
     * The Maven Session Object
     *
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    protected String buildDir;
}