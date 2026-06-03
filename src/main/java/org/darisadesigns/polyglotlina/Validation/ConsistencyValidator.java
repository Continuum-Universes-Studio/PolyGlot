package org.darisadesigns.polyglotlina.Validation;

import java.util.List;

/**
 * Validator component for a specific consistency domain.
 */
public interface ConsistencyValidator {
    void validate(ConsistencyCheckContext context, List<ConsistencyIssue> issues);
}
