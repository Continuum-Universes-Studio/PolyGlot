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

import java.util.ArrayList;
import java.util.List;
import org.darisadesigns.polyglotlina.ManagersCollections.PropertiesManager;

/**
 * Temporary state for the language/project creation wizard.
 */
public class LanguageCreationWizardModel {
    public enum PresetTemplate {
        BLANK_LANGUAGE,
        IPA_FIRST_LANGUAGE,
        HISTORICAL_DAUGHTER_LANGUAGE,
        SCRIPT_HEAVY_LANGUAGE
    }

    private String languageName = "";
    private String displayName = "";
    private String notes = "";
    private boolean useIpa = true;
    private boolean useRomanized = true;
    private boolean useRendered = true;
    private DisplayMode defaultDisplayMode = DisplayMode.RENDERED;
    private String customFontPath = "";
    private final GeneratorSettings generatorSettings = new GeneratorSettings();
    private boolean simplifiedConjugations = false;
    private boolean typesMandatory = false;
    private boolean localMandatory = false;
    private boolean wordUniqueness = false;
    private boolean localUniqueness = false;
    private final List<LinkedLanguage> linkedLanguages = new ArrayList<>();
    private final LanguageEvolutionProfile evolutionProfile = new LanguageEvolutionProfile();

    public void applyPreset(PresetTemplate preset) {
        String preservedLanguageName = languageName;
        String preservedDisplayName = displayName;
        String preservedNotes = notes;
        String preservedCustomFontPath = customFontPath;
        List<LinkedLanguage> preservedLinkedLanguages = getLinkedLanguages();
        LanguageEvolutionProfile preservedProfile = new LanguageEvolutionProfile(evolutionProfile);

        reset();

        languageName = preservedLanguageName;
        displayName = preservedDisplayName;
        notes = preservedNotes;
        customFontPath = preservedCustomFontPath;
        setLinkedLanguages(preservedLinkedLanguages);
        setEvolutionProfile(preservedProfile);

        if (preset == null) {
            return;
        }

        switch (preset) {
            case IPA_FIRST_LANGUAGE -> {
                useIpa = true;
                useRomanized = false;
                useRendered = true;
                defaultDisplayMode = DisplayMode.IPA;
                generatorSettings.applyDefaultPreset(4);
            }
            case HISTORICAL_DAUGHTER_LANGUAGE -> {
                useIpa = true;
                useRomanized = true;
                useRendered = true;
                defaultDisplayMode = DisplayMode.ROMANIZED;
                simplifiedConjugations = true;
                typesMandatory = true;
                generatorSettings.applyDefaultPreset(1);
            }
            case SCRIPT_HEAVY_LANGUAGE -> {
                useIpa = false;
                useRomanized = false;
                useRendered = true;
                defaultDisplayMode = DisplayMode.RENDERED;
                typesMandatory = true;
                localMandatory = true;
                generatorSettings.applyDefaultPreset(0);
            }
            case BLANK_LANGUAGE -> {
                generatorSettings.applyDefaultPreset(4);
            }
        }
    }

    public void reset() {
        languageName = "";
        displayName = "";
        notes = "";
        useIpa = true;
        useRomanized = true;
        useRendered = true;
        defaultDisplayMode = DisplayMode.RENDERED;
        customFontPath = "";
        generatorSettings.applyDefaultPreset(4);
        simplifiedConjugations = false;
        typesMandatory = false;
        localMandatory = false;
        wordUniqueness = false;
        localUniqueness = false;
        linkedLanguages.clear();
        evolutionProfile.clear();
    }

    public boolean isReadyForFinish() {
        return !languageName.isBlank();
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName == null ? "" : languageName.trim();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName == null ? "" : displayName.trim();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes.trim();
    }

    public boolean isUseIpa() {
        return useIpa;
    }

    public void setUseIpa(boolean useIpa) {
        this.useIpa = useIpa;
    }

    public boolean isUseRomanized() {
        return useRomanized;
    }

    public void setUseRomanized(boolean useRomanized) {
        this.useRomanized = useRomanized;
    }

    public boolean isUseRendered() {
        return useRendered;
    }

    public void setUseRendered(boolean useRendered) {
        this.useRendered = useRendered;
    }

    public DisplayMode getDefaultDisplayMode() {
        return defaultDisplayMode == null ? DisplayMode.RENDERED : defaultDisplayMode;
    }

    public void setDefaultDisplayMode(DisplayMode defaultDisplayMode) {
        this.defaultDisplayMode = defaultDisplayMode == null
                ? DisplayMode.RENDERED
                : defaultDisplayMode;
    }

    public String getCustomFontPath() {
        return customFontPath;
    }

    public void setCustomFontPath(String customFontPath) {
        this.customFontPath = customFontPath == null ? "" : customFontPath.trim();
    }

    public GeneratorSettings getGeneratorSettings() {
        return generatorSettings;
    }

    public boolean isSimplifiedConjugations() {
        return simplifiedConjugations;
    }

    public void setSimplifiedConjugations(boolean simplifiedConjugations) {
        this.simplifiedConjugations = simplifiedConjugations;
    }

    public boolean isTypesMandatory() {
        return typesMandatory;
    }

    public void setTypesMandatory(boolean typesMandatory) {
        this.typesMandatory = typesMandatory;
    }

    public boolean isLocalMandatory() {
        return localMandatory;
    }

    public void setLocalMandatory(boolean localMandatory) {
        this.localMandatory = localMandatory;
    }

    public boolean isWordUniqueness() {
        return wordUniqueness;
    }

    public void setWordUniqueness(boolean wordUniqueness) {
        this.wordUniqueness = wordUniqueness;
    }

    public boolean isLocalUniqueness() {
        return localUniqueness;
    }

    public void setLocalUniqueness(boolean localUniqueness) {
        this.localUniqueness = localUniqueness;
    }

    public List<LinkedLanguage> getLinkedLanguages() {
        List<LinkedLanguage> ret = new ArrayList<>();

        for (LinkedLanguage linkedLanguage : linkedLanguages) {
            ret.add(new LinkedLanguage(linkedLanguage));
        }

        return ret;
    }

    public void setLinkedLanguages(List<LinkedLanguage> linkedLanguages) {
        this.linkedLanguages.clear();

        if (linkedLanguages != null) {
            for (LinkedLanguage linkedLanguage : linkedLanguages) {
                addLinkedLanguage(linkedLanguage);
            }
        }
    }

    public void addLinkedLanguage(LinkedLanguage linkedLanguage) {
        if (linkedLanguage != null && linkedLanguage.isValid()) {
            linkedLanguages.add(new LinkedLanguage(linkedLanguage));
        }
    }

    public void removeLinkedLanguage(int index) {
        if (index > -1 && index < linkedLanguages.size()) {
            linkedLanguages.remove(index);
        }
    }

    public LanguageEvolutionProfile getEvolutionProfile() {
        return evolutionProfile;
    }

    public void setEvolutionProfile(LanguageEvolutionProfile profile) {
        evolutionProfile.clear();

        if (profile == null) {
            return;
        }

        evolutionProfile.setParentLanguagePath(profile.getParentLanguagePath());
        evolutionProfile.setCachedParentLanguageName(profile.getCachedParentLanguageName());
        evolutionProfile.setSoundChangeRules(profile.getSoundChangeRules());
        evolutionProfile.setRelatedLanguages(profile.getRelatedLanguages());
    }

    public void addRelatedLanguage(LanguageRelation relation) {
        evolutionProfile.addRelatedLanguage(relation);
    }

    public void removeRelatedLanguage(int index) {
        evolutionProfile.removeRelatedLanguage(index);
    }

    public void applyDefaultsTo(PropertiesManager propertiesManager) {
        if (propertiesManager == null) {
            return;
        }

        propertiesManager.setLangName(languageName);
        propertiesManager.setLocalLangName(displayName.isBlank() ? languageName : displayName);
        propertiesManager.setProjectNotes(notes);
        propertiesManager.setLexiconDisplayMode(getDefaultDisplayMode());
        propertiesManager.setUseSimplifiedConjugations(simplifiedConjugations);
        propertiesManager.setTypesMandatory(typesMandatory);
        propertiesManager.setLocalMandatory(localMandatory);
        propertiesManager.setWordUniqueness(wordUniqueness);
        propertiesManager.setLocalUniqueness(localUniqueness);
        propertiesManager.getGeneratorSettings().apply(generatorSettings);
        propertiesManager.getGeneratorSettings().setSyllableTypes(generatorSettings.getSyllableTypes());
        propertiesManager.getGeneratorSettings().setCategories(generatorSettings.getCategories());
        propertiesManager.getGeneratorSettings().setIllegalClusters(generatorSettings.getIllegalClusters());
        propertiesManager.getGeneratorSettings().setRewriteRules(generatorSettings.getRewriteRules());
        propertiesManager.getGeneratorSettings().setDropoffRate(generatorSettings.getDropoffRate());
        propertiesManager.getGeneratorSettings().setMonosyllableFrequency(generatorSettings.getMonosyllableFrequency());
        propertiesManager.getGeneratorSettings().setShowSyllables(generatorSettings.isShowSyllables());
        propertiesManager.getGeneratorSettings().setSlowSyllableDropoff(generatorSettings.isSlowSyllableDropoff());
        propertiesManager.getGeneratorSettings().setGenerationCount(generatorSettings.getGenerationCount());
        propertiesManager.getGeneratorSettings().setGenerateWords(generatorSettings.isGenerateWords());
        propertiesManager.setLinkedLanguages(getLinkedLanguages());
        propertiesManager.getLanguageEvolutionProfile().clear();
        propertiesManager.getLanguageEvolutionProfile().setParentLanguagePath(evolutionProfile.getParentLanguagePath());
        propertiesManager.getLanguageEvolutionProfile().setCachedParentLanguageName(evolutionProfile.getCachedParentLanguageName());
        propertiesManager.getLanguageEvolutionProfile().setSoundChangeRules(evolutionProfile.getSoundChangeRules());
        propertiesManager.getLanguageEvolutionProfile().setRelatedLanguages(evolutionProfile.getRelatedLanguages());
    }
}
