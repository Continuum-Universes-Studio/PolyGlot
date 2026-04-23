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

import java.util.Objects;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Persistent configuration for the Zompist word generator.
 *
 * @author draque
 */
public class GeneratorSettings {
    public static final int DEFAULT_DROPOFF_RATE = 31;
    public static final int DEFAULT_MONOSYLLABLE_FREQUENCY = 15;
    public static final String DEFAULT_GENERATION_COUNT = "150";
    public static final int PRESET_COUNT = 6;
    private static final String DEFAULT_PHONOTACTIC_CONSTRAINTS = "pw\nfw\nbw\ntl\ndl\nθl\ngw\nmb\nmv";

    private String categories = "";
    private String illegalClusters = "";
    private String rewriteRules = "";
    private String syllableTypes = "";
    private int dropoffRate = DEFAULT_DROPOFF_RATE;
    private int monosyllableFrequency = DEFAULT_MONOSYLLABLE_FREQUENCY;
    private boolean showSyllables = false;
    private boolean slowSyllableDropoff = false;
    private String generationCount = DEFAULT_GENERATION_COUNT;
    private boolean generateWords = true;

    public GeneratorSettings() {
    }

    public GeneratorSettings(GeneratorSettings generatorSettings) {
        apply(generatorSettings);
    }

    public final void apply(GeneratorSettings generatorSettings) {
        if (generatorSettings == null) {
            categories = "";
            illegalClusters = "";
            rewriteRules = "";
            syllableTypes = "";
            dropoffRate = DEFAULT_DROPOFF_RATE;
            monosyllableFrequency = DEFAULT_MONOSYLLABLE_FREQUENCY;
            showSyllables = false;
            slowSyllableDropoff = false;
            generationCount = DEFAULT_GENERATION_COUNT;
            generateWords = true;
            return;
        }

        categories = generatorSettings.categories;
        illegalClusters = generatorSettings.illegalClusters;
        rewriteRules = generatorSettings.rewriteRules;
        syllableTypes = generatorSettings.syllableTypes;
        dropoffRate = generatorSettings.dropoffRate;
        monosyllableFrequency = generatorSettings.monosyllableFrequency;
        showSyllables = generatorSettings.showSyllables;
        slowSyllableDropoff = generatorSettings.slowSyllableDropoff;
        generationCount = generatorSettings.generationCount;
        generateWords = generatorSettings.generateWords;
    }

    public boolean hasStoredRules() {
        return !categories.isBlank()
                || !illegalClusters.isBlank()
                || !rewriteRules.isBlank()
                || !syllableTypes.isBlank();
    }

    public void applyDefaultPreset(int presetIndex) {
        switch (presetIndex) {
            case 0 -> {
                // Large inventory
                categories = "C=ptknslrmbdgfvwyhšzñxčžŋ\nV=aiuoeɛɔâôüö\nR=rly";
                syllableTypes = "CV\nV\nCVC\nCRV";
                rewriteRules = "â|ai\nô|au";
                illegalClusters = DEFAULT_PHONOTACTIC_CONSTRAINTS;
            }
            case 1 -> {
                // Latinate
                categories = "C=tkpnslrmfbdghvyh\nV=aiueo\nU=aiuôê\nR=rl\nM=nsrmltc\nK=ptkbdg";
                syllableTypes = "CV\nCUM\nV\nUM\nKRV\nKRUM";
                rewriteRules = "ka|ca\nko|co\nku|cu\nkr|cr";
                illegalClusters = DEFAULT_PHONOTACTIC_CONSTRAINTS;
            }
            case 2 -> {
                // Simple
                categories = "C=tpknlrsmʎbdgñfh\nV=aieuoāīūēō\nN=nŋ";
                syllableTypes = "CV\nV\nCVN";
                rewriteRules = "aa|ā\nii|ī\nuu|ū\nee|ē\noo|ō\nnb|mb\nnp|mp";
                illegalClusters = DEFAULT_PHONOTACTIC_CONSTRAINTS;
            }
            case 3 -> {
                // Chinese
                categories = "C=ptknlsmšywčhfŋ\nV=auieo\nR=rly\nN=nnŋmktp\nW=io\nQ=ptkč";
                syllableTypes = "CV\nQʰV\nCVW\nCVN\nVN\nV\nQʰVN";
                rewriteRules = "uu|wo\noo|ou\nii|iu\naa|ia\nee|ie";
                illegalClusters = DEFAULT_PHONOTACTIC_CONSTRAINTS;
            }
            case 4 -> {
                // Original default
                categories = "C=ptkbdg\nR=rl\nV=ieaou";
                syllableTypes = "CV\nV\nCRV";
                rewriteRules = "ki|či";
                illegalClusters = DEFAULT_PHONOTACTIC_CONSTRAINTS;
            }
            case 5 -> {
                // Japanese-like
                categories = "C=tknsmrh\n"
                        + "V=aioeu\n"
                        + "U=auoāēū\n"
                        + "L=āīōēū";
                syllableTypes = "CV\n"
                        + "CVn\n"
                        + "CL\n"
                        + "CLn\n"
                        + "CyU\n"
                        + "CyUn\n"
                        + "Vn\n"
                        + "Ln\n"
                        + "CVq\n"
                        + "CLq\n"
                        + "yU\n"
                        + "yUn\n"
                        + "wa\n"
                        + "L\n"
                        + "V";
                rewriteRules = "hu|fu\n"
                        + "hū|fū\n"
                        + "si|shi\n"
                        + "sī|shī\n"
                        + "sy|sh\n"
                        + "ti|chi\n"
                        + "tī|chī\n"
                        + "ty|ch\n"
                        + "tu|tsu\n"
                        + "tū|tsū\n"
                        + "qk|kk\n"
                        + "qp|pp\n"
                        + "qt|tt\n"
                        + "q[^ptk]|";
                illegalClusters = DEFAULT_PHONOTACTIC_CONSTRAINTS;
            }
            default -> applyDefaultPreset(0);
        }

        generationCount = DEFAULT_GENERATION_COUNT;
        generateWords = true;
        showSyllables = false;
        slowSyllableDropoff = false;
    }

    public void writeXML(Document doc, Element rootElement) {
        appendXmlValue(doc, rootElement, PGTUtil.LANG_PROP_ZOMPIST_CATEGORIES, categories);
        appendXmlValue(doc, rootElement, PGTUtil.LANG_PROP_ZOMPIST_ILLEGAL_CLUSTERS, illegalClusters);
        appendXmlValue(doc, rootElement, PGTUtil.LANG_PROP_ZOMPIST_REWRITE_RULES, rewriteRules);
        appendXmlValue(doc, rootElement, PGTUtil.LANG_PROP_ZOMPIST_SYLLABLES, syllableTypes);
        appendXmlValue(doc, rootElement, PGTUtil.LANG_PROP_ZOMPIST_DROPOFF_RATE, Integer.toString(dropoffRate));
        appendXmlValue(doc, rootElement, PGTUtil.LANG_PROP_ZOMPIST_MONOSYLLABLE_FREQUENCY, Integer.toString(monosyllableFrequency));
        appendXmlValue(doc, rootElement, PGTUtil.LANG_PROP_ZOMPIST_SHOW_SYLLABLES, showSyllables ? PGTUtil.TRUE : PGTUtil.FALSE);
        appendXmlValue(doc, rootElement, PGTUtil.LANG_PROP_ZOMPIST_SLOW_SYLLABLE_DROPOFF, slowSyllableDropoff ? PGTUtil.TRUE : PGTUtil.FALSE);
        appendXmlValue(doc, rootElement, PGTUtil.LANG_PROP_ZOMPIST_GENERATION_COUNT, generationCount);
        appendXmlValue(doc, rootElement, PGTUtil.LANG_PROP_ZOMPIST_GENERATE_WORDS, generateWords ? PGTUtil.TRUE : PGTUtil.FALSE);
    }

    private void appendXmlValue(Document doc, Element rootElement, String elementName, String value) {
        Element wordValue = doc.createElement(elementName);
        wordValue.appendChild(doc.createTextNode(value == null ? "" : value));
        rootElement.appendChild(wordValue);
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories == null ? "" : categories;
    }

    public String getIllegalClusters() {
        return illegalClusters;
    }

    public void setIllegalClusters(String illegalClusters) {
        this.illegalClusters = illegalClusters == null ? "" : illegalClusters;
    }

    public String getRewriteRules() {
        return rewriteRules;
    }

    public void setRewriteRules(String rewriteRules) {
        this.rewriteRules = rewriteRules == null ? "" : rewriteRules;
    }

    public String getSyllableTypes() {
        return syllableTypes;
    }

    public void setSyllableTypes(String syllableTypes) {
        this.syllableTypes = syllableTypes == null ? "" : syllableTypes;
    }

    public int getDropoffRate() {
        return dropoffRate;
    }

    public void setDropoffRate(int dropoffRate) {
        this.dropoffRate = dropoffRate < 0 || dropoffRate > 45
                ? DEFAULT_DROPOFF_RATE
                : dropoffRate;
    }

    public int getMonosyllableFrequency() {
        return monosyllableFrequency;
    }

    public void setMonosyllableFrequency(int monosyllableFrequency) {
        this.monosyllableFrequency = monosyllableFrequency < 1 || monosyllableFrequency > 85
                ? DEFAULT_MONOSYLLABLE_FREQUENCY
                : monosyllableFrequency;
    }

    public boolean isShowSyllables() {
        return showSyllables;
    }

    public void setShowSyllables(boolean showSyllables) {
        this.showSyllables = showSyllables;
    }

    public boolean isSlowSyllableDropoff() {
        return slowSyllableDropoff;
    }

    public void setSlowSyllableDropoff(boolean slowSyllableDropoff) {
        this.slowSyllableDropoff = slowSyllableDropoff;
    }

    public String getGenerationCount() {
        return generationCount;
    }

    public void setGenerationCount(String generationCount) {
        this.generationCount = generationCount == null
                ? DEFAULT_GENERATION_COUNT
                : generationCount;
    }

    public boolean isGenerateWords() {
        return generateWords;
    }

    public void setGenerateWords(boolean generateWords) {
        this.generateWords = generateWords;
    }

    @Override
    public boolean equals(Object comp) {
        boolean ret = false;

        if (this == comp) {
            ret = true;
        } else if (comp instanceof GeneratorSettings settings) {
            ret = safeTrim(categories).equals(safeTrim(settings.categories));
            ret = ret && safeTrim(illegalClusters).equals(safeTrim(settings.illegalClusters));
            ret = ret && safeTrim(rewriteRules).equals(safeTrim(settings.rewriteRules));
            ret = ret && safeTrim(syllableTypes).equals(safeTrim(settings.syllableTypes));
            ret = ret && dropoffRate == settings.dropoffRate;
            ret = ret && monosyllableFrequency == settings.monosyllableFrequency;
            ret = ret && showSyllables == settings.showSyllables;
            ret = ret && slowSyllableDropoff == settings.slowSyllableDropoff;
            ret = ret && safeTrim(generationCount).equals(safeTrim(settings.generationCount));
            ret = ret && generateWords == settings.generateWords;
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(safeTrim(categories));
        hash = 79 * hash + Objects.hashCode(safeTrim(illegalClusters));
        hash = 79 * hash + Objects.hashCode(safeTrim(rewriteRules));
        hash = 79 * hash + Objects.hashCode(safeTrim(syllableTypes));
        hash = 79 * hash + dropoffRate;
        hash = 79 * hash + monosyllableFrequency;
        hash = 79 * hash + (showSyllables ? 1 : 0);
        hash = 79 * hash + (slowSyllableDropoff ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(safeTrim(generationCount));
        hash = 79 * hash + (generateWords ? 1 : 0);
        return hash;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
