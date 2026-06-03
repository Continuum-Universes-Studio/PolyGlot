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
package org.darisadesigns.polyglotlina;

import TestResources.DummyCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.DisplayMode;
import org.darisadesigns.polyglotlina.Nodes.PronunciationNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class LexiconDisplayRendererTest {

    @Test
    public void testRenderedUsesCanonicalValue() {
        DictCore core = DummyCore.newCore();
        ConWord word = buildWord(core, "kana");

        LexiconDisplayRenderer.DisplayValue displayValue
                = LexiconDisplayRenderer.getDisplayValue(word, core, DisplayMode.RENDERED);

        assertEquals("kana", displayValue.getText());
        assertTrue(displayValue.isUseConlangFont());
    }

    @Test
    public void testIpaUsesPronunciationOverride() {
        DictCore core = DummyCore.newCore();
        ConWord word = buildWord(core, "kana");
        word.setProcOverride(true);
        word.setPronunciation("ka.na");

        LexiconDisplayRenderer.DisplayValue displayValue
                = LexiconDisplayRenderer.getDisplayValue(word, core, DisplayMode.IPA);

        assertEquals("ka.na", displayValue.getText());
        assertFalse(displayValue.isUseConlangFont());
    }

    @Test
    public void testRomanizedUsesRomanizationManager() {
        DictCore core = DummyCore.newCore();
        ConWord word = buildWord(core, "kana");
        core.getRomManager().setEnabled(true);
        core.getRomManager().addPronunciation(new PronunciationNode("kana", "kana-roman"));

        LexiconDisplayRenderer.DisplayValue displayValue
                = LexiconDisplayRenderer.getDisplayValue(word, core, DisplayMode.ROMANIZED);

        assertEquals("kana-roman", displayValue.getText());
        assertFalse(displayValue.isUseConlangFont());
    }

    @Test
    public void testIpaFallsBackToCanonicalWhenUnavailable() {
        DictCore core = DummyCore.newCore();
        ConWord word = buildWord(core, "kana");

        LexiconDisplayRenderer.DisplayValue displayValue
                = LexiconDisplayRenderer.getDisplayValue(word, core, DisplayMode.IPA);

        assertEquals("kana", displayValue.getText());
        assertTrue(displayValue.isUseConlangFont());
    }

    @Test
    public void testRomanizedFallsBackToCanonicalWhenDisabled() {
        DictCore core = DummyCore.newCore();
        ConWord word = buildWord(core, "kana");
        core.getRomManager().addPronunciation(new PronunciationNode("kana", "kana-roman"));

        LexiconDisplayRenderer.DisplayValue displayValue
                = LexiconDisplayRenderer.getDisplayValue(word, core, DisplayMode.ROMANIZED);

        assertEquals("kana", displayValue.getText());
        assertTrue(displayValue.isUseConlangFont());
    }

    private ConWord buildWord(DictCore core, String value) {
        ConWord word = new ConWord();
        word.setCore(core);
        word.setValue(value);
        word.setLocalWord("meaning");
        return word;
    }
}
