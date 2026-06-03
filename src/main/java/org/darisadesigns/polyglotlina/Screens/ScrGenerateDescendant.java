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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import org.darisadesigns.polyglotlina.ManagersCollections.ConWordCollection.ConWordDisplay;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.DescendantLink;
import org.darisadesigns.polyglotlina.Nodes.LanguageEvolutionProfile;
import org.darisadesigns.polyglotlina.Nodes.SoundChangeStep;
import org.darisadesigns.polyglotlina.SoundChangeEngine;

/**
 * Generates a descendant form from a linked parent language word.
 *
 * @author draque
 */
public class ScrGenerateDescendant extends PDialog {
    private final ConWord childWord;
    private final LanguageEvolutionProfile profile;
    private DictCore parentCore = null;
    private final DefaultTableModel previewModel;
    private final PComboBox<ConWordDisplay> cmbParentWord;
    private final PTextField txtParentPath;
    private final PTextField txtSourceWord;
    private final PTextField txtPreviewOutput;
    private final PTextField txtStatus;
    private final PLabel lblOverride;
    private final PTable tblPreview;

    public ScrGenerateDescendant(DictCore _core, ConWord _childWord) {
        super(_core);
        childWord = _childWord;
        profile = core.getPropertiesManager().getLanguageEvolutionProfile();
        previewModel = new DefaultTableModel(new String[]{"Rule", "Before", "After"}, 0);
        cmbParentWord = new PComboBox<>(((DesktopPropertiesManager) core.getPropertiesManager()).getFontCon(), core);
        txtParentPath = new PTextField(core, true, "Parent Language File");
        txtSourceWord = new PTextField(core, false, "Parent Source Word");
        txtPreviewOutput = new PTextField(core, false, "Generated Descendant");
        txtStatus = new PTextField(core, true, "Status");
        lblOverride = new PLabel("");
        tblPreview = new PTable(core);

        buildUi();
        loadParentLanguage();
        updatePreview();
        setModal(true);
        pack();
    }

    private void buildUi() {
        setTitle("Generate Descendant");
        setMinimumSize(new Dimension(760, 520));
        getContentPane().setLayout(new BorderLayout(8, 8));

        txtParentPath.setEditable(false);
        txtSourceWord.setEditable(false);
        txtPreviewOutput.setEditable(false);
        txtStatus.setEditable(false);
        txtStatus.setEnabled(false);

        JPanel topPanel = new JPanel(new GridLayout(5, 1, 6, 6));
        topPanel.setBorder(BorderFactory.createTitledBorder("Parent Source"));

        cmbParentWord.setDefaultText("Select Parent Word");
        cmbParentWord.addActionListener((evt) -> updatePreview());
        topPanel.add(new PLabel("Linked Parent Language"));
        topPanel.add(txtParentPath);
        topPanel.add(cmbParentWord);
        topPanel.add(txtSourceWord);
        topPanel.add(lblOverride);

        tblPreview.setModel(previewModel);
        tblPreview.setEnabled(false);
        tblPreview.setRowHeight(26);

        JPanel previewPanel = new JPanel(new BorderLayout(6, 6));
        previewPanel.setBorder(BorderFactory.createTitledBorder("Transformation Preview"));
        previewPanel.add(txtPreviewOutput, BorderLayout.NORTH);
        previewPanel.add(new JScrollPane(tblPreview), BorderLayout.CENTER);
        previewPanel.add(txtStatus, BorderLayout.SOUTH);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 6, 6));
        PButton btnClear = new PButton(nightMode);
        btnClear.setText("Clear Link");
        btnClear.setEnabled(!childWord.getDescendantLink().isEmpty());
        btnClear.addActionListener((evt) -> clearLink());

        PButton btnCancel = new PButton(nightMode);
        btnCancel.setText("Cancel");
        btnCancel.addActionListener((evt) -> dispose());

        PButton btnApply = new PButton(nightMode);
        btnApply.setText("Apply");
        btnApply.addActionListener((evt) -> applyDescendant());

        buttonsPanel.add(btnClear);
        buttonsPanel.add(btnCancel);
        buttonsPanel.add(btnApply);

        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(previewPanel, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void loadParentLanguage() {
        txtParentPath.setText(profile.getParentLanguagePath());

        String parentPath = profile.getResolvedParentLanguagePath(core);
        if (parentPath.isBlank()) {
            txtStatus.setText("No parent language configured in the evolution profile.");
            cmbParentWord.setEnabled(false);
            return;
        }

        try {
            parentCore = loadReadOnlyCore(parentPath);
            populateParentWords();
        } catch (Exception e) {
            DesktopIOHandler.getInstance().writeErrorLog(e);
            txtStatus.setText("Unable to read linked parent language.");
            cmbParentWord.setEnabled(false);
        }
    }

    private void populateParentWords() {
        DefaultComboBoxModel<ConWordDisplay> model = new DefaultComboBoxModel<>();
        cmbParentWord.setModel(model);
        model.addElement(null);

        if (parentCore == null) {
            return;
        }

        ConWordDisplay[] displayWords = parentCore.getWordCollection()
                .toDisplayList(parentCore.getWordCollection().getWordNodes());
        Arrays.stream(displayWords).forEach(model::addElement);

        DescendantLink link = childWord.getDescendantLink();
        if (link != null && !link.isEmpty()) {
            for (int i = 1; i < model.getSize(); i++) {
                ConWordDisplay display = model.getElementAt(i);
                if (display.getConWord().getId() == link.getParentWordId()) {
                    cmbParentWord.setSelectedItem(display);
                    break;
                }
            }
        }
    }

    private DescendantLink getSelectedOrStoredLink() {
        Object selected = cmbParentWord.getSelectedItem();
        DescendantLink ret = new DescendantLink();

        if (selected instanceof ConWordDisplay display) {
            ConWord parentWord = display.getConWord();
            ret.setParentWordId(parentWord.getId());
            ret.setParentWordValue(parentWord.getValue());
            ret.setParentWordDefinition(parentWord.getDefinition());
            ret.setParentLanguageName(resolveParentLanguageName());
        } else if (!childWord.getDescendantLink().isEmpty()) {
            ret = new DescendantLink(childWord.getDescendantLink());
        }

        return ret;
    }

    private String resolveParentLanguageName() {
        if (parentCore != null && !parentCore.getPropertiesManager().getLangName().isBlank()) {
            return parentCore.getPropertiesManager().getLangName();
        }

        return profile.getCachedParentLanguageName();
    }

    private void updatePreview() {
        previewModel.setRowCount(0);
        txtPreviewOutput.setText("");

        DescendantLink sourceLink = getSelectedOrStoredLink();
        if (sourceLink.isEmpty()) {
            txtSourceWord.setText("");
            txtStatus.setText("Select a parent word to preview a descendant.");
            lblOverride.setText("");
            return;
        }

        txtSourceWord.setText(sourceLink.getParentWordValue());

        try {
            List<SoundChangeStep> steps = SoundChangeEngine.traceEvolution(
                    sourceLink.getParentWordValue(),
                    profile.getSoundChangeRules(),
                    core.getPropertiesManager());

            for (SoundChangeStep step : steps) {
                previewModel.addRow(new String[]{
                    step.getRuleText(),
                    step.getSourceValue(),
                    step.getResultValue()
                });
            }

            String previewOutput = SoundChangeEngine.evolve(sourceLink.getParentWordValue(),
                    profile.getSoundChangeRules(), core.getPropertiesManager());
            txtPreviewOutput.setText(previewOutput);
            txtStatus.setText("Preview ready.");

            if (childWord.getValue().equals(previewOutput)) {
                lblOverride.setText("Current word matches the generated descendant.");
            } else {
                lblOverride.setText("Current word currently overrides the generated descendant.");
            }
        } catch (IllegalArgumentException e) {
            txtStatus.setText("Preview error: " + e.getLocalizedMessage());
            lblOverride.setText("");
        }
    }

    private void applyDescendant() {
        DescendantLink newLink = getSelectedOrStoredLink();
        if (newLink.isEmpty()) {
            new DesktopInfoBox(this).warning("No Parent Word", "Select a parent word before applying a descendant.");
            return;
        }

        try {
            String descendant = SoundChangeEngine.evolve(newLink.getParentWordValue(),
                    profile.getSoundChangeRules(), core.getPropertiesManager());
            core.getEtymologyManager().setDescendantLink(childWord, newLink);
            childWord.setValue(descendant);
            dispose();
        } catch (IllegalArgumentException e) {
            new DesktopInfoBox(this).error("Rule Error", e.getLocalizedMessage());
        }
    }

    private void clearLink() {
        core.getEtymologyManager().removeDescendantLink(childWord);
        dispose();
    }

    private DictCore loadReadOnlyCore(String filePath)
            throws IOException, ParserConfigurationException {
        DictCore ret = new DictCore(new DesktopPropertiesManager(),
                core.getOSHandler(), new org.darisadesigns.polyglotlina.Desktop.PGTUtil(),
                new DesktopGrammarManager());

        try {
            ret.readFile(filePath);
        } catch (IllegalStateException e) {
            // Warnings still leave a usable parent source.
        }

        return ret;
    }

    @Override
    public void updateAllValues(DictCore _core) {
        // Modal dialog operates on current snapshot only.
    }
}
