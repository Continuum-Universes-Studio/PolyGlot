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
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.darisadesigns.polyglotlina.Nodes.DisplayMode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

public class LexiconDisplayModePersistenceTest {

    @Test
    public void testLexiconDisplayModeRoundTrip() {
        File languageFile = null;

        try {
            DictCore origin = DummyCore.newCore();
            DictCore reload = DummyCore.newCore();

            languageFile = File.createTempFile("POLYGLOT_LEXICON_DISPLAY", ".pgd");
            languageFile.deleteOnExit();

            origin.getPropertiesManager().setLexiconDisplayMode(DisplayMode.IPA);

            origin.writeFile(languageFile.toString(), false, false);
            reload.readFile(languageFile.toString());

            assertEquals(DisplayMode.IPA, reload.getPropertiesManager().getLexiconDisplayMode());
        } catch (Exception e) {
            fail(e);
        } finally {
            if (languageFile != null && languageFile.exists()) {
                languageFile.delete();
            }
        }
    }

    @Test
    public void testLegacyLanguageDefaultsToRenderedDisplayMode() {
        try {
            DictCore legacyCore = DummyCore.newCore();
            legacyCore.readFile(PGTUtil.TESTRESOURCES + "basic_lang.pgd");

            assertEquals(DisplayMode.RENDERED, legacyCore.getPropertiesManager().getLexiconDisplayMode());
        } catch (IOException | IllegalStateException | ParserConfigurationException e) {
            fail(e);
        }
    }
}
