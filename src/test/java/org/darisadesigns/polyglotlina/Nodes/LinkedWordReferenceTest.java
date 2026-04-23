/*
 * Copyright (c) 2026, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT License
 * See LICENSE.TXT included with this code to read the full license agreement.
 */
package org.darisadesigns.polyglotlina.Nodes;

import TestResources.DummyCore;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.darisadesigns.polyglotlina.DomParser.WordParser;
import org.darisadesigns.polyglotlina.PGTUtil;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class LinkedWordReferenceTest {

    @Test
    public void testEmptyReferenceIsOmittedFromXml() throws IOException, ParserConfigurationException, TransformerException {
        Document doc = newDocument();
        Element root = doc.createElement(PGTUtil.DICTIONARY_XID);
        doc.appendChild(root);

        ConWord word = new ConWord();
        word.setCore(DummyCore.newCore());
        word.setValue("word");
        word.setLocalWord("local");
        word.writeXML(doc, root);

        assertEquals(0, doc.getElementsByTagName(PGTUtil.WORD_LINK_REFERENCE_XID).getLength());
    }

    @Test
    public void testRoundTripPersistence() throws Exception {
        ConWord source = new ConWord();
        source.setCore(DummyCore.newCore());
        source.setId(42);
        source.setValue("source");
        source.setLocalWord("local");
        source.setDefinition("definition");
        source.setProcOverride(true);
        source.setPronunciation("pron");

        LinkedWordReference reference = new LinkedWordReference();
        reference.setTargetLanguagePath("../linked/other-language.pgd");
        reference.setCachedTargetLanguageName("Other Language");
        reference.setTargetWordId(17);
        reference.setCachedTargetWordValue("borrowed");
        reference.setCachedTargetWordDefinition("borrowed definition");
        reference.setRelationType(LexicalRelationType.LOANWORD);
        reference.setNotes("snapshot");
        source.setLinkedWordReference(reference);

        Document doc = newDocument();
        Element root = doc.createElement(PGTUtil.DICTIONARY_XID);
        doc.appendChild(root);
        source.writeXML(doc, root);

        Node wordNode = doc.getElementsByTagName(PGTUtil.WORD_XID).item(0);
        assertNotNull(wordNode);

        DummyCore reloadCore = DummyCore.newCore();
        new WordParser(new ArrayList<>()).parse(wordNode, reloadCore);

        ConWord loaded = reloadCore.getWordCollection().getNodeById(42);
        assertNotNull(loaded);
        assertEquals(reference, loaded.getLinkedWordReference());
        assertEquals("Other Language", loaded.getLinkedWordReference().getCachedTargetLanguageName());
        assertEquals(17, loaded.getLinkedWordReference().getTargetWordId());
        assertEquals(LexicalRelationType.LOANWORD, loaded.getLinkedWordReference().getRelationType());
    }

    private Document newDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }
}
