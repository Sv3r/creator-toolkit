package be.sv3r.util;

import java.util.regex.Pattern;

public class CommandUtil {
    public static final Pattern QUOTATION_PATTERN = Pattern.compile("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");

    public static String[] quotedSpaces(String[] args) {
        return QUOTATION_PATTERN.split(String.join(" ", args).replaceAll("^\"", ""));
    }
}
