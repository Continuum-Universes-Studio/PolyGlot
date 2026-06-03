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
package org.darisadesigns.polyglotlina.Nodes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Stores language-level evolution settings for deriving descendants from a
 * linked parent language.
 *
 * @author draque
 */
public class LanguageEvolutionProfile {
    private String parentLanguagePath = "";
    private String cachedParentLanguageName = "";
    private final List<String> soundChangeRules = new ArrayList<>();
    private final List<LanguageRelation> relatedLanguages = new ArrayList<>();

    public LanguageEvolutionProfile() {
    }

    public LanguageEvolutionProfile(LanguageEvolutionProfile profile) {
        if (profile != null) {
            parentLanguagePath = profile.parentLanguagePath;
            cachedParentLanguageName = profile.cachedParentLanguageName;
            soundChangeRules.addAll(profile.soundChangeRules);
            setRelatedLanguages(profile.relatedLanguages);
        }
    }

    public boolean isEmpty() {
        return parentLanguagePath.isBlank()
                && cachedParentLanguageName.isBlank()
                && soundChangeRules.isEmpty()
                && relatedLanguages.isEmpty();
    }

    public void clear() {
        parentLanguagePath = "";
        cachedParentLanguageName = "";
        soundChangeRules.clear();
        relatedLanguages.clear();
    }

    public String getParentLanguagePath() {
        return parentLanguagePath;
    }

    public void setParentLanguagePath(String parentLanguagePath) {
        this.parentLanguagePath = parentLanguagePath == null ? "" : parentLanguagePath.trim();
    }

    public String getCachedParentLanguageName() {
        return cachedParentLanguageName;
    }

    public void setCachedParentLanguageName(String cachedParentLanguageName) {
        this.cachedParentLanguageName = cachedParentLanguageName == null ? "" : cachedParentLanguageName.trim();
    }

    public String getResolvedParentLanguagePath(DictCore core) {
        if (parentLanguagePath.isBlank()) {
            return "";
        }

        Path parentPath = Paths.get(parentLanguagePath);
        if (parentPath.isAbsolute() || core == null || core.getCurFileName().isBlank()) {
            return parentPath.normalize().toString();
        }

        Path childPath = Paths.get(core.getCurFileName());
        Path parentDir = childPath.getParent();

        if (parentDir == null) {
            return parentPath.normalize().toString();
        }

        return parentDir.resolve(parentPath).normalize().toString();
    }

    public void setParentLanguagePathFromAbsolute(String absolutePath, DictCore core) {
        if (absolutePath == null || absolutePath.isBlank()) {
            parentLanguagePath = "";
            return;
        }

        Path target = Paths.get(absolutePath).normalize();

        if (core != null && !core.getCurFileName().isBlank()) {
            try {
                Path childDir = Paths.get(core.getCurFileName()).getParent();
                if (childDir != null) {
                    parentLanguagePath = childDir.relativize(target).toString();
                    return;
                }
            } catch (Exception e) {
                // If relative path generation fails, fall back to absolute.
            }
        }

        parentLanguagePath = target.toString();
    }

    public List<String> getSoundChangeRules() {
        return new ArrayList<>(soundChangeRules);
    }

    public void setSoundChangeRules(List<String> soundChangeRules) {
        this.soundChangeRules.clear();

        if (soundChangeRules != null) {
            for (String rule : soundChangeRules) {
                addSoundChangeRule(rule);
            }
        }
    }

    public void addSoundChangeRule(String rule) {
        if (rule != null && !rule.isBlank()) {
            soundChangeRules.add(rule.trim());
        }
    }

    public List<LanguageRelation> getRelatedLanguages() {
        List<LanguageRelation> ret = new ArrayList<>();

        for (LanguageRelation relation : relatedLanguages) {
            ret.add(new LanguageRelation(relation));
        }

        return ret;
    }

    public LanguageRelation getRelatedLanguage(int index) {
        if (index < 0 || index >= relatedLanguages.size()) {
            return null;
        }

        return new LanguageRelation(relatedLanguages.get(index));
    }

    public void setRelatedLanguages(List<LanguageRelation> relatedLanguages) {
        this.relatedLanguages.clear();

        if (relatedLanguages != null) {
            for (LanguageRelation relation : relatedLanguages) {
                addRelatedLanguage(relation);
            }
        }
    }

    public void addRelatedLanguage(LanguageRelation relation) {
        if (relation != null && relation.isValid()) {
            relatedLanguages.add(new LanguageRelation(relation));
        }
    }

    public void setRelatedLanguage(int index, LanguageRelation relation) {
        if (relation == null || !relation.isValid()) {
            return;
        }

        if (index > -1 && index < relatedLanguages.size()) {
            relatedLanguages.set(index, new LanguageRelation(relation));
        }
    }

    public void removeRelatedLanguage(int index) {
        if (index > -1 && index < relatedLanguages.size()) {
            relatedLanguages.remove(index);
        }
    }

    public void writeXML(Document doc, Element rootElement) {
        if (isEmpty()) {
            return;
        }

        Element profileNode = doc.createElement(PGTUtil.LANG_PROP_EVOLUTION_PROFILE_XID);

        Element value = doc.createElement(PGTUtil.LANG_PROP_EVOLUTION_PARENT_PATH_XID);
        value.appendChild(doc.createTextNode(parentLanguagePath));
        profileNode.appendChild(value);

        value = doc.createElement(PGTUtil.LANG_PROP_EVOLUTION_PARENT_NAME_XID);
        value.appendChild(doc.createTextNode(cachedParentLanguageName));
        profileNode.appendChild(value);

        Element rulesNode = doc.createElement(PGTUtil.LANG_PROP_EVOLUTION_RULES_XID);
        for (String rule : soundChangeRules) {
            Element ruleNode = doc.createElement(PGTUtil.LANG_PROP_EVOLUTION_RULE_XID);
            ruleNode.appendChild(doc.createTextNode(rule));
            rulesNode.appendChild(ruleNode);
        }
        profileNode.appendChild(rulesNode);

        if (!relatedLanguages.isEmpty()) {
            Element relationsNode = doc.createElement(PGTUtil.LANG_PROP_EVOLUTION_RELATIONS_XID);

            for (LanguageRelation relation : relatedLanguages) {
                relation.writeXML(doc, relationsNode);
            }

            profileNode.appendChild(relationsNode);
        }

        rootElement.appendChild(profileNode);
    }

    @Override
    public boolean equals(Object comp) {
        boolean ret = false;

        if (this == comp) {
            ret = true;
        } else if (comp instanceof LanguageEvolutionProfile profile) {
            ret = parentLanguagePath.equals(profile.parentLanguagePath);
            ret = ret && cachedParentLanguageName.equals(profile.cachedParentLanguageName);
            ret = ret && soundChangeRules.equals(profile.soundChangeRules);
            ret = ret && relatedLanguages.equals(profile.relatedLanguages);
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(parentLanguagePath);
        hash = 29 * hash + Objects.hashCode(cachedParentLanguageName);
        hash = 29 * hash + Objects.hashCode(soundChangeRules);
        hash = 29 * hash + Objects.hashCode(relatedLanguages);
        return hash;
    }
}
