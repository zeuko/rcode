package edu.agh.iwium.robots;

import org.jruleengine.RuleServiceProviderImpl;

import javax.rules.*;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RulesDispatcher {

    private StatelessRuleSession statelessRuleSession;

    public RulesDispatcher(String ruleFile) {
        this.statelessRuleSession = createRuleSession(ruleFile);
    }

    public List handleEvent(Object... inputObjects) {
        try {
            return statelessRuleSession.executeRules(Arrays.asList(inputObjects));
        } catch (InvalidRuleSessionException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private StatelessRuleSession createRuleSession(final String ruleFile) {
        try (InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream("edu/agh/iwium/robots/" + ruleFile)) {
            Class.forName(RuleServiceProviderImpl.class.getName());
            RuleServiceProvider ruleServiceProvider = RuleServiceProviderManager.getRuleServiceProvider("org.jruleengine");
            RuleAdministrator ruleAdministrator = ruleServiceProvider.getRuleAdministrator();
            RuleExecutionSet ruleExecutionSet = ruleAdministrator.getLocalRuleExecutionSetProvider(null).createRuleExecutionSet(fileInputStream, null);
            ruleAdministrator.registerRuleExecutionSet(ruleExecutionSet.getName(), ruleExecutionSet, null);
            RuleRuntime ruleRuntime = ruleServiceProvider.getRuleRuntime();
            return (StatelessRuleSession) ruleRuntime.createRuleSession(ruleExecutionSet.getName(), new HashMap(), RuleRuntime.STATELESS_SESSION_TYPE);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
