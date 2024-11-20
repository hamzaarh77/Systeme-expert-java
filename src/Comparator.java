public enum Comparator {
    EQUALS("==") {
        @Override
        public boolean compare(Object a, Object b) {
            return a.equals(b); // Comparaison pour égalité
        }
        
    },
    NOT_EQUALS("!=") {
        @Override
        public boolean compare(Object a, Object b) {
            return !a.equals(b); // Comparaison pour inégalité
        }
    },
    GREATER_THAN(">") {
        @Override
        public boolean compare(Object a, Object b) {
            if (a instanceof Comparable && b instanceof Comparable) {
                return ((Comparable) a).compareTo(b) > 0;
            }
            return false; // Si les objets ne sont pas comparables, retourne false
        }
    },
    LESS_THAN("<") {
         public boolean compare(Object a, Object b) {
            if (a instanceof Comparable && b instanceof Comparable) {
                return ((Comparable) a).compareTo(b) < 0;
            }
            return false;
        }
    },
    GREATER_THAN_OR_EQUALS(">=") {
        @Override
        public boolean compare(Object a, Object b) {
            if (a instanceof Comparable && b instanceof Comparable) {
                return ((Comparable) a).compareTo(b) >= 0;
            }
            return false;
        }
    },
    LESS_THAN_OR_EQUALS("<=") {
        @Override
        public boolean compare(Object a, Object b) {
            if (a instanceof Comparable && b instanceof Comparable) {
                return ((Comparable) a).compareTo(b) <= 0;
            }
            return false;
        }
    };

    private final String symbol;

    Comparator(String symbol) {
        this.symbol = symbol;
    }

    public abstract boolean compare(Object a, Object b);

    public String getSymbol() {
        return symbol;
    }
}
