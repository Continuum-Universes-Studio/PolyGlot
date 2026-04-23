package org.darisadesigns.polyglotlina.Validation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.darisadesigns.polyglotlina.DictCore;

/**
 * Entry point for modular consistency validation.
 */
public class ConsistencyChecker {
    private final List<ConsistencyValidator> validators = List.of(
            new PhonemeInventoryValidator(),
            new PhonotacticsValidator(),
            new OrthographyValidator(),
            new MorphologyValidator(),
            new DerivationValidator(),
            new LinkedLanguageValidator(),
            new LoanwordValidator(),
            new EvolutionValidator());

    public List<ConsistencyIssue> check(DictCore core) {
        List<ConsistencyIssue> issues = new ArrayList<>();
        ConsistencyCheckContext context = new ConsistencyCheckContext(core);

        for (ConsistencyValidator validator : validators) {
            validator.validate(context, issues);
        }

        issues.sort(Comparator
                .comparing(ConsistencyIssue::getSeverity)
                .reversed()
                .thenComparing(ConsistencyIssue::getCode)
                .thenComparing(issue -> issue.getAffectedNode() == null
                        ? "" : issue.getAffectedNode().getValue()));

        return issues;
    }
}
