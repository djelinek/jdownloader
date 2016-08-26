# JDownload Manager

## Plugin Repository
	<pluginRepositories>
		<pluginRepository>
			<id>bintray-apodhrad</id>
			<url>http://dl.bintray.com/apodhrad/maven/</url>
			<releases>
				<enabled>true</enabled>
			</releases>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</pluginRepository>
	</pluginRepositories>
	
## Java Library
		
	<dependency>
		<groupId>org.apodhrad.jdownload</groupId>
		<artifactId>jdownload-manager</artifactId>
		<version>1.0.3</version>
	</dependency>

## Maven Plugin
	
	<plugin>
		<groupId>org.apodhrad.jdownload</groupId>
		<artifactId>jdownload-maven-plugin</artifactId>
		<version>1.0.3</version>
		<executions>
			<execution>
				<id>get-fuse</id>
				<phase>package</phase>
				<goals>
					<goal>download</goal>
				</goals>
				<configuration>
					<url>${fuse.url}</url>
					<unpack>true</unpack>
					<outputDirectory>${project.build.directory}</outputDirectory>
				</configuration>
			</execution>
		</executions>
	</plugin>
