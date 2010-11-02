package deptools.plugin.enforcerrule;

import deptools.plugin.scala.ScalaMojo;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.factory.ArtifactFactory;

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: Sep 13, 2010
 * Time: 11:09:47 PM
 * To change this template use File | Settings | File Templates.
 */

public class DeptoolsEnforcerRule extends ScalaMojo implements EnforcerRule {


    //needed maven environemnt properties
    private MavenProject _project = null;
    private MavenSession _session = null;
    private String _buildDir = null;
    private PluginManager _pluginManager = null;
    private Log _logger = null;
	private ArtifactRepository _localRepository;
	private ArtifactFactory _artifactFactory;
	private ArtifactMetadataSource _artifactMetadataSource;
	private ArtifactCollector _artifactCollector;
    private DependencyTreeBuilder _dependencyTreeBuilder;

    //configurable propertied
    private String includePattern = null;
    private String excludePattern = null;




    public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {

        _logger = helper.getLog();
        

        try{
            // get the various expressions out of the helper.
            _project = (MavenProject) helper.evaluate( "${project}" );
            _session = (MavenSession) helper.evaluate( "${session}" );
            _buildDir = (String) helper.evaluate( "${project.build.directory}" );
            _pluginManager =  (PluginManager)helper.getComponent(PluginManager.class);
			_localRepository = (ArtifactRepository) helper.evaluate( "${localRepository}");
			_artifactFactory = (ArtifactFactory) helper.getComponent(ArtifactFactory.class);
			_artifactMetadataSource = (ArtifactMetadataSource) helper.getComponent(ArtifactMetadataSource.class);
			_artifactCollector = (ArtifactCollector) helper.getComponent(ArtifactCollector.class);
			_dependencyTreeBuilder = (DependencyTreeBuilder) helper.getComponent(DependencyTreeBuilder.class);
			
        }catch ( ComponentLookupException e )
        {
            throw new EnforcerRuleException( "Unable to lookup a component " + e.getLocalizedMessage(), e );
        }catch ( ExpressionEvaluationException e )
        {
            throw new EnforcerRuleException( "Unable to lookup an expression " + e.getLocalizedMessage(), e );
        }

        //now we have all needed input.. lets execute the real plugin


        try{
            super.execute();
        }catch(Exception e){
            //must catch exceptions thrown by the plugin and
            //transform it into EnforcerRuleException to get the
            //correct output from the enforcer-plugin.
            throw new EnforcerRuleException( e.getMessage());
        }

        //throw new EnforcerRuleException( "Failing because my param said so." );
    }

    public boolean isCacheable() {
        return false;//we're not cachable
    }

    public boolean isResultValid(EnforcerRule enforcerRule) {
        return false;//never cachable -> never valid
    }

    public String getCacheId() {
        return null;//don't need cacheId since we're not cachable
    }


    @Override
    public MavenProject getProject() {
        return _project;
    }

    @Override
    public MavenSession getSession() {
        return _session;
    }

    @Override
    public String getBuildDir() {
        return _buildDir;
    }

    @Override
    public PluginManager getPluginManager() {
        return _pluginManager;
    }

    @Override
    public String getIncludePattern() {
        return includePattern;
    }

    @Override
    public String getExcludePattern() {
        return excludePattern;
    }

    @Override
    public Log getLog() {
        return _logger;
    }

	@Override
	public ArtifactRepository getLocalRepository() {
		return _localRepository;
	}
	
	@Override
	public ArtifactFactory getArtifactFactory() {
		return _artifactFactory;
	}
	
	@Override
	public ArtifactMetadataSource getArtifactMetadataSource() {
		return _artifactMetadataSource;
	}
	
	@Override
	public ArtifactCollector getArtifactCollector() {
		return _artifactCollector;
	}

	@Override
	public DependencyTreeBuilder getDependencyTreeBuilder() {
		return _dependencyTreeBuilder;
	}


}
