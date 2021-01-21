package com.rules.config;

import lombok.extern.java.Log;
import org.kie.api.event.rule.*;

@Log
public class DroolsCustomEventListener implements AgendaEventListener {
    @Override
    public void matchCreated(MatchCreatedEvent matchCreatedEvent) {

    }

    @Override
    public void matchCancelled(MatchCancelledEvent matchCancelledEvent) {

    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent beforeMatchFiredEvent) {

    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent afterMatchFiredEvent) {
        log.info("Rule fired: " + afterMatchFiredEvent.getMatch().getRule().getName());
    }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent agendaGroupPoppedEvent) {

    }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent agendaGroupPushedEvent) {

    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent ruleFlowGroupActivatedEvent) {

    }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent ruleFlowGroupActivatedEvent) {

    }

    @Override
    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent ruleFlowGroupDeactivatedEvent) {

    }

    @Override
    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent ruleFlowGroupDeactivatedEvent) {

    }
}
