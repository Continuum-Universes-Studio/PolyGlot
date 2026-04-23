/*
 * Copyright (c) 2020 - 2023, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT License
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
import java.io.IOException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode.ProblemType;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.DescendantLink;
import org.darisadesigns.polyglotlina.Nodes.LexicalRelationType;
import org.darisadesigns.polyglotlina.Nodes.LinkedWordReference;
import org.darisadesigns.polyglotlina.Validation.ConsistencyChecker;
import org.darisadesigns.polyglotlina.Validation.ConsistencyIssue;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author draque
 */
public class CheckLanguageErrorsTest {
    
    private final DictCore badLexEntriesCore;
    private final DictCore badRegexEntriesCore;
    
    public CheckLanguageErrorsTest() {
        badLexEntriesCore = DummyCore.newCore();
        badRegexEntriesCore = DummyCore.newCore();
        
        try {
            badLexEntriesCore.readFile(PGTUtil.TESTRESOURCES + "test_lex_problems.pgd");
            badRegexEntriesCore.readFile(PGTUtil.TESTRESOURCES + "test_regex_problems.pgd");
        } catch (IOException | IllegalStateException | ParserConfigurationException e) {
            fail(e);
        }
    }

    /**
     * Test of checkCore method, of class CheckLanguageErrors.
     */
    @Test
    public void testCheckCore_LexIssues() {
        System.out.println("CheckLanguageErrorsTest.testCheckCore_LexIssues");
        int expectedProblems = 4;
        
        LexiconProblemNode[] problems = CheckLanguageErrors.checkCore(badLexEntriesCore, false);
        assertTrue(problems.length >= expectedProblems);

        assertProblem(problems, "bad-pattern", ProblemType.ConWord,
                "Word does not match enforced pattern for type: noun.");

        assertProblemContains(problems, "bad-romanization-1-noun", ProblemType.ConWord,
                "Word contains characters undefined in alphabet settings.",
                "Suspect characters:\"1\"",
                "Word cannot be romanized properly (missing regex pattern).");

        assertProblemContains(problems, "missing-POS-and-alphabet", ProblemType.ConWord,
                "Part of Speech set to mandatory.",
                "Word contains characters undefined in alphabet settings",
                "Suspect characters:\"POS\"",
                "Word pronunciation cannot be generated properly (missing regex pattern).");

        assertProblem(problems, "missing-local-noun", ProblemType.ConWord,
                "Local Lang word set to mandatory.");
    }
    
    @Test
    public void testCheckCore_regexIssues() {
        System.out.println("CheckLanguageErrorsTest.testCheckCore_regexIssues");
        
        int expectedProblems = 7;
        
        LexiconProblemNode[] problems = CheckLanguageErrors.checkCore(badRegexEntriesCore, false);
        assertTrue(problems.length >= expectedProblems);

        assertProblem(problems, "BadConjTransform", ProblemType.PoS,
                "\nThe replacement text \"(\" within rule broken2 is illegal.\nThe regex transform \"(\" within rule broken2 of up forth is illegal.");
        assertProblem(problems, "BadConjTransform", ProblemType.PoS,
                "\nThe regex transform \"(\" within rule broken-nonD of  is illegal.");
        assertProblem(problems, "badTypePattern", ProblemType.PoS,
                "Illegal regex value: \"(\"");
        assertProblem(problems, "(", ProblemType.Phonology,
                "Pronunciation regex: \"(\" is illegal.");
        assertProblem(problems, "(", ProblemType.Phonology,
                "Romanization regex: \"(\" is illegal.");
        assertProblem(problems, "PronuncRegex", ProblemType.Phonology,
                "Pronunciation text: \"(\" is illegal regex insertion.");
        assertProblem(problems, "RomanRegex", ProblemType.Phonology,
                "Romanization value: \"RomanRegex\" is illegal regex insertion.");
    }
    
    @Test
    public void testCheckLanguageNoAlphabet() {
        System.out.println("CheckLanguageErrorsTest.testCheckLanguageNoAlphabet");
        
        try {
            DictCore dictCore = DummyCore.newCore();
            dictCore.readFile(PGTUtil.TESTRESOURCES + "test_check_lang_no_alphabet.pgd");
            LexiconProblemNode[] problems = CheckLanguageErrors.checkCore(dictCore, false);
            
            assertEquals(problems.length, 0);
        } catch (IOException | IllegalStateException | ParserConfigurationException e) {
            fail(e);
        }
    }

    @Test
    public void testCheckCore_CompatibilityWrapperIncludesModularIssues() {
        System.out.println("CheckLanguageErrorsTest.testCheckCore_CompatibilityWrapperIncludesModularIssues");

        DictCore dictCore = buildModularValidationCore();
        LexiconProblemNode[] problems = CheckLanguageErrors.checkCore(dictCore, false);

        assertTrue(Arrays.stream(problems).anyMatch(problem
                -> "phoneme.inventory.invalid_ipa".equals(problem.issueCode)));
        assertTrue(Arrays.stream(problems).anyMatch(problem
                -> "linked.word.unknown_language".equals(problem.issueCode)));
    }

    private DictCore buildModularValidationCore() {
        DictCore dictCore = DummyCore.newCore();
        dictCore.getPropertiesManager().getAlphaOrder().put("a", 0);
        dictCore.getPronunciationMgr().setSyllableCompositionEnabled(true);
        dictCore.getPronunciationMgr().addSyllable("a");
        dictCore.getPronunciationMgr().addIllegalCluster("z");

        ConWord word = dictCore.getWordCollection().getBuffer();
        word.setValue("z");
        word.setLocalWord("z");
        word.setDefinition("z");
        word.setProcOverride(true);
        word.setPronunciation("☃");

        DescendantLink descendantLink = word.getDescendantLink();
        descendantLink.setParentWordValue("proto");
        descendantLink.setParentLanguageName("Ancestor");

        LinkedWordReference reference = new LinkedWordReference();
        reference.setTargetLanguagePath("/tmp/non-existent-linked-language.pgd");
        reference.setCachedTargetLanguageName("Missing");
        reference.setRelationType(LexicalRelationType.LOANWORD);
        word.setLinkedWordReference(reference);

        try {
            dictCore.getWordCollection().insert();
        } catch (Exception e) {
            fail(e);
        }

        return dictCore;
    }

    private LexiconProblemNode assertProblem(LexiconProblemNode[] problems, String wordValue,
            ProblemType type, String expectedDescription) {
        for (LexiconProblemNode problem : problems) {
            if (problem.problemType == type
                    && problem.problemWord != null
                    && wordValue.equals(problem.problemWord.getValue())
                    && expectedDescription.equals(problem.description)) {
                return problem;
            }
        }

        fail("Expected problem not found for word " + wordValue + " and description " + expectedDescription);
        return null;
    }

    private LexiconProblemNode assertProblemContains(LexiconProblemNode[] problems, String wordValue,
            ProblemType type, String... descriptionFragments) {
        for (LexiconProblemNode problem : problems) {
            if (problem.problemType != type
                    || problem.problemWord == null
                    || !wordValue.equals(problem.problemWord.getValue())) {
                continue;
            }

            boolean matches = true;
            for (String fragment : descriptionFragments) {
                if (!problem.description.contains(fragment)) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                return problem;
            }
        }

        fail("Expected problem not found for word " + wordValue + " with description fragments "
                + Arrays.toString(descriptionFragments));
        return null;
    }
}
