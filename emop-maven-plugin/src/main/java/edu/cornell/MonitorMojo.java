package edu.cornell;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import edu.cornell.emop.util.Util;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "monitor", requiresDirectInvocation = true, requiresDependencyResolution = ResolutionScope.TEST)
public class MonitorMojo extends AffectedSpecsMojo {

    private String monitorFile = "new-aop-ajc.xml";

    /**
     * The path that specify the Javamop Agent JAR file.
     */
    @Parameter(property = "javamopAgent")
    private String javamopAgent;

    public void execute() throws MojoExecutionException {
        super.execute();
        getLog().info("[eMOP] Invoking the Monitor Mojo...");
        long start = System.currentTimeMillis();
        generateNewMonitorFile();
        if (javamopAgent == null) {
            javamopAgent = getLocalRepository().getBasedir() + File.separator + "javamop-agent"
                    + File.separator + "javamop-agent"
                    + File.separator + "1.0"
                    + File.separator + "javamop-agent-1.0.jar";
        }
        Util.replaceSpecSelectionWithFile(javamopAgent, getArtifactsDir() + File.separator + monitorFile);
        long end = System.currentTimeMillis();
        getLog().info("[eMOP Timer] Generating aop-ajc.xml and replace it takes " + (end - start) + " ms");
        generateThirdPartyExclusion();
    }

    /**
     * Generates a String containing within() pointcuts so that instrumentation is only performed within the
     * packages in the maven project in question, effectively disabling instrumentation in third-party libraries.
     * @return
     */
    private String generateThirdPartyExclusion() {
        Set<String> packages = Util.classFilesWalk(getClassesDirectory(), getClassesDirectory().getAbsolutePath());
        StringBuilder stringBuilder = new StringBuilder();
        for (String packageName : packages) {
            stringBuilder.append("    !within(" + packageName + "..*) &&\n");
        }
        getLog().info("Generated:\n" + stringBuilder.toString());
        return stringBuilder.toString();
    }

    private void generateNewMonitorFile() throws MojoExecutionException {
        try (PrintWriter writer = new PrintWriter(getArtifactsDir() + File.separator + monitorFile)) {
            // Write header
            writer.println("<aspectj>");
            writer.println("<aspects>");
            // Write body
            for (String affectedSpec : affectedSpecs) {
                writer.println("<aspect name=\"mop." + affectedSpec + "\"/>");
            }
            // Write footer
            writer.println("</aspects>");
            // TODO: Hard-coded for now, make optional later (-verbose -showWeaveInfo)
            writer.println("<weaver options=\"-nowarn -Xlint:ignore\"></weaver>");
            writer.println("</aspectj>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
