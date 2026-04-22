/*
 * Copyright (c) 2026, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT Licence
 * See LICENSE.TXT included with this code to read the full license agreement.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.darisadesigns.polyglotlina;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.darisadesigns.polyglotlina.ManagersCollections.PropertiesManager;
import org.darisadesigns.polyglotlina.Nodes.SoundChangeRule;
import org.darisadesigns.polyglotlina.Nodes.SoundChangeStep;

/**
 * Minimal sound change engine for descendant generation.
 *
 * @author draque
 */
public final class SoundChangeEngine {
    private static final String DELETION_TOKEN = "∅";
    private static final Set<String> FALLBACK_VOWELS = Set.of(
            "a", "e", "i", "o", "u", "y",
            "A", "E", "I", "O", "U", "Y",
            "á", "é", "í", "ó", "ú",
            "à", "è", "ì", "ò", "ù",
            "ä", "ë", "ï", "ö", "ü",
            "æ", "œ", "ø", "å",
            "ə", "ɛ", "ɪ", "ʊ", "ɨ", "ʉ",
            "ɯ", "ɵ", "ɘ", "ɤ", "ɔ", "ɜ",
            "ɞ", "ʌ", "ɐ", "ɶ", "ɑ", "ɒ"
    );

    private SoundChangeEngine() {
    }

    public static List<SoundChangeStep> traceEvolution(String value, List<String> rawRules,
            PropertiesManager propMan) {
        List<SoundChangeStep> ret = new ArrayList<>();
        String current = value == null ? "" : value;
        CVMatcher matcher = new CVMatcher(propMan);

        if (rawRules == null) {
            return ret;
        }

        for (String rawRule : rawRules) {
            if (rawRule == null || rawRule.isBlank()) {
                continue;
            }

            SoundChangeRule rule = parseRule(rawRule);
            String next = applyRule(current, rule, matcher);
            ret.add(new SoundChangeStep(rawRule.trim(), current, next));
            current = next;
        }

        return ret;
    }

    public static String evolve(String value, List<String> rawRules, PropertiesManager propMan) {
        String ret = value == null ? "" : value;

        for (SoundChangeStep step : traceEvolution(ret, rawRules, propMan)) {
            ret = step.getResultValue();
        }

        return ret;
    }

    public static SoundChangeRule parseRule(String rawRule) {
        if (rawRule == null || rawRule.isBlank()) {
            throw new IllegalArgumentException("Rule may not be blank.");
        }

        String compactRule = rawRule.trim();
        int gtIndex = compactRule.indexOf('>');
        int slashIndex = compactRule.indexOf('/', gtIndex + 1);

        if (gtIndex <= 0 || slashIndex <= gtIndex + 1) {
            throw new IllegalArgumentException("Rule must use FROM > TO / ENVIRONMENT syntax.");
        }

        String fromValue = compactRule.substring(0, gtIndex).trim();
        String toValue = compactRule.substring(gtIndex + 1, slashIndex).trim();
        String environment = compactRule.substring(slashIndex + 1).trim().replace(" ", "");
        int underscoreIndex = environment.indexOf('_');

        if (fromValue.isBlank()) {
            throw new IllegalArgumentException("Rule must define a FROM value.");
        }

        if (fromValue.equals(DELETION_TOKEN)) {
            throw new IllegalArgumentException("Deletion is only supported as the TO value.");
        }

        if (environment.isBlank() || underscoreIndex == -1
                || underscoreIndex != environment.lastIndexOf('_')) {
            throw new IllegalArgumentException("Environment must contain exactly one target marker.");
        }

        String left = environment.substring(0, underscoreIndex);
        String right = environment.substring(underscoreIndex + 1);

        return new SoundChangeRule(compactRule, fromValue,
                toValue.equals(DELETION_TOKEN) ? "" : toValue, left, right);
    }

    private static String applyRule(String value, SoundChangeRule rule, CVMatcher matcher) {
        StringBuilder builder = new StringBuilder();
        int i = 0;

        while (i < value.length()) {
            if (value.startsWith(rule.getFromValue(), i)
                    && matchesLeftEnvironment(value, i, rule.getLeftEnvironment(), matcher)
                    && matchesRightEnvironment(value, i + rule.getFromValue().length(),
                            rule.getRightEnvironment(), matcher)) {
                builder.append(rule.getToValue());
                i += rule.getFromValue().length();
            } else {
                builder.append(value.charAt(i));
                i++;
            }
        }

        return builder.toString();
    }

    private static boolean matchesLeftEnvironment(String word, int matchStart, String environment,
            CVMatcher matcher) {
        int index = matchStart;

        for (int i = environment.length() - 1; i >= 0; i--) {
            char token = environment.charAt(i);

            if (token == '#') {
                if (index != 0) {
                    return false;
                }
            } else {
                if (index <= 0) {
                    return false;
                }

                String actual = Character.toString(word.charAt(index - 1));
                if (!tokenMatches(actual, token, matcher)) {
                    return false;
                }

                index--;
            }
        }

        return true;
    }

    private static boolean matchesRightEnvironment(String word, int matchEnd, String environment,
            CVMatcher matcher) {
        int index = matchEnd;

        for (int i = 0; i < environment.length(); i++) {
            char token = environment.charAt(i);

            if (token == '#') {
                if (index != word.length()) {
                    return false;
                }
            } else {
                if (index >= word.length()) {
                    return false;
                }

                String actual = Character.toString(word.charAt(index));
                if (!tokenMatches(actual, token, matcher)) {
                    return false;
                }

                index++;
            }
        }

        return true;
    }

    private static boolean tokenMatches(String actual, char token, CVMatcher matcher) {
        return switch (token) {
            case 'C' -> matcher.isConsonant(actual);
            case 'V' -> matcher.isVowel(actual);
            default -> actual.equals(Character.toString(token));
        };
    }

    private static class CVMatcher {
        private final Set<String> explicitConsonants = new HashSet<>();
        private final Set<String> explicitVowels = new HashSet<>();

        CVMatcher(PropertiesManager propMan) {
            if (propMan == null) {
                return;
            }

            for (String line : propMan.getZompistCategories().split("\n")) {
                line = line.trim();

                if (line.isEmpty() || !line.contains("=")) {
                    continue;
                }

                String[] split = line.split("=", 2);
                if (split.length != 2 || split[0].length() != 1) {
                    continue;
                }

                Set<String> targetSet;
                if (split[0].equals("C")) {
                    targetSet = explicitConsonants;
                } else if (split[0].equals("V")) {
                    targetSet = explicitVowels;
                } else {
                    continue;
                }

                String definition = split[1].trim();
                String splitter = definition.contains(",") ? "," : "(?!^)";
                for (String entry : definition.split(splitter)) {
                    entry = entry.trim();
                    if (!entry.isEmpty()) {
                        targetSet.add(entry);
                    }
                }
            }
        }

        boolean isVowel(String value) {
            if (explicitVowels.contains(value)) {
                return true;
            }

            if (explicitConsonants.contains(value)) {
                return false;
            }

            return FALLBACK_VOWELS.contains(value);
        }

        boolean isConsonant(String value) {
            if (explicitConsonants.contains(value)) {
                return true;
            }

            if (explicitVowels.contains(value)) {
                return false;
            }

            return !isVowel(value);
        }
    }
}
