/*
 * Copyright (c) 2026, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT Licence
 * See LICENSE.TXT included with this code to read the full license agreement.
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
 * Snapshot of a cross-language lexical link.
 *
 * The linked word is intentionally stored as a snapshot so older files remain
 * readable even if the source language changes later.
 *
 * @author draque
 */
public class LinkedWordReference {
    private String targetLanguagePath = "";
    private String cachedTargetLanguageName = "";
    private int targetWordId = -1;
    private String cachedTargetWordValue = "";
    private String cachedTargetWordDefinition = "";
    private LexicalRelationType relationType = LexicalRelationType.RELATED;
    private String notes = "";

    public LinkedWordReference() {
    }

    public LinkedWordReference(LinkedWordReference reference) {
        if (reference != null) {
            targetLanguagePath = reference.targetLanguagePath;
            cachedTargetLanguageName = reference.cachedTargetLanguageName;
            targetWordId = reference.targetWordId;
            cachedTargetWordValue = reference.cachedTargetWordValue;
            cachedTargetWordDefinition = reference.cachedTargetWordDefinition;
            relationType = reference.relationType;
            notes = reference.notes;
        }
    }

    public boolean isEmpty() {
        return targetLanguagePath.isBlank()
                && cachedTargetLanguageName.isBlank()
                && targetWordId == -1
                && cachedTargetWordValue.isBlank()
                && cachedTargetWordDefinition.isBlank()
                && notes.isBlank();
    }

    public boolean isValid() {
        return !targetLanguagePath.isBlank() && !cachedTargetLanguageName.isBlank();
    }

    public void clear() {
        targetLanguagePath = "";
        cachedTargetLanguageName = "";
        targetWordId = -1;
        cachedTargetWordValue = "";
        cachedTargetWordDefinition = "";
        relationType = LexicalRelationType.RELATED;
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
        this.cachedTargetLanguageName = cachedTargetLanguageName == null ? "" : cachedTargetLanguageName.trim();
    }

    public int getTargetWordId() {
        return targetWordId;
    }

    public void setTargetWordId(int targetWordId) {
        this.targetWordId = targetWordId;
    }

    public String getCachedTargetWordValue() {
        return cachedTargetWordValue;
    }

    public void setCachedTargetWordValue(String cachedTargetWordValue) {
        this.cachedTargetWordValue = cachedTargetWordValue == null ? "" : cachedTargetWordValue.trim();
    }

    public String getCachedTargetWordDefinition() {
        return cachedTargetWordDefinition;
    }

    public void setCachedTargetWordDefinition(String cachedTargetWordDefinition) {
        this.cachedTargetWordDefinition = cachedTargetWordDefinition == null
                ? "" : cachedTargetWordDefinition.trim();
    }

    public LexicalRelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(LexicalRelationType relationType) {
        this.relationType = relationType == null ? LexicalRelationType.RELATED : relationType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes.trim();
    }

    public String getResolvedTargetFile(DictCore core) {
        if (targetLanguagePath.isBlank()) {
            return "";
        }

        Path targetPath;
        try {
            targetPath = Paths.get(targetLanguagePath);
        } catch (Exception e) {
            return "";
        }
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

        Element linkedWordNode = doc.createElement(PGTUtil.WORD_LINK_REFERENCE_XID);

        Element value = doc.createElement(PGTUtil.WORD_LINK_LANGUAGE_PATH_XID);
        value.appendChild(doc.createTextNode(targetLanguagePath));
        linkedWordNode.appendChild(value);

        value = doc.createElement(PGTUtil.WORD_LINK_LANGUAGE_NAME_XID);
        value.appendChild(doc.createTextNode(cachedTargetLanguageName));
        linkedWordNode.appendChild(value);

        value = doc.createElement(PGTUtil.WORD_LINK_WORD_ID_XID);
        value.appendChild(doc.createTextNode(Integer.toString(targetWordId)));
        linkedWordNode.appendChild(value);

        value = doc.createElement(PGTUtil.WORD_LINK_WORD_VALUE_XID);
        value.appendChild(doc.createTextNode(cachedTargetWordValue));
        linkedWordNode.appendChild(value);

        value = doc.createElement(PGTUtil.WORD_LINK_WORD_DEFINITION_XID);
        value.appendChild(doc.createTextNode(cachedTargetWordDefinition));
        linkedWordNode.appendChild(value);

        value = doc.createElement(PGTUtil.WORD_LINK_RELATION_TYPE_XID);
        value.appendChild(doc.createTextNode(relationType.name()));
        linkedWordNode.appendChild(value);

        value = doc.createElement(PGTUtil.WORD_LINK_NOTES_XID);
        value.appendChild(doc.createTextNode(notes));
        linkedWordNode.appendChild(value);

        rootElement.appendChild(linkedWordNode);
    }

    @Override
    public boolean equals(Object comp) {
        boolean ret = false;

        if (this == comp) {
            ret = true;
        } else if (comp instanceof LinkedWordReference reference) {
            ret = targetLanguagePath.equals(reference.targetLanguagePath);
            ret = ret && cachedTargetLanguageName.equals(reference.cachedTargetLanguageName);
            ret = ret && targetWordId == reference.targetWordId;
            ret = ret && cachedTargetWordValue.equals(reference.cachedTargetWordValue);
            ret = ret && cachedTargetWordDefinition.equals(reference.cachedTargetWordDefinition);
            ret = ret && relationType == reference.relationType;
            ret = ret && notes.equals(reference.notes);
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(targetLanguagePath);
        hash = 53 * hash + Objects.hashCode(cachedTargetLanguageName);
        hash = 53 * hash + targetWordId;
        hash = 53 * hash + Objects.hashCode(cachedTargetWordValue);
        hash = 53 * hash + Objects.hashCode(cachedTargetWordDefinition);
        hash = 53 * hash + Objects.hashCode(relationType);
        hash = 53 * hash + Objects.hashCode(notes);
        return hash;
    }
}
