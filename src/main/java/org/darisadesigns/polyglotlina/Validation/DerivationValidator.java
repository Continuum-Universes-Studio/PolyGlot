package org.darisadesigns.polyglotlina.Validation;

import java.util.List;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.DescendantLink;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode.ProblemType;

/**
 * Validates derivational metadata on lexicon entries.
 */
public class DerivationValidator extends AbstractConsistencyValidator {
    @Override
    public void validate(ConsistencyCheckContext context, List<ConsistencyIssue> issues) {
        if (context == null || context.getCore() == null) {
            return;
        }

        var core = context.getCore();
        for (ConWord word : core.getWordCollection().getWordNodes()) {
            DescendantLink link = word.getDescendantLink();
            if (link == null || link.isEmpty()) {
                continue;
            }

            if (link.getParentWordId() <= 0 && link.getParentWordValue().isBlank()) {
                issues.add(issue(
                        ConsistencySeverity.ERROR,
                        "derivation.link.missing_parent",
                        "Derived entry does not point to a valid parent.",
                        word,
                        ProblemType.ConWord,
                        "No parent word id or snapshot is stored.",
                        "Select a valid ancestor word for this entry."));
                continue;
            }

            if (link.getParentWordId() > 0 && !core.getWordCollection().exists(link.getParentWordId())) {
                issues.add(issue(
                        ConsistencySeverity.ERROR,
                        "derivation.link.unknown_parent",
                        "Derived entry references a parent word that no longer exists.",
                        word,
                        ProblemType.ConWord,
                        "Parent id: " + link.getParentWordId(),
                        "Re-link the derived form to an existing ancestor or remove the derivation record."));
                continue;
            }

            if (link.getParentWordId() > 0) {
                ConWord parent = core.getWordCollection().getNodeById(link.getParentWordId());
                if (!parent.getValue().equals(link.getParentWordValue())) {
                    issues.add(issue(
                            ConsistencySeverity.WARNING,
                            "derivation.link.snapshot_changed",
                            "Stored parent snapshot differs from the current source entry.",
                            word,
                            ProblemType.ConWord,
                            "Stored: " + link.getParentWordValue() + " / Current: " + parent.getValue(),
                            "Refresh the derivation snapshot if the change was intentional."));
                }
            }
        }

        // Later passes can compare derivational chains against structured
        // morphology and sound-change traces instead of only checking snapshots.
    }
}
