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

import TestResources.DummyCore;
import java.util.ArrayList;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.MorphologyCondition;
import org.darisadesigns.polyglotlina.Nodes.MorphologyConditionOperator;
import org.darisadesigns.polyglotlina.Nodes.MorphologyOperationType;
import org.darisadesigns.polyglotlina.Nodes.MorphologyRule;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.Nodes.WordClass;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MorphologyEngineTest {

    @Test
    public void testOrderedRuleChaining() throws Exception {
        System.out.println("MorphologyEngineTest.testOrderedRuleChaining");

        TestContext context = buildContext("dor");
        MorphologyRule ruleOne = new MorphologyRule(context.typeId(), "plural");
        MorphologyRule ruleTwo = new MorphologyRule(context.typeId(), "plural");
        MorphologyEngine engine = new MorphologyEngine();

        ruleOne.setOrder(1);
        ruleOne.setOperationType(MorphologyOperationType.append_suffix);
        ruleOne.setValue1("en");

        ruleTwo.setOrder(2);
        ruleTwo.setOperationType(MorphologyOperationType.replace_regex);
        ruleTwo.setValue1("ren$");
        ruleTwo.setValue2("rin");

        assertEquals("dorin", engine.apply(
                context.word(),
                "plural",
                "Plural",
                new MorphologyRule[]{ruleOne, ruleTwo},
                new ArrayList<>()).getForm());
    }

    @Test
    public void testConditionOperators() throws Exception {
        System.out.println("MorphologyEngineTest.testConditionOperators");

        TestContext context = buildContext("tala");
        MorphologyEngine engine = new MorphologyEngine();
        MorphologyRule[] rules = new MorphologyRule[8];

        rules[0] = suffixRule(context.typeId(), 1, "a", new MorphologyCondition("partOfSpeech", MorphologyConditionOperator.equals, "noun"));
        rules[1] = suffixRule(context.typeId(), 2, "b", new MorphologyCondition("partOfSpeech", MorphologyConditionOperator.not_equals, "verb"));
        rules[2] = suffixRule(context.typeId(), 3, "c", new MorphologyCondition("form", MorphologyConditionOperator.ends_with, "b"));
        rules[3] = suffixRule(context.typeId(), 4, "d", new MorphologyCondition("form", MorphologyConditionOperator.starts_with, "talaa"));
        rules[4] = suffixRule(context.typeId(), 5, "e", new MorphologyCondition("form", MorphologyConditionOperator.contains, "aabc"));
        rules[5] = suffixRule(context.typeId(), 6, "f", new MorphologyCondition("form", MorphologyConditionOperator.matches_regex, "talaabcde"));
        rules[6] = suffixRule(context.typeId(), 7, "g", new MorphologyCondition("nounClass", MorphologyConditionOperator.has_feature, ""));
        rules[7] = suffixRule(context.typeId(), 8, "h", new MorphologyCondition("gender", MorphologyConditionOperator.lacks_feature, ""));

        assertEquals("talabcdefgh", engine.apply(
                context.word(),
                "plural",
                "Plural",
                rules,
                new ArrayList<>()).getForm());
    }

    @Test
    public void testSetFeatureAffectsLaterRules() throws Exception {
        System.out.println("MorphologyEngineTest.testSetFeatureAffectsLaterRules");

        TestContext context = buildContext("kar");
        MorphologyRule setFeature = new MorphologyRule(context.typeId(), "plural");
        MorphologyRule suffix = new MorphologyRule(context.typeId(), "plural");
        MorphologyEngine engine = new MorphologyEngine();

        setFeature.setOrder(1);
        setFeature.setOperationType(MorphologyOperationType.set_feature);
        setFeature.setValue1("number");
        setFeature.setValue2("plural");

        suffix.setOrder(2);
        suffix.setOperationType(MorphologyOperationType.append_suffix);
        suffix.setValue1("im");
        suffix.addCondition(new MorphologyCondition("number", MorphologyConditionOperator.equals, "plural"));

        assertEquals("karim", engine.apply(
                context.word(),
                "plural",
                "Plural",
                new MorphologyRule[]{setFeature, suffix},
                new ArrayList<>()).getForm());
    }

    @Test
    public void testCopyFormResetsCurrentForm() throws Exception {
        System.out.println("MorphologyEngineTest.testCopyFormResetsCurrentForm");

        TestContext context = buildContext("kar");
        MorphologyEngine engine = new MorphologyEngine();
        MorphologyRule appendEn = new MorphologyRule(context.typeId(), "plural");
        MorphologyRule resetToLemma = new MorphologyRule(context.typeId(), "plural");
        MorphologyRule appendIm = new MorphologyRule(context.typeId(), "plural");

        appendEn.setOrder(1);
        appendEn.setOperationType(MorphologyOperationType.append_suffix);
        appendEn.setValue1("en");

        resetToLemma.setOrder(2);
        resetToLemma.setOperationType(MorphologyOperationType.copy_form);
        resetToLemma.setValue1("lemma");

        appendIm.setOrder(3);
        appendIm.setOperationType(MorphologyOperationType.append_suffix);
        appendIm.setValue1("im");

        assertEquals("karim", engine.apply(
                context.word(),
                "plural",
                "Plural",
                new MorphologyRule[]{appendEn, resetToLemma, appendIm},
                new ArrayList<>()).getForm());
    }

    @Test
    public void testLemmaAndStemBuiltIns() throws Exception {
        System.out.println("MorphologyEngineTest.testLemmaAndStemBuiltIns");

        TestContext context = buildContext("kar");
        MorphologyEngine engine = new MorphologyEngine();

        assertEquals("kar", engine.apply(
                context.word(),
                "lemma",
                "Lemma",
                new MorphologyRule[0],
                new ArrayList<>()).getForm());
        assertEquals("kar", engine.apply(
                context.word(),
                "stem",
                "Stem",
                new MorphologyRule[0],
                new ArrayList<>()).getForm());
    }

    private MorphologyRule suffixRule(int typeId, int order, String suffix, MorphologyCondition condition) {
        MorphologyRule rule = new MorphologyRule(typeId, "plural");
        rule.setOrder(order);
        rule.setOperationType(MorphologyOperationType.append_suffix);
        rule.setValue1(suffix);
        rule.addCondition(condition);
        return rule;
    }

    private TestContext buildContext(String form) throws Exception {
        DictCore core = DummyCore.newCore();
        TypeNode noun = new TypeNode();
        noun.setValue("noun");
        int typeId = core.getTypes().addNode(noun);

        WordClass nounClass = new WordClass();
        nounClass.setValue("nounClass");
        nounClass.deleteApplyType(-1);
        nounClass.addApplyType(typeId);
        int strongId = nounClass.addValue("strong").getId();
        int classId = core.getWordClassCollection().addNode(nounClass);

        ConWord word = new ConWord();
        word.setValue(form);
        word.setWordTypeId(typeId);
        word.setClassValue(classId, strongId);
        int wordId = core.getWordCollection().addWord(word);

        return new TestContext(core.getWordCollection().getNodeById(wordId), typeId);
    }

    private record TestContext(ConWord word, int typeId) {
    }
}
