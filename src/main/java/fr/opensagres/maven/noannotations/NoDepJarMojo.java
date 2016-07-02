package fr.opensagres.maven.noannotations;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.jar.JarMojo;
import org.apache.maven.project.MavenProjectHelper;

@Mojo( name = "jar", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true)
public class NoDepJarMojo extends JarMojo {

	
	@Parameter(defaultValue = "${project.build.directory}/classes-no-dep", property="no-dep.build.outputDirectory",required=true, readonly=true)
    private File classesDirectory;
    
	@Component
    private MavenProjectHelper projectHelper;
    
	@Override
	public void execute() throws MojoExecutionException {
		File jarFile = createArchive();
		
		projectHelper.attachArtifact( getProject(), getType(), getClassifier(), jarFile );
		
	}
	
	/**
     * {@inheritDoc}
     */
    protected File getClassesDirectory()
    {
        return classesDirectory;
    }
    
    protected String getClassifier()
    {
        String classifier = super.getClassifier();
        if (classifier == null) {
        	return "no-annotation";
        }
        return classifier;
    }

}
