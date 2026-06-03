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

import java.util.Objects;

/**
 * Parsed representation of a sound change rule.
 *
 * @author draque
 */
public class SoundChangeRule {
    private final String rawRule;
    private final String fromValue;
    private final String toValue;
    private final String leftEnvironment;
    private final String rightEnvironment;

    public SoundChangeRule(String rawRule, String fromValue, String toValue,
            String leftEnvironment, String rightEnvironment) {
        this.rawRule = rawRule;
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.leftEnvironment = leftEnvironment;
        this.rightEnvironment = rightEnvironment;
    }

    public String getRawRule() {
        return rawRule;
    }

    public String getFromValue() {
        return fromValue;
    }

    public String getToValue() {
        return toValue;
    }

    public String getLeftEnvironment() {
        return leftEnvironment;
    }

    public String getRightEnvironment() {
        return rightEnvironment;
    }

    @Override
    public boolean equals(Object comp) {
        boolean ret = false;

        if (this == comp) {
            ret = true;
        } else if (comp instanceof SoundChangeRule rule) {
            ret = rawRule.equals(rule.rawRule);
            ret = ret && fromValue.equals(rule.fromValue);
            ret = ret && toValue.equals(rule.toValue);
            ret = ret && leftEnvironment.equals(rule.leftEnvironment);
            ret = ret && rightEnvironment.equals(rule.rightEnvironment);
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(rawRule);
        hash = 43 * hash + Objects.hashCode(fromValue);
        hash = 43 * hash + Objects.hashCode(toValue);
        hash = 43 * hash + Objects.hashCode(leftEnvironment);
        hash = 43 * hash + Objects.hashCode(rightEnvironment);
        return hash;
    }
}
