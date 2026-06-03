package org.darisadesigns.polyglotlina.Validation;

import java.util.List;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode.ProblemType;

/**
 * Checks word shape against phonotactic constraints.
 */
public class PhonotacticsValidator extends AbstractConsistencyValidator {
    @Override
    public void validate(ConsistencyCheckContext context, List<ConsistencyIssue> issues) {
        if (context == null || context.getCore() == null) {
            return;
        }

        var core = context.getCore();
        var procMan = core.getPronunciationMgr();

        for (ConWord word : core.getWordCollection().getWordNodes()) {
            String phonotacticForm = getPhonotacticForm(word);
            if (phonotacticForm.isBlank()) {
                continue;
            }

            String[] illegalClusters = procMan.findIllegalClusters(phonotacticForm);
            if (illegalClusters.length > 0) {
                issues.add(issue(
                        ConsistencySeverity.ERROR,
                        "phonotactics.illegal_cluster",
                        "Word contains an illegal consonant or vowel cluster.",
                        word,
                        ProblemType.ConWord,
                        "Illegal clusters: " + String.join(", ", illegalClusters),
                        "Revise the word form or update the phonotactic restrictions."));
            }

            if (procMan.isSyllableCompositionEnabled() && !procMan.canComposeSyllables(phonotacticForm)) {
                issues.add(issue(
                        ConsistencySeverity.WARNING,
                        "phonotactics.invalid_syllable_shape",
                        "Word cannot be segmented using the current syllable inventory.",
                        word,
                        ProblemType.ConWord,
                        "Syllable inventory may not cover this form.",
                        "Add the syllable shape to the inventory or revise the word."));
            }
        }

        // The next pass can validate explicit onset/coda templates once those
        // inventories are exposed in a structured form.
    }

    private String getPhonotacticForm(ConWord word) {
        if (word == null) {
            return "";
        }

        try {
            String pronunciation = word.getPronunciation();
            if (pronunciation != null && !pronunciation.isBlank()) {
                return pronunciation;
            }
        } catch (Exception e) {
            // Fall back to the orthographic form below.
        }

        return word.getValue() == null ? "" : word.getValue();
    }
}
