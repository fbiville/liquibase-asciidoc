package liquibase.ext.asciidoc.parser;

import liquibase.Scope;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ResourceAccessor;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

class LiquibaseAdocInclude extends IncludeProcessor {

    @Override
    public boolean handles(String target) {
        return true;
    }

    @Override
    public void process(Document document, PreprocessorReader reader, String target, Map<String, Object> attributes) {
        try {
            reader.pushInclude(
                    readContents(Scope.getCurrentScope().getResourceAccessor(), target),
                    target,
                    target,
                    0,
                    attributes
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readContents(ResourceAccessor resourceAccessor, String physicalChangeLogLocation) throws IOException {
        try (InputStream stream = resourceAccessor.openStream(null, physicalChangeLogLocation)) {
            if (stream == null) {
                throw new AdocIncludeException("could not find change log resource to include %s", physicalChangeLogLocation);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }
}
