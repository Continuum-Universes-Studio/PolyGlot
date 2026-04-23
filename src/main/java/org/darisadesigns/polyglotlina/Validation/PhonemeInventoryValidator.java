package org.darisadesigns.polyglotlina.Validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.darisadesigns.polyglotlina.IPAHandler;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode.ProblemType;

/**
 * Checks inventory-level phoneme / grapheme consistency.
 */
public class PhonemeInventoryValidator extends AbstractConsistencyValidator {
    private static final Set<String> IPA_PUNCTUATION = Set.of(
            " ", "-", ".", "ˈ", "ˌ", "ː", "ˑ", "˙", "͡", "ʲ", "ʷ", "˞", "̥", "̬", "̃", "̩", "̯");

    @Override
    public void validate(ConsistencyCheckContext context, List<ConsistencyIssue> issues) {
        if (context == null || context.getCore() == null) {
            return;
        }

        var core = context.getCore();
        var propMan = core.getPropertiesManager();
        var words = core.getWordCollection().getWordNodes();
        var allowedIpa = new HashSet<>(List.of(IPAHandler.getAllIpaChars()));

        for (ConWord word : words) {
            if (!propMan.getAlphaOrder().isEmpty() && !propMan.testStringAgainstAlphabet(word.getValue())) {
                issues.add(issue(
                        ConsistencySeverity.ERROR,
                        "phoneme.inventory.unknown_grapheme",
                        "Word contains characters not defined in the language inventory.",
                        word,
                        ProblemType.ConWord,
                        "Suspect characters: \"" + propMan.findBadLetters(word.getValue()) + "\"",
                        "Add the missing graphemes to the alphabet or revise the word spelling."));
            }

            String pronunciation;
            try {
                pronunciation = word.getPronunciation();
            } catch (Exception e) {
                issues.add(issue(
                        ConsistencySeverity.ERROR,
                        "phoneme.inventory.pronunciation_regex",
                        "Pronunciation could not be generated for this word.",
                        word,
                        ProblemType.Phonology,
                        e.getLocalizedMessage(),
                        "Fix the pronunciation rules or clear the pronunciation override."));
                continue;
            }

            if (pronunciation != null && !pronunciation.isBlank()) {

                if (!containsOnlyKnownIpa(pronunciation, allowedIpa)) {
                    issues.add(issue(
                            ConsistencySeverity.ERROR,
                            "phoneme.inventory.invalid_ipa",
                            "Pronunciation uses IPA symbols not in the configured inventory.",
                            word,
                            ProblemType.Phonology,
                            "Pronunciation: " + pronunciation,
                            "Add the missing IPA symbols or adjust the pronunciation rules."));
                }
            }
        }

        // Extension point: this can later validate segment inventories against
        // explicit feature bundles or phoneme classes rather than raw symbols.
        for (String alphaChar : propMan.getAlphaOrder().keySet()) {
            String[] ipaMapping = core.getPronunciationMgr().getIpaSoundsPerCharacter().get(alphaChar);
            if (ipaMapping == null || ipaMapping.length == 0) {
                issues.add(issue(
                        ConsistencySeverity.WARNING,
                        "phoneme.inventory.unmapped_grapheme",
                        "A grapheme has no IPA mapping.",
                        null,
                        ProblemType.Phonology,
                        "Grapheme: \"" + alphaChar + "\"",
                        "Add at least one pronunciation rule for this grapheme."));
            }
        }
    }

    private boolean containsOnlyKnownIpa(String pronunciation, Set<String> allowedIpa) {
        for (int i = 0; i < pronunciation.length(); i++) {
            String ch = Character.toString(pronunciation.charAt(i));
            if (Character.isWhitespace(pronunciation.charAt(i)) || IPA_PUNCTUATION.contains(ch)) {
                continue;
            }

            if (!allowedIpa.contains(ch)) {
                return false;
            }
        }

        return true;
    }
}
