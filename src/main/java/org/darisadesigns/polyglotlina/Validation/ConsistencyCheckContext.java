package org.darisadesigns.polyglotlina.Validation;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.LinkedLanguage;
import org.darisadesigns.polyglotlina.Nodes.LinkedWordReference;
import org.darisadesigns.polyglotlina.Desktop.DesktopGrammarManager;
import org.darisadesigns.polyglotlina.Desktop.DesktopPropertiesManager;

/**
 * Shared validation context with lazy loading for linked languages.
 */
public class ConsistencyCheckContext {
    private final DictCore core;
    private final Map<String, DictCore> loadedLinkedCores = new HashMap<>();

    public ConsistencyCheckContext(DictCore core) {
        this.core = core;
    }

    public DictCore getCore() {
        return core;
    }

    public DictCore loadLinkedCore(LinkedWordReference reference) {
        if (reference == null) {
            return null;
        }

        String resolvedPath = reference.getResolvedTargetFile(core);
        if (resolvedPath.isBlank()) {
            return null;
        }

        if (loadedLinkedCores.containsKey(resolvedPath)) {
            return loadedLinkedCores.get(resolvedPath);
        }

        DictCore linkedCore = loadCore(resolvedPath);
        loadedLinkedCores.put(resolvedPath, linkedCore);
        return linkedCore;
    }

    public DictCore loadLinkedCore(LinkedLanguage linkedLanguage) {
        if (linkedLanguage == null) {
            return null;
        }

        return loadCore(linkedLanguage.getResolvedTargetFile(core));
    }

    public LinkedLanguage findLinkedLanguage(String resolvedPath) {
        if (core == null || resolvedPath == null || resolvedPath.isBlank()) {
            return null;
        }

        String normalizedResolvedPath = normalizePath(resolvedPath);
        List<LinkedLanguage> linkedLanguages = core.getPropertiesManager().getLinkedLanguages();
        for (LinkedLanguage linkedLanguage : linkedLanguages) {
            String candidate = normalizePath(linkedLanguage.getResolvedTargetFile(core));
            if (normalizedResolvedPath.equals(candidate)) {
                return linkedLanguage;
            }
        }

        return null;
    }

    private DictCore loadCore(String path) {
        if (path == null || path.isBlank() || core == null) {
            return null;
        }

        try {
            DictCore linkedCore = new DictCore(new DesktopPropertiesManager(),
                    core.getOSHandler(),
                    new org.darisadesigns.polyglotlina.Desktop.PGTUtil(),
                    new DesktopGrammarManager());
            linkedCore.readFile(path);
            return linkedCore;
        } catch (IOException | IllegalStateException | ParserConfigurationException e) {
            // Validation should degrade gracefully if the referenced file cannot be loaded.
            return null;
        }
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }

        String normalized;
        try {
            normalized = Paths.get(path).normalize().toString();
        } catch (Exception e) {
            normalized = path.trim();
        }

        if (System.getProperty("os.name").startsWith("Windows")) {
            normalized = normalized.toLowerCase();
        }

        return normalized;
    }
}
