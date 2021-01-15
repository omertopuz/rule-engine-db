package com.rules.service;

import com.rules.model.KieSessionModel;
import com.rules.model.entity.RuleContent;
import com.rules.repository.RuleRepository;
import lombok.extern.java.Log;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Log
@Service
public class RuleService {

    @Autowired
    private RuleRepository ruleRepository;

    private List<RuleContent> allRules;
    private List<KieSessionModel> allRuleSessions;

    private KieServices kieServices;

    @PostConstruct
    private void loadAllRules(){
        kieServices = KieServices.Factory.get();
        allRules = ruleRepository.findAll();
        allRuleSessions = new ArrayList<>();
        allRules.forEach(r->{
            KieContainer kc = loadContainerFromString(r.getRuleContent());
            KieSession ks = kc.newKieSession();
            allRuleSessions.add(new KieSessionModel(ks,r));
        });

    }

    public <T> void fireRule(int ruleId, T rulableObject){
        allRuleSessions.stream().filter(p->p.getRuleContent().getRuleId() == ruleId)
                .findFirst()
        .ifPresent(r->{
            r.getKieSession().insert(rulableObject);
            int ruleCount = r.getKieSession().fireAllRules();
            log.info("ruleCount: " + ruleCount);
        });
    }

    public KieContainer loadContainerFromString(String ruleStr) {

        KieRepository kr = kieServices.getRepository();
        KieFileSystem kfs = kieServices.newKieFileSystem();

        kfs.write("src/main/resources/" + ruleStr.hashCode() + ".drl", ruleStr);

        KieBuilder kb = kieServices.newKieBuilder(kfs);
        kb.buildAll();

        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }
        KieContainer kContainer = kieServices.newKieContainer(kr.getDefaultReleaseId());

        return kContainer;
    }
}
