package org.darisadesigns.polyglotlina.Validation;

import java.util.Objects;
import org.darisadesigns.polyglotlina.Nodes.DictNode;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode.ProblemType;

/**
 * Structured issue emitted by the consistency checker.
 */
public class ConsistencyIssue {
    private final ConsistencySeverity severity;
    private final String code;
    private final String message;
    private final DictNode affectedNode;
    private final ProblemType problemType;
    private final String ruleDetails;
    private final String suggestedFix;

    public ConsistencyIssue(ConsistencySeverity severity, String code, String message,
            DictNode affectedNode, ProblemType problemType, String ruleDetails, String suggestedFix) {
        this.severity = severity == null ? ConsistencySeverity.INFO : severity;
        this.code = code == null ? "" : code;
        this.message = message == null ? "" : message;
        this.affectedNode = affectedNode;
        this.problemType = problemType == null ? ProblemType.ConWord : problemType;
        this.ruleDetails = ruleDetails == null ? "" : ruleDetails;
        this.suggestedFix = suggestedFix == null ? "" : suggestedFix;
    }

    public ConsistencySeverity getSeverity() {
        return severity;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public DictNode getAffectedNode() {
        return affectedNode;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public String getRuleDetails() {
        return ruleDetails;
    }

    public String getSuggestedFix() {
        return suggestedFix;
    }

    public LexiconProblemNode toLegacyNode() {
        return new LexiconProblemNode(
                affectedNode,
                message,
                problemType,
                severity.toLegacySeverity(),
                code,
                ruleDetails.isBlank() ? message : ruleDetails,
                suggestedFix);
    }

    @Override
    public boolean equals(Object comp) {
        boolean ret = false;

        if (this == comp) {
            ret = true;
        } else if (comp instanceof ConsistencyIssue issue) {
            ret = severity == issue.severity
                    && Objects.equals(code, issue.code)
                    && Objects.equals(message, issue.message)
                    && Objects.equals(affectedNode, issue.affectedNode)
                    && problemType == issue.problemType
                    && Objects.equals(ruleDetails, issue.ruleDetails)
                    && Objects.equals(suggestedFix, issue.suggestedFix);
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(severity);
        hash = 53 * hash + Objects.hashCode(code);
        hash = 53 * hash + Objects.hashCode(message);
        hash = 53 * hash + Objects.hashCode(affectedNode);
        hash = 53 * hash + Objects.hashCode(problemType);
        hash = 53 * hash + Objects.hashCode(ruleDetails);
        hash = 53 * hash + Objects.hashCode(suggestedFix);
        return hash;
    }
}
