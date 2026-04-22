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
 * Metadata describing a language file linked to the current language.
 *
 * @author draque
 */
public class LinkedLanguage {
    private String targetFile = "";
    private String languageName = "";
    private LanguageLinkType linkType = LanguageLinkType.CONTACT;
    private String notes = "";

    public LinkedLanguage() {
    }

    public LinkedLanguage(LinkedLanguage linkedLanguage) {
        if (linkedLanguage != null) {
            targetFile = linkedLanguage.targetFile;
            languageName = linkedLanguage.languageName;
            linkType = linkedLanguage.linkType;
            notes = linkedLanguage.notes;
        }
    }

    public boolean isValid() {
        return !targetFile.isBlank() && !languageName.isBlank();
    }

    public void clear() {
        targetFile = "";
        languageName = "";
        linkType = LanguageLinkType.CONTACT;
        notes = "";
    }

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile == null ? "" : targetFile.trim();
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName == null ? "" : languageName.trim();
    }

    public LanguageLinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LanguageLinkType linkType) {
        this.linkType = linkType == null ? LanguageLinkType.CONTACT : linkType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes.trim();
    }

    public String getResolvedTargetFile(DictCore core) {
        if (targetFile.isBlank()) {
            return "";
        }

        Path targetPath = Paths.get(targetFile);
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

    public void setTargetFileFromAbsolute(String absolutePath, DictCore core) {
        if (absolutePath == null || absolutePath.isBlank()) {
            targetFile = "";
            return;
        }

        Path target = Paths.get(absolutePath).normalize();

        if (core != null && !core.getCurFileName().isBlank()) {
            try {
                Path currentDir = Paths.get(core.getCurFileName()).getParent();
                if (currentDir != null) {
                    targetFile = currentDir.relativize(target).toString();
                    return;
                }
            } catch (Exception e) {
                // If relative path generation fails, fall back to absolute.
            }
        }

        targetFile = target.toString();
    }

    public void writeXML(Document doc, Element rootElement) {
        if (!isValid()) {
            return;
        }

        Element linkedLanguageNode = doc.createElement(PGTUtil.LANG_PROP_LINKED_LANGUAGE_XID);

        Element value = doc.createElement(PGTUtil.LANG_PROP_LINKED_LANGUAGE_TARGET_FILE_XID);
        value.appendChild(doc.createTextNode(targetFile));
        linkedLanguageNode.appendChild(value);

        value = doc.createElement(PGTUtil.LANG_PROP_LINKED_LANGUAGE_NAME_XID);
        value.appendChild(doc.createTextNode(languageName));
        linkedLanguageNode.appendChild(value);

        value = doc.createElement(PGTUtil.LANG_PROP_LINKED_LANGUAGE_TYPE_XID);
        value.appendChild(doc.createTextNode(linkType.name()));
        linkedLanguageNode.appendChild(value);

        value = doc.createElement(PGTUtil.LANG_PROP_LINKED_LANGUAGE_NOTES_XID);
        value.appendChild(doc.createTextNode(notes));
        linkedLanguageNode.appendChild(value);

        rootElement.appendChild(linkedLanguageNode);
    }

    @Override
    public boolean equals(Object comp) {
        boolean ret = false;

        if (this == comp) {
            ret = true;
        } else if (comp instanceof LinkedLanguage linkedLanguage) {
            ret = targetFile.equals(linkedLanguage.targetFile);
            ret = ret && languageName.equals(linkedLanguage.languageName);
            ret = ret && linkType == linkedLanguage.linkType;
            ret = ret && notes.equals(linkedLanguage.notes);
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(targetFile);
        hash = 53 * hash + Objects.hashCode(languageName);
        hash = 53 * hash + Objects.hashCode(linkType);
        hash = 53 * hash + Objects.hashCode(notes);
        return hash;
    }
}
