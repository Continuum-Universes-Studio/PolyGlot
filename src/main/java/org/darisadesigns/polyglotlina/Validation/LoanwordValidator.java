package org.darisadesigns.polyglotlina.Validation;

import java.util.List;
import org.darisadesigns.polyglotlina.SoundChangeEngine;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.LexicalRelationType;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode.ProblemType;
import org.darisadesigns.polyglotlina.Nodes.LinkedWordReference;

/**
 * Heuristic validator for loanwords.
 */
public class LoanwordValidator extends AbstractConsistencyValidator {
    @Override
    public void validate(ConsistencyCheckContext context, List<ConsistencyIssue> issues) {
        if (context == null || context.getCore() == null) {
            return;
        }

        var core = context.getCore();
        var rules = core.getPropertiesManager().getLanguageEvolutionProfile().getSoundChangeRules();

        for (ConWord word : core.getWordCollection().getWordNodes()) {
            LinkedWordReference reference = word.getLinkedWordReference();
            if (reference == null || reference.isEmpty() || reference.getRelationType() != LexicalRelationType.LOANWORD) {
                continue;
            }

            var linkedCore = context.loadLinkedCore(reference);
            if (linkedCore == null || reference.getTargetWordId() <= 0
                    || !linkedCore.getWordCollection().exists(reference.getTargetWordId())) {
                continue;
            }

            ConWord sourceWord = linkedCore.getWordCollection().getNodeById(reference.getTargetWordId());
            String evolvedSource = SoundChangeEngine.evolve(sourceWord.getValue(), rules, core.getPropertiesManager());

            if (word.getValue().equals(sourceWord.getValue())) {
                issues.add(issue(
                        ConsistencySeverity.WARNING,
                        "loanword.identity",
                        "Loanword matches the source form exactly.",
                        word,
                        ProblemType.ConWord,
                        "Source: " + sourceWord.getValue(),
                        "Verify that the borrowed form was actually adapted into the target language."));
            } else if (!rules.isEmpty() && !evolvedSource.isBlank() && !word.getValue().equals(evolvedSource)) {
                issues.add(issue(
                        ConsistencySeverity.WARNING,
                        "loanword.sound_change_mismatch",
                        "Borrowed form does not resemble the configured sound-adaptation path.",
                        word,
                        ProblemType.ConWord,
                        "Expected approximately: " + evolvedSource,
                        "Check the borrowing rules or mark the entry as a different relation type."));
            }
        }

        // Extension point: future versions can validate loan adaptation
        // against explicit borrowing rules rather than sound-change heuristics.
    }
}
