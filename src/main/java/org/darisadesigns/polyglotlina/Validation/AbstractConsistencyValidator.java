package org.darisadesigns.polyglotlina.Validation;

import org.darisadesigns.polyglotlina.Nodes.DictNode;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode.ProblemType;

/**
 * Convenience base for validators.
 */
public abstract class AbstractConsistencyValidator implements ConsistencyValidator {
    protected ConsistencyIssue issue(ConsistencySeverity severity, String code, String message,
            DictNode affectedNode, ProblemType problemType, String details, String suggestedFix) {
        return new ConsistencyIssue(severity, code, message, affectedNode, problemType, details, suggestedFix);
    }
}
