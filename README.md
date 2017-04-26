# Apache Ant task development task

Use previous Apache Ant build script task as foundation.
Create a project using following structure:
* ant
    * lib-ext
    * build.xml
* src
* pom.xml

Apache Maven should be used for compilation, copying of jar with custom task to lib-ext folder and packaging of artifact.
Project artifact should be represented as zip package, containing 
* lib-ext
* build.xml
Create Apache Ant task that renames input from ZIP package files under temp dir. 
It should add postfix to the filename: `input.html -> input-${ job.id}.html`.
User should be able to send job.id using parameter via command line `ant -Djob.id=x3q4hkj97zwp4`.
Default value for postfix should be current timestamp.
Custom task must be defined with ant.coreLoader.

Sample of definition of a custom ant task with ant.coreLoader reference :
```xml
<project xmlns:lib="antlib:com.epam.training" basedir="." default="default-end" name="test-project">
    <taskdef
            uri="antlib:com.epam.training"
            resource="com/epam/training/antlib.xml"
            classpathref="project.classpath"
            loaderref="ant.coreLoader"/>

   <target name="rename-files">
        <lib:rename-files destDir="${dir.temp}” jobId="${job.id}">
            <fileset dir="${dir.temp}">
                <include name="*.xml"/>
                <include name="*.html"/>
                <include name="*.xhtml"/>
            </fileset>
        </lib:rename-files >
   </target>

</project>
```

Attach sample input files along with project sources.
