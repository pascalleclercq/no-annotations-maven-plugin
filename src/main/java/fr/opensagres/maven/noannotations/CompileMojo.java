package fr.opensagres.maven.noannotations;

import java.io.File;
import java.util.Collections;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.compiler.Compiler;
import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.CompilerException;
import org.codehaus.plexus.compiler.manager.CompilerManager;
import org.codehaus.plexus.compiler.manager.NoSuchCompilerException;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE )
public class CompileMojo extends AbstractMojo {

    
    @Parameter(defaultValue = "${project.build.directory}/classes-no-dep", property="no-dep.build.outputDirectory",required=true, readonly=true)
    private File classesDirectory;
    
    /**
     * Location of the folder.
     */
    @Parameter( defaultValue = "${project.build.directory}/no-dep", property = "no-dep-source-folder", required = false )
    private File noAnnotationsSourceFolder;


    /**
     * Plexus compiler manager.
     */
    @Component
    private CompilerManager compilerManager;

    
    @Parameter( property = "maven.compiler.compilerId", defaultValue = "javac" )
    private String compilerId;
    /**
     * Set to <code>true</code> to optimize the compiled code using the compiler's optimization methods.
     */
    @Parameter( property = "maven.compiler.optimize", defaultValue = "false" )
    private boolean optimize;
    
    /**
     * Set to <code>true</code> to include debugging information in the compiled class files.
     */
    @Parameter( property = "maven.compiler.debug", defaultValue = "true" )
    private boolean debug = true;
    
    /**
     * Set to <code>true</code> to show messages about what the compiler is doing.
     */
    @Parameter( property = "maven.compiler.verbose", defaultValue = "false" )
    private boolean verbose;

    /**
     * Sets whether to show source locations where deprecated APIs are used.
     */
    @Parameter( property = "maven.compiler.showDeprecation", defaultValue = "false" )
    private boolean showDeprecation;

    /**
     * Set to <code>true</code> to show compilation warnings.
     */
    @Parameter( property = "maven.compiler.showWarnings", defaultValue = "false" )
    private boolean showWarnings;

    /**
     * The -source argument for the Java compiler.
     */
    @Parameter( property = "maven.compiler.source", defaultValue = "1.6" )
    protected String source;

    /**
     * The -target argument for the Java compiler.
     */
    @Parameter( property = "maven.compiler.target", defaultValue = "1.6" )
    protected String target;


    /**
     * The -encoding argument for the Java compiler.
     *
     * @since 2.1
     */
    @Parameter( property = "encoding", defaultValue = "${project.build.sourceEncoding}" )
    private String encoding;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

        Compiler compiler;
        getLog().debug( "Using compiler '" + compilerId + "'." );

        try
        {
            compiler = compilerManager.getCompiler( compilerId );
        }
        catch ( NoSuchCompilerException e )
        {
            throw new MojoExecutionException( "No such compiler '" + e.getCompilerId() + "'." );
        }

        
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();

        compilerConfiguration.setOutputLocation( classesDirectory.getAbsolutePath());

        //compilerConfiguration.setClasspathEntries( getClasspathElements() );

        compilerConfiguration.setSourceLocations( Collections.singletonList(noAnnotationsSourceFolder.getAbsolutePath()) );

        compilerConfiguration.setOptimize( optimize );

        compilerConfiguration.setDebug( debug );

        compilerConfiguration.setVerbose( verbose );

        compilerConfiguration.setShowWarnings( showWarnings );

        compilerConfiguration.setShowDeprecation( showDeprecation );

        compilerConfiguration.setSourceVersion( source );

        compilerConfiguration.setTargetVersion( target );

        compilerConfiguration.setSourceEncoding( encoding );
        
        try {
			compiler.performCompile(compilerConfiguration);
		} catch (CompilerException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

	}

}
