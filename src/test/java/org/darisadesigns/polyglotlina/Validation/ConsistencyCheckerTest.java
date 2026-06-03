/*
 * Copyright (c) 2026, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT License
 * See LICENSE.TXT included with this code to read the full license agreement.
 */
package org.darisadesigns.polyglotlina.Validation;

import TestResources.DummyCore;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.DescendantLink;
import org.darisadesigns.polyglotlina.Nodes.LexicalRelationType;
import org.darisadesigns.polyglotlina.Nodes.LinkedWordReference;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ConsistencyCheckerTest {

    @Test
    public void testCheckerEmitsCoreIssueTypes() {
        var core = DummyCore.newCore();
        core.getPropertiesManager().getAlphaOrder().put("a", 0);
        core.getPronunciationMgr().setSyllableCompositionEnabled(true);
        core.getPronunciationMgr().addSyllable("a");
        core.getPronunciationMgr().addIllegalCluster("z");

        ConWord word = core.getWordCollection().getBuffer();
        word.setValue("z");
        word.setLocalWord("z");
        word.setDefinition("definition");
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
            core.getWordCollection().insert();
        } catch (Exception e) {
            fail(e);
        }

        List<ConsistencyIssue> issues = new ConsistencyChecker().check(core);
        List<String> codes = issues.stream().map(ConsistencyIssue::getCode).collect(Collectors.toList());

        assertTrue(codes.contains("orthography.invalid_grapheme"));
        assertTrue(codes.contains("phonotactics.illegal_cluster"));
        assertTrue(codes.contains("phoneme.inventory.invalid_ipa"));
        assertTrue(codes.contains("linked.word.unknown_language"));
        assertTrue(codes.contains("evolution.descendant.no_rules"));

        assertTrue(issues.stream().anyMatch(issue -> issue.getSeverity() == ConsistencySeverity.ERROR));
        assertTrue(issues.stream().anyMatch(issue -> issue.getSeverity() == ConsistencySeverity.WARNING));
        assertTrue(issues.stream().anyMatch(issue -> issue.getSeverity() == ConsistencySeverity.INFO));
    }
}
