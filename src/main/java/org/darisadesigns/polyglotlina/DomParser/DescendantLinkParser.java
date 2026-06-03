/*
 * Copyright (c) 2026, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT License
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
package org.darisadesigns.polyglotlina.DomParser;

import java.util.List;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.DescendantLink;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.w3c.dom.Node;

/**
 * Parses a descendant link from XML.
 *
 * @author draque
 */
public class DescendantLinkParser extends BaseParser {
    private final ConWord conWord;
    private final DescendantLink descendantLink = new DescendantLink();

    public DescendantLinkParser(List<String> _parseIssues, ConWord _conWord) {
        super(_parseIssues);
        conWord = _conWord;
    }

    @Override
    public void parse(Node parent, DictCore core) throws PDomException {
        super.parse(parent, core);
        conWord.setDescendantLink(descendantLink);
    }

    @Override
    public void consumeChild(Node node, DictCore core) throws Exception {
        switch (node.getNodeName()) {
            case PGTUtil.WORD_DESCENDANT_PARENT_ID_XID -> {
                descendantLink.setParentWordId(Integer.parseInt(node.getTextContent()));
            }
            case PGTUtil.WORD_DESCENDANT_PARENT_VALUE_XID -> {
                descendantLink.setParentWordValue(node.getTextContent());
            }
            case PGTUtil.WORD_DESCENDANT_PARENT_DEFINITION_XID -> {
                descendantLink.setParentWordDefinition(node.getTextContent());
            }
            case PGTUtil.WORD_DESCENDANT_PARENT_LANGUAGE_XID -> {
                descendantLink.setParentLanguageName(node.getTextContent());
            }
            default ->
                throw new PDomException("Unexpected node in " + this.getClass().getName() + " : " + node.getNodeName());
        }
    }
}
