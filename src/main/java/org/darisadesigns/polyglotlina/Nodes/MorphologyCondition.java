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

public class MorphologyCondition {
    private String fieldName = "";
    private MorphologyConditionOperator operator = MorphologyConditionOperator.equals;
    private String value = "";

    public MorphologyCondition() {
    }

    public MorphologyCondition(String fieldName, MorphologyConditionOperator operator, String value) {
        this.fieldName = fieldName == null ? "" : fieldName;
        this.operator = operator == null ? MorphologyConditionOperator.equals : operator;
        this.value = value == null ? "" : value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName == null ? "" : fieldName;
    }

    public MorphologyConditionOperator getOperator() {
        return operator;
    }

    public void setOperator(MorphologyConditionOperator operator) {
        this.operator = operator == null ? MorphologyConditionOperator.equals : operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? "" : value;
    }

    public void writeXML(Document doc, Element rootElement) {
        Element conditionNode = doc.createElement(PGTUtil.MORPH_CONDITION_XID);
        rootElement.appendChild(conditionNode);

        Element child = doc.createElement(PGTUtil.MORPH_CONDITION_FIELD_XID);
        child.appendChild(doc.createTextNode(fieldName));
        conditionNode.appendChild(child);

        child = doc.createElement(PGTUtil.MORPH_CONDITION_OPERATOR_XID);
        child.appendChild(doc.createTextNode(operator.name()));
        conditionNode.appendChild(child);

        child = doc.createElement(PGTUtil.MORPH_CONDITION_VALUE_XID);
        child.appendChild(doc.createTextNode(value));
        conditionNode.appendChild(child);
    }

    @Override
    public boolean equals(Object comp) {
        boolean ret = false;

        if (this == comp) {
            ret = true;
        } else if (comp instanceof MorphologyCondition condition) {
            ret = Objects.equals(fieldName, condition.fieldName)
                    && operator == condition.operator
                    && Objects.equals(value, condition.value);
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.fieldName);
        hash = 67 * hash + Objects.hashCode(this.operator);
        hash = 67 * hash + Objects.hashCode(this.value);
        return hash;
    }
}
