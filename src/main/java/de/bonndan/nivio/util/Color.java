package de.bonndan.nivio.util;

import org.springframework.util.StringUtils;

public class Color {

    /**
     * https://stackoverflow.com/questions/2464745/compute-hex-color-code-for-an-arbitrary-string
     *
     * @param name of a group etc
     * @return a hex color
     */
    public static String intToARGB(String name){
        if (StringUtils.isEmpty(name))
            return "FFFFFF";

        int i = name.hashCode();

        return //Integer.toHexString(((i>>24)&0xFF))+
                Integer.toHexString(((i>>16)&0xFF))+
                Integer.toHexString(((i>>8)&0xFF))+
                Integer.toHexString((i&0xFF));
    }
}
