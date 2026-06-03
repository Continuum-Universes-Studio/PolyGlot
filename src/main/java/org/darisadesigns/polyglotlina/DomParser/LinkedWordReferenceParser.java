/*
 * Copyright (c) 2026, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT Licence
 * See LICENSE.TXT included with this code to read the full license agreement.
 */
package org.darisadesigns.polyglotlina.DomParser;

import java.util.List;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.LexicalRelationType;
import org.darisadesigns.polyglotlina.Nodes.LinkedWordReference;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.w3c.dom.Node;

/**
 * Parses a word-level linked language reference.
 *
 * @author draque
 */
public class LinkedWordReferenceParser extends BaseParser {
    private final ConWord conWord;
    private final LinkedWordReference linkedWordReference = new LinkedWordReference();

    public LinkedWordReferenceParser(List<String> _parseIssues, ConWord _conWord) {
        super(_parseIssues);
        conWord = _conWord;
    }

    @Override
    public void parse(Node parent, DictCore core) throws PDomException {
        super.parse(parent, core);
        conWord.setLinkedWordReference(linkedWordReference);
    }

    @Override
    public void consumeChild(Node node, DictCore core) throws Exception {
        switch (node.getNodeName()) {
            case PGTUtil.WORD_LINK_LANGUAGE_PATH_XID -> linkedWordReference.setTargetLanguagePath(node.getTextContent());
            case PGTUtil.WORD_LINK_LANGUAGE_NAME_XID -> linkedWordReference.setCachedTargetLanguageName(node.getTextContent());
            case PGTUtil.WORD_LINK_WORD_ID_XID -> linkedWordReference.setTargetWordId(Integer.parseInt(node.getTextContent()));
            case PGTUtil.WORD_LINK_WORD_VALUE_XID -> linkedWordReference.setCachedTargetWordValue(node.getTextContent());
            case PGTUtil.WORD_LINK_WORD_DEFINITION_XID -> linkedWordReference.setCachedTargetWordDefinition(node.getTextContent());
            case PGTUtil.WORD_LINK_RELATION_TYPE_XID -> linkedWordReference.setRelationType(
                    LexicalRelationType.fromSerializedValue(node.getTextContent()));
            case PGTUtil.WORD_LINK_NOTES_XID -> linkedWordReference.setNotes(node.getTextContent());
            default -> throw new PDomException("Unexpected node in " + this.getClass().getName()
                    + " : " + node.getNodeName());
        }
    }
}
