package fr.opensagres.maven.noannotations;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class Tdd {

	RemoveAnnotationsMojo sut = new RemoveAnnotationsMojo();
	String givenAClassWithAnnotations;
	@Test
	public void testName() throws Exception {
		givenJavaFileWithAnnotations();

		final String newSource = sut.cleanupAnnotations(givenAClassWithAnnotations);
		
		annotationsAreRemoved(newSource);

	}

	private String givenJavaFileWithAnnotations() {
		//@formatter:off
		givenAClassWithAnnotations ="package fr.opensagres.maven.noannotations;\n"+
					 "import java.io.Serializable;\n"+
					 "import one.MemberAnnotation;\n"+
					 "import two.PropertyAnnotation;\n"+
					 "import three.MethodAnnotation;\n"+
					 "\n"+	
					 "@MemberAnnotation\n"+
					 "public class Demo implements Serializable {\n"+
					 "\n"+
					 "	@PropertyAnnotation\n"+
					 "	int deprecatedAtttibute;\n"+
					 "\n"+	
					 "\n"+	
					 "	@MethodAnnotation\n"+
					 "	public String toString() {\n"+
					 "		return super.toString();\n"+
					 "	}\n"+
					 "}";
		//@formatter:on
		return givenAClassWithAnnotations;
	}

	private void annotationsAreRemoved(final String newSource) {
		//@formatter:off
		String expectedContent ="package fr.opensagres.maven.noannotations;\n"+
				"import java.io.Serializable;\n"+
				"\n"+
				"public class Demo implements Serializable {\n"+
				"\n"+
				"	int deprecatedAtttibute;\n"+
				"\n"+
				"\n"+
				"	public String toString() {\n"+
				"		return super.toString();\n"+
				"	}\n"+
				"}";
		//@formatter:on
		Assert.assertEquals(expectedContent, newSource);
	}

	@Test
	public void fromUser_java() throws Exception {
		String content = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("User.java").getFile()));
		sut.cleanupAnnotations(content);
		//System.out.println(sut.cleanupAnnotations(content));
		
	}
}
