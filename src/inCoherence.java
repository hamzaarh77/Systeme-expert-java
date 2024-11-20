import java.util.*;

public class inCoherence {
    List<List<Fact>> incoherences;

    public inCoherence(){ incoherences=new ArrayList<>();  }

    public void addCritereIncoherence(List<Fact> facts){
        this.incoherences.add(facts);
    }


    public List<Fact> isCoherente(FactBase fb) {
        List<Fact> conflicts = new ArrayList<>();  // Liste des faits incohérents
        List<Fact> facts = fb.getFacts();          // Liste des faits dans la base

        // Parcourir les incohérences
        for (List<Fact> incoherence : incoherences) {
            // Vérifier si tous les faits de l'incohérence sont présents dans la base
            boolean allPresent = true;
            for (Fact fact : incoherence) {
                if (!facts.contains(fact)) {
                    allPresent = false;
                    break;
                }
            }

            // Si tous les faits de l'incohérence sont présents dans la base
            if (allPresent) {
                // Ajouter tous les faits de l'incohérence à la liste des conflits
                conflicts.addAll(incoherence);
            }
        }
    return conflicts;
    }
    

}
