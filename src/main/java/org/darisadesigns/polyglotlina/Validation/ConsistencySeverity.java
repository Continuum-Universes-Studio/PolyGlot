package org.darisadesigns.polyglotlina.Validation;

/**
 * Severity for consistency checker output.
 */
public enum ConsistencySeverity {
    INFO,
    WARNING,
    ERROR;

    public int toLegacySeverity() {
        return switch (this) {
            case INFO -> 0;
            case WARNING -> 1;
            case ERROR -> 2;
        };
    }
}
