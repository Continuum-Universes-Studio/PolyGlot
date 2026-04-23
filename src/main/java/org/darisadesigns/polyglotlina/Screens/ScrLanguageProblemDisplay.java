/*
 * Copyright (c) 2026, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT License
 * See LICENSE.TXT included with this code to read the full license agreement.
 */
package org.darisadesigns.polyglotlina.Screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.DesktopInfoBox;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PButton;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PComboBox;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PDialog;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PLabel;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PTextField;
import org.darisadesigns.polyglotlina.Desktop.PolyGlot;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode.ProblemType;

/**
 * Severity-grouped language problem display.
 */
public class ScrLanguageProblemDisplay extends PDialog {
    private final ScrMainMenu main;
    private final List<LexiconProblemNode> problems;
    private final DefaultTreeModel treeModel;
    private final JTree issueTree;
    private final JTextArea detailsArea;
    private final PTextField summaryField;
    private final PComboBox<String> severityFilter;
    private final JLabel emptyLabel;

    public ScrLanguageProblemDisplay(List<LexiconProblemNode> problems, DictCore _core) {
        super(_core);
        main = (ScrMainMenu) PolyGlot.getPolyGlot().getRootWindow();
        this.problems = problems == null ? new ArrayList<>() : new ArrayList<>(problems);

        treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("issues"));
        issueTree = new JTree(treeModel);
        detailsArea = new JTextArea();
        summaryField = new PTextField(core, true, "Issue Summary");
        severityFilter = new PComboBox<>(((org.darisadesigns.polyglotlina.Desktop.DesktopPropertiesManager) core.getPropertiesManager()).getFontMenu(), core);
        emptyLabel = new JLabel("No issues");

        buildUi();
        refreshTree();
        setModal(true);
        pack();
    }

    private void buildUi() {
        setTitle("Language Consistency Check");
        setMinimumSize(new Dimension(760, 520));
        getContentPane().setLayout(new BorderLayout(8, 8));

        summaryField.setEditable(false);
        summaryField.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultComboBoxModel<String> severityModel = new DefaultComboBoxModel<>();
        severityModel.addElement("All");
        severityModel.addElement("Errors");
        severityModel.addElement("Warnings");
        severityModel.addElement("Info");
        severityFilter.setModel(severityModel);
        severityFilter.setSelectedIndex(0);
        severityFilter.addActionListener(evt -> refreshTree());

        JPanel top = new JPanel(new BorderLayout(8, 8));
        JPanel leftTop = new JPanel(new BorderLayout(6, 6));
        leftTop.add(new PLabel("Filter"), BorderLayout.WEST);
        leftTop.add(severityFilter, BorderLayout.CENTER);
        top.add(summaryField, BorderLayout.CENTER);
        top.add(leftTop, BorderLayout.EAST);

        issueTree.setRootVisible(false);
        issueTree.setShowsRootHandles(true);
        issueTree.setCellRenderer(new IssueTreeRenderer());
        issueTree.addTreeSelectionListener(new IssueSelectionHandler());

        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBackground(Color.WHITE);
        detailsArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(issueTree),
                new JScrollPane(detailsArea));
        split.setDividerLocation(250);

        JPanel bottomButtons = new JPanel(new BorderLayout());
        PButton close = new PButton(nightMode);
        close.setText("Close");
        close.addActionListener(evt -> dispose());
        bottomButtons.add(close, BorderLayout.EAST);

        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(split, BorderLayout.CENTER);
        getContentPane().add(bottomButtons, BorderLayout.SOUTH);
    }

    private void refreshTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        int severitySelection = severityFilter.getSelectedIndex();
        List<LexiconProblemNode> visibleProblems = problems.stream()
                .filter(problem -> matchesFilter(problem, severitySelection))
                .collect(Collectors.toList());

        DefaultMutableTreeNode errorNode = new DefaultMutableTreeNode("Errors");
        DefaultMutableTreeNode warningNode = new DefaultMutableTreeNode("Warnings");
        DefaultMutableTreeNode infoNode = new DefaultMutableTreeNode("Info");

        for (LexiconProblemNode problem : visibleProblems) {
            DefaultMutableTreeNode issueNode = new DefaultMutableTreeNode(problem);
            switch (problem.severity) {
                case LexiconProblemNode.SEVARITY_ERROR -> errorNode.add(issueNode);
                case LexiconProblemNode.SEVARITY_WARNING -> warningNode.add(issueNode);
                default -> infoNode.add(issueNode);
            }
        }

        if (errorNode.getChildCount() > 0) {
            root.add(errorNode);
        }
        if (warningNode.getChildCount() > 0) {
            root.add(warningNode);
        }
        if (infoNode.getChildCount() > 0) {
            root.add(infoNode);
        }

        treeModel.setRoot(root);
        issueTree.expandRow(0);
        issueTree.expandRow(1);
        issueTree.expandRow(2);

        summaryField.setText("Issues: " + visibleProblems.size()
                + "  |  Errors: " + visibleProblems.stream().filter(p -> p.severity == LexiconProblemNode.SEVARITY_ERROR).count()
                + "  |  Warnings: " + visibleProblems.stream().filter(p -> p.severity == LexiconProblemNode.SEVARITY_WARNING).count()
                + "  |  Info: " + visibleProblems.stream().filter(p -> p.severity == LexiconProblemNode.SEVARITY_INFO).count());

        if (visibleProblems.isEmpty()) {
            detailsArea.setText("No consistency issues were found.");
        } else {
            detailsArea.setText("Select an issue to view details.");
        }
    }

    private boolean matchesFilter(LexiconProblemNode problem, int severitySelection) {
        return switch (severitySelection) {
            case 1 -> problem.severity == LexiconProblemNode.SEVARITY_ERROR;
            case 2 -> problem.severity == LexiconProblemNode.SEVARITY_WARNING;
            case 3 -> problem.severity == LexiconProblemNode.SEVARITY_INFO;
            default -> true;
        };
    }

    private void showIssue(LexiconProblemNode issue) {
        if (issue == null) {
            detailsArea.setText("");
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(issue.issueCode.isBlank() ? issue.shortDescription : issue.issueCode).append("\n\n");
        builder.append(issue.description).append("\n\n");
        if (!issue.details.isBlank() && !issue.details.equals(issue.description)) {
            builder.append("Details:\n").append(issue.details).append("\n\n");
        }
        if (!issue.suggestedFix.isBlank()) {
            builder.append("Suggested fix:\n").append(issue.suggestedFix).append("\n");
        }

        detailsArea.setText(builder.toString().trim());
        detailsArea.setCaretPosition(0);

        if (main != null && issue.problemWord instanceof ConWord conWord) {
            main.setWordSelectedById(conWord.getId());
        }
    }

    @Override
    public void updateAllValues(DictCore _core) {
        // Not used in this dialog.
    }

    private final class IssueSelectionHandler implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            if (path == null) {
                return;
            }

            Object selected = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
            if (selected instanceof LexiconProblemNode issue) {
                showIssue(issue);
            }
        }
    }

    private static final class IssueTreeRenderer extends DefaultTreeCellRenderer {
        @Override
        public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof LexiconProblemNode issue) {
                setText((issue.issueCode.isBlank() ? "" : issue.issueCode + " - ") + issue.shortDescription);
            } else {
                setText(String.valueOf(userObject));
            }

            return this;
        }
    }
}
