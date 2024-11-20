import java.util.ArrayList;
import java.util.List;

public class FactBase {
    private List<Fact> facts;

    public FactBase(List<Fact> facts){
        this.facts = facts ;
    }
    public FactBase(){
        facts = new ArrayList<>();
    }

    // methods
    public boolean contains(Fact f){
        for(Fact fact : facts){
            if(fact.equals(f))
                return true ;
        }
        return false ;
    }

    public void addFact(Fact f){
        if(!facts.contains(f))
            this.facts.add(f);


    }


    

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("____________________\n");
         for(Fact f : facts){
            sb.append("|"+f.toString());
            for(int i=0;i<(18-f.toString().length());i++){sb.append(" ");}
            sb.append("|\n"); 
        }        sb.append("____________________\n");
        return sb.toString();
    }


    // getters ans setters 
    public List<Fact> getFacts() {
        return this.facts;
    }

    public void setFacts(List<Fact> facts) {
        this.facts = facts;
    }

    public Fact getFact(Fact fact){
        for(Fact f : facts){
            if(f.getName().equals(fact.getName()))
                return f;
        }
        return null ;
    }

    
}
