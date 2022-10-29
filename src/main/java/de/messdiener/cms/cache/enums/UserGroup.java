package de.messdiener.cms.cache.enums;

public enum UserGroup {

        ADMIN("Admin", "text-danger"),
        LEKTOR1("Lektor 1", "text-warning"),
        LEKTOR2("Lektor 2", "text-warning"),
        USER("Default", "text-primary"),
        DEAKTIVIERT("Deaktiviert", "text-primary"),
        REQUESTED("Wartet...", "text-primary");

        private final String name;
        private final String className;

        UserGroup(String name, String className) {
                this.name = name;
                this.className = className;
        }

        public String getName() {
                return name;
        }

        public String getClassName() {
                return className;
        }
}
