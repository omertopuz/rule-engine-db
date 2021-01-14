package com.rules.config;

import com.rules.model.entity.RuleContent;
import com.rules.repository.RuleRepository;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

@Configuration
public class DroolsConfig {

    private KieServices kieServices = KieServices.Factory.get();

    @Autowired
    private RuleRepository ruleRepository;

    @Bean
    public KieContainer loadContainerFromString() {
        KieRepository kr = kieServices.getRepository();
        KieFileSystem kfs = kieServices.newKieFileSystem();

        List<RuleContent> allRules = ruleRepository.findAll();

        for (RuleContent r: allRules) {
            kfs.write("src/main/resources/" + r.getRuleName().hashCode() + ".drl", r.getRuleContent());
        }


        KieBuilder kb = kieServices.newKieBuilder(kfs);

        kb.buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }
        KieContainer kContainer = kieServices.newKieContainer(kr.getDefaultReleaseId());
        return kContainer;
    }

    @Bean
    public KieSession getKieSession() throws IOException {
        System.out.println("session created...");
        return loadContainerFromString().newKieSession();

    }

}
