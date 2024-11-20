import java.util.ArrayList;
import java.util.List;

public class RuleBase {
    private List<Rule> rules;

public RuleBase(List<Rule> rules){
    this.rules = rules ;
}
public RuleBase(){
    rules = new ArrayList<>();
}

// methods
public boolean contains(Rule r){
    for(Rule rule : rules){
        if(rule.equals(r))
            return true ;
    }
    return false ;
}

public void addRule(Rule r){
    if(! rules.contains(r))
        this.rules.add(r);
}

public String toString(){
    StringBuilder sb = new StringBuilder();
    for(Rule r: rules){
        sb.append(r.toString()+"\n");
    }
    return sb.toString();
}


// getters ans setters 
public List<Rule> getRules() {
    return this.rules;
}

public void setRules(List<Rule> rules) {
    this.rules = rules;
}


}
