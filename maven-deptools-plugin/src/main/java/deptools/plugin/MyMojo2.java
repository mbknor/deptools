package deptools.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import deptools.plugin.integration.DeptoolsExecutor;
import deptools.plugin.scala.DoStuff;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.project.MavenProject;

/**
 * Goal which touches a timestamp file.
 *
 * set aggregator-annotation to make our plugin only execute once when building
 * project with modules..
 *
 * @aggregator true
 *
 * @goal check2
 *
 * @phase process-sources
 */
public class MyMojo2
    extends AbstractMojo
{
    /**
     * The Maven Project Object
     *
     * @parameter expression="${testString}" default-value="default-value"
     * @required
     * @readonly
     */
    protected String testString;

    public void execute()
        throws MojoExecutionException
    {

        getLog().info("-------> testString: " + testString);

       
    }
}