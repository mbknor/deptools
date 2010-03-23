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
 * @goal version-checker
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
     * The scala class has declered abstract getter-methods for all
     * maven-specific-properties.
     *
     * This java class extends the scala class, has actuall implementasjons
     * of the getter-methods.
     *
     * It also has the properties which we annotate with mojo-annotations..
     *
     * This is an hack to make it possible to write the actuall plugin-code in scala
     */

    /**
     * The Maven Project Object
     *
     * @parameter expression="${testString}" default-value=""
     * @required
     * @readonly
     */
    //protected String testString;

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



    /**
     * The Maven Session Object
     *
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    protected String buildDir;


    @Override
    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    @Override
    public String getBuildDir() {
        return buildDir;
    }

    public void setBuildDir(String buildDir) {
        this.buildDir = buildDir;
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public MavenSession getSession() {
        return session;
    }

    public void setSession(MavenSession session) {
        this.session = session;
    }
}