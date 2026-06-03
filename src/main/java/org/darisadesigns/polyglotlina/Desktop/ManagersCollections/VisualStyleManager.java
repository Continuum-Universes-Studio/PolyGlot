/*
 * Copyright (c) 2018-2019, Draque Thompson
 * All rights reserved.
 *
 * Licensed under: MIT Licence
 * See LICENSE.TXT included with this code to read the full license agreement.

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
package org.darisadesigns.polyglotlina.Desktop.ManagersCollections;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import org.darisadesigns.polyglotlina.Desktop.PGTUtil;

/**
 * Handles all elements of PolyGlot relating to visual styles and colors of the program
 * @author DThompson
 */
public class VisualStyleManager {

    private static final Color COLOR_PANEL_BG_NIGHT = Color.decode("#202124");
    private static final Color COLOR_CONTROL_BG_NIGHT = Color.decode("#2b2d31");
    private static final Color COLOR_BORDER_NIGHT = Color.decode("#5f6368");
    private static final Color COLOR_SELECTION_NIGHT = Color.decode("#4f8cff");
    private static final Color COLOR_LOCAL_TEXT_NIGHT = Color.decode("#8ab4f8");
    
    private VisualStyleManager() {
    }

    public static Color getPanelBGColor(boolean isNightMode) {
        return isNightMode ? COLOR_PANEL_BG_NIGHT : Color.white;
    }

    public static Color getControlBGColor(boolean isNightMode) {
        return isNightMode ? COLOR_CONTROL_BG_NIGHT : Color.white;
    }

    public static Color getBorderColor(boolean isNightMode) {
        return isNightMode ? COLOR_BORDER_NIGHT : Color.black;
    }

    public static Color getSelectionBGColor(boolean isNightMode) {
        return isNightMode ? COLOR_SELECTION_NIGHT : PGTUtil.COLOR_SELECTED_BG;
    }

    public static Color getSelectionFGColor(boolean isNightMode) {
        return isNightMode ? Color.white : Color.black;
    }

    public static Color getListExtraTextColor(boolean isNightMode) {
        return isNightMode ? COLOR_LOCAL_TEXT_NIGHT : Color.blue;
    }
    
    // color of regular text
    public static Color getTextColor(boolean isNightMode) {
        return isNightMode ?
                PGTUtil.COLOR_TEXT_NIGHT:
                PGTUtil.COLOR_TEXT;
    }
    
    // color of regular text background
    public static Color getTextBGColor(boolean isNightMode) {
        return isNightMode ?
                PGTUtil.COLOR_TEXT_BG_NIGHT:
                PGTUtil.COLOR_TEXT_BG;
    }
    
    // color of default value text
    public static Color getDefaultTextColor(boolean isNightMode) {
        return isNightMode ?
                PGTUtil.COLOR_DEFAULT_TEXT_NIGHT:
                PGTUtil.COLOR_DEFAULT_TEXT;
    }
    
    // color of disabled text
    public static Color getDisabledTextColor(boolean isNightMode) {
        return isNightMode ?
                PGTUtil.COLOR_TEXT_DISABLED_NIGHT:
                PGTUtil.COLOR_TEXT_DISABLED;
    }
    
    // color of disabled text BG
    public static Color getDisabledTextColorBG(boolean isNightMode) {
        return isNightMode ?
                PGTUtil.COLOR_TEXT_DISABLED_BG_NIGHT:
                PGTUtil.COLOR_TEXT_DISABLED_BG;
    }
    
    public static Color getCheckBoxSelected(boolean isEnabled, boolean isNightMode) {
        Color ret = PGTUtil.COLOR_CHECKBOX_SELECTED_DISABLED;
        
        if (isEnabled && isNightMode) {
            ret = PGTUtil.COLOR_CHECKBOX_SELECTED_NIGHT;
        } else if (isEnabled) {
            ret = PGTUtil.COLOR_CHECKBOX_SELECTED;
        }
        
        return ret;
    }
    
    public static Color getCheckBoxBG(boolean isEnabled, boolean isNightMode) {
        Color ret = PGTUtil.COLOR_CHECKBOX_BG_DISABLED;
        
        if (isEnabled && isNightMode) {
            ret = PGTUtil.COLOR_CHECKBOX_BG_NIGHT;
        } else if (isEnabled) {
            ret = PGTUtil.COLOR_CHECKBOX_BG;
        }
        
        return ret;
    }
    
    public static Color getCheckBoxOutline(boolean isEnabled, boolean isNightMode) {
        Color ret = PGTUtil.COLOR_CHECKBOX_OUTLINE_DISABLED;
        
        if (isEnabled && isNightMode) {
            ret = PGTUtil.COLOR_CHECKBOX_OUTLINE_NIGHT;
        } else if (isEnabled) {
            ret = PGTUtil.COLOR_CHECKBOX_OUTLINE;
        }
        
        return ret;
    }
    
    public static Color getCheckBoxHover(boolean isEnabled, boolean isNightMode) {
        Color ret = PGTUtil.COLOR_CHECKBOX_HOVER_DISABLED;
        
        if (isEnabled && isNightMode) {
            ret = PGTUtil.COLOR_CHECKBOX_HOVER_NIGHT;
        } else if (isEnabled) {
            ret = PGTUtil.COLOR_CHECKBOX_HOVER;
        }
        
        return ret;
    }
    
    public static Color getCheckBoxClicked(boolean isEnabled, boolean isNightMode) {
        Color ret = PGTUtil.COLOR_CHECKBOX_CLICKED_DISABLED;
        
        if (isEnabled && isNightMode) {
            ret = PGTUtil.COLOR_CHECKBOX_CLICKED_NIGHT;
        } else if (isEnabled) {
            ret = PGTUtil.COLOR_CHECKBOX_CLICKED;
        }
        
        return ret;
    }
    
    
    public static Color getCheckBoxFieldBack(boolean isEnabled, boolean isNightMode) {
        Color ret = PGTUtil.COLOR_CHECKBOX_FIELD_BACK_DISABLED;
        
        if (isEnabled && isNightMode) {
            ret = PGTUtil.COLOR_CHECKBOX_FIELD_BACK_NIGHT;
        } else if (isEnabled) {
            ret = PGTUtil.COLOR_CHECKBOX_FIELD_BACK;
        }
        
        return ret;
    }

    public static void applyThemeDefaults(boolean isNightMode) {
        Color panelBackground = getPanelBGColor(isNightMode);
        Color controlBackground = getControlBGColor(isNightMode);
        Color borderColor = getBorderColor(isNightMode);
        Color textColor = getTextColor(isNightMode);
        Color textBackground = getTextBGColor(isNightMode);
        Color disabledText = getDisabledTextColor(isNightMode);
        Color disabledTextBackground = getDisabledTextColorBG(isNightMode);
        Color selectionBackground = getSelectionBGColor(isNightMode);
        Color selectionForeground = getSelectionFGColor(isNightMode);

        UIManager.put("ScrollBarUI", "org.darisadesigns.polyglotlina.Desktop.CustomControls.PScrollBarUI");
        UIManager.put("SplitPaneUI", "org.darisadesigns.polyglotlina.Desktop.CustomControls.PSplitPaneUI");
        UIManager.put("ToolTipUI", "org.darisadesigns.polyglotlina.Desktop.CustomControls.PToolTipUI");

        UIManager.put("OptionPane.background", panelBackground);
        UIManager.put("OptionPane.messageForeground", textColor);
        UIManager.put("Panel.background", panelBackground);
        UIManager.put("Panel.foreground", textColor);
        UIManager.put("Viewport.background", panelBackground);
        UIManager.put("ScrollPane.background", panelBackground);
        UIManager.put("ScrollPane.foreground", textColor);
        UIManager.put("Separator.background", borderColor);
        UIManager.put("Separator.foreground", borderColor);
        UIManager.put("SplitPane.background", panelBackground);
        UIManager.put("SplitPane.foreground", textColor);
        UIManager.put("TabbedPane.background", panelBackground);
        UIManager.put("TabbedPane.foreground", textColor);
        UIManager.put("TabbedPane.selected", controlBackground);

        UIManager.put("Label.foreground", textColor);
        UIManager.put("Button.foreground", textColor);
        UIManager.put("ToolTip.background", controlBackground);
        UIManager.put("ToolTip.foreground", textColor);
        UIManager.put("PopupMenu.background", controlBackground);
        UIManager.put("PopupMenu.foreground", textColor);
        UIManager.put("MenuBar.background", controlBackground);
        UIManager.put("MenuBar.foreground", textColor);
        UIManager.put("Menu.background", controlBackground);
        UIManager.put("Menu.foreground", textColor);
        UIManager.put("MenuItem.background", controlBackground);
        UIManager.put("MenuItem.foreground", textColor);

        UIManager.put("TextField.foreground", textColor);
        UIManager.put("TextField.background", textBackground);
        UIManager.put("TextField.caretForeground", textColor);
        UIManager.put("TextField.selectionBackground", selectionBackground);
        UIManager.put("TextField.selectionForeground", selectionForeground);
        UIManager.put("TextField.inactiveForeground", disabledText);
        UIManager.put("TextField.inactiveBackground", disabledTextBackground);
        UIManager.put("TextArea.foreground", textColor);
        UIManager.put("TextArea.background", textBackground);
        UIManager.put("TextArea.caretForeground", textColor);
        UIManager.put("TextArea.selectionBackground", selectionBackground);
        UIManager.put("TextArea.selectionForeground", selectionForeground);
        UIManager.put("TextPane.foreground", textColor);
        UIManager.put("TextPane.background", textBackground);
        UIManager.put("TextPane.caretForeground", textColor);
        UIManager.put("TextPane.selectionBackground", selectionBackground);
        UIManager.put("TextPane.selectionForeground", selectionForeground);
        UIManager.put("EditorPane.foreground", textColor);
        UIManager.put("EditorPane.background", textBackground);
        UIManager.put("EditorPane.caretForeground", textColor);
        UIManager.put("EditorPane.selectionBackground", selectionBackground);
        UIManager.put("EditorPane.selectionForeground", selectionForeground);
        UIManager.put("FormattedTextField.foreground", textColor);
        UIManager.put("FormattedTextField.background", textBackground);
        UIManager.put("PasswordField.foreground", textColor);
        UIManager.put("PasswordField.background", textBackground);
        UIManager.put("ComboBox.foreground", textColor);
        UIManager.put("ComboBox.background", textBackground);
        UIManager.put("ComboBox.selectionBackground", selectionBackground);
        UIManager.put("ComboBox.selectionForeground", selectionForeground);
        UIManager.put("List.foreground", textColor);
        UIManager.put("List.background", panelBackground);
        UIManager.put("List.selectionBackground", selectionBackground);
        UIManager.put("List.selectionForeground", selectionForeground);
        UIManager.put("Tree.textForeground", textColor);
        UIManager.put("Tree.textBackground", panelBackground);
        UIManager.put("Tree.selectionForeground", selectionForeground);
        UIManager.put("Tree.selectionBackground", selectionBackground);
        UIManager.put("Table.foreground", textColor);
        UIManager.put("Table.background", panelBackground);
        UIManager.put("Table.selectionForeground", selectionForeground);
        UIManager.put("Table.selectionBackground", selectionBackground);
        UIManager.put("Table.gridColor", borderColor);
        UIManager.put("TableHeader.foreground", textColor);
        UIManager.put("TableHeader.background", controlBackground);
        UIManager.put("TableHeader.focusCellBackground", controlBackground);
        UIManager.put("TableHeader.focusCellForeground", textColor);

        if (isNightMode) {
            UIManager.put("control", panelBackground);
            UIManager.put("info", controlBackground);
            UIManager.put("nimbusBase", controlBackground);
            UIManager.put("nimbusBlueGrey", Color.decode("#3c4043"));
            UIManager.put("nimbusLightBackground", textBackground);
            UIManager.put("text", textColor);
            UIManager.put("nimbusSelectionBackground", selectionBackground);
            UIManager.put("nimbusSelectedText", selectionForeground);
        }

        UIManager.getLookAndFeelDefaults().put("Panel.background", panelBackground);
    }

    public static void applyTheme(Component component, boolean isNightMode) {
        if (!isNightMode || component == null) {
            return;
        }

        applyThemeToSingleComponent(component);

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyTheme(child, true);
            }
        }

        if (component instanceof JTable table) {
            applyTheme(table.getTableHeader(), true);
        }
    }
        
    public static UIDefaults generateUIOverrides(boolean isNightMode) {
        UIDefaults overrides = new UIDefaults();
        overrides.put("TextField[Disabled].backgroundPainter", (Painter<JTextField>) 
                (Graphics2D g, JTextField field, int width, int height) -> {
            if (isNightMode) {
                g.setColor(getDisabledTextColorBG(true));
                g.fill(new Rectangle(1, 1, width-2, height-2));
            }
        });
        overrides.put("TextField[Disabled].borderPainter", (Painter<JTextField>) 
                (Graphics2D g, JTextField field, int width, int height) -> {
            g.setColor(isNightMode ? getBorderColor(true) : Color.lightGray);
            g.drawLine(1, 1, width-2, 1);
            g.drawLine(1, height-2, width-2, height-2);
            g.drawLine(width-2, 1, width-2, height-2);
            g.drawLine(1, 1, 1, height-2);
        });
        overrides.put("TextArea[Disabled].backgroundPainter", (Painter<JTextField>) 
                (Graphics2D g, JTextField field, int width, int height) -> {
            if (isNightMode) {
                g.setColor(getDisabledTextColorBG(true));
                g.fill(new Rectangle(1, 1, width-2, height-2));
            }
        });
        overrides.put("TextArea[Disabled].borderPainter", (Painter<JTextField>) 
                (Graphics2D g, JTextField field, int width, int height) -> {
            g.setColor(isNightMode ? getBorderColor(true) : Color.lightGray);
            g.drawLine(1, 1, width-2, 1);
            g.drawLine(1, height-2, width-2, height-2);
            g.drawLine(width-2, 1, width-2, height-2);
            g.drawLine(1, 1, 1, height-2);
        });
        overrides.put("TextPane[Disabled].backgroundPainter", (Painter<JTextField>) 
                (Graphics2D g, JTextField field, int width, int height) -> {
            if (isNightMode) {
                g.setColor(getDisabledTextColorBG(true));
                g.fill(new Rectangle(1, 1, width-2, height-2));
            }
        });
        overrides.put("TextPane[Disabled].borderPainter", (Painter<JTextField>) 
                (Graphics2D g, JTextField field, int width, int height) -> {
            g.setColor(isNightMode ? getBorderColor(true) : Color.lightGray);
            g.drawLine(1, 1, width-2, 1);
            g.drawLine(1, height-2, width-2, height-2);
            g.drawLine(width-2, 1, width-2, height-2);
            g.drawLine(1, 1, 1, height-2);
        });
        
        return overrides;
    }

    private static void applyThemeToSingleComponent(Component component) {
        if (component instanceof JPanel || component instanceof JLayeredPane
                || component instanceof JRootPane || component instanceof JScrollPane
                || component instanceof JSplitPane || component instanceof JTabbedPane
                || component instanceof JViewport) {
            setBackgroundIfNeutral(component, getPanelBGColor(true));
        }

        if (component instanceof JLabel) {
            setForegroundIfNeutral(component, getTextColor(true));
        }

        if (component instanceof AbstractButton) {
            setForegroundIfNeutral(component, getTextColor(true));
        }

        if (component instanceof JTextComponent textComponent) {
            setBackgroundIfNeutral(textComponent, getTextBGColor(true));
            setForegroundIfNeutral(textComponent, getTextColor(true));
            textComponent.setCaretColor(getTextColor(true));
            textComponent.setSelectionColor(getSelectionBGColor(true));
            textComponent.setSelectedTextColor(getSelectionFGColor(true));
            textComponent.setDisabledTextColor(getDisabledTextColor(true));
        }

        if (component instanceof JComboBox<?> comboBox) {
            setBackgroundIfNeutral(comboBox, getTextBGColor(true));
            setForegroundIfNeutral(comboBox, getTextColor(true));

            if (comboBox.isEditable()) {
                applyTheme(comboBox.getEditor().getEditorComponent(), true);
            }
        }

        if (component instanceof JList<?> list) {
            setBackgroundIfNeutral(list, getPanelBGColor(true));
            setForegroundIfNeutral(list, getTextColor(true));
            list.setSelectionBackground(getSelectionBGColor(true));
            list.setSelectionForeground(getSelectionFGColor(true));
        }

        if (component instanceof JTree tree) {
            setBackgroundIfNeutral(tree, getPanelBGColor(true));
            setForegroundIfNeutral(tree, getTextColor(true));
        }

        if (component instanceof JTable table) {
            setBackgroundIfNeutral(table, getPanelBGColor(true));
            setForegroundIfNeutral(table, getTextColor(true));
            table.setSelectionBackground(getSelectionBGColor(true));
            table.setSelectionForeground(getSelectionFGColor(true));
            table.setGridColor(getBorderColor(true));
        }

        if (component instanceof JMenuBar || component instanceof JMenu
                || component instanceof JMenuItem || component instanceof JPopupMenu) {
            setBackgroundIfNeutral(component, getControlBGColor(true));
            setForegroundIfNeutral(component, getTextColor(true));
        }

        if (component instanceof JSeparator separator) {
            separator.setForeground(getBorderColor(true));
            separator.setBackground(getBorderColor(true));
        }

        if (component instanceof JComponent jComponent) {
            Border themedBorder = getThemedBorder(jComponent.getBorder(), true);

            if (themedBorder != jComponent.getBorder()) {
                jComponent.setBorder(themedBorder);
            }
        }
    }

    private static void setBackgroundIfNeutral(Component component, Color replacement) {
        Color curColor = component.getBackground();

        if (curColor == null || curColor instanceof UIResource
                || sameColor(curColor, Color.white)
                || sameColor(curColor, Color.lightGray)) {
            component.setBackground(replacement);
        }
    }

    private static void setForegroundIfNeutral(Component component, Color replacement) {
        Color curColor = component.getForeground();

        if (curColor == null || curColor instanceof UIResource
                || sameColor(curColor, Color.black)
                || sameColor(curColor, Color.darkGray)
                || sameColor(curColor, Color.gray)
                || sameColor(curColor, Color.white)) {
            component.setForeground(replacement);
        }
    }

    private static boolean sameColor(Color first, Color second) {
        return first != null && second != null && first.getRGB() == second.getRGB();
    }

    private static Border getThemedBorder(Border border, boolean isNightMode) {
        if (!isNightMode || border == null) {
            return border;
        }

        if (border instanceof CompoundBorder compoundBorder) {
            Border outside = getThemedBorder(compoundBorder.getOutsideBorder(), true);
            Border inside = getThemedBorder(compoundBorder.getInsideBorder(), true);

            if (outside != compoundBorder.getOutsideBorder() || inside != compoundBorder.getInsideBorder()) {
                return BorderFactory.createCompoundBorder(outside, inside);
            }
        } else if (border instanceof TitledBorder titledBorder) {
            Border inside = getThemedBorder(titledBorder.getBorder(), true);
            return BorderFactory.createTitledBorder(
                    inside,
                    titledBorder.getTitle(),
                    titledBorder.getTitleJustification(),
                    titledBorder.getTitlePosition(),
                    titledBorder.getTitleFont(),
                    getTextColor(true));
        } else if (border instanceof LineBorder lineBorder
                && (lineBorder.getLineColor() instanceof UIResource
                || sameColor(lineBorder.getLineColor(), Color.black)
                || sameColor(lineBorder.getLineColor(), Color.gray))) {
            return BorderFactory.createLineBorder(
                    getBorderColor(true),
                    lineBorder.getThickness(),
                    lineBorder.getRoundedCorners());
        }

        return border;
    }
}
