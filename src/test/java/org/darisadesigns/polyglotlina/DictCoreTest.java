/*
 * Copyright (c) 2020-2023, Draque Thompson, draquemail@gmail.com
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
package org.darisadesigns.polyglotlina;

import TestResources.DummyCore;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.DesktopGrammarChapNode;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.DescendantLink;
import org.darisadesigns.polyglotlina.Nodes.GeneratorSettings;
import org.darisadesigns.polyglotlina.Nodes.LanguageLinkType;
import org.darisadesigns.polyglotlina.Nodes.LanguageRelation;
import org.darisadesigns.polyglotlina.Nodes.LanguageRelationType;
import org.darisadesigns.polyglotlina.Nodes.LinkedLanguage;
import org.darisadesigns.polyglotlina.Nodes.MorphologyCondition;
import org.darisadesigns.polyglotlina.Nodes.MorphologyConditionOperator;
import org.darisadesigns.polyglotlina.Nodes.MorphologyOperationType;
import org.darisadesigns.polyglotlina.Nodes.MorphologyRule;
import org.darisadesigns.polyglotlina.Nodes.PronunciationNode;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.Nodes.WordClass;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author draque
 */
public class DictCoreTest {
    DictCore core;
    
    public DictCoreTest() {
        core = DummyCore.newCore();
    }

    @Test
    public void testIsLanguageEmptyYes() {
        System.out.println("DictCoreTest.testIsLanguageEmptyYes");
        assertTrue(core.isLanguageEmpty(), "DictCoreTest.testIsLanguageEmptyYes:F");
    }
    
    @Test
    public void testIsLanguageEmptyNoLexicon() {
        System.out.println("DictCoreTest.testIsLanguageEmptyNoLexicon");
        
        try {
            core.getWordCollection().addWord(new ConWord());
            assertFalse(core.isLanguageEmpty(), "DictCoreTest.testIsLanguageEmptyNoLexicon:F");
        } catch (Exception e) {
            fail(e);
        }
    }
    
    @Test
    public void testIsLanguageEmptyNoGrammar() {
        System.out.println("DictCoreTest.testIsLanguageEmptyNoGrammar");
        
        try {
            core.getGrammarManager().addChapter(new DesktopGrammarChapNode(null));
            assertFalse(core.isLanguageEmpty(), "DictCoreTest.testIsLanguageEmptyNoGrammar:F");
        } catch (Exception e) {
            fail(e);
        }
    }
    
    @Test
    public void testIsLanguageEmptyNoPronunciation() {
        System.out.println("DictCoreTest.testIsLanguageEmptyNoPronunciation");
        
        try {
            core.getPronunciationMgr().addPronunciation(new PronunciationNode());
            assertFalse(core.isLanguageEmpty(), "DictCoreTest.testIsLanguageEmptyNoPronunciation:F");
        } catch (Exception e) {
            fail(e);
        }
    }
    
    @Test
    public void testIsLanguageEmptyNoPOS() {
        System.out.println("DictCoreTest.testIsLanguageEmptyNoPOS");
        try {
            core.getTypes().addNode(new TypeNode());
            assertFalse(core.isLanguageEmpty(), "DictCoreTest.testIsLanguageEmptyNoPOS:F");
        } catch (Exception e) {
            fail(e);
        }
    }
    
    @Test
    public void testSaveLanguageIntegrityDeep() {
        System.out.println("DictCoreTest.testSaveLanguageIntegrityDeep");
        
        try {
            DictCore origin = DummyCore.newCore();
            DictCore target = DummyCore.newCore();
            File targetFile = File.createTempFile("POLYGLOT", "pgt",
                PGTUtil.getTempDirectory().toFile());
            targetFile.deleteOnExit();
            
            origin.readFile(PGTUtil.TESTRESOURCES + "test_equality.pgd");
            origin.writeFile(targetFile.toString(), false, false);
            target.readFile(targetFile.toString());
            
            assertEquals(origin, target, "DictCoreTest.testIsLanguageEmptyNoPOS:F");
        } catch (IOException | IllegalStateException | ParserConfigurationException | TransformerException e) {
            // e.printStackTrace();
            // System.out.println(e.getMessage());
            fail(e);
        }
    }
    
    @Test
    public void testReadCorruptedArchive() {
        System.out.println("DictCoreTest.testReadCorruptedArchive");
        
        try {
            DictCore origin = DummyCore.newCore();
            DictCore target = DummyCore.newCore();
            File targetFile = File.createTempFile("POLYGLOT", "pgt",
                PGTUtil.getTempDirectory().toFile());
            targetFile.deleteOnExit();
            
            origin.readFile(PGTUtil.TESTRESOURCES + "test_equality.pgd");
            origin.writeFile(targetFile.toString(), false, false);
            target.readFile(targetFile.toString());
            
            assertEquals(origin, target, "DictCoreTest.testIsLanguageEmptyNoPOS:F");
        } catch (IOException | IllegalStateException | ParserConfigurationException | TransformerException e) {
            // e.printStackTrace();
            // System.out.println(e.getMessage());
            fail(e);
        }
    }

    @Test
    public void testLanguageEvolutionRoundTrip() {
        System.out.println("DictCoreTest.testLanguageEvolutionRoundTrip");

        File parentFile = null;
        File childFile = null;
        File siblingFile = null;
        File contactFile = null;

        try {
            DictCore parentCore = DummyCore.newCore();
            DictCore siblingCore = DummyCore.newCore();
            DictCore contactCore = DummyCore.newCore();
            DictCore origin = DummyCore.newCore();
            DictCore reload = DummyCore.newCore();

            parentFile = File.createTempFile("POLYGLOT_PARENT", ".pgd",
                    PGTUtil.getTempDirectory().toFile());
            siblingFile = File.createTempFile("POLYGLOT_SIBLING", ".pgd",
                    PGTUtil.getTempDirectory().toFile());
            contactFile = File.createTempFile("POLYGLOT_CONTACT", ".pgd",
                    PGTUtil.getTempDirectory().toFile());
            childFile = File.createTempFile("POLYGLOT_CHILD", ".pgd",
                    PGTUtil.getTempDirectory().toFile());
            parentFile.deleteOnExit();
            siblingFile.deleteOnExit();
            contactFile.deleteOnExit();
            childFile.deleteOnExit();

            parentCore.getPropertiesManager().setLangName("Proto Test");
            ConWord parentWord = new ConWord();
            parentWord.setValue("atha");
            parentWord.setDefinition("ancestor");
            int parentWordId = parentCore.getWordCollection().addWord(parentWord);
            parentWord = parentCore.getWordCollection().getNodeById(parentWordId);
            parentCore.writeFile(parentFile.toString(), false, false);

            siblingCore.getPropertiesManager().setLangName("Sibling Test");
            siblingCore.writeFile(siblingFile.toString(), false, false);

            contactCore.getPropertiesManager().setLangName("Contact Test");
            contactCore.writeFile(contactFile.toString(), false, false);

            origin.setCurFileName(childFile.toString());
            origin.getPropertiesManager().setLangName("Desc Test");
            origin.getPropertiesManager().getLanguageEvolutionProfile()
                    .setParentLanguagePathFromAbsolute(parentFile.toString(), origin);
            origin.getPropertiesManager().getLanguageEvolutionProfile()
                    .setCachedParentLanguageName("Proto Test");
            origin.getPropertiesManager().getLanguageEvolutionProfile()
                    .setSoundChangeRules(List.of("th > d / V_V", "a > e / _#"));

            LanguageRelation siblingRelation = new LanguageRelation();
            siblingRelation.setTargetLanguagePathFromAbsolute(siblingFile.toString(), origin);
            siblingRelation.setCachedTargetLanguageName("Sibling Test");
            siblingRelation.setRelationType(LanguageRelationType.SIBLING);
            siblingRelation.setNotes("Shared branch");
            origin.getPropertiesManager().getLanguageEvolutionProfile()
                    .addRelatedLanguage(siblingRelation);

            LanguageRelation contactRelation = new LanguageRelation();
            contactRelation.setTargetLanguagePathFromAbsolute(contactFile.toString(), origin);
            contactRelation.setCachedTargetLanguageName("Contact Test");
            contactRelation.setRelationType(LanguageRelationType.CONTACT);
            contactRelation.setNotes("Trade language influence");
            origin.getPropertiesManager().getLanguageEvolutionProfile()
                    .addRelatedLanguage(contactRelation);

            ConWord childWord = new ConWord();
            childWord.setValue("ade");
            int childWordId = origin.getWordCollection().addWord(childWord);
            childWord = origin.getWordCollection().getNodeById(childWordId);

            DescendantLink link = new DescendantLink();
            link.setParentWordId(parentWordId);
            link.setParentWordValue(parentWord.getValue());
            link.setParentWordDefinition(parentWord.getDefinition());
            link.setParentLanguageName("Proto Test");
            origin.getEtymologyManager().setDescendantLink(childWord, link);

            origin.writeFile(childFile.toString(), false, false);
            reload.readFile(childFile.toString());

            assertEquals(origin, reload);
        } catch (Exception e) {
            fail(e);
        } finally {
            if (parentFile != null && parentFile.exists()) {
                parentFile.delete();
            }

            if (siblingFile != null && siblingFile.exists()) {
                siblingFile.delete();
            }

            if (contactFile != null && contactFile.exists()) {
                contactFile.delete();
            }

            if (childFile != null && childFile.exists()) {
                childFile.delete();
            }
        }
    }

    @Test
    public void testLegacyLanguageEvolutionLoadWithoutRelations() {
        System.out.println("DictCoreTest.testLegacyLanguageEvolutionLoadWithoutRelations");

        try {
            DictCore legacyCore = DummyCore.newCore();
            legacyCore.readFile(PGTUtil.TESTRESOURCES + "basic_lang.pgd");

            assertTrue(legacyCore.getPropertiesManager()
                    .getLanguageEvolutionProfile().getRelatedLanguages().isEmpty());
        } catch (IOException | IllegalStateException | ParserConfigurationException e) {
            fail(e);
        }
    }

    @Test
    public void testLinkedLanguagesRoundTrip() {
        System.out.println("DictCoreTest.testLinkedLanguagesRoundTrip");

        File parentFile = null;
        File siblingFile = null;
        File languageFile = null;

        try {
            DictCore parentCore = DummyCore.newCore();
            DictCore siblingCore = DummyCore.newCore();
            DictCore origin = DummyCore.newCore();
            DictCore reload = DummyCore.newCore();

            parentFile = File.createTempFile("POLYGLOT_LINK_PARENT", ".pgd",
                    PGTUtil.getTempDirectory().toFile());
            siblingFile = File.createTempFile("POLYGLOT_LINK_SIBLING", ".pgd",
                    PGTUtil.getTempDirectory().toFile());
            languageFile = File.createTempFile("POLYGLOT_LINK_MAIN", ".pgd",
                    PGTUtil.getTempDirectory().toFile());
            parentFile.deleteOnExit();
            siblingFile.deleteOnExit();
            languageFile.deleteOnExit();

            parentCore.getPropertiesManager().setLangName("Proto Test");
            parentCore.writeFile(parentFile.toString(), false, false);

            siblingCore.getPropertiesManager().setLangName("Sibling Test");
            siblingCore.writeFile(siblingFile.toString(), false, false);

            origin.setCurFileName(languageFile.toString());

            LinkedLanguage parentLink = new LinkedLanguage();
            parentLink.setTargetFileFromAbsolute(parentFile.toString(), origin);
            parentLink.setLanguageName("Proto Test");
            parentLink.setLinkType(LanguageLinkType.PARENT);
            parentLink.setNotes("Primary ancestor");
            origin.getPropertiesManager().addLinkedLanguage(parentLink);

            LinkedLanguage siblingLink = new LinkedLanguage();
            siblingLink.setTargetFileFromAbsolute(siblingFile.toString(), origin);
            siblingLink.setLanguageName("Sibling Test");
            siblingLink.setLinkType(LanguageLinkType.SIBLING);
            siblingLink.setNotes("Shared family branch");
            origin.getPropertiesManager().addLinkedLanguage(siblingLink);

            origin.writeFile(languageFile.toString(), false, false);
            reload.readFile(languageFile.toString());

            assertEquals(origin.getPropertiesManager().getLinkedLanguages(),
                    reload.getPropertiesManager().getLinkedLanguages());
        } catch (IOException | IllegalStateException | ParserConfigurationException | TransformerException e) {
            fail(e);
        } finally {
            if (parentFile != null && parentFile.exists()) {
                parentFile.delete();
            }

            if (siblingFile != null && siblingFile.exists()) {
                siblingFile.delete();
            }

            if (languageFile != null && languageFile.exists()) {
                languageFile.delete();
            }
        }
    }

    @Test
    public void testLegacyLanguageLoadWithoutLinkedLanguages() {
        System.out.println("DictCoreTest.testLegacyLanguageLoadWithoutLinkedLanguages");

        try {
            DictCore legacyCore = DummyCore.newCore();
            legacyCore.readFile(PGTUtil.TESTRESOURCES + "basic_lang.pgd");

            assertTrue(legacyCore.getPropertiesManager().getLinkedLanguages().isEmpty());
        } catch (IOException | IllegalStateException | ParserConfigurationException e) {
            fail(e);
        }
    }

    @Test
    public void testGeneratorSettingsRoundTrip() {
        System.out.println("DictCoreTest.testGeneratorSettingsRoundTrip");

        File languageFile = null;

        try {
            DictCore origin = DummyCore.newCore();
            DictCore reload = DummyCore.newCore();

            languageFile = File.createTempFile("POLYGLOT_GENERATOR", ".pgd");
            languageFile.deleteOnExit();

            GeneratorSettings settings = origin.getPropertiesManager().getGeneratorSettings();
            settings.setCategories("C=ptk\nV=aei");
            settings.setIllegalClusters("pt");
            settings.setRewriteRules("aa|ā");
            settings.setSyllableTypes("CV\nCVC");
            settings.setDropoffRate(12);
            settings.setMonosyllableFrequency(55);
            settings.setShowSyllables(true);
            settings.setSlowSyllableDropoff(true);
            settings.setGenerationCount("42");
            settings.setGenerateWords(false);

            origin.writeFile(languageFile.toString(), false, false);
            reload.readFile(languageFile.toString());

            assertEquals(origin.getPropertiesManager().getGeneratorSettings(),
                    reload.getPropertiesManager().getGeneratorSettings());
        } catch (IOException | IllegalStateException | ParserConfigurationException | TransformerException e) {
            fail(e);
        } finally {
            if (languageFile != null && languageFile.exists()) {
                languageFile.delete();
            }
        }
    }

    @Test
    public void testLegacyLanguageLoadWithoutGeneratorStateUsesDefaults() {
        System.out.println("DictCoreTest.testLegacyLanguageLoadWithoutGeneratorStateUsesDefaults");

        try {
            DictCore legacyCore = DummyCore.newCore();
            legacyCore.readFile(PGTUtil.TESTRESOURCES + "basic_lang.pgd");

            GeneratorSettings settings = legacyCore.getPropertiesManager().getGeneratorSettings();
            assertEquals(GeneratorSettings.DEFAULT_DROPOFF_RATE, settings.getDropoffRate());
            assertEquals(GeneratorSettings.DEFAULT_MONOSYLLABLE_FREQUENCY, settings.getMonosyllableFrequency());
            assertEquals(GeneratorSettings.DEFAULT_GENERATION_COUNT, settings.getGenerationCount());
            assertFalse(settings.isShowSyllables());
            assertFalse(settings.isSlowSyllableDropoff());
            assertTrue(settings.isGenerateWords());
        } catch (IOException | IllegalStateException | ParserConfigurationException e) {
            fail(e);
        }
    }
    
    @Test
    public void testRevertLanguage() {
        System.out.println("DictCoreTest.testRevertLanguage");
        Exception exception = null;
        var expectedMessage = "Encountered corrupted XML file";
        var expectedLexiconLength = 79;
        
        try {
            core.readFile(PGTUtil.TESTRESOURCES + "corrupted" + File.separator + "truncated_archive.pgd");
        } catch (IOException | IllegalStateException | ParserConfigurationException e) {
            exception = e;
        }
        
        assertTrue(exception instanceof IOException);
        assertTrue(exception.getLocalizedMessage().startsWith(expectedMessage));
        assertEquals(expectedLexiconLength, core.getWordCollection().getWordCount());
    }
    
    @Test
    public void testRecoverMissingOpeningXmlTags() {
        System.out.println("DictCoreTest.testRecoverMissingOpeningXmlTags");
        
        try {
            core.readFile(PGTUtil.TESTRESOURCES + "missing_no_element.pgd");
            DictCore corruptCore = DummyCore.newCore();
            corruptCore.readFile(PGTUtil.TESTRESOURCES + "missing_opening_element.pgd");

            assertEquals(core, corruptCore);
        } catch (IOException | IllegalStateException | ParserConfigurationException e) {
            fail(e);
        }
    }
    
    @Test
    public void testRecoverMissingClosingXmlTags() {
        System.out.println("DictCoreTest.testRecoverMissingClosingXmlTags");
        
        try {
            core.readFile(PGTUtil.TESTRESOURCES + "missing_no_element.pgd");
            DictCore corruptCore = DummyCore.newCore();
            corruptCore.readFile(PGTUtil.TESTRESOURCES + "missing_closing_elements.pgd");

            assertEquals(core, corruptCore);
        } catch (IOException | IllegalStateException | ParserConfigurationException e) {
            fail(e);
        }
    }

    @Test
    public void testMorphologyRoundTripPersistence() {
        System.out.println("DictCoreTest.testMorphologyRoundTripPersistence");

        try {
            DictCore origin = DummyCore.newCore();
            DictCore reload = DummyCore.newCore();
            TypeNode noun = new TypeNode();
            noun.setValue("noun");
            int typeId = origin.getTypes().addNode(noun);

            WordClass nounClass = new WordClass();
            nounClass.setValue("nounClass");
            nounClass.deleteApplyType(-1);
            nounClass.addApplyType(typeId);
            int strongId = nounClass.addValue("strong").getId();
            int classId = origin.getWordClassCollection().addNode(nounClass);

            ConWord word = new ConWord();
            word.setValue("kar");
            word.setWordTypeId(typeId);
            word.setClassValue(classId, strongId);
            int wordId = origin.getWordCollection().addWord(word);

            MorphologyRule rule = new MorphologyRule(typeId, "plural");
            rule.setOperationType(MorphologyOperationType.append_suffix);
            rule.setValue1("im");
            rule.addCondition(new MorphologyCondition("nounClass", MorphologyConditionOperator.equals, "strong"));
            origin.getConjugationManager().addMorphologyRule(rule);

            reload.readFile("morphology_roundtrip.pgd",
                    origin.getRawXml().getBytes(StandardCharsets.UTF_8),
                    false);

            ConWord reloadWord = reload.getWordCollection().getNodeById(wordId);
            assertEquals("karim", reloadWord.getWordForm("plural"));
            assertEquals(1, reload.getConjugationManager().getMorphologyRulesForTarget(typeId, "plural").length);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testLegacyFilesLoadWithoutMorphologyRules() {
        System.out.println("DictCoreTest.testLegacyFilesLoadWithoutMorphologyRules");

        try {
            DictCore legacyCore = DummyCore.newCore();
            legacyCore.readFile(PGTUtil.TESTRESOURCES + "basic_lang.pgd");

            for (TypeNode type : legacyCore.getTypes().getNodes()) {
                assertEquals(0, legacyCore.getConjugationManager().getMorphologyRulesForType(type.getId()).length);
            }
        } catch (IOException | IllegalStateException | ParserConfigurationException e) {
            fail(e);
        }
    }
}
