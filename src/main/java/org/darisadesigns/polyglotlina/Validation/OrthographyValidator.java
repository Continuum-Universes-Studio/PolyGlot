package org.darisadesigns.polyglotlina.Validation;

import java.util.List;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode.ProblemType;

/**
 * Checks grapheme-level orthography rules.
 */
public class OrthographyValidator extends AbstractConsistencyValidator {
    @Override
    public void validate(ConsistencyCheckContext context, List<ConsistencyIssue> issues) {
        if (context == null || context.getCore() == null) {
            return;
        }

        var core = context.getCore();
        var propMan = core.getPropertiesManager();

        for (ConWord word : core.getWordCollection().getWordNodes()) {
            if (!propMan.getAlphaOrder().isEmpty() && !propMan.testStringAgainstAlphabet(word.getValue())) {
                issues.add(issue(
                        ConsistencySeverity.ERROR,
                        "orthography.invalid_grapheme",
                        "Orthography uses characters outside the defined alphabet.",
                        word,
                        ProblemType.ConWord,
                        "Suspect characters: \"" + propMan.findBadLetters(word.getValue()) + "\"",
                        "Add the missing characters to the alphabet or normalize the spelling."));
            }
        }

        // Future versions can validate orthographic digraph rules and font
        // coverage here instead of only checking the raw alphabet inventory.
    }
}
