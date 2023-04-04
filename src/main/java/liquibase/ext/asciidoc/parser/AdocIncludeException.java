package liquibase.ext.asciidoc.parser;

class AdocIncludeException extends RuntimeException {

    public AdocIncludeException(String message, Object... args) {
        super(String.format(message, args));
    }
}
