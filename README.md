# no-annotations-maven-plugin
A maven plugin to easily cleanup annotations on API Pojos
To use It, simple add to your pom :

			<plugin>
				<groupId>fr.opensagres.maven.plugin</groupId>
				<artifactId>no-annotations-maven-plugin</artifactId>
				<version>0.5.0</version>
				<extensions>true</extensions>
			</plugin>
			
Note : <extensions>true</extensions> is important to override default lifzcycle.			