/*
 * Copyright (c) 2026, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT Licence
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

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.darisadesigns.polyglotlina.DictCore;

public class MorphologyFeatureSet {
    private final Map<String, String> values = new LinkedHashMap<>();

    public static MorphologyFeatureSet fromWord(ConWord word) {
        MorphologyFeatureSet ret = new MorphologyFeatureSet();

        if (word == null || word.getCore() == null) {
            return ret;
        }

        DictCore core = word.getCore();
        WordClass[] classes = core.getWordClassCollection().getClassesForType(word.getWordTypeId());

        for (WordClass wordClass : classes) {
            String key = wordClass.getValue();

            if (wordClass.isFreeText()) {
                String textValue = word.getClassTextValue(wordClass.getId());

                if (!textValue.isBlank()) {
                    ret.set(key, textValue);
                }

                continue;
            }

            Integer valueId = word.getClassValue(wordClass.getId());

            if (valueId == -1) {
                continue;
            }

            try {
                ret.set(key, wordClass.getValueById(valueId).getValue());
            } catch (Exception e) {
                ret.set(key, Integer.toString(valueId));
            }
        }

        return ret;
    }

    public MorphologyFeatureSet copy() {
        MorphologyFeatureSet ret = new MorphologyFeatureSet();
        ret.values.putAll(values);
        return ret;
    }

    public void set(String key, String value) {
        String normalized = normalize(key);

        if (normalized.isEmpty()) {
            return;
        }

        values.put(normalized, value == null ? "" : value);
    }

    public String get(String key) {
        return values.getOrDefault(normalize(key), "");
    }

    public boolean has(String key) {
        return values.containsKey(normalize(key));
    }

    public Set<Entry<String, String>> entrySet() {
        return values.entrySet();
    }

    public static String normalize(String key) {
        return key == null ? "" : key.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object comp) {
        boolean ret = false;

        if (this == comp) {
            ret = true;
        } else if (comp instanceof MorphologyFeatureSet featureSet) {
            ret = Objects.equals(values, featureSet.values);
        }

        return ret;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(values);
    }
}
