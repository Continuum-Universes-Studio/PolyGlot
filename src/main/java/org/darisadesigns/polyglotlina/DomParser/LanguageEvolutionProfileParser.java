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
import org.darisadesigns.polyglotlina.Nodes.LanguageEvolutionProfile;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.w3c.dom.Node;

/**
 * Parses a language evolution profile from XML.
 *
 * @author draque
 */
public class LanguageEvolutionProfileParser extends BaseParser {

    public LanguageEvolutionProfileParser(List<String> _parseIssues) {
        super(_parseIssues);
    }

    @Override
    public void parse(Node parent, DictCore core) throws PDomException {
        core.getPropertiesManager().getLanguageEvolutionProfile().clear();
        super.parse(parent, core);
    }

    @Override
    public void consumeChild(Node node, DictCore core) throws Exception {
        LanguageEvolutionProfile profile = core.getPropertiesManager().getLanguageEvolutionProfile();

        switch (node.getNodeName()) {
            case PGTUtil.LANG_PROP_EVOLUTION_PARENT_PATH_XID -> {
                profile.setParentLanguagePath(node.getTextContent());
            }
            case PGTUtil.LANG_PROP_EVOLUTION_PARENT_NAME_XID -> {
                profile.setCachedParentLanguageName(node.getTextContent());
            }
            case PGTUtil.LANG_PROP_EVOLUTION_RULES_XID -> {
                new LanguageEvolutionRulesParser(parseIssues).parse(node, core);
            }
            case PGTUtil.LANG_PROP_EVOLUTION_RELATIONS_XID -> {
                new LanguageEvolutionRelationsParser(parseIssues).parse(node, core);
            }
            default ->
                throw new PDomException("Unexpected node in " + this.getClass().getName() + " : " + node.getNodeName());
        }
    }
}
