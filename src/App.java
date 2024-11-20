import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        // Création de la base de faits

         FactBase factBase = new FactBase();
              // Faits
              Fact fact1 = new Fact("maladie",  true);
              Fact fact2 = new Fact("grippe" , false);
              Fact fact3 = new Fact("symptome" ,2);
              Fact fact4 = new Fact("fievre", 40);
              Fact fact5 = new Fact("essouflement",true);
              Fact fact6 = new Fact("toux",true);
              Fact fact7 = new Fact("bronchite",true);
      
              // Ajouter des faits à la base
              factBase.addFact(fact3);
              factBase.addFact(fact4);
              factBase.addFact(fact5);
      
              //teste incoherence
             /* factBase.addFact(fact6); 
              factBase.addFact(fact2); 
              factBase.addFact(fact4);
              factBase.addFact(new Fact("maladie", false));*/


              // Création de la base de règles
              // Création de Facts
              Fact c1=new Fact("symptome",Comparator.GREATER_THAN,1);

              Rule r1 = new Rule(List.of(c1),fact1);
              Rule r2=new Rule(List.of(fact1,fact5), fact6);
              Rule r3 = new Rule(List.of(fact6,fact2,new Fact("fievre",Comparator.GREATER_THAN_OR_EQUALS,20)),fact7);
              Rule r4 = new Rule(List.of(fact6), fact2);
              Rule r5 = new Rule(List.of(new Fact("fievre",Comparator.GREATER_THAN_OR_EQUALS, 38)), new Fact("Danger"));

       RuleBase ruleBase = new RuleBase( List.of(r1, r2, r3, r4,r5));
        
       // RuleBase ruleBase = new RuleBase( List.of( r3)); //teste incoherence 

        // Création de la base de connaissances
        Moteur moteur = new Moteur(ruleBase,factBase);
        moteur.getCoherence().addCritereIncoherence(List.of(new Fact("grippe", true),fact7)); // sa ne peut pas etre une bronchite ET une grippe
        moteur.getCoherence().addCritereIncoherence(List.of(new Fact("maladie", false),fact7)); //broncite EST une maladie
        moteur.getCoherence().addCritereIncoherence(List.of(new Fact("maladie", false),new Fact("grippe", true))); //mm chose pour grippe
        // Application des règles
         
        moteur.chainageAvant(Criteres.critereIndex);
       // moteur.appliquerPaquet();
      // moteur.chainageArriere(fact7);
       moteur.trace.tracerDetaille();
       //moteur.trace.tracerAbrege();
      // moteur.trace.error();
 
    }
}
