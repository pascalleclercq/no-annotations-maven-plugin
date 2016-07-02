package fr.opensagres.maven.noannotations;


import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

@Mojo(name = "removeAnnotations", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class RemoveAnnotationsMojo
    extends AbstractMojo
{

    /**
     * Location of the folder.
     */
    @Parameter( defaultValue = "${project.build.directory}/no-dep", property = "no-dep-source-folder", required = false )
    private File noAnnotationsSourceFolder;

    @Component
    private MavenProject project;

    
    
    public void execute()
        throws MojoExecutionException
    {
    	
        transform(new File(project.getBuild().getSourceDirectory()));
    }


	
     
    @Parameter
    private String[] includes = {"**/*.java"};
 
 
    @Parameter
    private String[] excludes;
 

    
    /**
     * Scans a single directory.
     *
     * @param root Directory to scan
     * @throws MojoExecutionException in case of IO errors
     */
    private void transform(File root) throws MojoExecutionException {
        final Log log = getLog();
         
        if (!root.exists()) {
            return;
        }
 
        log.info("scanning source file directory '" + root + "'");
         
        final DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.setIncludes(includes);
        directoryScanner.setExcludes(excludes);
        directoryScanner.setBasedir(root);
        directoryScanner.scan();
 
        for (String fileName : directoryScanner.getIncludedFiles()) {
            final File file = new File(root, fileName);
            try {
				String content = FileUtils.readFileToString(file);
				String noAnnotations = cleanupAnnotations(content);
				File target = new File(noAnnotationsSourceFolder, fileName);
				FileUtils.writeStringToFile(target, noAnnotations);
			} catch (IOException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}
        }       
    }

    
    public void setProject(MavenProject project) {
        this.project = project;
    }
    

	String cleanupAnnotations(String content) throws MojoExecutionException {
		Document doc = new Document(content);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(doc.get().toCharArray());
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.recordModifications();
		
		final Set<Name> annotations = new HashSet<Name>();
		collectAndRemoveAnnotations(cu, annotations);
		removeUnusedImports(cu, annotations);
		TextEdit edits = cu.rewrite(doc, null);
		try {
			edits.apply(doc);
		} catch (MalformedTreeException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (BadLocationException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return doc.get();
	}

	private void removeUnusedImports(final CompilationUnit cu,
			final Set<Name> annotations) {
		cu.accept(new ASTVisitor() {
			
			@Override
			public boolean visit(ImportDeclaration node) {
				for (Name name : annotations) {
					if(node.getName().getFullyQualifiedName().endsWith("."+name.getFullyQualifiedName())) {
						node.delete();
					}
				}
				return super.visit(node);
			}
		});
	}

	private void collectAndRemoveAnnotations(final CompilationUnit cu,
			final Set<Name> annotations) {
		
		cu.accept(new ASTVisitor() {

			@Override
			public boolean visit(SingleMemberAnnotation node) {
				
				annotations.add(node.getTypeName());
				node.delete();
				return super.visit(node);
			}
			@Override
			public boolean visit(MarkerAnnotation node) {
				annotations.add(node.getTypeName());
				node.delete();
				return super.visit(node);
			}
			
			@Override
			public boolean visit(NormalAnnotation node) {
				annotations.add(node.getTypeName());
				node.delete();
				return super.visit(node);
			}
		});
	}
}
