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
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Tests for the v1 sound-change engine.
 *
 * @author draque
 */
public class SoundChangeEngineTest {

    @Test
    public void testParseFailures() {
        assertThrows(IllegalArgumentException.class,
                () -> SoundChangeEngine.parseRule("th s #_"));
        assertThrows(IllegalArgumentException.class,
                () -> SoundChangeEngine.parseRule(" > s / #_"));
        assertThrows(IllegalArgumentException.class,
                () -> SoundChangeEngine.parseRule("th > s / ##"));
    }

    @Test
    public void testWordInitialRule() {
        var core = DummyCore.newCore();
        var result = SoundChangeEngine.evolve("that",
                List.of("th > s / #_"), core.getPropertiesManager());

        assertEquals("sat", result);
    }

    @Test
    public void testWordFinalDeletionRule() {
        var core = DummyCore.newCore();
        var result = SoundChangeEngine.evolve("tam",
                List.of("m > ∅ / _#"), core.getPropertiesManager());

        assertEquals("ta", result);
    }

    @Test
    public void testConsonantEnvironmentRule() {
        var core = DummyCore.newCore();
        var result = SoundChangeEngine.evolve("bat",
                List.of("a > e / C_C"), core.getPropertiesManager());

        assertEquals("bet", result);
    }

    @Test
    public void testOrderedRulesApplySequentially() {
        var core = DummyCore.newCore();
        var result = SoundChangeEngine.evolve("tha",
                List.of("th > s / #_", "s > z / _V"), core.getPropertiesManager());

        assertEquals("za", result);
    }

    @Test
    public void testMultiCharacterFromValue() {
        var core = DummyCore.newCore();
        var result = SoundChangeEngine.evolve("atha",
                List.of("th > d / V_V"), core.getPropertiesManager());

        assertEquals("ada", result);
    }

    @Test
    public void testZompistCategoriesAffectCVMatching() {
        var core = DummyCore.newCore();
        core.getPropertiesManager().setZompistCategories("C=xy\nV=12");

        var result = SoundChangeEngine.evolve("x1y",
                List.of("1 > 2 / C_C"), core.getPropertiesManager());

        assertEquals("x2y", result);
    }

    @Test
    public void testFallbackVowelMatchingWithoutCategories() {
        var core = DummyCore.newCore();
        core.getPropertiesManager().setZompistCategories("");

        var result = SoundChangeEngine.evolve("ba",
                List.of("b > p / _V"), core.getPropertiesManager());

        assertEquals("pa", result);
    }
}
