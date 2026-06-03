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
package org.darisadesigns.polyglotlina.Screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PAddRemoveButton;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PButton;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PDialog;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PList;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PLabel;
import org.darisadesigns.polyglotlina.Desktop.DesktopPropertiesManager;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConjugationPair;
import org.darisadesigns.polyglotlina.Nodes.MorphologyCondition;
import org.darisadesigns.polyglotlina.Nodes.MorphologyConditionOperator;
import org.darisadesigns.polyglotlina.Nodes.MorphologyOperationType;
import org.darisadesigns.polyglotlina.Nodes.MorphologyRule;
import org.darisadesigns.polyglotlina.Nodes.WordClass;

public final class ScrMorphologySetup extends PDialog {

    private final int typeId;
    private final DefaultListModel<ConjugationPair> targetModel = new DefaultListModel<>();
    private final PList<ConjugationPair> lstTargets;
    private final JTable tblRules = new JTable();
    private final JTable tblConditions = new JTable();
    private final RuleTableModel ruleTableModel = new RuleTableModel();
    private final ConditionTableModel conditionTableModel = new ConditionTableModel();
    private final PButton btnRuleUp = new PButton(nightMode);
    private final PButton btnRuleDown = new PButton(nightMode);
    private final PAddRemoveButton btnAddRule = new PAddRemoveButton("+");
    private final PAddRemoveButton btnDeleteRule = new PAddRemoveButton("-");
    private final PAddRemoveButton btnAddCondition = new PAddRemoveButton("+");
    private final PAddRemoveButton btnDeleteCondition = new PAddRemoveButton("-");

    public ScrMorphologySetup(DictCore core, int typeId) {
        super(core, true, null);

        this.typeId = typeId;
        lstTargets = new PList<>(((DesktopPropertiesManager) core.getPropertiesManager()).getFontLocal(), core);

        initComponents();
        setupListeners();
        populateTargets();
        populateRules();

        setTitle("Morphology Setup: " + core.getTypes().getNodeById(typeId).getValue());
    }

    public static ScrMorphologySetup run(DictCore core, int typeId) {
        ScrMorphologySetup dialog = new ScrMorphologySetup(core, typeId);
        dialog.setVisible(true);
        return dialog;
    }

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));

        lstTargets.setModel(targetModel);
        lstTargets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblRules.setModel(ruleTableModel);
        tblRules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRules.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tblRules.setRowHeight(tblRules.getRowHeight() + 4);
        tblRules.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox<>(MorphologyOperationType.values())));

        tblConditions.setModel(conditionTableModel);
        tblConditions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblConditions.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tblConditions.setRowHeight(tblConditions.getRowHeight() + 4);
        tblConditions.getColumnModel().getColumn(0).setCellEditor(buildConditionFieldEditor());
        tblConditions.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox<>(MorphologyConditionOperator.values())));

        btnRuleUp.setText("Up");
        btnRuleDown.setText("Down");
        btnAddRule.setToolTipText("Add morphology rule");
        btnDeleteRule.setToolTipText("Delete selected morphology rule");
        btnAddCondition.setToolTipText("Add condition to selected rule");
        btnDeleteCondition.setToolTipText("Delete selected condition");

        JPanel targetPanel = new JPanel(new BorderLayout(0, 5));
        targetPanel.add(new PLabel("Targets"), BorderLayout.NORTH);
        targetPanel.add(new JScrollPane(lstTargets), BorderLayout.CENTER);

        JPanel ruleButtons = new JPanel();
        ruleButtons.add(btnAddRule);
        ruleButtons.add(btnDeleteRule);
        ruleButtons.add(btnRuleUp);
        ruleButtons.add(btnRuleDown);

        JPanel rulesPanel = new JPanel(new BorderLayout(0, 5));
        rulesPanel.add(new PLabel("Rules"), BorderLayout.NORTH);
        rulesPanel.add(new JScrollPane(tblRules), BorderLayout.CENTER);
        rulesPanel.add(ruleButtons, BorderLayout.SOUTH);

        JPanel conditionButtons = new JPanel();
        conditionButtons.add(btnAddCondition);
        conditionButtons.add(btnDeleteCondition);

        JPanel conditionsPanel = new JPanel(new BorderLayout(0, 5));
        conditionsPanel.add(new PLabel("Conditions"), BorderLayout.NORTH);
        conditionsPanel.add(new JScrollPane(tblConditions), BorderLayout.CENTER);
        conditionsPanel.add(conditionButtons, BorderLayout.SOUTH);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rulesPanel, conditionsPanel);
        rightSplit.setResizeWeight(0.65);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, targetPanel, rightSplit);
        mainSplit.setResizeWeight(0.2);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainSplit, BorderLayout.CENTER);

        pack();
        setSize(1000, 650);
    }

    private void setupListeners() {
        lstTargets.addListSelectionListener((ListSelectionEvent event) -> {
            if (!event.getValueIsAdjusting()) {
                stopTableEditing();
                populateRules();
            }
        });

        tblRules.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
            if (!event.getValueIsAdjusting()) {
                stopConditionEditing();
                populateConditions();
            }
        });

        btnAddRule.addActionListener((event) -> addRule());
        btnDeleteRule.addActionListener((event) -> deleteSelectedRule());
        btnRuleUp.addActionListener((event) -> moveSelectedRuleUp());
        btnRuleDown.addActionListener((event) -> moveSelectedRuleDown());
        btnAddCondition.addActionListener((event) -> addCondition());
        btnDeleteCondition.addActionListener((event) -> deleteSelectedCondition());
    }

    private DefaultCellEditor buildConditionFieldEditor() {
        JComboBox<String> cmbFields = new JComboBox<>(getSuggestedConditionFields());
        cmbFields.setEditable(true);
        return new DefaultCellEditor(cmbFields);
    }

    private String[] getSuggestedConditionFields() {
        LinkedHashSet<String> fields = new LinkedHashSet<>();
        fields.add("form");
        fields.add("lemma");
        fields.add("stem");
        fields.add("target");
        fields.add("targetLabel");
        fields.add("partOfSpeech");

        for (WordClass wordClass : core.getWordClassCollection().getClassesForType(typeId)) {
            fields.add(wordClass.getValue());
        }

        return fields.toArray(new String[0]);
    }

    private void populateTargets() {
        targetModel.clear();

        for (ConjugationPair pair : core.getConjugationManager().getMorphologyTargets(typeId)) {
            targetModel.addElement(pair);
        }

        if (targetModel.getSize() > 0) {
            int selected = lstTargets.getSelectedIndex();
            lstTargets.setSelectedIndex(selected >= 0 && selected < targetModel.getSize() ? selected : 0);
        }
    }

    private void populateRules() {
        ConjugationPair target = getSelectedTarget();

        if (target == null) {
            ruleTableModel.setRules(new ArrayList<>());
            conditionTableModel.setConditions(new ArrayList<>());
            return;
        }

        ruleTableModel.setRules(new ArrayList<>(Arrays.asList(
                core.getConjugationManager().getMorphologyRulesForTarget(typeId, target.combinedId))));

        if (ruleTableModel.getRowCount() > 0) {
            tblRules.setRowSelectionInterval(0, 0);
        } else {
            conditionTableModel.setConditions(new ArrayList<>());
        }
    }

    private void populateConditions() {
        MorphologyRule rule = getSelectedRule();

        if (rule == null) {
            conditionTableModel.setConditions(new ArrayList<>());
            return;
        }

        conditionTableModel.setConditions(rule.getConditions());
    }

    private void addRule() {
        stopTableEditing();
        ConjugationPair target = getSelectedTarget();

        if (target == null) {
            return;
        }

        MorphologyRule rule = new MorphologyRule(typeId, target.combinedId);
        rule.setName("New Rule");
        core.getConjugationManager().addMorphologyRule(rule);
        populateRules();
        selectRuleById(rule.getId());
    }

    private void deleteSelectedRule() {
        stopTableEditing();
        MorphologyRule rule = getSelectedRule();

        if (rule == null) {
            return;
        }

        core.getConjugationManager().deleteMorphologyRule(rule);
        populateRules();
    }

    private void moveSelectedRuleUp() {
        stopTableEditing();
        MorphologyRule rule = getSelectedRule();
        ConjugationPair target = getSelectedTarget();

        if (rule == null || target == null) {
            return;
        }

        core.getConjugationManager().moveMorphologyRulesUp(typeId, target.combinedId, List.of(rule));
        populateRules();
        selectRuleById(rule.getId());
    }

    private void moveSelectedRuleDown() {
        stopTableEditing();
        MorphologyRule rule = getSelectedRule();
        ConjugationPair target = getSelectedTarget();

        if (rule == null || target == null) {
            return;
        }

        core.getConjugationManager().moveMorphologyRulesDown(typeId, target.combinedId, List.of(rule));
        populateRules();
        selectRuleById(rule.getId());
    }

    private void addCondition() {
        stopConditionEditing();
        MorphologyRule rule = getSelectedRule();

        if (rule == null) {
            return;
        }

        rule.addCondition(new MorphologyCondition("form", MorphologyConditionOperator.equals, ""));
        populateConditions();

        if (conditionTableModel.getRowCount() > 0) {
            tblConditions.setRowSelectionInterval(conditionTableModel.getRowCount() - 1, conditionTableModel.getRowCount() - 1);
        }
    }

    private void deleteSelectedCondition() {
        stopConditionEditing();
        MorphologyRule rule = getSelectedRule();
        int selectedRow = tblConditions.getSelectedRow();

        if (rule == null || selectedRow < 0 || selectedRow >= rule.getConditions().size()) {
            return;
        }

        rule.removeCondition(rule.getConditions().get(selectedRow));
        populateConditions();
    }

    private ConjugationPair getSelectedTarget() {
        return lstTargets.getSelectedValue();
    }

    private MorphologyRule getSelectedRule() {
        int row = tblRules.getSelectedRow();

        if (row < 0 || row >= ruleTableModel.getRowCount()) {
            return null;
        }

        return ruleTableModel.getRuleAt(row);
    }

    private void selectRuleById(int ruleId) {
        for (int i = 0; i < ruleTableModel.getRowCount(); i++) {
            if (ruleTableModel.getRuleAt(i).getId() == ruleId) {
                tblRules.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    private void stopTableEditing() {
        if (tblRules.isEditing()) {
            tblRules.getCellEditor().stopCellEditing();
        }

        stopConditionEditing();
    }

    private void stopConditionEditing() {
        if (tblConditions.isEditing()) {
            tblConditions.getCellEditor().stopCellEditing();
        }
    }

    @Override
    public void updateAllValues(DictCore _core) {
        core = _core;
        populateTargets();
        populateRules();
    }

    private static class RuleTableModel extends AbstractTableModel {
        private final String[] columns = {"Enabled", "Name", "Operation", "Value 1", "Value 2", "Notes"};
        private List<MorphologyRule> rules = new ArrayList<>();

        public void setRules(List<MorphologyRule> rules) {
            this.rules = rules;
            fireTableDataChanged();
        }

        public MorphologyRule getRuleAt(int row) {
            return rules.get(row);
        }

        @Override
        public int getRowCount() {
            return rules.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0 -> Boolean.class;
                case 2 -> MorphologyOperationType.class;
                default -> String.class;
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            MorphologyRule rule = rules.get(rowIndex);

            return switch (columnIndex) {
                case 0 -> rule.isEnabled();
                case 1 -> rule.getName();
                case 2 -> rule.getOperationType();
                case 3 -> rule.getValue1();
                case 4 -> rule.getValue2();
                case 5 -> rule.getNotes();
                default -> "";
            };
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            MorphologyRule rule = rules.get(rowIndex);

            switch (columnIndex) {
                case 0 -> rule.setEnabled(Boolean.TRUE.equals(aValue));
                case 1 -> rule.setName(aValue == null ? "" : aValue.toString());
                case 2 -> rule.setOperationType((MorphologyOperationType) aValue);
                case 3 -> rule.setValue1(aValue == null ? "" : aValue.toString());
                case 4 -> rule.setValue2(aValue == null ? "" : aValue.toString());
                case 5 -> rule.setNotes(aValue == null ? "" : aValue.toString());
                default -> {
                }
            }

            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    private static class ConditionTableModel extends AbstractTableModel {
        private final String[] columns = {"Field", "Operator", "Value"};
        private List<MorphologyCondition> conditions = new ArrayList<>();

        public void setConditions(List<MorphologyCondition> conditions) {
            this.conditions = conditions;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return conditions.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 1 ? MorphologyConditionOperator.class : String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            MorphologyCondition condition = conditions.get(rowIndex);

            return switch (columnIndex) {
                case 0 -> condition.getFieldName();
                case 1 -> condition.getOperator();
                case 2 -> condition.getValue();
                default -> "";
            };
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            MorphologyCondition condition = conditions.get(rowIndex);

            switch (columnIndex) {
                case 0 -> condition.setFieldName(aValue == null ? "" : aValue.toString());
                case 1 -> condition.setOperator((MorphologyConditionOperator) aValue);
                case 2 -> condition.setValue(aValue == null ? "" : aValue.toString());
                default -> {
                }
            }

            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}
