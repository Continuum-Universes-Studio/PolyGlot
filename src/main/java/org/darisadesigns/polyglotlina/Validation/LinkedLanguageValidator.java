package org.darisadesigns.polyglotlina.Validation;

import java.util.List;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode.ProblemType;
import org.darisadesigns.polyglotlina.Nodes.LinkedLanguage;
import org.darisadesigns.polyglotlina.Nodes.LinkedWordReference;

/**
 * Validates cross-language lexical references.
 */
public class LinkedLanguageValidator extends AbstractConsistencyValidator {
    @Override
    public void validate(ConsistencyCheckContext context, List<ConsistencyIssue> issues) {
        if (context == null || context.getCore() == null) {
            return;
        }

        var core = context.getCore();
        for (ConWord word : core.getWordCollection().getWordNodes()) {
            LinkedWordReference reference = word.getLinkedWordReference();
            if (reference == null || reference.isEmpty()) {
                continue;
            }

            String resolvedPath = reference.getResolvedTargetFile(core);
            LinkedLanguage linkedLanguage = context.findLinkedLanguage(resolvedPath);

            if (linkedLanguage == null) {
                issues.add(issue(
                        ConsistencySeverity.WARNING,
                        "linked.word.unknown_language",
                        "Word references a language that is not registered in this project.",
                        word,
                        ProblemType.ConWord,
                        "Linked path: " + resolvedPath,
                        "Add the language to the project linked-language list or update the link path."));
            }

            if (reference.getTargetWordId() <= 0
                    && reference.getCachedTargetWordValue().isBlank()
                    && reference.getCachedTargetWordDefinition().isBlank()) {
                issues.add(issue(
                        ConsistencySeverity.INFO,
                        "linked.word.language_only",
                        "Linked language is selected but no specific source word is linked yet.",
                        word,
                        ProblemType.ConWord,
                        "Language: " + reference.getCachedTargetLanguageName(),
                        "Select a source word if this entry should represent a specific loanword or cognate."));
            }

            var linkedCore = context.loadLinkedCore(reference);
            if (linkedCore == null) {
                continue;
            }

            if (reference.getTargetWordId() > 0) {
                if (!linkedCore.getWordCollection().exists(reference.getTargetWordId())) {
                    issues.add(issue(
                            ConsistencySeverity.ERROR,
                            "linked.word.missing_source_word",
                            "Linked word id no longer exists in the source language.",
                            word,
                            ProblemType.ConWord,
                            "Source id: " + reference.getTargetWordId(),
                            "Re-select the source word in the linked language."));
                    continue;
                }

                ConWord sourceWord = linkedCore.getWordCollection().getNodeById(reference.getTargetWordId());
                if (!sourceWord.getValue().equals(reference.getCachedTargetWordValue())) {
                    issues.add(issue(
                            ConsistencySeverity.WARNING,
                            "linked.word.snapshot_mismatch",
                            "Linked word snapshot differs from the current source entry.",
                            word,
                            ProblemType.ConWord,
                            "Stored: " + reference.getCachedTargetWordValue()
                                    + " / Current: " + sourceWord.getValue(),
                            "Refresh the linked-word snapshot if the source change was intentional."));
                }
            }
        }

        // Future passes can inspect semantic links and multi-word source
        // matches once the linked-word data model grows richer.
    }
}
