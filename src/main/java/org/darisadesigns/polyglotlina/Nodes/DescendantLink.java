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

import java.util.Objects;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Stores a snapshot of the parent word used to derive a descendant.
 *
 * @author draque
 */
public class DescendantLink {
    private int parentWordId = -1;
    private String parentWordValue = "";
    private String parentWordDefinition = "";
    private String parentLanguageName = "";

    public DescendantLink() {
    }

    public DescendantLink(DescendantLink link) {
        if (link != null) {
            parentWordId = link.parentWordId;
            parentWordValue = link.parentWordValue;
            parentWordDefinition = link.parentWordDefinition;
            parentLanguageName = link.parentLanguageName;
        }
    }

    public boolean isEmpty() {
        return parentWordId == -1
                && parentWordValue.isBlank()
                && parentWordDefinition.isBlank()
                && parentLanguageName.isBlank();
    }

    public void clear() {
        parentWordId = -1;
        parentWordValue = "";
        parentWordDefinition = "";
        parentLanguageName = "";
    }

    public int getParentWordId() {
        return parentWordId;
    }

    public void setParentWordId(int parentWordId) {
        this.parentWordId = parentWordId;
    }

    public String getParentWordValue() {
        return parentWordValue;
    }

    public void setParentWordValue(String parentWordValue) {
        this.parentWordValue = parentWordValue == null ? "" : parentWordValue;
    }

    public String getParentWordDefinition() {
        return parentWordDefinition;
    }

    public void setParentWordDefinition(String parentWordDefinition) {
        this.parentWordDefinition = parentWordDefinition == null ? "" : parentWordDefinition;
    }

    public String getParentLanguageName() {
        return parentLanguageName;
    }

    public void setParentLanguageName(String parentLanguageName) {
        this.parentLanguageName = parentLanguageName == null ? "" : parentLanguageName;
    }

    public void writeXML(Document doc, Element rootElement) {
        if (isEmpty()) {
            return;
        }

        Element linkNode = doc.createElement(PGTUtil.WORD_DESCENDANT_LINK_XID);

        Element value = doc.createElement(PGTUtil.WORD_DESCENDANT_PARENT_ID_XID);
        value.appendChild(doc.createTextNode(Integer.toString(parentWordId)));
        linkNode.appendChild(value);

        value = doc.createElement(PGTUtil.WORD_DESCENDANT_PARENT_VALUE_XID);
        value.appendChild(doc.createTextNode(parentWordValue));
        linkNode.appendChild(value);

        value = doc.createElement(PGTUtil.WORD_DESCENDANT_PARENT_DEFINITION_XID);
        value.appendChild(doc.createTextNode(parentWordDefinition));
        linkNode.appendChild(value);

        value = doc.createElement(PGTUtil.WORD_DESCENDANT_PARENT_LANGUAGE_XID);
        value.appendChild(doc.createTextNode(parentLanguageName));
        linkNode.appendChild(value);

        rootElement.appendChild(linkNode);
    }

    @Override
    public boolean equals(Object comp) {
        boolean ret = false;

        if (this == comp) {
            ret = true;
        } else if (comp instanceof DescendantLink link) {
            ret = parentWordId == link.parentWordId;
            ret = ret && parentWordValue.equals(link.parentWordValue);
            ret = ret && parentWordDefinition.equals(link.parentWordDefinition);
            ret = ret && parentLanguageName.equals(link.parentLanguageName);
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + parentWordId;
        hash = 31 * hash + Objects.hashCode(parentWordValue);
        hash = 31 * hash + Objects.hashCode(parentWordDefinition);
        hash = 31 * hash + Objects.hashCode(parentLanguageName);
        return hash;
    }
}
