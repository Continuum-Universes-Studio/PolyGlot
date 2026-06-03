/*
 * Copyright (c) 2026, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT Licence
 * See LICENSE.TXT included with this code to read the full license agreement.
 */
package org.darisadesigns.polyglotlina.Nodes;

import java.util.Locale;

/**
 * Relation type for word-level cross-language links.
 *
 * @author draque
 */
public enum LexicalRelationType {
    LOANWORD,
    INHERITED,
    COGNATE,
    RELATED;

    public static LexicalRelationType fromSerializedValue(String value) {
        if (value == null || value.isBlank()) {
            return RELATED;
        }

        try {
            return LexicalRelationType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return RELATED;
        }
    }
}
