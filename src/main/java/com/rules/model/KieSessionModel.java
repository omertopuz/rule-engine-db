package com.rules.model;

import com.rules.model.entity.RuleContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.kie.api.runtime.KieSession;

@Getter
@Data
@AllArgsConstructor
public final class KieSessionModel {

    private final KieSession kieSession;
    private final RuleContent ruleContent;

}
