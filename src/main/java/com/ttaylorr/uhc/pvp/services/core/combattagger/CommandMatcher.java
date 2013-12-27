package com.ttaylorr.uhc.pvp.services.core.combattagger;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches a command against a set of specified patterns.
 * Utility construct method included which also supports plain strings.
 */
public class CommandMatcher {
    private final Collection<Pattern> patterns;
    private final Mode mode;
    private static final Pattern regexPattern = Pattern.compile("^/(.*)/(i?)$", Pattern.LITERAL);

    public CommandMatcher(Collection<Pattern> patterns, CommandMatcher.Mode mode) {
        this.patterns = patterns;
        this.mode = mode;
    }

    public boolean isAllowed(String message) {
        for(Pattern pattern : patterns) {
            if(pattern.matcher(message).matches())
                return mode == Mode.Whitelist;
        }
        return mode == Mode.Blacklist;
    }

    /**
     * Constructs a CommandMatcher based on the supplied config
     * @param config The config. The name has to be either command-whitelist pr command-blacklist
     * @return       A CommandMatcher based on the input
     */
    public static CommandMatcher construct(ConfigurationSection config) {
        Preconditions.checkNotNull(config);
        Mode mode;
        if(config.isList(Mode.Whitelist.getName()))
            mode = Mode.Whitelist;
        else if(config.isList(Mode.Blacklist.getName()))
            mode = Mode.Blacklist;
        else
            return new CommandMatcher(Collections.<Pattern>emptySet(), Mode.Blacklist);

        List<Pattern> patterns = new ArrayList<>();

        for(String stringPattern : config.getStringList(mode.getName())) {
            Matcher matcher = regexPattern.matcher(stringPattern);
            if(!matcher.matches()) {
                stringPattern = stringPattern.replace(" ", "\\s+");
                patterns.add(Pattern.compile("^" + stringPattern + "\\b", Pattern.CASE_INSENSITIVE));
                continue;
            }

            stringPattern = matcher.group(1);
            String flagString = matcher.group(2);

            int flags = 0;
            if(flagString.contains("i"))
                flags |= Pattern.CASE_INSENSITIVE;

            patterns.add(Pattern.compile(stringPattern, flags));
        }

        return new CommandMatcher(patterns, mode);
    }

    public enum Mode {
        Whitelist("command-whitelist"),
        Blacklist("command-blacklist");

        private String name;

        Mode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
