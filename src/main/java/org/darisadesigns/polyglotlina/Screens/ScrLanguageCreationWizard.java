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
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.DesktopInfoBox;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PButton;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PDialog;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PLabel;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PTextField;
import org.darisadesigns.polyglotlina.Desktop.DesktopPropertiesManager;
import org.darisadesigns.polyglotlina.Desktop.PGTUtil;
import org.darisadesigns.polyglotlina.Desktop.PolyGlot;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.DisplayMode;
import org.darisadesigns.polyglotlina.Nodes.GeneratorSettings;
import org.darisadesigns.polyglotlina.Nodes.LanguageCreationWizardModel;
import org.darisadesigns.polyglotlina.Nodes.LanguageLinkType;
import org.darisadesigns.polyglotlina.Nodes.LanguageRelation;
import org.darisadesigns.polyglotlina.Nodes.LanguageRelationType;
import org.darisadesigns.polyglotlina.Nodes.LinkedLanguage;

/**
 * Guided creation flow for a new language/project.
 */
public final class ScrLanguageCreationWizard extends PDialog {
    private static final String CARD_BASIC = "basic";
    private static final String CARD_REPRESENTATION = "representation";
    private static final String CARD_GENERATOR = "generator";
    private static final String CARD_GRAMMAR = "grammar";
    private static final String CARD_RELATIONSHIPS = "relationships";
    private static final String CARD_REVIEW = "review";

    private final LanguageCreationWizardModel model = new LanguageCreationWizardModel();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final JLabel lblStepTitle = new PLabel("", SwingConstants.LEFT);
    private final JLabel lblStepInfo = new PLabel("", SwingConstants.LEFT);
    private final JButton btnBack = new PButton();
    private final JButton btnNext = new PButton();
    private final JButton btnFinish = new PButton();
    private final JButton btnCancel = new PButton();

    private final JComboBox<LanguageCreationWizardModel.PresetTemplate> cmbPreset
            = new JComboBox<>(LanguageCreationWizardModel.PresetTemplate.values());

    private final JTextField txtLanguageName = new PTextField(core, true, "Language Name");
    private final JTextField txtDisplayName = new PTextField(core, true, "Display Name");
    private final JTextArea txtNotes = new JTextArea(4, 24);

    private final JCheckBox chkUseIpa = new JCheckBox("Use IPA display");
    private final JCheckBox chkUseRomanized = new JCheckBox("Use Romanized display");
    private final JCheckBox chkUseRendered = new JCheckBox("Use rendered glyph layer");
    private final JComboBox<DisplayMode> cmbDisplayMode = new JComboBox<>(DisplayMode.values());
    private final JTextField txtCustomFont = new PTextField(core, true, "Optional font file");
    private final JButton btnBrowseFont = new PButton();
    private final JButton btnClearFont = new PButton();

    private final JTextArea txtPhonemeInventory = new JTextArea(4, 24);
    private final JTextArea txtSyllableTypes = new JTextArea(4, 24);
    private final JTextArea txtIllegalClusters = new JTextArea(4, 24);
    private final JTextArea txtRewriteRules = new JTextArea(4, 24);
    private final JSpinner spnGenerationCount = new JSpinner(new SpinnerNumberModel(150, 1, 10000, 1));
    private final JSpinner spnDropoffRate = new JSpinner(new SpinnerNumberModel(31, 0, 45, 1));
    private final JSpinner spnMonoFrequency = new JSpinner(new SpinnerNumberModel(15, 1, 85, 1));
    private final JCheckBox chkGenerateWords = new JCheckBox("Generate words by default");

    private final JCheckBox chkSimplifiedConjugations = new JCheckBox("Use simplified conjugation generation");
    private final JCheckBox chkTypesMandatory = new JCheckBox("Require part-of-speech assignment");
    private final JCheckBox chkLocalMandatory = new JCheckBox("Require local-language values");
    private final JCheckBox chkWordUniqueness = new JCheckBox("Enforce unique conwords");
    private final JCheckBox chkLocalUniqueness = new JCheckBox("Enforce unique local words");

    private final JTextField txtParentPath = new PTextField(core, true, "Optional parent language file");
    private final JTextField txtParentName = new PTextField(core, true, "Parent language name");
    private final JButton btnBrowseParent = new PButton();
    private final DefaultListModel<LinkedLanguage> linkedLanguageModel = new DefaultListModel<>();
    private final DefaultListModel<LanguageRelation> relatedLanguageModel = new DefaultListModel<>();
    private final JList<LinkedLanguage> lstLinkedLanguages = new JList<>(linkedLanguageModel);
    private final JList<LanguageRelation> lstRelatedLanguages = new JList<>(relatedLanguageModel);
    private final JButton btnAddLinked = new PButton();
    private final JButton btnEditLinked = new PButton();
    private final JButton btnRemoveLinked = new PButton();
    private final JButton btnAddRelated = new PButton();
    private final JButton btnEditRelated = new PButton();
    private final JButton btnRemoveRelated = new PButton();

    private final JTextArea txtSummary = new JTextArea();

    private int currentStep = 0;
    private boolean suppressModelEvents = false;
    private LanguageCreationWizardModel result = null;

    public ScrLanguageCreationWizard(DictCore _core) {
        super(_core);
        setModal(true);
        setTitle("Create Language Wizard");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUi();
        loadModelIntoUi();
        showCurrentStep();
    }

    public LanguageCreationWizardModel showDialog() {
        setVisible(true);
        return result;
    }

    public LanguageCreationWizardModel getResult() {
        return result;
    }

    private void initUi() {
        setMinimumSize(new Dimension(940, 700));

        var header = new JPanel(new GridBagLayout());
        header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 4, 0);

        lblStepTitle.setFont(PGTUtil.MENU_FONT.deriveFont(18f));
        header.add(lblStepTitle, gbc);

        gbc.gridy = 1;
        lblStepInfo.setFont(PGTUtil.MENU_FONT.deriveFont(12f));
        header.add(lblStepInfo, gbc);

        cardPanel.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        cardPanel.add(buildBasicInfoCard(), CARD_BASIC);
        cardPanel.add(buildRepresentationCard(), CARD_REPRESENTATION);
        cardPanel.add(buildGeneratorCard(), CARD_GENERATOR);
        cardPanel.add(buildGrammarCard(), CARD_GRAMMAR);
        cardPanel.add(buildRelationshipsCard(), CARD_RELATIONSHIPS);
        cardPanel.add(buildReviewCard(), CARD_REVIEW);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBorder(BorderFactory.createEmptyBorder(0, 18, 12, 18));

        btnBack.setText("Back");
        btnBack.addActionListener(this::goBack);
        btnNext.setText("Next");
        btnNext.addActionListener(this::goNext);
        btnFinish.setText("Finish");
        btnFinish.addActionListener(this::finishWizard);
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(evt -> cancelWizard());

        footer.add(btnBack);
        footer.add(btnNext);
        footer.add(btnFinish);
        footer.add(btnCancel);

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        installChangeTracking();
    }

    private JPanel buildBasicInfoCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        GridBagConstraints gbc = fieldConstraints();
        int row = 0;

        addSectionTitle(panel, gbc, row++, "Template");
        addRow(panel, row++, "Preset", cmbPreset);
        gbcInfo(panel, row++, "Use a preset to seed the starting defaults.");

        addSectionTitle(panel, gbc, row++, "Basic Info");
        addRow(panel, row++, "Language name", txtLanguageName);
        addRow(panel, row++, "Display name", txtDisplayName);
        addTextAreaRow(panel, row++, "Notes / description", txtNotes, 4);

        JButton btnApplyPreset = new PButton();
        btnApplyPreset.setText("Apply Preset");
        btnApplyPreset.addActionListener(evt -> applySelectedPreset());
        gbc = rowButtonConstraints(row++);
        panel.add(btnApplyPreset, gbc);

        return wrapScrollable(panel);
    }

    private JPanel buildRepresentationCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        int row = 0;
        addSectionTitle(panel, rowConstraints(row++), "Representation");
        addCheckRow(panel, row++, chkUseIpa);
        addCheckRow(panel, row++, chkUseRomanized);
        addCheckRow(panel, row++, chkUseRendered);

        row = addComboRow(panel, row, "Default display mode", cmbDisplayMode);
        JPanel fontRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        txtCustomFont.setPreferredSize(new Dimension(280, txtCustomFont.getPreferredSize().height));
        fontRow.add(txtCustomFont);
        fontRow.add(btnBrowseFont);
        fontRow.add(btnClearFont);
        addRow(panel, row++, "Custom font", fontRow);
        addSectionTitle(panel, rowConstraints(row++), "Notes");
        gbcInfo(panel, row++, "The custom font is optional and only applied if the file can be loaded.");

        btnBrowseFont.setText("Browse");
        btnBrowseFont.addActionListener(evt -> chooseFontFile());
        btnClearFont.setText("Clear");
        btnClearFont.addActionListener(evt -> txtCustomFont.setText(""));

        return wrapScrollable(panel);
    }

    private JPanel buildGeneratorCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        int row = 0;
        addSectionTitle(panel, rowConstraints(row++), "Generator Defaults");
        addTextAreaRow(panel, row++, "Phoneme inventory / categories", txtPhonemeInventory, 5);
        addTextAreaRow(panel, row++, "Syllable structure", txtSyllableTypes, 4);
        addTextAreaRow(panel, row++, "Illegal clusters", txtIllegalClusters, 4);
        addTextAreaRow(panel, row++, "Rewrite rules", txtRewriteRules, 4);
        addRow(panel, row++, "Generation count", spnGenerationCount);
        addRow(panel, row++, "Dropoff rate", spnDropoffRate);
        addRow(panel, row++, "Monosyllable frequency", spnMonoFrequency);
        addCheckRow(panel, row++, chkGenerateWords);

        ((NumberEditor) spnGenerationCount.getEditor()).getTextField().setColumns(8);

        return wrapScrollable(panel);
    }

    private JPanel buildGrammarCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        int row = 0;
        addSectionTitle(panel, rowConstraints(row++), "Grammar / Morphology");
        addCheckRow(panel, row++, chkSimplifiedConjugations);
        addCheckRow(panel, row++, chkTypesMandatory);
        addCheckRow(panel, row++, chkLocalMandatory);
        addCheckRow(panel, row++, chkWordUniqueness);
        addCheckRow(panel, row++, chkLocalUniqueness);
        gbcInfo(panel, row++, "Keep this page light. More advanced grammar can be refined later.");

        return wrapScrollable(panel);
    }

    private JPanel buildRelationshipsCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        int row = 0;
        addSectionTitle(panel, rowConstraints(row++), "Parent Language");
        addRow(panel, row++, "Parent path", txtParentPath, btnBrowseParent);
        addRow(panel, row++, "Parent name", txtParentName);

        addSectionTitle(panel, rowConstraints(row++), "Linked Languages");
        lstLinkedLanguages.setVisibleRowCount(4);
        lstLinkedLanguages.setCellRenderer(new LinkedLanguageRenderer());
        panel.add(wrapListWithButtons(lstLinkedLanguages, btnAddLinked, btnEditLinked, btnRemoveLinked), rowSpanConstraints(row++, 1));

        addSectionTitle(panel, rowConstraints(row++), "Related Languages");
        lstRelatedLanguages.setVisibleRowCount(4);
        lstRelatedLanguages.setCellRenderer(new RelatedLanguageRenderer());
        panel.add(wrapListWithButtons(lstRelatedLanguages, btnAddRelated, btnEditRelated, btnRemoveRelated), rowSpanConstraints(row++, 1));

        btnBrowseParent.setText("Browse");
        btnBrowseParent.addActionListener(evt -> chooseExistingLanguageFile(txtParentPath));
        btnAddLinked.setText("Add");
        btnEditLinked.setText("Edit");
        btnRemoveLinked.setText("Remove");
        btnAddRelated.setText("Add");
        btnEditRelated.setText("Edit");
        btnRemoveRelated.setText("Remove");

        btnAddLinked.addActionListener(evt -> addLinkedLanguage());
        btnEditLinked.addActionListener(evt -> editSelectedLinkedLanguage());
        btnRemoveLinked.addActionListener(evt -> removeSelectedLinkedLanguage());
        btnAddRelated.addActionListener(evt -> addRelatedLanguage());
        btnEditRelated.addActionListener(evt -> editSelectedRelatedLanguage());
        btnRemoveRelated.addActionListener(evt -> removeSelectedRelatedLanguage());

        return wrapScrollable(panel);
    }

    private JPanel buildReviewCard() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        txtSummary.setEditable(false);
        txtSummary.setLineWrap(true);
        txtSummary.setWrapStyleWord(true);
        txtSummary.setFont(PGTUtil.MENU_FONT);
        panel.add(new JScrollPane(txtSummary), BorderLayout.CENTER);

        return panel;
    }

    private void installChangeTracking() {
        DocumentListener refreshListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                modelChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                modelChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                modelChanged();
            }
        };

        txtLanguageName.getDocument().addDocumentListener(refreshListener);
        txtDisplayName.getDocument().addDocumentListener(refreshListener);
        txtNotes.getDocument().addDocumentListener(refreshListener);
        txtCustomFont.getDocument().addDocumentListener(refreshListener);
        txtPhonemeInventory.getDocument().addDocumentListener(refreshListener);
        txtSyllableTypes.getDocument().addDocumentListener(refreshListener);
        txtIllegalClusters.getDocument().addDocumentListener(refreshListener);
        txtRewriteRules.getDocument().addDocumentListener(refreshListener);
        txtParentPath.getDocument().addDocumentListener(refreshListener);
        txtParentName.getDocument().addDocumentListener(refreshListener);

        cmbPreset.addActionListener(evt -> updateNavigationState());
        cmbDisplayMode.addActionListener(evt -> updateNavigationState());
        chkUseIpa.addActionListener(evt -> updateNavigationState());
        chkUseRomanized.addActionListener(evt -> updateNavigationState());
        chkUseRendered.addActionListener(evt -> updateNavigationState());
        chkGenerateWords.addActionListener(evt -> updateNavigationState());
        chkSimplifiedConjugations.addActionListener(evt -> updateNavigationState());
        chkTypesMandatory.addActionListener(evt -> updateNavigationState());
        chkLocalMandatory.addActionListener(evt -> updateNavigationState());
        chkWordUniqueness.addActionListener(evt -> updateNavigationState());
        chkLocalUniqueness.addActionListener(evt -> updateNavigationState());
        spnGenerationCount.addChangeListener(evt -> updateNavigationState());
        spnDropoffRate.addChangeListener(evt -> updateNavigationState());
        spnMonoFrequency.addChangeListener(evt -> updateNavigationState());
    }

    private void applySelectedPreset() {
        LanguageCreationWizardModel.PresetTemplate preset
                = (LanguageCreationWizardModel.PresetTemplate) cmbPreset.getSelectedItem();
        String languageName = txtLanguageName.getText();
        String displayName = txtDisplayName.getText();
        String notes = txtNotes.getText();
        String customFont = txtCustomFont.getText();

        model.applyPreset(preset);
        model.setLanguageName(languageName);
        model.setDisplayName(displayName);
        model.setNotes(notes);
        model.setCustomFontPath(customFont);
        loadModelIntoUi();
    }

    private void loadModelIntoUi() {
        suppressModelEvents = true;
        try {
            txtLanguageName.setText(model.getLanguageName());
            txtDisplayName.setText(model.getDisplayName());
            txtNotes.setText(model.getNotes());
            chkUseIpa.setSelected(model.isUseIpa());
            chkUseRomanized.setSelected(model.isUseRomanized());
            chkUseRendered.setSelected(model.isUseRendered());
            cmbDisplayMode.setSelectedItem(model.getDefaultDisplayMode());
            txtCustomFont.setText(model.getCustomFontPath());
            txtPhonemeInventory.setText(model.getGeneratorSettings().getCategories());
            txtSyllableTypes.setText(model.getGeneratorSettings().getSyllableTypes());
            txtIllegalClusters.setText(model.getGeneratorSettings().getIllegalClusters());
            txtRewriteRules.setText(model.getGeneratorSettings().getRewriteRules());
            spnGenerationCount.setValue(parseIntOrDefault(model.getGeneratorSettings().getGenerationCount(), 150));
            spnDropoffRate.setValue(model.getGeneratorSettings().getDropoffRate());
            spnMonoFrequency.setValue(model.getGeneratorSettings().getMonosyllableFrequency());
            chkGenerateWords.setSelected(model.getGeneratorSettings().isGenerateWords());
            chkSimplifiedConjugations.setSelected(model.isSimplifiedConjugations());
            chkTypesMandatory.setSelected(model.isTypesMandatory());
            chkLocalMandatory.setSelected(model.isLocalMandatory());
            chkWordUniqueness.setSelected(model.isWordUniqueness());
            chkLocalUniqueness.setSelected(model.isLocalUniqueness());
            txtParentPath.setText(model.getEvolutionProfile().getParentLanguagePath());
            txtParentName.setText(model.getEvolutionProfile().getCachedParentLanguageName());

            linkedLanguageModel.clear();
            for (LinkedLanguage linkedLanguage : model.getLinkedLanguages()) {
                linkedLanguageModel.addElement(linkedLanguage);
            }

            relatedLanguageModel.clear();
            for (LanguageRelation relation : model.getEvolutionProfile().getRelatedLanguages()) {
                relatedLanguageModel.addElement(relation);
            }
        } finally {
            suppressModelEvents = false;
        }

        updateButtonsAndSummary();
    }

    private void storeUiToModel() {
        model.setLanguageName(txtLanguageName.getText());
        model.setDisplayName(txtDisplayName.getText());
        model.setNotes(txtNotes.getText());
        model.setUseIpa(chkUseIpa.isSelected());
        model.setUseRomanized(chkUseRomanized.isSelected());
        model.setUseRendered(chkUseRendered.isSelected());
        model.setCustomFontPath(txtCustomFont.getText());

        DisplayMode selectedMode = (DisplayMode) cmbDisplayMode.getSelectedItem();
        if (selectedMode == DisplayMode.IPA && !model.isUseIpa()) {
            selectedMode = model.isUseRomanized() ? DisplayMode.ROMANIZED : DisplayMode.RENDERED;
        } else if (selectedMode == DisplayMode.ROMANIZED && !model.isUseRomanized()) {
            selectedMode = model.isUseIpa() ? DisplayMode.IPA : DisplayMode.RENDERED;
        } else if (selectedMode == DisplayMode.RENDERED && !model.isUseRendered()) {
            selectedMode = model.isUseIpa()
                    ? DisplayMode.IPA
                    : model.isUseRomanized()
                    ? DisplayMode.ROMANIZED
                    : DisplayMode.RENDERED;
        }
        model.setDefaultDisplayMode(selectedMode);

        model.getGeneratorSettings().setCategories(txtPhonemeInventory.getText());
        model.getGeneratorSettings().setSyllableTypes(txtSyllableTypes.getText());
        model.getGeneratorSettings().setIllegalClusters(txtIllegalClusters.getText());
        model.getGeneratorSettings().setRewriteRules(txtRewriteRules.getText());
        model.getGeneratorSettings().setGenerationCount(Integer.toString(((Number) spnGenerationCount.getValue()).intValue()));
        model.getGeneratorSettings().setDropoffRate(((Number) spnDropoffRate.getValue()).intValue());
        model.getGeneratorSettings().setMonosyllableFrequency(((Number) spnMonoFrequency.getValue()).intValue());
        model.getGeneratorSettings().setGenerateWords(chkGenerateWords.isSelected());
        model.setSimplifiedConjugations(chkSimplifiedConjugations.isSelected());
        model.setTypesMandatory(chkTypesMandatory.isSelected());
        model.setLocalMandatory(chkLocalMandatory.isSelected());
        model.setWordUniqueness(chkWordUniqueness.isSelected());
        model.setLocalUniqueness(chkLocalUniqueness.isSelected());
        model.getEvolutionProfile().setParentLanguagePath(txtParentPath.getText());
        model.getEvolutionProfile().setCachedParentLanguageName(txtParentName.getText());

        List<LinkedLanguage> linkedLanguages = new ArrayList<>();
        for (int i = 0; i < linkedLanguageModel.size(); i++) {
            linkedLanguages.add(new LinkedLanguage(linkedLanguageModel.get(i)));
        }
        model.setLinkedLanguages(linkedLanguages);

        List<LanguageRelation> relatedLanguages = new ArrayList<>();
        for (int i = 0; i < relatedLanguageModel.size(); i++) {
            relatedLanguages.add(new LanguageRelation(relatedLanguageModel.get(i)));
        }
        model.getEvolutionProfile().setRelatedLanguages(relatedLanguages);
    }

    private void goBack(ActionEvent evt) {
        if (currentStep == 0) {
            return;
        }

        storeUiToModel();
        currentStep--;
        showCurrentStep();
    }

    private void goNext(ActionEvent evt) {
        if (currentStep >= stepCount() - 1) {
            return;
        }

        storeUiToModel();
        currentStep++;
        showCurrentStep();
    }

    private void finishWizard(ActionEvent evt) {
        storeUiToModel();

        if (!isWizardValid()) {
            new DesktopInfoBox(this).error("Validation Error",
                    "Please complete the required fields and resolve any invalid selections.");
            return;
        }

        result = model;
        dispose();
    }

    private void cancelWizard() {
        result = null;
        dispose();
    }

    private void showCurrentStep() {
        cardLayout.show(cardPanel, cardName(currentStep));
        lblStepTitle.setText(stepTitle(currentStep));
        lblStepInfo.setText(stepSubtitle(currentStep));

        if (currentStep == stepCount() - 1) {
            updateSummaryPage();
        }

        updateButtonsAndSummary();
    }

    private void updateButtonsAndSummary() {
        boolean isLast = currentStep == stepCount() - 1;
        btnBack.setEnabled(currentStep > 0);
        btnNext.setEnabled(!isLast);
        btnFinish.setEnabled(isLast && isWizardValid());

        if (currentStep == stepCount() - 1) {
            updateSummaryPage();
        }
    }

    private void updateNavigationState() {
        updateButtonsAndSummary();
    }

    private boolean isWizardValid() {
        if (!model.isReadyForFinish()) {
            return false;
        }

        if (!model.isUseIpa() && !model.isUseRomanized() && !model.isUseRendered()) {
            return false;
        }

        if (!model.getCustomFontPath().isBlank()) {
            try {
                if (!Files.exists(Path.of(model.getCustomFontPath()))) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        boolean parentPathBlank = txtParentPath.getText().isBlank();
        boolean parentNameBlank = txtParentName.getText().isBlank();
        if (parentPathBlank != parentNameBlank) {
            return false;
        }

        return true;
    }

    private void updateSummaryPage() {
        storeUiToModel();
        txtSummary.setText(buildSummary());
        txtSummary.setCaretPosition(0);
        btnFinish.setEnabled(isWizardValid());
    }

    private void modelChanged() {
        if (suppressModelEvents) {
            return;
        }

        updateButtonsAndSummary();
    }

    private String buildSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Language name: ").append(blankFallback(model.getLanguageName())).append('\n');
        sb.append("Display name: ").append(blankFallback(model.getDisplayName().isBlank()
                ? model.getLanguageName()
                : model.getDisplayName())).append('\n');
        sb.append("Notes: ").append(blankFallback(model.getNotes())).append('\n');
        sb.append('\n');
        sb.append("Representation:\n");
        sb.append("  IPA: ").append(model.isUseIpa() ? "yes" : "no").append('\n');
        sb.append("  Romanized: ").append(model.isUseRomanized() ? "yes" : "no").append('\n');
        sb.append("  Rendered: ").append(model.isUseRendered() ? "yes" : "no").append('\n');
        sb.append("  Default display mode: ").append(model.getDefaultDisplayMode()).append('\n');
        sb.append("  Font: ").append(blankFallback(model.getCustomFontPath())).append('\n');
        sb.append('\n');
        sb.append("Generator defaults:\n");
        sb.append("  Categories: ").append(blankFallback(model.getGeneratorSettings().getCategories())).append('\n');
        sb.append("  Syllables: ").append(blankFallback(model.getGeneratorSettings().getSyllableTypes())).append('\n');
        sb.append("  Illegal clusters: ").append(blankFallback(model.getGeneratorSettings().getIllegalClusters())).append('\n');
        sb.append("  Rewrite rules: ").append(blankFallback(model.getGeneratorSettings().getRewriteRules())).append('\n');
        sb.append('\n');
        sb.append("Grammar:\n");
        sb.append("  Simplified conjugations: ").append(model.isSimplifiedConjugations() ? "yes" : "no").append('\n');
        sb.append("  Types mandatory: ").append(model.isTypesMandatory() ? "yes" : "no").append('\n');
        sb.append("  Local mandatory: ").append(model.isLocalMandatory() ? "yes" : "no").append('\n');
        sb.append('\n');
        sb.append("Relationships:\n");
        sb.append("  Parent: ").append(blankFallback(model.getEvolutionProfile().getCachedParentLanguageName())).append('\n');
        sb.append("  Linked languages: ").append(linkedLanguageModel.size()).append('\n');
        sb.append("  Related languages: ").append(relatedLanguageModel.size()).append('\n');
        return sb.toString();
    }

    private static String blankFallback(String value) {
        return value == null || value.isBlank() ? "(none)" : value.trim();
    }

    private void chooseFontFile() {
        JFileChooser chooser = buildLanguageFileChooser("Select Font File", "Font Files", "ttf", "otf", "ttc", "dfont");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtCustomFont.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void chooseExistingLanguageFile(JTextField target) {
        JFileChooser chooser = buildLanguageFileChooser("Select Language File", "PolyGlot Dictionaries", PGTUtil.POLYGLOT_FILE_SUFFIX);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            target.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private JFileChooser buildLanguageFileChooser(String title, String description, String... extensions) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setFileFilter(new FileNameExtensionFilter(description, extensions));

        String curFileName = core.getCurFileName();
        if (curFileName != null && !curFileName.isBlank()) {
            File current = new File(curFileName);
            if (current.isDirectory()) {
                chooser.setCurrentDirectory(current);
            } else if (current.getParentFile() != null) {
                chooser.setCurrentDirectory(current.getParentFile());
            }
        }

        return chooser;
    }

    private void addLinkedLanguage() {
        LinkedLanguage linkedLanguage = editLinkedLanguage(null);
        if (linkedLanguage != null) {
            linkedLanguageModel.addElement(linkedLanguage);
            updateButtonsAndSummary();
        }
    }

    private void editSelectedLinkedLanguage() {
        int index = lstLinkedLanguages.getSelectedIndex();
        if (index < 0) {
            return;
        }

        LinkedLanguage updated = editLinkedLanguage(linkedLanguageModel.get(index));
        if (updated != null) {
            linkedLanguageModel.set(index, updated);
            updateButtonsAndSummary();
        }
    }

    private void removeSelectedLinkedLanguage() {
        int index = lstLinkedLanguages.getSelectedIndex();
        if (index >= 0) {
            linkedLanguageModel.remove(index);
            updateButtonsAndSummary();
        }
    }

    private void addRelatedLanguage() {
        LanguageRelation relation = editLanguageRelation(null);
        if (relation != null) {
            relatedLanguageModel.addElement(relation);
            updateButtonsAndSummary();
        }
    }

    private void editSelectedRelatedLanguage() {
        int index = lstRelatedLanguages.getSelectedIndex();
        if (index < 0) {
            return;
        }

        LanguageRelation updated = editLanguageRelation(relatedLanguageModel.get(index));
        if (updated != null) {
            relatedLanguageModel.set(index, updated);
            updateButtonsAndSummary();
        }
    }

    private void removeSelectedRelatedLanguage() {
        int index = lstRelatedLanguages.getSelectedIndex();
        if (index >= 0) {
            relatedLanguageModel.remove(index);
            updateButtonsAndSummary();
        }
    }

    private LinkedLanguage editLinkedLanguage(LinkedLanguage seed) {
        JTextField txtPath = new JTextField(seed == null ? "" : seed.getTargetFile(), 24);
        JTextField txtName = new JTextField(seed == null ? "" : seed.getLanguageName(), 24);
        JComboBox<LanguageLinkType> cmbType = new JComboBox<>(LanguageLinkType.values());
        JTextArea txtNotes = new JTextArea(seed == null ? "" : seed.getNotes(), 4, 24);
        cmbType.setSelectedItem(seed == null ? LanguageLinkType.CONTACT : seed.getLinkType());

        JPanel panel = new JPanel(new GridBagLayout());
        int row = 0;
        addRow(panel, row++, "Target file", txtPath, browseButtonForField(txtPath));
        addRow(panel, row++, "Language name", txtName);
        addRow(panel, row++, "Link type", cmbType);
        addTextAreaRow(panel, row++, "Notes", txtNotes, 4);

        while (true) {
            int choice = JOptionPane.showConfirmDialog(this, panel, "Linked Language",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) {
                return null;
            }

            LinkedLanguage linkedLanguage = new LinkedLanguage();
            linkedLanguage.setTargetFile(txtPath.getText());
            linkedLanguage.setLanguageName(txtName.getText());
            linkedLanguage.setLinkType((LanguageLinkType) cmbType.getSelectedItem());
            linkedLanguage.setNotes(txtNotes.getText());

            if (linkedLanguage.isValid()) {
                return linkedLanguage;
            }

            new DesktopInfoBox(this).error("Validation Error", "Linked languages require both a file path and a language name.");
        }
    }

    private LanguageRelation editLanguageRelation(LanguageRelation seed) {
        JTextField txtPath = new JTextField(seed == null ? "" : seed.getTargetLanguagePath(), 24);
        JTextField txtName = new JTextField(seed == null ? "" : seed.getCachedTargetLanguageName(), 24);
        JComboBox<LanguageRelationType> cmbType = new JComboBox<>(LanguageRelationType.values());
        JTextArea txtNotes = new JTextArea(seed == null ? "" : seed.getNotes(), 4, 24);
        cmbType.setSelectedItem(seed == null ? LanguageRelationType.CONTACT : seed.getRelationType());

        JPanel panel = new JPanel(new GridBagLayout());
        int row = 0;
        addRow(panel, row++, "Target path", txtPath, browseButtonForField(txtPath));
        addRow(panel, row++, "Language name", txtName);
        addRow(panel, row++, "Relation type", cmbType);
        addTextAreaRow(panel, row++, "Notes", txtNotes, 4);

        while (true) {
            int choice = JOptionPane.showConfirmDialog(this, panel, "Related Language",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) {
                return null;
            }

            LanguageRelation relation = new LanguageRelation();
            relation.setTargetLanguagePath(txtPath.getText());
            relation.setCachedTargetLanguageName(txtName.getText());
            relation.setRelationType((LanguageRelationType) cmbType.getSelectedItem());
            relation.setNotes(txtNotes.getText());

            if (relation.isValid()) {
                return relation;
            }

            new DesktopInfoBox(this).error("Validation Error", "Related languages require both a file path and a language name.");
        }
    }

    private JButton browseButtonForField(JTextField field) {
        JButton button = new JButton("Browse");
        button.addActionListener(evt -> chooseExistingLanguageFile(field));
        return button;
    }

    private JPanel wrapListWithButtons(JList<?> list, JButton add, JButton edit, JButton remove) {
        JPanel outer = new JPanel(new BorderLayout(0, 6));
        JScrollPane scroller = new JScrollPane(list);
        scroller.setPreferredSize(new Dimension(520, 140));
        outer.add(scroller, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.add(add);
        buttons.add(edit);
        buttons.add(remove);
        outer.add(buttons, BorderLayout.SOUTH);
        return outer;
    }

    private JPanel wrapScrollable(JPanel panel) {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private void addSectionTitle(JPanel panel, GridBagConstraints gbc, int row, String title) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 6, 0);
        panel.add(new PLabel(title), gbc);
        gbc.gridwidth = 1;
    }

    private void addSectionTitle(JPanel panel, GridBagConstraints gbc, String title) {
        addSectionTitle(panel, gbc, gbc == null ? 0 : gbc.gridy, title);
    }

    private void addRow(JPanel panel, int row, String label, Component component) {
        addRow(panel, row, label, component, null);
    }

    private void addRow(JPanel panel, int row, String label, Component component, Component extra) {
        GridBagConstraints labelConstraints = rowLabelConstraints(row);
        panel.add(new PLabel(label), labelConstraints);

        GridBagConstraints fieldConstraints = rowFieldConstraints(row);
        panel.add(component, fieldConstraints);

        if (extra != null) {
            GridBagConstraints extraConstraints = rowExtraConstraints(row);
            panel.add(extra, extraConstraints);
        }
    }

    private int addComboRow(JPanel panel, int row, String label, JComboBox<?> combo) {
        addRow(panel, row, label, combo);
        return row + 1;
    }

    private void addCheckRow(JPanel panel, int row, JCheckBox checkBox) {
        GridBagConstraints gbc = rowSingleConstraints(row);
        checkBox.setFont(PGTUtil.MENU_FONT);
        panel.add(checkBox, gbc);
    }

    private void addTextAreaRow(JPanel panel, int row, String label, JTextArea area, int rows) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(PGTUtil.MENU_FONT);
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(420, rows * 24));
        addRow(panel, row, label, scrollPane);
    }

    private void addTextAreaRow(JPanel panel, int row, String label, JTextArea area, int rows, Component extra) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(PGTUtil.MENU_FONT);
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(420, rows * 24));
        addRow(panel, row, label, scrollPane, extra);
    }

    private void gbcInfo(JPanel panel, int row, String text) {
        GridBagConstraints gbc = rowSpanConstraints(row, 1);
        PLabel info = new PLabel(text);
        info.setFont(PGTUtil.MENU_FONT.deriveFont(11f));
        panel.add(info, gbc);
    }

    private GridBagConstraints fieldConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 8, 0);
        return gbc;
    }

    private GridBagConstraints rowConstraints(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridwidth = 2;
        return gbc;
    }

    private GridBagConstraints rowLabelConstraints(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 0, 4, 12);
        return gbc;
    }

    private GridBagConstraints rowFieldConstraints(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        return gbc;
    }

    private GridBagConstraints rowExtraConstraints(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = row;
        gbc.insets = new Insets(4, 8, 4, 0);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private GridBagConstraints rowButtonConstraints(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 0, 0);
        return gbc;
    }

    private GridBagConstraints rowSingleConstraints(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 0, 4, 0);
        return gbc;
    }

    private GridBagConstraints rowSpanConstraints(int row, int gridWidth) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = gridWidth;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 0, 6, 0);
        return gbc;
    }

    private String cardName(int step) {
        return switch (step) {
            case 0 -> CARD_BASIC;
            case 1 -> CARD_REPRESENTATION;
            case 2 -> CARD_GENERATOR;
            case 3 -> CARD_GRAMMAR;
            case 4 -> CARD_RELATIONSHIPS;
            default -> CARD_REVIEW;
        };
    }

    private String stepTitle(int step) {
        return switch (step) {
            case 0 -> "Step 1 of 6 - Basic Info";
            case 1 -> "Step 2 of 6 - Representation";
            case 2 -> "Step 3 of 6 - Generator Defaults";
            case 3 -> "Step 4 of 6 - Grammar / Morphology";
            case 4 -> "Step 5 of 6 - Relationships";
            default -> "Step 6 of 6 - Review / Finish";
        };
    }

    private String stepSubtitle(int step) {
        return switch (step) {
            case 0 -> "Name the project and decide which preset, if any, should seed the defaults.";
            case 1 -> "Choose how the language should appear by default in the lexicon.";
            case 2 -> "Seed the generator with the minimum useful phonotactic defaults.";
            case 3 -> "Keep the grammar setup light; advanced morphology can be refined later.";
            case 4 -> "Attach a parent, linked languages, or related languages if they already exist.";
            default -> "Check the summary and create the new project only when everything looks right.";
        };
    }

    private int stepCount() {
        return 6;
    }

    private int parseIntOrDefault(String value, int fallback) {
        try {
            return Integer.parseInt(value == null ? "" : value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    @Override
    public void updateAllValues(DictCore _core) {
        setCore(_core);
    }

    private static final class LinkedLanguageRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof LinkedLanguage linkedLanguage) {
                setText(linkedLanguage.getLanguageName() + " - " + linkedLanguage.getTargetFile());
            }
            return this;
        }
    }

    private static final class RelatedLanguageRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof LanguageRelation relation) {
                setText(relation.getCachedTargetLanguageName() + " - " + relation.getTargetLanguagePath()
                        + " [" + relation.getRelationType() + "]");
            }
            return this;
        }
    }
}
