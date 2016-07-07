package fr.opensagres.maven.plugins.noannotations;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;

import fr.opensagres.maven.plugins.noannotations.RemoveAnnotationsMojo;

public class RemoveAnnotationsMojoTest extends AbstractMojoTestCase {

	private RemoveAnnotationsMojo getMojo(String pomXml) throws Exception {
		File testPom = new File(getBasedir(), pomXml);

		RemoveAnnotationsMojo mojo = (RemoveAnnotationsMojo) lookupMojo(
				"removeAnnotations", testPom);
		MavenProject mockMavenProject = getMockMavenProject();
		setVariableValueToObject(mojo, "project", mockMavenProject);
		setVariableValueToObject(mojo, "noAnnotationsSourceFolder", new File(
				mockMavenProject.getBuild().getDirectory() + "/no-dep"));
		assertNotNull(mojo);

		return mojo;
	}

	private MavenProject getMockMavenProject() {
		MavenProject mp = new MavenProject();
		mp.getBuild().setSourceDirectory("target/test-classes/basic-test/src/main/java");
		mp.getBuild().setDirectory("target/test-classes/basic-test/target");

		return mp;
	}

	public void testBasic() throws Exception {
		RemoveAnnotationsMojo mojo = getMojo("target/test-classes/basic-test/plugin-config.xml");

		mojo.execute();

		File noAnnotationsSourceFolder = mojo.getNoAnnotationsSourceFolder();

		assertTrue(noAnnotationsSourceFolder.list()[0].equals("User.java"));

	}
}
