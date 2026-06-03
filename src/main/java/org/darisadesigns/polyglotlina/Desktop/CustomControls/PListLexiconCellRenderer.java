/*
 * Copyright (c) 2021, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT License
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
package org.darisadesigns.polyglotlina.Desktop.CustomControls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.darisadesigns.polyglotlina.LexiconDisplayRenderer;
import org.darisadesigns.polyglotlina.Desktop.DesktopPropertiesManager;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.ConWordCollection.ConWordDisplay;
import org.darisadesigns.polyglotlina.Nodes.ConWord;

/**
 *
 * @author draque
 */
public class PListLexiconCellRenderer extends DefaultListCellRenderer {
    private final DictCore core;
    private ConWordDisplay curVal = null;
    
    public PListLexiconCellRenderer(DictCore _core) {
        core = _core;
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        // prints expanded word display if set in properties
        if (curVal != null && core.getPropertiesManager().isExpandedLexListDisplay()) {
            ConWord word = curVal.getConWord();
            Font localFont = ((DesktopPropertiesManager)core.getPropertiesManager()).getFontLocal();
            LexiconDisplayRenderer.DisplayValue displayValue = LexiconDisplayRenderer.getDisplayValue(word, core);
            Font lexiconFont = displayValue.isUseConlangFont()
                    ? ((DesktopPropertiesManager)core.getPropertiesManager()).getFontCon()
                    : localFont;
            FontMetrics localMetrics = g.getFontMetrics(localFont);
            FontMetrics lexiconMetrics = g.getFontMetrics(lexiconFont);
            
            int wordEnd;
            int dropPosition;
            int height;
            String printValue;
            
            if (core.getPropertiesManager().isUseLocalWordLex()) {
                printValue = displayValue.getText();
                wordEnd = localMetrics.stringWidth(word.getLocalWord());
                dropPosition = (lexiconMetrics.getHeight() * 6) / 7;
                height = localMetrics.getHeight();
                g.setFont(lexiconFont);
            } else {
                printValue = word.getLocalWord();
                wordEnd = lexiconMetrics.stringWidth(displayValue.getText());
                dropPosition = (localMetrics.getHeight() * 6) / 7;
                height = lexiconMetrics.getHeight();
                g.setFont(localFont);
            }
            
            if (!printValue.isBlank()) {
                g.setColor(Color.blue);
                g.drawLine(wordEnd + 10, 0, wordEnd + 10, height);
                g.setColor(Color.darkGray);
                g.drawString(printValue, wordEnd + 15, dropPosition);
            }
        }
    }
    
    @Override
    public Component getListCellRendererComponent(
        JList<?> list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
    {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof ConWordDisplay _value) {
            curVal = _value;
            ConWord word = _value.getConWord();
            Font localFont = ((DesktopPropertiesManager)core.getPropertiesManager()).getFontLocal();

            if (core.getPropertiesManager().isUseLocalWordLex()) {
                setFont(localFont);
                setText(word.getLocalWord().isBlank() ? " " : word.getLocalWord());
            } else {
                LexiconDisplayRenderer.DisplayValue displayValue = LexiconDisplayRenderer.getDisplayValue(word, core);
                setFont(displayValue.isUseConlangFont()
                        ? ((DesktopPropertiesManager)core.getPropertiesManager()).getFontCon()
                        : localFont);
                setText(displayValue.getText().isBlank() ? " " : displayValue.getText());
            }
        } else {
            curVal = null;
        }
        
        return component;
    }
}
