LE FORMAT D'ENTREE DES REGLE
1. Faits

Les faits représentent des assertions initiales sur les valeurs des variables. Chaque fait est écrit sur une ligne distincte et peut prendre les formes suivantes :

    Une déclaration booléenne (par exemple, A=true, E=false).
    Une valeur numérique ou textuelle (par exemple, B=42, C=hello).
    Une négation (not D), qui indique que la valeur de D est fausse.

Exemple :

    Faits :
    A=true
    B=42
    C="hello"
    not D
    E=false

2. Règles

Les règles décrivent les relations logiques entre les faits et déterminent les conclusions basées sur les conditions spécifiées. Chaque règle suit la structure suivante :

Condition => Conclusion

    Condition : Une ou plusieurs assertions combinées à l’aide de l'opérateurs logiques ET (conjonction) 
    Conclusion : Un fait ou une variable qui devient vrai(e) si la condition est remplie.
Exemple :

    Règles :
    A=true ET B=42 => F=valid
    C="hello" ET E=false => G=success
    F="valid" ET G="success" => H=100


