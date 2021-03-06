package deptools.plugin;

import deptools.plugin.scala.ScalaMojo;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.factory.ArtifactFactory;

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

    /**
     * Optional Regex pattern used to find groupId/ArtifactId's to run version-check on
     *
     * @parameter expression="${includePattern}"
     */
    protected String includePattern;

    /**
     * Optional Regex pattern used to exclude groupId/ArtifactId's to run version-check on
     *
     * @parameter expression="${excludePattern}"
     */
    protected String excludePattern;


	/**
	* The artifact repository to use.
	* 
	* @parameter expression="${localRepository}"
	* @required
	* @readonly
	*/
	private ArtifactRepository localRepository;

	/**
	* The artifact factory to use.
	* 
	* @component
	* @required
	* @readonly
	*/
	private ArtifactFactory artifactFactory;

	/**
	* The artifact metadata source to use.
	* 
	* @component
	* @required
	* @readonly
	*/
	private ArtifactMetadataSource artifactMetadataSource;

	/**
	* The artifact collector to use.
	* 
	* @component
	* @required
	* @readonly
	*/
	private ArtifactCollector artifactCollector;


	/**
     * The dependency tree builder to use.
     * 
     * @component
     * @required
     * @readonly
     */
    private DependencyTreeBuilder dependencyTreeBuilder;


    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public String getBuildDir() {
        return buildDir;
    }

    public void setBuildDir(String buildDir) {
        this.buildDir = buildDir;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public MavenSession getSession() {
        return session;
    }

    public void setSession(MavenSession session) {
        this.session = session;
    }

    public String getExcludePattern() {
        return excludePattern;
    }

    public void setExcludePattern(String excludePattern) {
        this.excludePattern = excludePattern;
    }

    public String getIncludePattern() {
        return includePattern;
    }

    public void setIncludePattern(String includePattern) {
        this.includePattern = includePattern;
    }

	public ArtifactRepository getLocalRepository() {
		return localRepository;
	}
	
	public ArtifactFactory getArtifactFactory() {
		return artifactFactory;
	}
	
	public ArtifactMetadataSource getArtifactMetadataSource() {
		return artifactMetadataSource;
	}
	
	public ArtifactCollector getArtifactCollector() {
		return artifactCollector;
	}

	public DependencyTreeBuilder getDependencyTreeBuilder() {
		return dependencyTreeBuilder;
	}

}