import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Parser {
    Moteur m;

    private static Fact traitementfact(String line){
                if(line.startsWith("not")){
                    String factName = line.substring(4).trim();
                    return new Fact(factName,false);
                }
                String premise = line.trim();
                String[] factParts;
                Comparator comparator = Comparator.EQUALS;
               

                    if (premise.contains("<=")) {
                        factParts = premise.split("<=");
                        comparator = Comparator.LESS_THAN_OR_EQUALS;
                    } else if (premise.contains(">=")) {
                        factParts = premise.split(">=");
                        comparator = Comparator.GREATER_THAN_OR_EQUALS;
                    } else if (premise.contains("<")) {
                        factParts = premise.split("<");
                        comparator = Comparator.LESS_THAN;
                    } else if (premise.contains(">")) {
                        factParts = premise.split(">");
                        comparator = Comparator.GREATER_THAN;
                    } else if (premise.contains("=")) {
                        factParts = premise.split("=");
                        comparator = Comparator.EQUALS;
                    } else {
                        factParts = new String[] { premise };
                    }

                    String factName = factParts[0].trim();
                    Object value;
                    if (factParts.length > 1) {
                        String valuePart = factParts[1].trim();
                        if (valuePart.equals("true")) {
                            value = true;
                        } else if (valuePart.equals("false")) {
                            value = false;
                        } else {
                            try {
                                value = Integer.parseInt(valuePart);
                            } catch (NumberFormatException e) {
                                value = valuePart;
                            }
                        }
                    } else {
                        value = true;
                    }
                    return new Fact(factName, comparator,value);
    }

    public static Moteur parseFromFile(String filePath) throws IOException{
        FactBase factBase = new FactBase();
        RuleBase ruleBase = new RuleBase();

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        String traceMode ="" ;
        boolean parsingFacts = false;
        boolean parsingRules = false;

        while((line = reader.readLine())!=null){
            line = line.trim();

            if(line.isEmpty())
            {
                continue;
            }
            if(line.equals("Faits :")){
                parsingFacts = true;
                parsingRules = false;
                continue;
            }
            if(line.equals("Regles :")){

                parsingFacts = false ;
                parsingRules = true;
                continue;
            }
            











            // gestion des faits
            if(parsingFacts){
                factBase.addFact(traitementfact(line));
            }





            //gestion des regles
            if(parsingRules){
                String[] parts = line.split("=>");
                String[] premissesParts = parts[0].split("ET");
                List<Fact> premisses = new ArrayList<>();

                for (String premise : premissesParts) {
                    premisses.add(traitementfact(premise));
                }

                // gestion de la conclusion pour les regles
                String conclusionPart = parts[1].trim();
                Fact conclusionRegle = traitementfact(conclusionPart);

                // construction de la regle
                ruleBase.addRule(new Rule(premisses, conclusionRegle));
            }
        }
        reader.close();
        Moteur m = new Moteur(ruleBase, factBase);














        











        m.afficherMenu();        
        return m;
        
    }


}
