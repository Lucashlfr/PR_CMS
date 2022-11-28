package de.messdiener.cms.cache.enums;

public class FlowEnums {

    public enum Type{
        CASH_FLOW("Auslagen");

        final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum State{
        OPEN("Offen"), WAITING("Warten auf Pfarrbüro"), FINISHED("Beendet");

        final String name;

        State(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
