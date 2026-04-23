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

import TestResources.DummyCore;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PComboBox;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.DisplayMode;
import org.darisadesigns.polyglotlina.Nodes.PronunciationNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

public class ScrLexiconDisplayModeTest {

    @Test
    public void testLexiconDisplayModeToggleUpdatesPreview() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        try {
            DictCore core = DummyCore.newCore();
            ConWord word = new ConWord();
            word.setCore(core);
            word.setValue("kana");
            word.setLocalWord("meaning");
            word.setProcOverride(true);
            word.setPronunciation("ka.na");
            core.getWordCollection().addWord(word);
            core.getRomManager().setEnabled(true);
            core.getRomManager().addPronunciation(new PronunciationNode("kana", "kana-roman"));

            ScrLexicon lexicon = new ScrLexicon(core, null);
            PComboBox<DisplayMode> displayModeCombo = getFieldValue(lexicon, "cmbLexiconDisplayMode", PComboBox.class);
            JTextField previewField = getFieldValue(lexicon, "txtRom", JTextField.class);

            waitForCondition(() -> displayModeCombo.getItemCount() == DisplayMode.values().length, 5000);
            waitForCondition(() -> "kana".equals(previewField.getText()), 5000);

            setDisplayMode(displayModeCombo, DisplayMode.ROMANIZED);
            waitForCondition(() -> "kana-roman".equals(previewField.getText()), 5000);
            assertEquals(DisplayMode.ROMANIZED, core.getPropertiesManager().getLexiconDisplayMode());

            setDisplayMode(displayModeCombo, DisplayMode.IPA);
            waitForCondition(() -> "ka.na".equals(previewField.getText()), 5000);
            assertEquals(DisplayMode.IPA, core.getPropertiesManager().getLexiconDisplayMode());

            setDisplayMode(displayModeCombo, DisplayMode.RENDERED);
            waitForCondition(() -> "kana".equals(previewField.getText()), 5000);
            assertEquals(DisplayMode.RENDERED, core.getPropertiesManager().getLexiconDisplayMode());

            lexicon.dispose();
        } catch (Exception e) {
            fail(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getFieldValue(Object target, String fieldName, Class<?> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) type.cast(field.get(target));
    }

    private void setDisplayMode(PComboBox<DisplayMode> displayModeCombo, DisplayMode mode) throws Exception {
        SwingUtilities.invokeAndWait(() -> displayModeCombo.setSelectedItem(mode));
    }

    private void waitForCondition(CheckedBooleanSupplier condition, long timeoutMs) throws Exception {
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < timeoutMs) {
            if (condition.getAsBoolean()) {
                return;
            }

            Thread.sleep(50);
        }

        fail("Timed out waiting for condition.");
    }

    @FunctionalInterface
    private interface CheckedBooleanSupplier {
        boolean getAsBoolean() throws Exception;
    }
}
