package org.solrmarc.mixin.ssb.utils;

public class Commons {
    public static String swapName(String name) {
        if (name.indexOf(",") != -1) {
            if (name.lastIndexOf(",") == name.length() - 1) {
                name = name.substring(0, name.length() - 1);
            }
            String firstname = name.substring(name.indexOf(",") + 1).trim();
            String surname = name.substring(0, name.indexOf(",")).trim();
            return firstname + " " + surname;
        }
        return name;
    }
}
