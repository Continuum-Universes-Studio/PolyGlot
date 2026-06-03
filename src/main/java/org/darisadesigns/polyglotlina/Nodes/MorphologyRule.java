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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MorphologyRule implements Comparable<MorphologyRule> {
    private int id = -1;
    private int typeId = -1;
    private String targetKey = "";
    private String name = "";
    private boolean enabled = true;
    private int order = -1;
    private MorphologyOperationType operationType = MorphologyOperationType.append_suffix;
    private String value1 = "";
    private String value2 = "";
    private String notes = "";
    private final List<MorphologyCondition> conditions = new ArrayList<>();

    public MorphologyRule() {
    }

    public MorphologyRule(int typeId, String targetKey) {
        this.typeId = typeId;
        this.targetKey = targetKey == null ? "" : targetKey;
    }

    public void setEqual(MorphologyRule rule) {
        id = rule.id;
        typeId = rule.typeId;
        targetKey = rule.targetKey;
        name = rule.name;
        enabled = rule.enabled;
        order = rule.order;
        operationType = rule.operationType;
        value1 = rule.value1;
        value2 = rule.value2;
        notes = rule.notes;
        conditions.clear();
        rule.conditions.forEach(condition -> {
            conditions.add(new MorphologyCondition(
                    condition.getFieldName(),
                    condition.getOperator(),
                    condition.getValue()));
        });
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey == null ? "" : targetKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? "" : name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public MorphologyOperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(MorphologyOperationType operationType) {
        this.operationType = operationType == null ? MorphologyOperationType.append_suffix : operationType;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1 == null ? "" : value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2 == null ? "" : value2;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes;
    }

    public void addCondition(MorphologyCondition condition) {
        conditions.add(condition == null ? new MorphologyCondition() : condition);
    }

    public void removeCondition(MorphologyCondition condition) {
        conditions.remove(condition);
    }

    public List<MorphologyCondition> getConditions() {
        return conditions;
    }

    public void writeXML(Document doc, Element rootElement) {
        Element ruleNode = doc.createElement(PGTUtil.MORPH_RULE_XID);
        rootElement.appendChild(ruleNode);

        Element child = doc.createElement(PGTUtil.MORPH_RULE_ID_XID);
        child.appendChild(doc.createTextNode(Integer.toString(id)));
        ruleNode.appendChild(child);

        child = doc.createElement(PGTUtil.MORPH_RULE_TYPE_XID);
        child.appendChild(doc.createTextNode(Integer.toString(typeId)));
        ruleNode.appendChild(child);

        child = doc.createElement(PGTUtil.MORPH_RULE_TARGET_XID);
        child.appendChild(doc.createTextNode(targetKey));
        ruleNode.appendChild(child);

        child = doc.createElement(PGTUtil.MORPH_RULE_NAME_XID);
        child.appendChild(doc.createTextNode(name));
        ruleNode.appendChild(child);

        child = doc.createElement(PGTUtil.MORPH_RULE_ENABLED_XID);
        child.appendChild(doc.createTextNode(enabled ? PGTUtil.TRUE : PGTUtil.FALSE));
        ruleNode.appendChild(child);

        child = doc.createElement(PGTUtil.MORPH_RULE_ORDER_XID);
        child.appendChild(doc.createTextNode(Integer.toString(order)));
        ruleNode.appendChild(child);

        child = doc.createElement(PGTUtil.MORPH_RULE_OPERATION_XID);
        child.appendChild(doc.createTextNode(operationType.name()));
        ruleNode.appendChild(child);

        child = doc.createElement(PGTUtil.MORPH_RULE_VALUE_1_XID);
        child.appendChild(doc.createTextNode(value1));
        ruleNode.appendChild(child);

        child = doc.createElement(PGTUtil.MORPH_RULE_VALUE_2_XID);
        child.appendChild(doc.createTextNode(value2));
        ruleNode.appendChild(child);

        child = doc.createElement(PGTUtil.MORPH_RULE_NOTES_XID);
        child.appendChild(doc.createTextNode(notes));
        ruleNode.appendChild(child);

        conditions.forEach(condition -> {
            condition.writeXML(doc, ruleNode);
        });
    }

    @Override
    public int compareTo(MorphologyRule compare) {
        int ret = Integer.compare(order, compare.order);

        if (ret == 0) {
            ret = Integer.compare(id, compare.id);
        }

        return ret;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object comp) {
        boolean ret = false;

        if (this == comp) {
            ret = true;
        } else if (comp instanceof MorphologyRule rule) {
            ret = id == rule.id
                    && typeId == rule.typeId
                    && enabled == rule.enabled
                    && order == rule.order
                    && Objects.equals(targetKey, rule.targetKey)
                    && Objects.equals(name, rule.name)
                    && operationType == rule.operationType
                    && Objects.equals(value1, rule.value1)
                    && Objects.equals(value2, rule.value2)
                    && Objects.equals(notes, rule.notes)
                    && Objects.equals(conditions, rule.conditions);
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.id;
        hash = 59 * hash + this.typeId;
        hash = 59 * hash + Objects.hashCode(this.targetKey);
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + (this.enabled ? 1 : 0);
        hash = 59 * hash + this.order;
        hash = 59 * hash + Objects.hashCode(this.operationType);
        hash = 59 * hash + Objects.hashCode(this.value1);
        hash = 59 * hash + Objects.hashCode(this.value2);
        hash = 59 * hash + Objects.hashCode(this.notes);
        hash = 59 * hash + Objects.hashCode(this.conditions);
        return hash;
    }
}
