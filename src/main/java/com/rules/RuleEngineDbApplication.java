package com.rules;

import com.rules.config.DroolsCustomEventListener;
import com.rules.model.RuleInventory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RuleEngineDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(RuleEngineDbApplication.class, args);
	}

	@Bean
	public RuleInventory getRuleContext(){
		return new RuleInventory(new DroolsCustomEventListener());
	}

}
