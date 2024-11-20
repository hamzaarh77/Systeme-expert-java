import java.util.List;

public class Rule {
    private List<Fact> premisses ;
    private Fact conclusion ;
    boolean active;
    private static int counter = 0;
    String name;


    public Rule(List<Fact> p, Fact c){
        this.premisses = p;
        this.conclusion = c ;
        this.active=true;
        int x= ++counter;
        this.name="R"+x;
    }

    // methods
    // est ce que la regle erst applicable par rapport a notre base de fait actuelle
    public boolean estApplicable(FactBase fb){
        if(active){
             for(Fact premisse : premisses){
                if(! premisse.evaluer(fb)){
                    return false;
                }             
            }
            return true ;
        }
        return false;
    }
    


    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" : SI ");
        for(Fact c : premisses){
            sb.append(c.toString()+" Et ");
        }
        sb.deleteCharAt(sb.length() -2); //enleve le dernier ET
        sb.deleteCharAt(sb.length() -2);

        sb.append(" => "+this.conclusion.toString());
        return sb.toString();
    }

    // getters and setters
    public List<Fact> getPremisses() {
        return this.premisses;
    }

    public int getNbrPremisses(){
        return premisses.size();
    }

    public void setPremisses(List<Fact> premisses) {
        this.premisses = premisses;
    }

    public Fact getConclusion() {
        return this.conclusion;
    }

    public void setConclusion(Fact conclusion) {
        this.conclusion = conclusion;
    }

    public void disable(){
        this.active=false;
    }

    public boolean isActive(){
        return active;
    }
    

}
