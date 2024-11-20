import java.util.ArrayList;
import java.util.List;

class RulePacket {
    private List<Rule> rules;  // Liste de règles dans le paquet

    public RulePacket() {
        this.rules = new ArrayList<>();
    }
    
    public RulePacket(List<Rule> rules) {
        this.rules = rules;

    }

    public void addRule(Rule rule) {
        rules.add(rule);
    }

    public List<Rule> getRules() {
        return rules;
    }

    public int size() {
        return rules.size();
    }
}
