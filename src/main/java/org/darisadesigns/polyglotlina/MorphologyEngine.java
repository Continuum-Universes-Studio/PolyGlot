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
import java.util.Arrays;
import java.util.List;
import org.darisadesigns.polyglotlina.ManagersCollections.ConjugationManager;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.MorphologyCondition;
import org.darisadesigns.polyglotlina.Nodes.MorphologyConditionOperator;
import org.darisadesigns.polyglotlina.Nodes.MorphologyFeatureSet;
import org.darisadesigns.polyglotlina.Nodes.MorphologyResult;
import org.darisadesigns.polyglotlina.Nodes.MorphologyRule;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;

public class MorphologyEngine {

    public MorphologyResult apply(ConWord word,
            String targetKey,
            String targetLabel,
            MorphologyRule[] rules,
            List<String> debugEntries) throws Exception {
        String lemma = word.getValue();
        String stem = word.getValue();
        String partOfSpeech = "";
        MorphologyFeatureSet features = MorphologyFeatureSet.fromWord(word);

        if (word.getCore() != null) {
            TypeNode type = word.getCore().getTypes().getNodeById(word.getWordTypeId());

            if (type != null) {
                partOfSpeech = type.getValue();
            }
        }

        MorphologyResult result = new MorphologyResult(lemma, features);
        List<MorphologyRule> orderedRules = new ArrayList<>(Arrays.asList(rules));
        orderedRules.sort(null);

        for (MorphologyRule rule : orderedRules) {
            StringBuilder debug = new StringBuilder("--------------------------------------\n");
            debug.append("Morphology Rule: ")
                    .append(rule.getName().isBlank() ? "(unnamed)" : rule.getName())
                    .append("\n");

            if (!rule.isEnabled()) {
                debug.append("    Rule disabled. Rule will not be applied.\n");
                debugEntries.add(debug.toString());
                continue;
            }

            if (!conditionsMatch(rule, result, lemma, stem, targetKey, targetLabel, partOfSpeech, debug)) {
                debugEntries.add(debug.toString());
                continue;
            }

            String before = result.getForm();
            applyRule(rule, result, lemma, stem, targetKey, targetLabel, partOfSpeech);
            debug.append("    Effect: ").append(before).append(" -> ").append(result.getForm()).append("\n");
            debugEntries.add(debug.toString());
        }

        return result;
    }

    private boolean conditionsMatch(MorphologyRule rule,
            MorphologyResult result,
            String lemma,
            String stem,
            String targetKey,
            String targetLabel,
            String partOfSpeech,
            StringBuilder debug) throws Exception {
        for (MorphologyCondition condition : rule.getConditions()) {
            boolean matches = conditionMatches(condition, result, lemma, stem, targetKey, targetLabel, partOfSpeech);

            debug.append("    Condition: ")
                    .append(condition.getFieldName())
                    .append(" ")
                    .append(condition.getOperator().name())
                    .append(" ")
                    .append(condition.getValue())
                    .append(" -> ")
                    .append(matches ? "matched" : "did not match")
                    .append("\n");

            if (!matches) {
                return false;
            }
        }

        if (!rule.getConditions().isEmpty()) {
            debug.append("    All conditions matched. Rule will be applied.\n");
        } else {
            debug.append("    Rule has no conditions. Rule will be applied.\n");
        }

        return true;
    }

    private boolean conditionMatches(MorphologyCondition condition,
            MorphologyResult result,
            String lemma,
            String stem,
            String targetKey,
            String targetLabel,
            String partOfSpeech) throws Exception {
        MorphologyConditionOperator operator = condition.getOperator();
        String candidate = resolveFieldValue(condition.getFieldName(), result, lemma, stem, targetKey, targetLabel, partOfSpeech);
        String expected = condition.getValue();

        return switch (operator) {
            case equals -> candidate.equals(expected);
            case not_equals -> !candidate.equals(expected);
            case ends_with -> candidate.endsWith(expected);
            case starts_with -> candidate.startsWith(expected);
            case contains -> candidate.contains(expected);
            case matches_regex -> candidate.matches(expected);
            case has_feature -> hasField(condition.getFieldName(), result, lemma, stem, targetKey, targetLabel, partOfSpeech);
            case lacks_feature -> !hasField(condition.getFieldName(), result, lemma, stem, targetKey, targetLabel, partOfSpeech);
        };
    }

    private void applyRule(MorphologyRule rule,
            MorphologyResult result,
            String lemma,
            String stem,
            String targetKey,
            String targetLabel,
            String partOfSpeech) throws Exception {
        String form = result.getForm();

        switch (rule.getOperationType()) {
            case append_suffix -> result.setForm(form + rule.getValue1());
            case prepend_prefix -> result.setForm(rule.getValue1() + form);
            case replace_regex -> result.setForm(form.replaceAll(rule.getValue1(), rule.getValue2()));
            case replace_literal -> result.setForm(form.replace(rule.getValue1(), rule.getValue2()));
            case delete_regex -> result.setForm(form.replaceAll(rule.getValue1(), ""));
            case set_feature -> result.getFeatures().set(rule.getValue1(), rule.getValue2());
            case copy_form -> result.setForm(resolveFieldValue(
                    rule.getValue1().isBlank() ? ConjugationManager.MORPH_TARGET_LEMMA : rule.getValue1(),
                    result,
                    lemma,
                    stem,
                    targetKey,
                    targetLabel,
                    partOfSpeech));
        }
    }

    private boolean hasField(String fieldName,
            MorphologyResult result,
            String lemma,
            String stem,
            String targetKey,
            String targetLabel,
            String partOfSpeech) {
        String normalized = MorphologyFeatureSet.normalize(fieldName);

        return switch (normalized) {
            case "form" -> !result.getForm().isBlank();
            case ConjugationManager.MORPH_TARGET_LEMMA -> !lemma.isBlank();
            case ConjugationManager.MORPH_TARGET_STEM -> !stem.isBlank();
            case "target" -> !targetKey.isBlank();
            case "targetlabel" -> !targetLabel.isBlank();
            case "partofspeech" -> !partOfSpeech.isBlank();
            default -> result.getFeatures().has(fieldName);
        };
    }

    private String resolveFieldValue(String fieldName,
            MorphologyResult result,
            String lemma,
            String stem,
            String targetKey,
            String targetLabel,
            String partOfSpeech) {
        String normalized = MorphologyFeatureSet.normalize(fieldName);

        return switch (normalized) {
            case "form" -> result.getForm();
            case ConjugationManager.MORPH_TARGET_LEMMA -> lemma;
            case ConjugationManager.MORPH_TARGET_STEM -> stem;
            case "target" -> targetKey;
            case "targetlabel" -> targetLabel;
            case "partofspeech" -> partOfSpeech;
            default -> result.getFeatures().get(fieldName);
        };
    }
}
