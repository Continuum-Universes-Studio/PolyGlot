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
package org.darisadesigns.polyglotlina.Screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.DesktopInfoBox;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PButton;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PComboBox;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PDialog;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PLabel;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PTable;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PTextField;
import org.darisadesigns.polyglotlina.Desktop.DesktopIOHandler;
import org.darisadesigns.polyglotlina.Desktop.DesktopPropertiesManager;
import org.darisadesigns.polyglotlina.Desktop.ManagersCollections.DesktopGrammarManager;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.LanguageEvolutionProfile;
import org.darisadesigns.polyglotlina.Nodes.LanguageRelation;
import org.darisadesigns.polyglotlina.Nodes.LanguageRelationType;
import org.darisadesigns.polyglotlina.Nodes.SoundChangeStep;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.darisadesigns.polyglotlina.SoundChangeEngine;

/**
 * Editor dialog for language-level evolution settings.
 *
 * @author draque
 */
public class ScrEvolutionProfile extends PDialog {
    private final DesktopPropertiesManager propMan;
    private final LanguageEvolutionProfile workingProfile;
    private final DefaultTableModel ruleModel;
    private final DefaultTableModel previewModel;
    private final DefaultTableModel relationModel;
    private final PTextField txtParentPath;
    private final PTextField txtParentName;
    private final PTextField txtSampleInput;
    private final PTextField txtPreviewOutput;
    private final PTextField txtPreviewStatus;
    private final PTable tblRules;
    private final PTable tblPreview;
    private final PTable tblRelations;

    public ScrEvolutionProfile(DictCore _core) {
        super(_core);
        propMan = (DesktopPropertiesManager) core.getPropertiesManager();
        workingProfile = new LanguageEvolutionProfile(propMan.getLanguageEvolutionProfile());

        ruleModel = new DefaultTableModel(new String[]{"Ordered Sound Changes"}, 0);
        previewModel = new DefaultTableModel(new String[]{"Rule", "Before", "After"}, 0);
        relationModel = new DefaultTableModel(
                new String[]{"Target File", "Language Name", "Relation Type", "Notes"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblRules = new PTable(core);
        tblPreview = new PTable(core);
        tblRelations = new PTable(core);
        txtParentPath = new PTextField(core, true, "Parent Language File");
        txtParentName = new PTextField(core, true, "Parent Language Name");
        txtSampleInput = new PTextField(core, false, "Preview Source Word");
        txtPreviewOutput = new PTextField(core, false, "Preview Output");
        txtPreviewStatus = new PTextField(core, true, "Preview Status");

        buildUi();
        populateFromProfile();
        setupListeners();
        updatePreview();
        setModal(true);
        pack();
    }

    private void buildUi() {
        setTitle("Evolution Profile");
        setMinimumSize(new Dimension(900, 560));
        getContentPane().setLayout(new BorderLayout(8, 8));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Evolution", buildEvolutionTab());
        tabs.addTab("Related Languages", buildRelatedLanguagesTab());

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 6, 6));
        PButton btnCancel = new PButton(nightMode);
        btnCancel.setText("Cancel");
        btnCancel.addActionListener((evt) -> dispose());
        PButton btnOk = new PButton(nightMode);
        btnOk.setText("OK");
        btnOk.addActionListener((evt) -> saveAndClose());
        buttonsPanel.add(btnCancel);
        buttonsPanel.add(btnOk);

        getContentPane().add(tabs, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JPanel buildEvolutionTab() {
        JPanel evolutionPanel = new JPanel(new BorderLayout(8, 8));

        JPanel parentPanel = new JPanel();
        parentPanel.setBorder(BorderFactory.createTitledBorder("Parent Language"));
        parentPanel.setLayout(new GridLayout(3, 1, 6, 6));

        txtParentPath.setEditable(false);
        txtParentName.setEditable(false);

        PButton btnChooseParent = new PButton(nightMode);
        btnChooseParent.setText("Select Parent...");
        btnChooseParent.addActionListener((evt) -> chooseParentLanguage());

        PButton btnClearParent = new PButton(nightMode);
        btnClearParent.setText("Clear Parent");
        btnClearParent.addActionListener((evt) -> {
            workingProfile.setParentLanguagePath("");
            workingProfile.setCachedParentLanguageName("");
            populateParentFields();
        });

        JPanel parentButtons = new JPanel(new GridLayout(1, 2, 6, 6));
        parentButtons.add(btnChooseParent);
        parentButtons.add(btnClearParent);

        parentPanel.add(txtParentPath);
        parentPanel.add(txtParentName);
        parentPanel.add(parentButtons);

        JPanel rulesPanel = new JPanel(new BorderLayout(6, 6));
        rulesPanel.setBorder(BorderFactory.createTitledBorder("Ordered Sound Changes"));
        tblRules.setModel(ruleModel);
        tblRules.setRowHeight(26);
        tblRules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel ruleButtons = new JPanel(new GridLayout(1, 4, 6, 6));
        PButton btnAddRule = new PButton(nightMode);
        btnAddRule.setText("+");
        btnAddRule.addActionListener((evt) -> ruleModel.addRow(new String[]{""}));
        PButton btnDeleteRule = new PButton(nightMode);
        btnDeleteRule.setText("-");
        btnDeleteRule.addActionListener((evt) -> deleteSelectedRule());
        PButton btnMoveUp = new PButton(nightMode);
        btnMoveUp.setText("Up");
        btnMoveUp.addActionListener((evt) -> moveRule(-1));
        PButton btnMoveDown = new PButton(nightMode);
        btnMoveDown.setText("Down");
        btnMoveDown.addActionListener((evt) -> moveRule(1));
        ruleButtons.add(btnAddRule);
        ruleButtons.add(btnDeleteRule);
        ruleButtons.add(btnMoveUp);
        ruleButtons.add(btnMoveDown);

        rulesPanel.add(new JScrollPane(tblRules), BorderLayout.CENTER);
        rulesPanel.add(ruleButtons, BorderLayout.SOUTH);

        JPanel previewPanel = new JPanel(new BorderLayout(6, 6));
        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        tblPreview.setModel(previewModel);
        tblPreview.setEnabled(false);
        tblPreview.setRowHeight(26);

        txtPreviewOutput.setEditable(false);
        txtPreviewStatus.setEditable(false);
        txtPreviewStatus.setEnabled(false);

        JPanel previewInputs = new JPanel(new GridLayout(3, 1, 6, 6));
        previewInputs.add(txtSampleInput);
        previewInputs.add(txtPreviewOutput);
        previewInputs.add(txtPreviewStatus);

        previewPanel.add(previewInputs, BorderLayout.NORTH);
        previewPanel.add(new JScrollPane(tblPreview), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        centerPanel.add(rulesPanel);
        centerPanel.add(previewPanel);

        evolutionPanel.add(parentPanel, BorderLayout.NORTH);
        evolutionPanel.add(centerPanel, BorderLayout.CENTER);

        return evolutionPanel;
    }

    private JPanel buildRelatedLanguagesTab() {
        JPanel relatedPanel = new JPanel(new BorderLayout(6, 6));
        relatedPanel.setBorder(BorderFactory.createTitledBorder("Related Languages"));

        tblRelations.setModel(relationModel);
        tblRelations.setRowHeight(26);
        tblRelations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel relationButtons = new JPanel(new GridLayout(1, 3, 6, 6));
        PButton btnAddRelation = new PButton(nightMode);
        btnAddRelation.setText("Add");
        btnAddRelation.addActionListener((evt) -> addRelatedLanguage());

        PButton btnEditRelation = new PButton(nightMode);
        btnEditRelation.setText("Edit");
        btnEditRelation.addActionListener((evt) -> editRelatedLanguage());

        PButton btnRemoveRelation = new PButton(nightMode);
        btnRemoveRelation.setText("Remove");
        btnRemoveRelation.addActionListener((evt) -> removeRelatedLanguage());

        relationButtons.add(btnAddRelation);
        relationButtons.add(btnEditRelation);
        relationButtons.add(btnRemoveRelation);

        relatedPanel.add(new JScrollPane(tblRelations), BorderLayout.CENTER);
        relatedPanel.add(relationButtons, BorderLayout.SOUTH);

        return relatedPanel;
    }

    private void setupListeners() {
        txtSampleInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePreview();
            }
        });

        ruleModel.addTableModelListener((evt) -> updatePreview());
    }

    private void chooseParentLanguage() {
        JFileChooser chooser = buildLanguageChooser("");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String chosenPath = chooser.getSelectedFile().getAbsolutePath();

            try {
                DictCore parentCore = loadReadOnlyCore(chosenPath);
                workingProfile.setParentLanguagePathFromAbsolute(chosenPath, core);
                workingProfile.setCachedParentLanguageName(
                        parentCore.getPropertiesManager().getLangName());
                populateParentFields();
            } catch (Exception e) {
                DesktopIOHandler.getInstance().writeErrorLog(e);
                new DesktopInfoBox(this).error("Parent Language Error",
                        "Unable to read selected parent language:\n" + e.getLocalizedMessage());
            }
        }
    }

    private JFileChooser buildLanguageChooser(String initialPath) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "PolyGlot Languages", PGTUtil.POLYGLOT_FILE_SUFFIX));

        File baseDirectory = null;
        if (initialPath != null && !initialPath.isBlank()) {
            File initialFile = new File(initialPath);
            baseDirectory = initialFile.isDirectory() ? initialFile : initialFile.getParentFile();
        } else if (!core.getCurFileName().isBlank()) {
            baseDirectory = DesktopIOHandler.getInstance().getDirectoryFromPath(core.getCurFileName());
        }

        if (baseDirectory != null && baseDirectory.exists()) {
            chooser.setCurrentDirectory(baseDirectory);
        }

        return chooser;
    }

    private void populateFromProfile() {
        populateParentFields();

        ruleModel.setRowCount(0);
        for (String rule : workingProfile.getSoundChangeRules()) {
            ruleModel.addRow(new String[]{rule});
        }

        refreshRelatedLanguagesTable();
    }

    private void populateParentFields() {
        txtParentPath.setText(workingProfile.getParentLanguagePath());
        txtParentName.setText(workingProfile.getCachedParentLanguageName());
    }

    private void refreshRelatedLanguagesTable() {
        relationModel.setRowCount(0);

        for (LanguageRelation relation : workingProfile.getRelatedLanguages()) {
            relationModel.addRow(new Object[]{
                relation.getTargetLanguagePath(),
                relation.getCachedTargetLanguageName(),
                relation.getRelationType(),
                relation.getNotes()
            });
        }
    }

    private void addRelatedLanguage() {
        LanguageRelationEditorDialog dialog = new LanguageRelationEditorDialog(null);
        dialog.setVisible(true);

        LanguageRelation relation = dialog.getRelation();
        if (relation != null) {
            workingProfile.addRelatedLanguage(relation);
            refreshRelatedLanguagesTable();
            selectRelationRow(relationModel.getRowCount() - 1);
        }
    }

    private void editRelatedLanguage() {
        int selectedRow = tblRelations.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        LanguageRelation relation = workingProfile.getRelatedLanguage(selectedRow);
        LanguageRelationEditorDialog dialog = new LanguageRelationEditorDialog(relation);
        dialog.setVisible(true);

        LanguageRelation updatedRelation = dialog.getRelation();
        if (updatedRelation != null) {
            workingProfile.setRelatedLanguage(selectedRow, updatedRelation);
            refreshRelatedLanguagesTable();
            selectRelationRow(selectedRow);
        }
    }

    private void removeRelatedLanguage() {
        int selectedRow = tblRelations.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        workingProfile.removeRelatedLanguage(selectedRow);
        refreshRelatedLanguagesTable();
        selectRelationRow(Math.min(selectedRow, relationModel.getRowCount() - 1));
    }

    private void selectRelationRow(int row) {
        if (row > -1 && row < relationModel.getRowCount()) {
            tblRelations.changeSelection(row, 0, false, false);
        }
    }

    private void deleteSelectedRule() {
        int selectedRow = tblRules.getSelectedRow();

        if (selectedRow > -1) {
            ruleModel.removeRow(selectedRow);
        }
    }

    private void moveRule(int direction) {
        int selectedRow = tblRules.getSelectedRow();
        int targetRow = selectedRow + direction;

        if (selectedRow > -1 && targetRow > -1 && targetRow < ruleModel.getRowCount()) {
            Object rule = ruleModel.getValueAt(selectedRow, 0);
            ruleModel.setValueAt(ruleModel.getValueAt(targetRow, 0), selectedRow, 0);
            ruleModel.setValueAt(rule, targetRow, 0);
            tblRules.changeSelection(targetRow, 0, false, false);
        }
    }

    private List<String> getRuleTexts() {
        List<String> ret = new ArrayList<>();

        for (int i = 0; i < ruleModel.getRowCount(); i++) {
            Object value = ruleModel.getValueAt(i, 0);
            if (value instanceof String rule && !rule.isBlank()) {
                ret.add(rule.trim());
            }
        }

        return ret;
    }

    private void updatePreview() {
        previewModel.setRowCount(0);
        txtPreviewOutput.setText("");
        txtPreviewStatus.setText("");

        try {
            List<String> rules = getRuleTexts();
            List<SoundChangeStep> steps = SoundChangeEngine.traceEvolution(
                    txtSampleInput.getText(), rules, propMan);

            for (SoundChangeStep step : steps) {
                previewModel.addRow(new String[]{
                    step.getRuleText(),
                    step.getSourceValue(),
                    step.getResultValue()
                });
            }

            String output = SoundChangeEngine.evolve(txtSampleInput.getText(), rules, propMan);
            txtPreviewOutput.setText(output);
            txtPreviewStatus.setText("Preview ready.");
        } catch (IllegalArgumentException e) {
            txtPreviewStatus.setText("Preview error: " + e.getLocalizedMessage());
        }
    }

    private void saveAndClose() {
        var cellEditor = tblRules.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }

        workingProfile.setSoundChangeRules(getRuleTexts());

        LanguageEvolutionProfile profile = propMan.getLanguageEvolutionProfile();
        profile.clear();
        profile.setParentLanguagePath(workingProfile.getParentLanguagePath());
        profile.setCachedParentLanguageName(workingProfile.getCachedParentLanguageName());
        profile.setSoundChangeRules(workingProfile.getSoundChangeRules());
        profile.setRelatedLanguages(workingProfile.getRelatedLanguages());
        dispose();
    }

    private String resolveLanguageDisplayName(DictCore languageCore, String filePath) {
        String languageName = languageCore == null ? "" : languageCore.getPropertiesManager().getLangName();
        if (!languageName.isBlank()) {
            return languageName;
        }

        String fileName = new File(filePath).getName();
        String suffix = "." + PGTUtil.POLYGLOT_FILE_SUFFIX;
        if (fileName.endsWith(suffix)) {
            fileName = fileName.substring(0, fileName.length() - suffix.length());
        }

        return fileName;
    }

    private DictCore loadReadOnlyCore(String filePath)
            throws IOException, ParserConfigurationException {
        DictCore parentCore = new DictCore(new DesktopPropertiesManager(),
                core.getOSHandler(), new org.darisadesigns.polyglotlina.Desktop.PGTUtil(),
                new DesktopGrammarManager());

        try {
            parentCore.readFile(filePath);
        } catch (IllegalStateException e) {
            // Warnings do not invalidate the linked language for metadata or preview.
        }

        return parentCore;
    }

    @Override
    public void updateAllValues(DictCore _core) {
        // Modal dialog works on a local copy only.
    }

    private final class LanguageRelationEditorDialog extends PDialog {
        private final LanguageRelation workingRelation;
        private final PTextField txtTargetPath;
        private final PTextField txtTargetName;
        private final PComboBox<LanguageRelationType> cmbRelationType;
        private final PTextField txtNotes;
        private LanguageRelation result = null;

        private LanguageRelationEditorDialog(LanguageRelation relation) {
            super(ScrEvolutionProfile.this.core);
            workingRelation = relation == null ? new LanguageRelation() : new LanguageRelation(relation);
            txtTargetPath = new PTextField(core, true, "Related Language File");
            txtTargetName = new PTextField(core, true, "Related Language Name");
            cmbRelationType = new PComboBox<>(
                    ((DesktopPropertiesManager) core.getPropertiesManager()).getFontMenu(), core);
            txtNotes = new PTextField(core, true, "Notes");

            buildUi(relation == null);
            populateFromRelation();
            setModal(true);
            pack();
        }

        private void buildUi(boolean adding) {
            setTitle(adding ? "Add Related Language" : "Edit Related Language");
            setMinimumSize(new Dimension(620, 260));
            getContentPane().setLayout(new BorderLayout(8, 8));

            txtTargetPath.setEditable(false);
            txtTargetName.setEditable(false);

            DefaultComboBoxModel<LanguageRelationType> model = new DefaultComboBoxModel<>(
                    LanguageRelationType.values());
            cmbRelationType.setModel(model);

            PButton btnChooseLanguage = new PButton(nightMode);
            btnChooseLanguage.setText("Choose Related Language...");
            btnChooseLanguage.addActionListener((evt) -> chooseRelatedLanguage());

            JPanel chooserPanel = new JPanel(new GridLayout(1, 1, 6, 6));
            chooserPanel.add(btnChooseLanguage);

            JPanel relationTypePanel = new JPanel(new BorderLayout(6, 6));
            relationTypePanel.add(new PLabel("Relation Type"), BorderLayout.WEST);
            relationTypePanel.add(cmbRelationType, BorderLayout.CENTER);

            JPanel fieldsPanel = new JPanel(new GridLayout(4, 1, 6, 6));
            fieldsPanel.add(txtTargetPath);
            fieldsPanel.add(txtTargetName);
            fieldsPanel.add(relationTypePanel);
            fieldsPanel.add(txtNotes);

            JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 6, 6));
            PButton btnCancel = new PButton(nightMode);
            btnCancel.setText("Cancel");
            btnCancel.addActionListener((evt) -> dispose());

            PButton btnOk = new PButton(nightMode);
            btnOk.setText("OK");
            btnOk.addActionListener((evt) -> saveAndClose());

            buttonsPanel.add(btnCancel);
            buttonsPanel.add(btnOk);

            getContentPane().add(chooserPanel, BorderLayout.NORTH);
            getContentPane().add(fieldsPanel, BorderLayout.CENTER);
            getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        }

        private void populateFromRelation() {
            txtTargetPath.setText(workingRelation.getTargetLanguagePath());
            txtTargetName.setText(workingRelation.getCachedTargetLanguageName());
            cmbRelationType.setSelectedItem(workingRelation.getRelationType());
            txtNotes.setText(workingRelation.getNotes());
        }

        private void chooseRelatedLanguage() {
            String existingPath = workingRelation.getResolvedTargetLanguagePath(core);
            JFileChooser chooser = buildLanguageChooser(existingPath);

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String chosenPath = chooser.getSelectedFile().getAbsolutePath();

                try {
                    DictCore targetCore = loadReadOnlyCore(chosenPath);
                    workingRelation.setTargetLanguagePathFromAbsolute(chosenPath, core);
                    workingRelation.setCachedTargetLanguageName(
                            resolveLanguageDisplayName(targetCore, chosenPath));
                    populateFromRelation();
                } catch (Exception e) {
                    DesktopIOHandler.getInstance().writeErrorLog(e);
                    new DesktopInfoBox(this).error("Related Language Error",
                            "Unable to read selected related language:\n" + e.getLocalizedMessage());
                }
            }
        }

        private void saveAndClose() {
            workingRelation.setCachedTargetLanguageName(txtTargetName.getText());
            workingRelation.setRelationType((LanguageRelationType) cmbRelationType.getSelectedItem());
            workingRelation.setNotes(txtNotes.getText());

            if (!workingRelation.isValid()) {
                new DesktopInfoBox(this).warning("Related Language Error",
                        "Select a related language before saving.");
                return;
            }

            result = new LanguageRelation(workingRelation);
            dispose();
        }

        private LanguageRelation getRelation() {
            return result;
        }

        @Override
        public void updateAllValues(DictCore _core) {
            // Modal dialog works on a local copy only.
        }
    }
}
