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

import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.DisplayMode;

/**
 * Centralizes lexicon-facing display decisions so UI layers can render
 * canonical, IPA, or romanized text without changing lexical storage.
 *
 * @author draque
 */
public final class LexiconDisplayRenderer {

    private LexiconDisplayRenderer() {
    }

    public static DisplayValue getDisplayValue(ConWord word, DictCore core) {
        DisplayMode mode = core == null || core.getPropertiesManager() == null
                ? DisplayMode.RENDERED
                : core.getPropertiesManager().getLexiconDisplayMode();

        return getDisplayValue(word, core, mode);
    }

    public static DisplayValue getDisplayValue(ConWord word, DictCore core, DisplayMode mode) {
        if (word == null) {
            return new DisplayValue("", true);
        }

        String canonicalValue = word.getValue() == null ? "" : word.getValue();
        DisplayMode safeMode = mode == null ? DisplayMode.RENDERED : mode;

        return switch (safeMode) {
            case IPA -> buildIpaValue(word, canonicalValue);
            case ROMANIZED -> buildRomanizedValue(word, core, canonicalValue);
            case RENDERED -> new DisplayValue(canonicalValue, true);
        };
    }

    private static DisplayValue buildIpaValue(ConWord word, String canonicalValue) {
        try {
            String pronunciation = word.getPronunciation();

            if (!pronunciation.isBlank()) {
                return new DisplayValue(pronunciation, false);
            }
        } catch (Exception e) {
            // Fall back to canonical rendered text.
        }

        return new DisplayValue(canonicalValue, true);
    }

    private static DisplayValue buildRomanizedValue(ConWord word, DictCore core, String canonicalValue) {
        if (core == null || !core.getRomManager().isEnabled()) {
            return new DisplayValue(canonicalValue, true);
        }

        try {
            String romanized = core.getRomManager().getPronunciation(canonicalValue);

            if (!romanized.isBlank()) {
                return new DisplayValue(romanized, false);
            }
        } catch (Exception e) {
            // Fall back to canonical rendered text.
        }

        return new DisplayValue(canonicalValue, true);
    }

    public static final class DisplayValue {
        private final String text;
        private final boolean useConlangFont;

        public DisplayValue(String text, boolean useConlangFont) {
            this.text = text == null ? "" : text;
            this.useConlangFont = useConlangFont;
        }

        public String getText() {
            return text;
        }

        public boolean isUseConlangFont() {
            return useConlangFont;
        }
    }
}
