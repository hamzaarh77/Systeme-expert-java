import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Moteur {
    private RuleBase rb ;
    private FactBase fb;
    Trace trace;
    inCoherence coherence;

    public Moteur(RuleBase rb,FactBase fb){
       this.rb=rb;
       this.fb=fb;
       trace=new Trace(fb,rb);
       this.coherence=new inCoherence();
    }




    inCoherence getCoherence(){ return coherence;}
    
    public List<Rule>  RulesApplicables(){
        List<Rule> rules=new ArrayList<>();
        for(Rule r: rb.getRules()){
            if(r.isActive()){ //inutile de reparcourir les Rules deja utilisé
                if(r.estApplicable(fb)){
                    rules.add(r);
                }
            }
        }
        return rules;
    }

    public Rule critereNbrPrem(){ //retourne la Rule applicable ayant le plus de premisses à satisfaire
        List<Rule> ens=RulesApplicables();
        if(ens.size()==1){
            return ens.get(0);
        }
        else if(ens.size()==0){
            return null;
        }
        int maxPrem=ens.get(0).getNbrPremisses();
        int indRule=0;
        for(int i=1;i<ens.size();i++){ 
            if(ens.get(i).getNbrPremisses()>maxPrem){
                maxPrem=ens.get(i).getNbrPremisses();
                indRule=i;
            }
        }
        return ens.get(indRule);
    }
    public Rule criterePremRecent(){ //retourne la Rule applicable comportant comme premisses les faits déduit les plus récent
        List<Rule> ens=RulesApplicables();
        int sum=0;
        int most_recent=0;
        int indRule=0;
        if(ens.size()==1){ //si un seul choix possible pas besoin de faire de traitement
            return ens.get(0);
        }else if(ens.size()==0){ return null;}
        //lorsque on ajoute un nouveau fait à la bf on met a jour tour avec le tour ou il a ete deduit
        for(int i=0;i<ens.size();i++){ 
            for(Fact c : ens.get(i).getPremisses()){
                //en faisant la sum des attribus tour des premisses d'un regle on obtient un score comparatif 
                sum+=c.getTour();
            }
            if(sum>most_recent){
                most_recent=sum;
                indRule=i;
            }
        }
    
        return ens.get(indRule);
    }

    public List<Fact> isConsistent(FactBase fb){
        List<Fact> facts=new ArrayList<>();
        Fact f1,f2;
        // critére de coherence:
        // il ne doit pas y'avoir un fait qui a plusieur valeur dans la base de faitt
        for(int i=0;i<fb.getFacts().size();i++){
            f1=fb.getFacts().get(i);
            for(int j=i+1;j<fb.getFacts().size();j++){
                f2=fb.getFacts().get(j);
                if(f1.getName().equals(f2.getName())&&  ! f1.getValue().equals(f2.getValue())){
                    facts.add(f1);
                    facts.add(f2);
                    return facts;
                }
            }
        }
        return null;
    }

    public void gererCoherence(FactBase fb){
        List<Fact> facts=isConsistent(fb);
        Scanner scanner = new Scanner(System.in);
        int userInput;
        if(facts!=null && facts.size()>1){
            trace.add("incoherence entre: "+facts.get(0).toString()+" and "+facts.get(1).toString(),false);
            System.out.println("incoherence entre les faits : \n");
            do{
                System.out.println("0:"+facts.get(0).toString()+" et 1:"+facts.get(1).toString()+"\nsaisir le numero du fait que vous souhaitez retirer :");
                 userInput = scanner.nextInt();
            }while(userInput!=0 && userInput!=1);
            fb.getFacts().remove(facts.get(userInput));
            trace.add("nous avons retiré le fait : "+facts.get(userInput).toString(),false);
            return;
        }
        facts=coherence.isCoherente(fb);
        if(facts!=null && facts.size()>1){
            trace.add("incoherence entre: "+facts.get(0).toString()+" and "+facts.get(1).toString(),false);
            System.out.println("incoherence entre les faits : \n");
            do{
                System.out.println("0:"+facts.get(0).toString()+" et 1:"+facts.get(1).toString()+"\nsaisir le numero du fait que vous souhaitez retirer :");
                 userInput = scanner.nextInt();
            }while(userInput!=0 && userInput!=1);
            fb.getFacts().remove(facts.get(userInput));
            trace.add("nous avons retiré le fait : "+facts.get(userInput).toString(),false);
            return;
        }
    }
    
     public void addRule(Rule r) {this.rb.addRule(r); }

    void chainageAvant(Criteres c){
        trace.add("CHAINAGE AVANT \n ",true);
        Rule r;
        int tour=1;
        boolean newFact=true;

        trace.add("LE CRITERE DE SELECTION DES REGLES EST : \n",true);
        if(c==Criteres.critereNbr){
            trace.add(" regle ayant le plus de premisses à satisfaire \n",true);
         }else{
            trace.add(" regle ayant les premisses déduits les plus récent \n",true);
         }
         System.out.println("aaaaaaaaaaaaa");
        do{
            if(c==Criteres.critereNbr){
                r=critereNbrPrem();
             }else{
                r=criterePremRecent();
             }
            if(r!=null){
                trace.add(r);
                //on ajoute le consequent de la regle dans la base de fait
                Fact conclusion = r.getConclusion();
                if(!fb.contains(conclusion)){
                    Fact f= new Fact (conclusion.getName(),  conclusion.getValue());
                    f.updateTour(tour);
                    fb.addFact(f);
                    verifyAfterAdd(f);
                    trace.add(f);
                    trace.add(fb);
                }
                r.disable(); //une fois utilisé on desactive la régle
                tour++;
                gererCoherence(fb);                  
            }
            else {
                 newFact=false;
                 trace.add("\n il n'y a plus de régle à appliquer . \nFIN",false);
                 trace.fin(fb,rb);
            }
        }while(newFact);
    }

    void verifyAfterAdd(Fact fait){
    for (Fact existant : fb.getFacts()) {
        if (fait.getName().equals(existant.getName())) {
            // Combiner les faits existants si possible
            Fact fusion = fusionnerFaits(existant, fait);
            if (fusion != null) {
                trace.add("Faits fusionnés : Remplacement de " + existant.toString() + " et " + fait.toString() + " par " + fusion.toString(), true);
                fb.getFacts().remove(existant); // Supprimer l'ancien fait

                fb.addFact(fusion);     // Ajouter le nouveau fait fusionné
                return;
            }
        }
    }
    }

    private boolean isFaitProuve(Fact fait) {
        for (Fact existant : fb.getFacts()) {
            if (fait.getName().equals(existant.getName())) {
                // Utilisation directe de la méthode `compare` de Comparator
                if (fait.getComparator().compare(existant.getValue(), fait.getValue())) {
                    return true;
                }
            }
        }
        return false; // Aucun fait correspondant trouvé ou aucune comparaison valide
    }
    void chainageAvant(Criteres c,Fact fait){
        trace.add("CHAINAGE AVANT POUR SATISFAIRE LE BUT : \n",true);
        trace.add(fait.toString(),true);
        Rule r;
        int tour=1;
        boolean newFact=true;
        boolean faitAveree=false;

        trace.add("LE CRITERE DE SELECTION DES REGLES EST : \n",true);
        if(c==Criteres.critereNbr){
            trace.add(" regle ayant le plus de premisses à satisfaire \n",true);
         }else{
            trace.add(" regle ayant les premisses déduits les plus récent \n",true);
         }
         
        do{
            if(c==Criteres.critereNbr){
                r=critereNbrPrem();
            }else{
                r=criterePremRecent();
            }
            if(r!=null){
                trace.add(r);
                //on ajoute le consequent de la regle dans la base de fait
                Fact conclusion = r.getConclusion();
                if(!fb.contains(conclusion)){
                    Fact f= new Fact (conclusion.getName(), conclusion.getValue());
                    f.updateTour(tour);
                    fb.addFact(f);
                    verifyAfterAdd(f);
                    trace.add(f);
                    trace.add(fb);

                }
                r.disable(); //une fois utilisé on desactive la régle
                tour++;
                gererCoherence(fb); //a revoir on doit gere les coherence a chaque ajout pas juste dans le chainage avant
                if(fb.contains(fait)){
                    faitAveree=true;
                }
            }
            else {
                 newFact=false;
                 trace.add("\n il n'y a plus de régle à appliquer . \nFIN",false);
                 trace.fin(fb,rb);

            }
        }while(newFact && !(faitAveree));  
    }


    public List<Rule> concluantButApplicable(Fact f){ //retourne la liste de regle APPLICABLE qui ont comme conclusion le but 
        List<Rule> ens=RulesApplicables();
        List<Rule> c =new ArrayList<>();
        for(Rule r : ens){
            if(r.getConclusion().equals(f)){
                c.add(r);
            }
        }
        return c;

    }
    
    public List<Rule> concluantBut(Fact f){ //retourne la liste de regle qui ont comme conclusion le but 
        List<Rule> c =new ArrayList<>();
        for(Rule r : rb.getRules()){
            if(r.getConclusion().equals(f)){
                c.add(r);
            }
        }
        return c;
    }

    

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("moteur d'inference 0+ " +"\n");
        return sb.toString();
    }

    //ne pas toucher

    public List<RulePacket> createRulePacketsConclusion() {  //Regroupement par Type de Conclusion
    List<RulePacket> packets = new ArrayList<>();
    Map<String, RulePacket> packetMap = new HashMap<>();
    
    // Parcours de toutes les règles pour les regrouper par conclusion
    for (Rule rule : rb.getRules()) {
        // Utilisation de la conclusion de la règle comme clé pour le paquet
        String conclusion = rule.getConclusion().getName();
        
        // Si un paquet existe déjà pour cette conclusion, on l'ajoute
        if (!packetMap.containsKey(conclusion)) {
            packetMap.put(conclusion, new RulePacket());
        }
        
        // Ajout de la règle au paquet correspondant
        packetMap.get(conclusion).addRule(rule);
    }
    
    // Conversion des paquets regroupés en liste
    packets.addAll(packetMap.values());
    
    return packets;
    }

    public void appliquerPaquetConclusion(List<RulePacket> packets) {    
        trace.add("APPLIQUER LES PAQUETS REGROUPÉ PAR TYPE DE CONNCLUSION",true);
        boolean changed=false;
        do{
            changed = false;
            for (RulePacket packet : packets) { //on parcours chaque paquet
                 for(Rule r : packet.getRules()){
                    if(r.estApplicable(fb) && r.active){
                    Fact conclusion = r.getConclusion();
                    if(!fb.contains(conclusion)){
                        Fact f= new Fact (conclusion.getName(), conclusion.getValue());
                        fb.addFact(f);
                        trace.add(f);
                        trace.add(fb);

                    }
                    trace.add(r);
                    r.disable();
                    gererCoherence(fb);
                    packets.remove(packet); //on peut retirer le paquet de la liste des paquet 
                    changed=true;
                    break; //si on a appliquee une des regles de ce paquet on peut passer a un autre paquet
                    }
                }
            }

            
        }while(changed);
        trace.add("\n La base de fait est saturée \nFIN",false);
        trace.fin(fb,rb);
    }


    public List<RulePacket> createRulePackets() {  
        List<RulePacket> packets = new ArrayList<>();
        FactBase auxfb = new FactBase(fb.getFacts()); // Copie des faits initiaux
        List<Rule> auxrb = new ArrayList<>(rb.getRules()); // Copie des règles à traiter
        List<Fact> newFact=new ArrayList<>();
        boolean changed;
    
        do {
            changed = false;
            List<Rule> packet = new ArrayList<>(); // Paquet pour cette itération
            List<Rule> copie = new ArrayList<>(auxrb); // Copie des règles restantes pour l'itération
    
            for (Rule rule : copie) {
                if (rule.estApplicable(auxfb)) { // Si la règle est applicable
                    packet.add(rule); // Ajouter au paquet
                    newFact.add(rule.getConclusion());
                    auxrb.remove(rule); // Supprimer de la liste des règles restantes
                    changed = true;
                }
            }
            for(Fact f: newFact){
                auxfb.addFact(f);// Mettre à jour la base de faits
            }
            
            if (!packet.isEmpty()) {
                packets.add(new RulePacket(new ArrayList<>(packet))); // Ajouter le paquet à la liste
            }
    
        } while (!auxrb.isEmpty() && changed); // Tant qu'il reste des règles et qu'il y a eu des changements
    
        return packets;
    }

    public void appliquerPaquet() {    
        trace.add("APPLIQUER LES PAQUETS REGROUPÉ ",true);
        //afiche paquet
        List<RulePacket> packets =createRulePackets();
        trace.add(packets);
            for ( int i=0;i<packets.size();i++) { //on parcours chaque paquet
                trace.add("\non applique les regles du paquet "+i,false);
                 for(Rule r : packets.get(i).getRules()){ //et on applique chacune de ses regles
                    if(r.estApplicable(fb) && r.active){
                        trace.add(r);
                    Fact conclusion = r.getConclusion();
                    Fact f= new Fact (conclusion.getName(), conclusion.getValue());
                    fb.addFact(f);
                    trace.add(f);
                    trace.add(fb);

                    }
                    r.disable();
                    gererCoherence(fb);
                    }
                }
        trace.add("\n La base de fait est saturée \nFIN",false);
        trace.fin(fb,rb);
    }



    boolean demanderFaitVrai(Fact fait){
        System.out.println("dans le cadre du chainage arriere nous n'avons pu prouver ce fait "+fait.toString());
        System.out.println("\nvoulez vous confirmer que ce fait est averé ? si Oui saisir o sinon saisir autre :    ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if(input.equalsIgnoreCase("o")){
            return true;
        }
        else {return false;}

    }

  
    
    public void chainageArriere(Fact fait) {
        trace.add("CHAINAGE ARRIERE pour satisfaire le but : ", true);
        trace.add(fait.toString(), true);
    
        // Vérifier si le fait est déjà prouvé ou peut être dérivé d'un fait existant
        if (isFaitProuve(fait)) {
            trace.add("Le fait est déjà prouvé ou implicite : " + fait.toString(), false);
            return;
        }
    
        // Trouver les règles concluant ce but
        List<Rule> rules = concluantButApplicable(fait);
    
        if (!rules.isEmpty()) {
            // Au moins une règle conclut le but
            Rule rule = rules.get(0); // Utiliser la première règle applicable
            trace.add("Le but est vérifié par la règle : " + rule.toString(), true);
            fb.addFact(fait); // Ajouter le fait prouvé à la base de faits
            trace.add(fait);
            trace.add(fb);
            rule.disable(); // Désactiver la règle pour éviter de la réutiliser
            return;
        }
    
        // Aucun règle ne conclut directement ce but, examiner les prémisses
        List<Rule> allRules = concluantBut(fait); // Récupérer toutes les règles concluant ce but
        if (allRules.isEmpty()) {
            trace.add("Aucune règle ne peut conclure le but : " + fait.toString(), true);
            if(demanderFaitVrai(fait)){
                trace.add("l'utilisateur a affirmer que le fait est vrai ",false);
                fb.addFact(fait); // Ajouter le fait 
                trace.add(fait);
                trace.add(fb);
            }
            return; // Aucun moyen de prouver ce but
        }
    
        for (Rule rule : allRules) {
            trace.add("Examiner la règle : " + rule.toString(), false);
            List<Fact> sousButs = rule.getPremisses();
    
            boolean allPremissesVerified = true;
            for (Fact sousBut : sousButs) {
                trace.add("Nouveau sous-but : " + sousBut.toString(), false);
                chainageArriere(sousBut); // Appel récursif pour prouver le sous-but
    
                if (!isFaitProuve(sousBut)) {
                    allPremissesVerified = false;
                    trace.add("Le sous-but n'a pas pu être prouvé : " + sousBut.toString(), false);
                    break; // Si une prémisse échoue, pas besoin de vérifier les autres
                }
            }
    
            if (allPremissesVerified) {
                trace.add("Les prémisses de la règle sont vérifiées, le but est prouvé : " + fait.toString(), true);
                fb.addFact(fait);
                rule.disable();
                return; // Le but est prouvé, on arrête
            }
        }
    
        trace.add("Le but n'a pas pu être prouvé : " + fait.toString(), true);
        trace.fin(fb, rb);
    }


    //menu


    public void afficherMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean quitter = false;
    
        while (!quitter) {
            try {
                System.out.println("\n=== Menu SystÃ¨me Expert ===");
                System.out.println("1. Afficher la base de faits");
                System.out.println("2. Afficher la base de rÃ¨gles");
                System.out.println("3. ChaÃ®nage avant");
                System.out.println("4. ChaÃ®nage arriÃ¨re");
                System.out.println("5. Groupement par paquets");
                System.out.println("6. ajouter regle d'incoherence");
                System.out.println("7. Quiter");
                System.out.print("Votre choix : ");
    
                String input = scanner.nextLine(); // Lire l'entrÃ©e utilisateur comme une chaÃ®ne
                int choix;
    
                // VÃ©rifiez si l'entrÃ©e est un entier
                try {
                    choix = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("EntrÃ©e invalide. Veuillez entrer un numÃ©ro valide.");
                    continue; // Recommencez la boucle
                }
    
                // Effectuez le choix basÃ© sur l'entrÃ©e valide
                switch (choix) {
                    case 1 -> trace.afficherBf(fb);
                    case 2 -> trace.afficherBr(rb); 
                    case 3 -> demarrerChainageAvant(scanner);
                    case 4 -> demarrerChainageArriere(scanner);
                    case 5 -> demarerGroupement(scanner);
                    case 6 -> demarerAjoutIncoherence(scanner);

                    case 7-> {
                        System.out.println("Au revoir !");
                        quitter = true;
                    }
                    default -> System.out.println("Choix invalide. Veuillez rÃ©essayer.");
                }
            } catch (Exception e) {
                System.out.println("Une erreur est survenue : " + e.getMessage());
                e.printStackTrace();
            }
        }
    
        scanner.close();
    }

    void tracer(){
        //gestion des trace
        System.out.println("si vous voulez des trace abrege entrer '1'  si vous voulez des trace detaille entrer '2' si vous ne voulez pas de trace entrer rien");
        Scanner scanner = new Scanner(System.in);
        String traceMode = scanner.nextLine();
        switch(traceMode){
            case "1":
                System.out.println("trace abrege\n");
                trace.tracerAbrege();
            break;

            case "2":
                System.out.println("trace detaille \n");
                trace.tracerDetaille();
            break;

            default:
                System.out.println("on affiche pas de trace");
            break;
        }
    }

    private Fact fusionnerFaits(Fact existant, Fact nouveau) {
        // Vérifier si les noms correspondent
        if (!existant.getName().equals(nouveau.getName())) {
            return null; // Noms différents, pas de fusion possible
        }
    
        Object valeurExistante = existant.getValue();
        Object nouvelleValeur = nouveau.getValue();
    
        // Cas : fusion de comparateurs numériques compatibles
        if (valeurExistante instanceof Number && nouvelleValeur instanceof Number) {
            double v1 = ((Number) valeurExistante).doubleValue();
            double v2 = ((Number) nouvelleValeur).doubleValue();
    
            // Exemple : si x = 20 et x > 20, on peut conclure x >= 20
            if (existant.getComparator() == Comparator.EQUALS && nouveau.getComparator() == Comparator.GREATER_THAN) {
                if (v1 == v2) {
                    return new Fact(existant.getName(), Comparator.GREATER_THAN_OR_EQUALS, v1);
                }
            } else if (existant.getComparator() == Comparator.GREATER_THAN && nouveau.getComparator() == Comparator.EQUALS) {
                if (v1 == v2) {
                    return new Fact(existant.getName(), Comparator.GREATER_THAN_OR_EQUALS, v1);
                }
            }
    
            // Ajouter des règles similaires pour combiner d'autres comparateurs
            if (existant.getComparator() == Comparator.GREATER_THAN && nouveau.getComparator() == Comparator.GREATER_THAN) {
                return new Fact(existant.getName(), Comparator.GREATER_THAN, Math.max(v1, v2));
            }
    
            if (existant.getComparator() == Comparator.LESS_THAN && nouveau.getComparator() == Comparator.LESS_THAN) {
                return new Fact(existant.getName(), Comparator.LESS_THAN, Math.min(v1, v2));
            }
    
            if (existant.getComparator() == Comparator.GREATER_THAN_OR_EQUALS && nouveau.getComparator() == Comparator.GREATER_THAN_OR_EQUALS) {
                return new Fact(existant.getName(), Comparator.GREATER_THAN_OR_EQUALS, Math.max(v1, v2));
            }
    
            if (existant.getComparator() == Comparator.LESS_THAN_OR_EQUALS && nouveau.getComparator() == Comparator.LESS_THAN_OR_EQUALS) {
                return new Fact(existant.getName(), Comparator.LESS_THAN_OR_EQUALS, Math.min(v1, v2));
            }
        }
    
        // Aucun cas de fusion applicable
        return null;
    }

    private void demarerAjoutIncoherence(Scanner scanner){
        System.out.println("pour declarer une incoherence saisire deux faits l'un à la suite de l'autre");
        List<Fact> paire=new ArrayList<>();
        boolean continuer=false;
        do{
            continuer=false;
        for(int i=1;i<3;i++){
        System.out.print("Entrez le nom du fait "+i+"   :");
        String nomFaitBut = scanner.nextLine().toString();
        System.out.print("Entrez le comparateur : \n1. =\n2. >\n3.>=\n4. <\n5. <=\n6. !=\n");
        System.out.print("Votre choix : ");

        String comparateur= scanner.nextLine();
        int comparateurNum = Integer.parseInt(comparateur);
        System.out.print("Entrez la valeur du fait   "+i+"   :");
        String valeurFaitBut = scanner.nextLine();

        Object valeur = valeurFaitBut; // Par dÃ©faut, la valeur reste un texte
        if (valeurFaitBut.equalsIgnoreCase("true") || valeurFaitBut.equalsIgnoreCase("false")) {
            valeur = Boolean.parseBoolean(valeurFaitBut);
        } else {
            try {
                valeur = Double.parseDouble(valeurFaitBut);
            } catch (NumberFormatException e) {
                // Si ce n'est ni un boolÃ©en ni un nombre, on garde le texte
            }
        }
        Comparator c=Comparator.EQUALS;;
        switch (comparateurNum) {
            case 1:
                c= Comparator.EQUALS;

            case 2:
                c= Comparator.GREATER_THAN;
            
            case 3:
                c= Comparator.GREATER_THAN_OR_EQUALS;

            case 4:
                c= Comparator.LESS_THAN;
                
            case 5:
                c= Comparator.LESS_THAN;

            case 6:
                c= Comparator.NOT_EQUALS;

            break;
        }
        Fact faitBut = new Fact(nomFaitBut,c,valeur);         
        paire.add(faitBut);
        System.out.println("Fait "+i+faitBut.toString());
    }
        System.out.println("ajout incoherence fini.\nvoulez vous ajouter d'autre ? si oui saisire O");
        System.out.println("saisire: ");
        String lettreO= scanner.nextLine();
        if(lettreO=="o" || lettreO=="O"){
            continuer=true;
        }
    
    this.coherence.addCritereIncoherence(paire);
    }while(continuer);


    }
    private void demarrerChainageAvant(Scanner scanner) {
        System.out.println("\n=== ChaÃ®nage avant ===");
        System.out.println("1. Avec le but de saturer la base de fait ");
        System.out.println("2. Avec le but de prouver un fait");
        System.out.print("Votre choix : ");
    
        int choix;
        try {
            choix = Integer.parseInt(scanner.nextLine()); // Utilisation de nextLine pour permettre une meilleure gestion des entrÃ©es
        } catch (NumberFormatException e) {
            System.out.println("EntrÃ©e invalide. Veuillez entrer un numÃ©ro valide.");
            return;
        }
    
        if (choix == 2) {
            while (true) { // Boucle pour permettre de saisir un nouveau fait si le prÃ©cÃ©dent est inatteignable
                System.out.print("Entrez le nom du fait but : ");
                String nomFaitBut = scanner.nextLine().toString();
                System.out.print("Entrez le comparateur : \n1. =\n2. >\n3.>=\n4. <\n5. <=\n6. !=\n");
                System.out.print("Votre choix : ");

                String comparateur= scanner.nextLine();
                int comparateurNum = Integer.parseInt(comparateur);
                System.out.print("Entrez la valeur du fait but : ");
                String valeurFaitBut = scanner.nextLine();
    
                Object valeur = valeurFaitBut; // Par dÃ©faut, la valeur reste un texte
                if (valeurFaitBut.equalsIgnoreCase("true") || valeurFaitBut.equalsIgnoreCase("false")) {
                    valeur = Boolean.parseBoolean(valeurFaitBut);
                } else {
                    try {
                        valeur = Double.parseDouble(valeurFaitBut);
                    } catch (NumberFormatException e) {
                        // Si ce n'est ni un boolÃ©en ni un nombre, on garde le texte
                    }
                }
                Comparator c=Comparator.EQUALS;;
                switch (comparateurNum) {
                    case 1:
                        c= Comparator.EQUALS;

                    case 2:
                        c= Comparator.GREATER_THAN;
                    
                    case 3:
                        c= Comparator.GREATER_THAN_OR_EQUALS;

                    case 4:
                        c= Comparator.LESS_THAN;
                        
                    case 5:
                        c= Comparator.LESS_THAN;

                    case 6:
                        c= Comparator.NOT_EQUALS;

                    break;
                }
                Fact faitBut = new Fact(nomFaitBut,c,valeur); 
                Criteres critere = ChoixCritere(scanner);
                chainageAvant(critere, faitBut);
                tracer();
                break; // Sortir de la boucle une fois le chaÃ®nage terminÃ©
            }
        } else if (choix == 1) {
            Criteres critere = ChoixCritere(scanner);
            chainageAvant(null);      
            tracer();
        } else {
            System.out.println("Choix invalide. Veuillez rÃ©essayer.");
        }
    }

    private void demarrerChainageArriere(Scanner scanner) {
        System.out.println("\n=== ChaÃ®nage arriÃ¨re ===");
    
        while (true) {
            // DÃ©finir le fait but
            System.out.print("Entrez le nom du fait but : ");
            String nomFaitBut = scanner.nextLine().toLowerCase();
            System.out.print("Entrez la valeur du fait but  : ");
            String valeurFaitBut = scanner.nextLine();
    
            Object valeur = valeurFaitBut; // Par dÃ©faut, la valeur reste un texte
            if (valeurFaitBut.equalsIgnoreCase("true") || valeurFaitBut.equalsIgnoreCase("false")) {
                valeur = Boolean.parseBoolean(valeurFaitBut);
            } else {
                try {
                    valeur = Double.parseDouble(valeurFaitBut);
                } catch (NumberFormatException e) {
                    // Si ce n'est ni un boolÃ©en ni un nombre, on garde le texte
                }
            }

            System.out.print("Entrez le comparateur : \n1. =\n2. >\n3.>=\n4. <\n5. <=\n6. !=\n");
            System.out.println("saisire le numero :     ");
                String comparateur= scanner.nextLine();
                int comparateurNum = Integer.parseInt(comparateur);
            Comparator c=Comparator.EQUALS;;
            switch (comparateurNum) {
                case 1:
                    c= Comparator.EQUALS;

                case 2:
                    c= Comparator.GREATER_THAN;
                
                case 3:
                    c= Comparator.GREATER_THAN_OR_EQUALS;

                case 4:
                    c= Comparator.LESS_THAN;
                    
                case 5:
                    c= Comparator.LESS_THAN;

                case 6:
                    c= Comparator.NOT_EQUALS;

                break;
            }
            Fact faitBut = new Fact(nomFaitBut,c,valeur);

            // Lancer le chaÃ®nage arriÃ¨re
            chainageArriere(faitBut);
            tracer();
                break; // So;rtir de la boucle une fois le chaÃ®nage terminÃ©
        }
    }

    private void  demarerGroupement(Scanner scanner){
        appliquerPaquet();
        tracer();
    }
    private Criteres ChoixCritere(Scanner scanner) {
        Criteres critere = null;
    
        while (critere == null) {
            System.out.println("\nChoisissez un critÃ¨re de sÃ©lection :");
            System.out.println("1. NOMBRE_DE_PREMISSES");
            System.out.println("2. FAITS_PLUS_RECENT");
            System.out.print("Votre choix : ");
            int choixCritere = scanner.nextInt();
            scanner.nextLine(); // Consomme la nouvelle ligne
    
            if (choixCritere == 1) {
                critere = Criteres.critereNbr;
            } else if (choixCritere == 2) {
                critere =Criteres.critereIndex;
            } else {
                System.out.println("Choix invalide. Veuillez rÃ©essayer.");
            }
        }
        
        System.out.println("\n");
        return critere;
    }
}
