/*
 * Copyright (c) 2018-2023, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT Licence
 * See LICENSE.TXT included with this code to read the full license agreement.

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
package org.darisadesigns.polyglotlina.ManagersCollections;
import TestResources.DummyCore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.darisadesigns.polyglotlina.Desktop.DesktopIOHandler;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.ConjugationGenRule;
import org.darisadesigns.polyglotlina.Nodes.ConjugationGenTransform;
import org.darisadesigns.polyglotlina.Nodes.ConjugationNode;
import org.darisadesigns.polyglotlina.Nodes.ConjugationPair;
import org.darisadesigns.polyglotlina.Nodes.MorphologyCondition;
import org.darisadesigns.polyglotlina.Nodes.MorphologyConditionOperator;
import org.darisadesigns.polyglotlina.Nodes.MorphologyOperationType;
import org.darisadesigns.polyglotlina.Nodes.MorphologyRule;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.Nodes.WordClass;
import org.darisadesigns.polyglotlina.PGTUtil;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author DThompson
 */
public class ConjugationManagerTest {
    final DictCore core;

    public ConjugationManagerTest() {
        core = DummyCore.newCore();
    }
    
    @Test
    public void testZeroDimNoExtra() {
        try {
            System.out.println("ConjugationManagerTest.testZeroDimNoExtra");
            core.readFile(PGTUtil.TESTRESOURCES + "zero_dim_zero_extra_zero_dep.pgd");
            ConjugationManager decMan = core.getConjugationManager();
            ConWord word = core.getWordCollection().getWordNodes()[0];

            assertEquals(0, decMan.getAllCombinedIds(word.getWordTypeId()).length);
            assertEquals(0, decMan.getDimensionalConjugationListWord(word.getId()).length);
            assertEquals(decMan.getDeprecatedForms(word).size(), 0);
            assertEquals(0, decMan.getSingletonConjugationList(word.getId()).length);
            assertEquals(0, decMan.getDimensionalConjugationListWord(word.getId()).length);
            assertEquals(decMan.getWordConjugation(word.getId()).size(), 0);
        } catch (IOException | IllegalStateException | ParserConfigurationException e) {
            DesktopIOHandler.getInstance().writeErrorLog(e, "testZeroDimNoExtra");
            fail(e);
        }
    }
    
    @Test
    public void testOneDimNoExtra() {
        try {
            System.out.println("ConjugationManagerTest.testOneDimNoExtra");
            core.readFile(PGTUtil.TESTRESOURCES + "one_dim_zero_extra_zero_dep.pgd");
            ConjugationManager decMan = core.getConjugationManager();
            ConWord word = core.getWordCollection().getWordNodes()[0];
            String[] expectedForms = {"testa", "testb"};

            assertEquals(expectedForms.length, decMan.getAllCombinedIds(word.getWordTypeId()).length);
            assertEquals(0, decMan.getDimensionalConjugationListWord(word.getId()).length);
            assertEquals(decMan.getDeprecatedForms(word).size(), 0);
            assertEquals(decMan.getWordConjugation(word.getId()).size(), 0);
            assertTrue(allFormsPresent(decMan, word, expectedForms));
        } catch (Exception e) {
            DesktopIOHandler.getInstance().writeErrorLog(e, "testZeroDimNoExtra");
            fail(e);
        }
    }
    
    @Test
    public void testOneDimNoExtraFourDep() {
        try{
            System.out.println("ConjugationManagerTest.testOneDimNoExtraFourDep");
            core.readFile(PGTUtil.TESTRESOURCES + "one_dim_zero_extra_four_dep.pgd");
            ConjugationManager decMan = core.getConjugationManager();
            ConWord word = core.getWordCollection().getWordNodes()[0];
            String[] expectedForms = {"testa", "testb"};
            List<String> expectedDeprecated = Arrays.asList("testaczzz", "testbczzz", "testadzzz", "testbdzzz");

            assertEquals(expectedForms.length, decMan.getAllCombinedIds(word.getWordTypeId()).length);
            assertEquals(4, decMan.getDimensionalConjugationListWord(word.getId()).length);
            assertEquals(decMan.getDeprecatedForms(word).size(), expectedDeprecated.size());
            assertEquals(decMan.getWordConjugation(word.getId()).size(), 4);
            assertTrue(allFormsPresent(decMan, word, expectedForms));
            assertTrue(allDeprecatedFormsPresent(decMan, word, expectedDeprecated));
        } catch (Exception e) {
            DesktopIOHandler.getInstance().writeErrorLog(e, "testZeroDimNoExtra");
            fail(e);
        }
    }
    
    @Test
    public void testOneDimNoExtraNoDep() {
        try {
            System.out.println("ConjugationManagerTest.testOneDimNoExtraNoDep");
            core.readFile(PGTUtil.TESTRESOURCES + "one_dim_zero_extra_zero_dep.pgd");
            ConjugationManager decMan = core.getConjugationManager();
            ConWord word = core.getWordCollection().getWordNodes()[0];
            String[] expectedForms = {"testa", "testb"};

            assertEquals(expectedForms.length, decMan.getAllCombinedIds(word.getWordTypeId()).length);
            assertEquals(0, decMan.getDimensionalConjugationListWord(word.getId()).length);
            assertEquals(decMan.getDeprecatedForms(word).size(), 0);
            assertEquals(decMan.getWordConjugation(word.getId()).size(), 0);
            assertTrue(allFormsPresent(decMan, word, expectedForms));
        } catch (Exception e) {
            DesktopIOHandler.getInstance().writeErrorLog(e, "testZeroDimNoExtra");
            fail(e);
        }
    }
    
    @Test
    public void testTwoDimOneExtraNoDep() {
        try{ 
            System.out.println("ConjugationManagerTest.testTwoDimOneExtraNoDep");
            core.readFile(PGTUtil.TESTRESOURCES + "two_dim_one_extra_zero_dep.pgd");
            ConjugationManager decMan = core.getConjugationManager();
            ConWord word = core.getWordCollection().getWordNodes()[0];
            String[] expectedForms = {"testac", "testad", "testbc", "testbd", "testEXTRA"};
            List<String> expectedDeprecated = Arrays.asList();

            assertEquals(expectedForms.length, decMan.getAllCombinedIds(word.getWordTypeId()).length);
            assertEquals(0, decMan.getDimensionalConjugationListWord(word.getId()).length);
            assertEquals(decMan.getDeprecatedForms(word).size(), expectedDeprecated.size());
            assertEquals(decMan.getWordConjugation(word.getId()).size(), 0);
            assertTrue(allFormsPresent(decMan, word, expectedForms));
            assertTrue(allDeprecatedFormsPresent(decMan, word, expectedDeprecated));
        } catch (Exception e) {
            DesktopIOHandler.getInstance().writeErrorLog(e, "testZeroDimNoExtra");
            fail(e);
        }
    }
    
    @Test
    public void testTwoDimOneExtraNoDepOneDisabled() {
        try{
            System.out.println("ConjugationManagerTest.testTwoDimOneExtraNoDepOneDisabled");
            core.readFile(PGTUtil.TESTRESOURCES + "two_dim_one_extra_zero_dep_one_disabled.pgd");
            ConjugationManager decMan = core.getConjugationManager();
            ConWord word = core.getWordCollection().getWordNodes()[0];
            String[] expectedForms = {"testac", "testad", "testbc", "testEXTRA"};
            List<String> expectedDeprecated = Arrays.asList();

            assertEquals(expectedForms.length + 1, decMan.getAllCombinedIds(word.getWordTypeId()).length); // includes surpressed form
            assertEquals(0, decMan.getDimensionalConjugationListWord(word.getId()).length);
            assertEquals(decMan.getDeprecatedForms(word).size(), expectedDeprecated.size());
            assertEquals(decMan.getWordConjugation(word.getId()).size(), 0);
            assertTrue(allFormsPresent(decMan, word, expectedForms));
            assertTrue(allDeprecatedFormsPresent(decMan, word, expectedDeprecated));
        } catch (Exception e) {
            DesktopIOHandler.getInstance().writeErrorLog(e, "testZeroDimNoExtra");
            fail(e);
        }
    }
    
    @Test
    public void testTwoDimNoExtraNoDep() {
        try {
            System.out.println("ConjugationManagerTest.testTwoDimOneExtraNoDepOneDisabled");
            
            core.readFile(PGTUtil.TESTRESOURCES + "two_dim_zero_extra_zero_dep.pgd");
            ConjugationManager decMan = core.getConjugationManager();
            ConWord word = core.getWordCollection().getWordNodes()[0];
            String[] expectedForms = {"testac", "testad", "testbc", "testbd"};
            List<String> expectedDeprecated = Arrays.asList();

            assertEquals(expectedForms.length, decMan.getAllCombinedIds(word.getWordTypeId()).length);
            assertEquals(0, decMan.getDimensionalConjugationListWord(word.getId()).length);
            assertEquals(decMan.getDeprecatedForms(word).size(), expectedDeprecated.size());
            assertEquals(decMan.getWordConjugation(word.getId()).size(), 0);
            assertTrue(allFormsPresent(decMan, word, expectedForms));
            assertTrue(allDeprecatedFormsPresent(decMan, word, expectedDeprecated));
        } catch (Exception e) {
            DesktopIOHandler.getInstance().writeErrorLog(e, "testZeroDimNoExtra");
            fail(e);
        }
    }
    
    @Test
    public void testevolveConjugatedWordForms_posFilterActive() {
        System.out.println("ConjugationManagerTest.testevolveConjugatedWordForms_posFilterActive");
        
        ConjugationManager manager = core.getConjugationManager();
        
        ConjugationGenRule rule = new ConjugationGenRule(1, "1,1");
        rule.addTransform(new ConjugationGenTransform("aaa", "bbb"));
        rule.addTransform(new ConjugationGenTransform("bbb", "ccc"));
        manager.addConjugationGenRule(rule);
        
        rule = new ConjugationGenRule(2, "1,1");
        rule.addTransform(new ConjugationGenTransform("aaa", "bbb"));
        rule.addTransform(new ConjugationGenTransform("bbb", "ccc"));
        manager.addConjugationGenRule(rule);
    }
    
    @Test
    public void testevolveConjugatedWordForms_posFilterInactive() {}
    
    public void testevolveConjugatedWordRules() {
        
    }

    @Test
    public void testMorphologyOnlyGeneration() {
        System.out.println("ConjugationManagerTest.testMorphologyOnlyGeneration");

        try {
            TestContext context = buildMorphologyContext("kar");
            MorphologyRule rule = new MorphologyRule(context.typeId(), "plural");
            ConjugationManager manager = core.getConjugationManager();

            rule.setOperationType(MorphologyOperationType.append_suffix);
            rule.setValue1("im");
            rule.addCondition(new MorphologyCondition("nounClass", MorphologyConditionOperator.equals, "strong"));
            manager.addMorphologyRule(rule);

            assertEquals("karim", manager.generateWordForm(context.word(), "plural"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testMorphologyThenLegacyInterop() {
        System.out.println("ConjugationManagerTest.testMorphologyThenLegacyInterop");

        try {
            TestContext context = buildMorphologyContext("dor");
            ConjugationManager manager = core.getConjugationManager();
            ConjugationNode template = new ConjugationNode(-1, manager);
            template.setValue("Plural");
            template.setDimensionless(true);
            ConjugationNode node = manager.addConjugationToTemplate(context.typeId(), -1, template);
            String targetKey = node.getCombinedDimId();
            MorphologyRule morphRule = new MorphologyRule(context.typeId(), targetKey);
            ConjugationGenRule legacyRule = new ConjugationGenRule(context.typeId(), targetKey);

            morphRule.setOperationType(MorphologyOperationType.append_suffix);
            morphRule.setValue1("en");
            manager.addMorphologyRule(morphRule);

            legacyRule.setRegex(".*");
            legacyRule.addClassToFilterList(-1, -1);
            legacyRule.addTransform(new ConjugationGenTransform("en$", "in"));
            manager.addConjugationGenRule(legacyRule);

            assertEquals("dorin", manager.generateWordForm(context.word(), targetKey));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testLegacyOnlyRulesStillWork() {
        System.out.println("ConjugationManagerTest.testLegacyOnlyRulesStillWork");

        try {
            TestContext context = buildMorphologyContext("dor");
            ConjugationManager manager = core.getConjugationManager();
            ConjugationNode template = new ConjugationNode(-1, manager);
            template.setValue("Plural");
            template.setDimensionless(true);
            ConjugationNode node = manager.addConjugationToTemplate(context.typeId(), -1, template);
            String targetKey = node.getCombinedDimId();
            ConjugationGenRule legacyRule = new ConjugationGenRule(context.typeId(), targetKey);

            legacyRule.setRegex(".*");
            legacyRule.addClassToFilterList(-1, -1);
            legacyRule.addTransform(new ConjugationGenTransform("$", "en"));
            manager.addConjugationGenRule(legacyRule);

            assertEquals("doren", manager.declineWord(context.word(), targetKey));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testConWordGetWordFormUsesMorphologyEngine() {
        System.out.println("ConjugationManagerTest.testConWordGetWordFormUsesMorphologyEngine");

        try {
            TestContext context = buildMorphologyContext("kar");
            MorphologyRule rule = new MorphologyRule(context.typeId(), "plural");

            rule.setOperationType(MorphologyOperationType.append_suffix);
            rule.setValue1("im");
            core.getConjugationManager().addMorphologyRule(rule);

            assertEquals("karim", context.word().getWordForm("plural"));
        } catch (Exception e) {
            fail(e);
        }
    }

    private TestContext buildMorphologyContext(String value) throws Exception {
        TypeNode type = new TypeNode();
        type.setValue("noun");
        int typeId = core.getTypes().addNode(type);

        WordClass wordClass = new WordClass();
        wordClass.setValue("nounClass");
        wordClass.deleteApplyType(-1);
        wordClass.addApplyType(typeId);
        int strongId = wordClass.addValue("strong").getId();
        int classId = core.getWordClassCollection().addNode(wordClass);

        ConWord word = new ConWord();
        word.setValue(value);
        word.setWordTypeId(typeId);
        word.setClassValue(classId, strongId);
        int wordId = core.getWordCollection().addWord(word);

        return new TestContext(core.getWordCollection().getNodeById(wordId), typeId);
    }

    private record TestContext(ConWord word, int typeId) {}
    
    private boolean allDeprecatedFormsPresent(ConjugationManager decMan, 
            ConWord word, 
            List<String> expectedForms) {
        boolean ret = true;
        List<String> depForms = new ArrayList<>();
        
        decMan.getDeprecatedForms(word).values().forEach((depWord)->{
            depForms.add(depWord.getValue());
        });
        
        for (String expectedForm : expectedForms) {
            if (!depForms.contains(expectedForm)) {
                ret = false;
                break;
            }
        }
        
        return ret;
    }

    private boolean allFormsPresent(ConjugationManager decMan, ConWord word, String[] forms) throws Exception {
        boolean ret = true;
        
        ConjugationPair[] pairs = decMan.getAllCombinedIds(word.getWordTypeId());
        List<String> finalForms = new ArrayList<>();
        
        for (ConjugationPair pair : pairs) {
            if (!decMan.isCombinedConjlSurpressed(pair.combinedId, word.getWordTypeId())) {
                finalForms.add(decMan.declineWord(word, pair.combinedId));
            }
        }
        
        for (String form : forms) {
            if (!finalForms.contains(form)) {
                ret = false;
                break;
            }
        }
        
        return ret;
    }
}
