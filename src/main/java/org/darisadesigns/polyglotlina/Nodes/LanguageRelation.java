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
package org.darisadesigns.polyglotlina.Nodes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Metadata describing a related language in an evolution profile.
 *
 * @author draque
 */
public class LanguageRelation {
    private String targetLanguagePath = "";
    private String cachedTargetLanguageName = "";
    private LanguageRelationType relationType = LanguageRelationType.CONTACT;
    private String notes = "";

    public LanguageRelation() {
    }

    public LanguageRelation(LanguageRelation relation) {
        if (relation != null) {
            targetLanguagePath = relation.targetLanguagePath;
            cachedTargetLanguageName = relation.cachedTargetLanguageName;
            relationType = relation.relationType;
            notes = relation.notes;
        }
    }

    public boolean isEmpty() {
        return targetLanguagePath.isBlank()
                && cachedTargetLanguageName.isBlank()
                && notes.isBlank();
    }

    public boolean isValid() {
        return !targetLanguagePath.isBlank() && !cachedTargetLanguageName.isBlank();
    }

    public void clear() {
        targetLanguagePath = "";
        cachedTargetLanguageName = "";
        relationType = LanguageRelationType.CONTACT;
        notes = "";
    }

    public String getTargetLanguagePath() {
        return targetLanguagePath;
    }

    public void setTargetLanguagePath(String targetLanguagePath) {
        this.targetLanguagePath = targetLanguagePath == null ? "" : targetLanguagePath.trim();
    }

    public String getCachedTargetLanguageName() {
        return cachedTargetLanguageName;
    }

    public void setCachedTargetLanguageName(String cachedTargetLanguageName) {
        this.cachedTargetLanguageName = cachedTargetLanguageName == null
                ? "" : cachedTargetLanguageName.trim();
    }

    public LanguageRelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(LanguageRelationType relationType) {
        this.relationType = relationType == null ? LanguageRelationType.CONTACT : relationType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes.trim();
    }

    public String getResolvedTargetLanguagePath(DictCore core) {
        if (targetLanguagePath.isBlank()) {
            return "";
        }

        Path targetPath = Paths.get(targetLanguagePath);
        if (targetPath.isAbsolute() || core == null || core.getCurFileName().isBlank()) {
            return targetPath.normalize().toString();
        }

        Path currentPath = Paths.get(core.getCurFileName());
        Path currentDir = currentPath.getParent();

        if (currentDir == null) {
            return targetPath.normalize().toString();
        }

        return currentDir.resolve(targetPath).normalize().toString();
    }

    public void setTargetLanguagePathFromAbsolute(String absolutePath, DictCore core) {
        if (absolutePath == null || absolutePath.isBlank()) {
            targetLanguagePath = "";
            return;
        }

        Path target = Paths.get(absolutePath).normalize();

        if (core != null && !core.getCurFileName().isBlank()) {
            try {
                Path currentDir = Paths.get(core.getCurFileName()).getParent();
                if (currentDir != null) {
                    targetLanguagePath = currentDir.relativize(target).toString();
                    return;
                }
            } catch (Exception e) {
                // If relative path generation fails, fall back to absolute.
            }
        }

        targetLanguagePath = target.toString();
    }

    public void writeXML(Document doc, Element rootElement) {
        if (!isValid()) {
            return;
        }

        Element relationNode = doc.createElement(PGTUtil.LANG_PROP_EVOLUTION_RELATION_XID);

        Element value = doc.createElement(PGTUtil.LANG_PROP_EVOLUTION_RELATION_PATH_XID);
        value.appendChild(doc.createTextNode(targetLanguagePath));
        relationNode.appendChild(value);

        value = doc.createElement(PGTUtil.LANG_PROP_EVOLUTION_RELATION_NAME_XID);
        value.appendChild(doc.createTextNode(cachedTargetLanguageName));
        relationNode.appendChild(value);

        value = doc.createElement(PGTUtil.LANG_PROP_EVOLUTION_RELATION_TYPE_XID);
        value.appendChild(doc.createTextNode(relationType.name()));
        relationNode.appendChild(value);

        value = doc.createElement(PGTUtil.LANG_PROP_EVOLUTION_RELATION_NOTES_XID);
        value.appendChild(doc.createTextNode(notes));
        relationNode.appendChild(value);

        rootElement.appendChild(relationNode);
    }

    @Override
    public boolean equals(Object comp) {
        boolean ret = false;

        if (this == comp) {
            ret = true;
        } else if (comp instanceof LanguageRelation relation) {
            ret = targetLanguagePath.equals(relation.targetLanguagePath);
            ret = ret && cachedTargetLanguageName.equals(relation.cachedTargetLanguageName);
            ret = ret && relationType == relation.relationType;
            ret = ret && notes.equals(relation.notes);
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(targetLanguagePath);
        hash = 31 * hash + Objects.hashCode(cachedTargetLanguageName);
        hash = 31 * hash + Objects.hashCode(relationType);
        hash = 31 * hash + Objects.hashCode(notes);
        return hash;
    }
}
