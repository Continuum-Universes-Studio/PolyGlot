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
package org.darisadesigns.polyglotlina.Nodes;

/**
 * Supported display modes for lexicon-facing conlang text.
 *
 * @author draque
 */
public enum DisplayMode {
    IPA("IPA"),
    ROMANIZED("Romanized"),
    RENDERED("Rendered");

    private final String label;

    DisplayMode(String label) {
        this.label = label;
    }

    public static DisplayMode fromSerializedValue(String value) {
        if (value == null || value.isBlank()) {
            return RENDERED;
        }

        try {
            return DisplayMode.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            for (DisplayMode mode : values()) {
                if (mode.label.equalsIgnoreCase(value.trim())) {
                    return mode;
                }
            }

            return RENDERED;
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
