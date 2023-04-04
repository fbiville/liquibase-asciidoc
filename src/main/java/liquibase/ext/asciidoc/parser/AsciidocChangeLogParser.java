package liquibase.ext.asciidoc.parser;

import liquibase.change.core.RawSQLChange;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.ChangeLogParseException;
import liquibase.parser.ChangeLogParser;
import liquibase.resource.ResourceAccessor;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class AsciidocChangeLogParser implements ChangeLogParser {

    private final Asciidoctor asciidocParser;

    public AsciidocChangeLogParser() {
        Asciidoctor asciidocParser = Asciidoctor.Factory.create();
        asciidocParser.javaExtensionRegistry().includeProcessor(new LiquibaseAdocInclude());
        this.asciidocParser = asciidocParser;
    }

    @Override
    public boolean supports(String changeLogFile, ResourceAccessor resourceAccessor) {
        return changeLogFile.endsWith(".asciidoc") || changeLogFile.endsWith(".adoc");
    }


    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public DatabaseChangeLog parse(String physicalChangeLogLocation, ChangeLogParameters changeLogParameters, ResourceAccessor resourceAccessor) throws ChangeLogParseException {
        DatabaseChangeLog changeLog = new DatabaseChangeLog();
        changeLog.setPhysicalFilePath(physicalChangeLogLocation);
        changeLog.setChangeLogParameters(changeLogParameters);

        try {
            List<StructuralNode> nodes = findCodeListings(physicalChangeLogLocation, resourceAccessor);
            for (StructuralNode node : nodes) {
                Block block = (Block) node;
                if (!targetsLiquibase(node)) {
                    continue;
                }
                ChangeSet changeSet = convertToChangeSet(physicalChangeLogLocation, changeLog, block);
                changeLog.addChangeSet(changeSet);
            }
        } catch (IOException e) {
            throw new ChangeLogParseException(e);
        }
        return changeLog;
    }

    private List<StructuralNode> findCodeListings(String physicalChangeLogLocation, ResourceAccessor resourceAccessor) throws IOException, ChangeLogParseException {
        String content = readContents(resourceAccessor, physicalChangeLogLocation);
        Document document = loadDocument(content);
        Map<Object, Object> selectors = new HashMap<>(2);
        selectors.put("context", ":listing");
        selectors.put("style", "source");
        return document.findBy(selectors);
    }

    private Document loadDocument(String content) throws ChangeLogParseException {
        try {
            return asciidocParser.load(
                    content,
                    Options.builder().safe(SafeMode.SAFE).build()
            );
        } catch (AdocIncludeException e) {
            throw new ChangeLogParseException(e);
        }
    }

    private static String readContents(ResourceAccessor resourceAccessor, String physicalChangeLogLocation) throws IOException {
        try (InputStream stream = resourceAccessor.openStream(null, physicalChangeLogLocation)) {
            if (stream == null) {
                throw new IOException(String.format("could not find change log resource %s", physicalChangeLogLocation));
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }

    private static ChangeSet convertToChangeSet(String location, DatabaseChangeLog changeLog, Block block) throws ChangeLogParseException {
        validateLiquibaseBlock(block);
        ChangeSet changeSet = new ChangeSet(
                (String) block.getAttribute("id"),
                (String) block.getAttribute("author"),
                Boolean.parseBoolean((String) block.getAttribute("runAlways", "false")),
                Boolean.parseBoolean((String) block.getAttribute("runOnChange", "false")),
                location,
                (String) block.getAttribute("contexts", ""),
                (String) block.getAttribute("dbms", ""),
                Boolean.parseBoolean((String) block.getAttribute("runInTransaction", "true")),
                changeLog
        );
        changeSet.addChange(convertToChange(block.getSource()));
        return changeSet;
    }

    private static void validateLiquibaseBlock(Block block) throws ChangeLogParseException {
        Object runOnChange = block.getAttribute("runOnChange");
        if (runOnChange != null && !(isCaseInsensitiveTrue(runOnChange) || isCaseInsensitiveFalse(runOnChange))) {
            throw new ChangeLogParseException(
                    String.format("Liquibase code listing runOnChange attribute must be set to either \"true\" or \"false\", found: %s", runOnChange));
        }
        Object runAlways = block.getAttribute("runAlways");
        if (runAlways != null && !(isCaseInsensitiveTrue(runAlways) || isCaseInsensitiveFalse(runAlways))) {
            throw new ChangeLogParseException(
                    String.format("Liquibase code listing runAlways attribute must be set to either \"true\" or \"false\", found: %s", runAlways));
        }
        Object runInTransaction = block.getAttribute("runInTransaction");
        if (runInTransaction != null && !(isCaseInsensitiveTrue(runInTransaction) || isCaseInsensitiveFalse(runInTransaction))) {
            throw new ChangeLogParseException(
                    String.format("Liquibase code listing runInTransaction attribute must be set to either \"true\" or \"false\", found: %s", runInTransaction));
        }
    }

    private static RawSQLChange convertToChange(String source) {
        RawSQLChange change = new RawSQLChange();
        change.setSql(source);
        return change;
    }

    private static boolean targetsLiquibase(StructuralNode node) {
        if (isCaseInsensitiveFalse(node.getAttribute("liquibase", "true"))) {
            return false;
        }
        return node.hasAttribute("id") && node.hasAttribute("author");
    }

    private static boolean isCaseInsensitiveTrue(Object value) {
        if (!(value instanceof String)) {
            return false;
        }
        return ((String) value).toLowerCase(Locale.ENGLISH).equals("true");
    }

    private static boolean isCaseInsensitiveFalse(Object value) {
        if (!(value instanceof String)) {
            return false;
        }
        return ((String) value).toLowerCase(Locale.ENGLISH).equals("false");
    }
}
