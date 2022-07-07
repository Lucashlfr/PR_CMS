package de.messdiener.cms.web.utils;

import de.messdiener.cms.web.security.SecurityHelper;

public class Utils {

    public static String getMailClose(){
        return "Viele Grüße \nfür das Communications-Team\n" + SecurityHelper.getUsername();
    }

}
