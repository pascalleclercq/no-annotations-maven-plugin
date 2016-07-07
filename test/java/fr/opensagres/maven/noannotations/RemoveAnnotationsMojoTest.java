package fr.opensagres.maven.noannotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collections;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.project.MavenProject;

public class RemoveAnnotationsMojoTest extends AbstractMojoTestCase {

	 private RemoveAnnotationsMojo getCompilerMojo( String pomXml )
		        throws Exception
		    {
		        File testPom = new File( getBasedir(), pomXml );

		        RemoveAnnotationsMojo mojo = (RemoveAnnotationsMojo) lookupMojo( "removeAnnotations", testPom );
		        
		        setVariableValueToObject( mojo, "project", getMockMavenProject() );

		        assertNotNull( mojo );

		        return mojo;
		    }
	    
	 
	    private MavenProject getMockMavenProject()
	    {
	        MavenProject mp = new MavenProject();
	        mp.getBuild().setSourceDirectory("src");
	        mp.getBuild().setDirectory( "target" );

	        return mp;
	    }
	 /**
     * tests the ability of the plugin to compile a basic file
     *
     * @throws Exception
     */
    public void testCompilerBasic()
        throws Exception
    {
    	RemoveAnnotationsMojo compileMojo = getCompilerMojo( "target/test-classes/compiler-basic-test/plugin-config.xml" );

        compileMojo.execute();

        File noAnnotationsSourceFolder =compileMojo.getNoAnnotationsSourceFolder();

        assertTrue( noAnnotationsSourceFolder.isDirectory() );

        assertTrue( noAnnotationsSourceFolder.list().length >0 );

     }
}
