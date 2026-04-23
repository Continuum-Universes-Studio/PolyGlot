package org.darisadesigns.polyglotlina.Validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode.ProblemType;
import org.darisadesigns.polyglotlina.Nodes.MorphologyCondition;
import org.darisadesigns.polyglotlina.Nodes.MorphologyConditionOperator;
import org.darisadesigns.polyglotlina.Nodes.MorphologyOperationType;
import org.darisadesigns.polyglotlina.Nodes.MorphologyRule;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.RegexTools;

/**
 * Checks basic morphology rule sanity.
 */
public class MorphologyValidator extends AbstractConsistencyValidator {
    @Override
    public void validate(ConsistencyCheckContext context, List<ConsistencyIssue> issues) {
        if (context == null || context.getCore() == null) {
            return;
        }

        var core = context.getCore();
        var conjMan = core.getConjugationManager();
        Map<String, MorphologyRule> seenOrders = new HashMap<>();

        for (TypeNode type : core.getTypes().getAllValues()) {
            MorphologyRule[] rules = conjMan.getMorphologyRulesForType(type.getId());
            if (rules.length == 0) {
                continue;
            }

            for (MorphologyRule rule : rules) {
                validateRule(type, rule, conjMan, seenOrders, issues);
            }
        }

        // Future validation can simulate morphology application per paradigm
        // and compare the generated forms against stored inflection data.
    }

    private void validateRule(TypeNode type, MorphologyRule rule, 
            org.darisadesigns.polyglotlina.ManagersCollections.ConjugationManager conjMan,
            Map<String, MorphologyRule> seenOrders, List<ConsistencyIssue> issues) {
        if (rule.getName().isBlank()) {
            issues.add(issue(
                    ConsistencySeverity.INFO,
                    "morphology.rule.unnamed",
                    "Morphology rule has no display name.",
                    type,
                    ProblemType.PoS,
                    "Rule id " + rule.getId() + " for target \"" + rule.getTargetKey() + "\"",
                    "Give the rule a label so it is easier to audit later."));
        }

        String[] targets = conjMan.getMorphologyTargets(type.getId());
        if (targets != null && targets.length > 0
                && rule.getTargetKey() != null
                && rule.getTargetKey().equals(conjMan.getMorphologyTargetLabel(type.getId(), rule.getTargetKey()))
                && !conjMan.isMorphologyBuiltInTarget(rule.getTargetKey())) {
            issues.add(issue(
                    ConsistencySeverity.ERROR,
                    "morphology.rule.invalid_target",
                    "Morphology rule points at an unknown target.",
                    type,
                    ProblemType.PoS,
                    "Target key: " + rule.getTargetKey(),
                    "Choose a valid lemma, stem, or combined morphology target."));
        }

        String orderKey = type.getId() + ":" + rule.getTargetKey() + ":" + rule.getOrder();
        if (seenOrders.containsKey(orderKey)) {
            issues.add(issue(
                    ConsistencySeverity.WARNING,
                    "morphology.rule.duplicate_order",
                    "Two morphology rules share the same ordering slot.",
                    type,
                    ProblemType.PoS,
                    "Conflicting rules: " + seenOrders.get(orderKey).getName() + " / " + rule.getName(),
                    "Adjust the rule order so the application sequence is explicit."));
        } else {
            seenOrders.put(orderKey, rule);
        }

        if (rule.getOperationType() == MorphologyOperationType.append_suffix
                || rule.getOperationType() == MorphologyOperationType.prepend_prefix
                || rule.getOperationType() == MorphologyOperationType.replace_literal
                || rule.getOperationType() == MorphologyOperationType.replace_regex
                || rule.getOperationType() == MorphologyOperationType.delete_regex) {
            if (rule.getValue1().isBlank()) {
                issues.add(issue(
                        ConsistencySeverity.ERROR,
                        "morphology.rule.blank_input",
                        "Morphology rule is missing its source value.",
                        type,
                        ProblemType.PoS,
                        "Rule: " + rule.getName(),
                        "Provide the affix or source pattern that this rule should use."));
            }
        }

        if (rule.getOperationType() == MorphologyOperationType.replace_regex
                || rule.getOperationType() == MorphologyOperationType.delete_regex) {
            if (!RegexTools.isRegexLegal(rule.getValue1())) {
                issues.add(issue(
                        ConsistencySeverity.ERROR,
                        "morphology.rule.invalid_regex",
                        "Morphology rule uses an invalid regex pattern.",
                        type,
                        ProblemType.PoS,
                        "Rule: " + rule.getName() + "\nPattern: " + rule.getValue1(),
                        "Correct the regex pattern before saving."));
            }
        }

        if (rule.getOperationType() == MorphologyOperationType.set_feature
                && (rule.getValue1().isBlank() || rule.getValue2().isBlank())) {
            issues.add(issue(
                    ConsistencySeverity.WARNING,
                    "morphology.rule.incomplete_feature",
                    "Feature-setting rule is missing a key or value.",
                    type,
                    ProblemType.PoS,
                    "Rule: " + rule.getName(),
                    "Provide both the feature name and the feature value."));
        }

        if (rule.getOperationType() == MorphologyOperationType.copy_form && rule.getValue1().isBlank()) {
            issues.add(issue(
                    ConsistencySeverity.INFO,
                    "morphology.rule.copy_default",
                    "Copy-form rule will default to the lemma field.",
                    type,
                    ProblemType.PoS,
                    "Rule: " + rule.getName(),
                    "This is only a notice; the rule is still valid."));
        }

        for (MorphologyCondition condition : rule.getConditions()) {
            if (condition.getOperator() == MorphologyConditionOperator.matches_regex
                    && !RegexTools.isRegexLegal(condition.getValue())) {
                issues.add(issue(
                        ConsistencySeverity.ERROR,
                        "morphology.rule.invalid_condition_regex",
                        "Morphology rule condition uses an invalid regex.",
                        type,
                        ProblemType.PoS,
                        "Rule: " + rule.getName() + "\nCondition field: " + condition.getFieldName(),
                        "Correct the condition regex before saving."));
            }
        }
    }
}
