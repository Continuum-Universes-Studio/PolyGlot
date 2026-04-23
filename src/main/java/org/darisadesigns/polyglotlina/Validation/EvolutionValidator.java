package org.darisadesigns.polyglotlina.Validation;

import java.util.List;
import org.darisadesigns.polyglotlina.SoundChangeEngine;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.DescendantLink;
import org.darisadesigns.polyglotlina.Nodes.LexicalRelationType;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode.ProblemType;
import org.darisadesigns.polyglotlina.Nodes.LinkedWordReference;

/**
 * Heuristic validator for historical and inherited forms.
 */
public class EvolutionValidator extends AbstractConsistencyValidator {
    @Override
    public void validate(ConsistencyCheckContext context, List<ConsistencyIssue> issues) {
        if (context == null || context.getCore() == null) {
            return;
        }

        var core = context.getCore();
        var rules = core.getPropertiesManager().getLanguageEvolutionProfile().getSoundChangeRules();

        for (ConWord word : core.getWordCollection().getWordNodes()) {
            validateDescendantLink(word, rules, core, issues);
            validateInheritedReference(context, word, rules, issues);
        }

        // Future work: sound-change parsing can be expanded to support
        // directionality, dated strata, and intermediate ancestor nodes.
    }

    private void validateDescendantLink(ConWord word, List<String> rules, 
            org.darisadesigns.polyglotlina.DictCore core, List<ConsistencyIssue> issues) {
        DescendantLink link = word.getDescendantLink();
        if (link == null || link.isEmpty()) {
            return;
        }

        if (link.getParentWordValue().isBlank()) {
            issues.add(issue(
                    ConsistencySeverity.INFO,
                    "evolution.descendant.snapshot_missing",
                    "Derived form is missing a parent snapshot.",
                    word,
                    ProblemType.ConWord,
                    "The link exists, but no parent word snapshot was stored.",
                    "Refresh the derivation metadata so future checks can verify it."));
            return;
        }

        String evolved = SoundChangeEngine.evolve(link.getParentWordValue(), rules, core.getPropertiesManager());
        if (rules.isEmpty()) {
            issues.add(issue(
                    ConsistencySeverity.INFO,
                    "evolution.descendant.no_rules",
                    "Descendant form exists but no sound-change rules are configured.",
                    word,
                    ProblemType.ConWord,
                    "Parent snapshot: " + link.getParentWordValue(),
                    "Add historical sound changes to enable stricter validation."));
        } else if (!evolved.isBlank() && !word.getValue().equals(evolved)) {
            issues.add(issue(
                    ConsistencySeverity.WARNING,
                    "evolution.descendant.mismatch",
                    "Derived form does not match the current sound-change profile.",
                    word,
                    ProblemType.ConWord,
                    "Expected approximately: " + evolved,
                    "Check the sound-change profile or mark the derivation as uncertain."));
        }
    }

    private void validateInheritedReference(ConsistencyCheckContext context, ConWord word,
            List<String> rules, List<ConsistencyIssue> issues) {
        LinkedWordReference reference = word.getLinkedWordReference();
        if (reference == null || reference.isEmpty() || reference.getRelationType() != LexicalRelationType.INHERITED) {
            return;
        }

        var linkedCore = context.loadLinkedCore(reference);
        if (linkedCore == null || reference.getTargetWordId() <= 0
                || !linkedCore.getWordCollection().exists(reference.getTargetWordId())) {
            return;
        }

        ConWord sourceWord = linkedCore.getWordCollection().getNodeById(reference.getTargetWordId());
        String evolved = SoundChangeEngine.evolve(sourceWord.getValue(), rules,
                context.getCore().getPropertiesManager());

        if (rules.isEmpty()) {
            issues.add(issue(
                    ConsistencySeverity.INFO,
                    "evolution.inherited.no_rules",
                    "Inherited form exists but no sound-change rules are configured.",
                    word,
                    ProblemType.ConWord,
                    "Source: " + sourceWord.getValue(),
                    "Add historical sound changes to enable stricter validation."));
        } else if (!evolved.isBlank() && !word.getValue().equals(evolved)) {
            issues.add(issue(
                    ConsistencySeverity.WARNING,
                    "evolution.inherited.mismatch",
                    "Inherited form does not match the configured sound-change profile.",
                    word,
                    ProblemType.ConWord,
                    "Expected approximately: " + evolved,
                    "Check the inheritance relation or the sound-change rules."));
        }
    }
}
