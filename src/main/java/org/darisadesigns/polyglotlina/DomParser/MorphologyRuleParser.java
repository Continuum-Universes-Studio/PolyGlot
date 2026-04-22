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
import org.darisadesigns.polyglotlina.Nodes.MorphologyOperationType;
import org.darisadesigns.polyglotlina.Nodes.MorphologyRule;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.w3c.dom.Node;

public class MorphologyRuleParser extends BaseParser {

    private MorphologyRule rule;

    public MorphologyRuleParser(List<String> _parseIssues) {
        super(_parseIssues);
    }

    @Override
    public void parse(Node parent, DictCore core) throws PDomException {
        rule = new MorphologyRule();
        super.parse(parent, core);
        core.getConjugationManager().addMorphologyRule(rule);
    }

    @Override
    public void consumeChild(Node node, DictCore core) throws Exception {
        switch (node.getNodeName()) {
            case PGTUtil.MORPH_RULE_ID_XID -> rule.setId(Integer.parseInt(node.getTextContent()));
            case PGTUtil.MORPH_RULE_TYPE_XID -> rule.setTypeId(Integer.parseInt(node.getTextContent()));
            case PGTUtil.MORPH_RULE_TARGET_XID -> rule.setTargetKey(node.getTextContent());
            case PGTUtil.MORPH_RULE_NAME_XID -> rule.setName(node.getTextContent());
            case PGTUtil.MORPH_RULE_ENABLED_XID -> rule.setEnabled(PGTUtil.TRUE.equals(node.getTextContent()));
            case PGTUtil.MORPH_RULE_ORDER_XID -> rule.setOrder(Integer.parseInt(node.getTextContent()));
            case PGTUtil.MORPH_RULE_OPERATION_XID -> rule.setOperationType(MorphologyOperationType.fromString(node.getTextContent()));
            case PGTUtil.MORPH_RULE_VALUE_1_XID -> rule.setValue1(node.getTextContent());
            case PGTUtil.MORPH_RULE_VALUE_2_XID -> rule.setValue2(node.getTextContent());
            case PGTUtil.MORPH_RULE_NOTES_XID -> rule.setNotes(node.getTextContent());
            case PGTUtil.MORPH_CONDITION_XID -> new MorphologyConditionParser(parseIssues, rule).parse(node, core);
            default -> throw new PDomException("Unexpected node in " + this.getClass().getName() + " : " + node.getNodeName());
        }
    }
}
