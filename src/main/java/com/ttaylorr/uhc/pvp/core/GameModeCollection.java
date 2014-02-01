package com.ttaylorr.uhc.pvp.core;

import com.google.common.base.Function;
import com.ttaylorr.uhc.pvp.PVPManagerPlugin;
import com.ttaylorr.uhc.pvp.core.gamemodes.GameMode;
import com.ttaylorr.uhc.pvp.util.Checker;
import com.ttaylorr.uhc.pvp.util.PVPManagerCommand;
import nl.dykam.dev.reutil.data.ComponentHandle;
import nl.dykam.dev.reutil.data.ComponentManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static com.ttaylorr.uhc.pvp.util.Message.warn;

public class GameModeCollection {
    private Function<Player, GameMode> defaultGameMode;

    private List<GameMode> gameModes;

    private HashMap<GameMode, HashMap<String, GameMode>> transitions = new HashMap<>();
    private ComponentHandle<Player, UserData> dataHandle;
    private UserManager userManager;
    public GameModeCollection(UserManager userManager, Function<Player, GameMode> defaultGameMode) {
        this.userManager = userManager;
        this.defaultGameMode = defaultGameMode;
        dataHandle = ComponentManager.get(PVPManagerPlugin.get()).get(UserData.class);
        gameModes = new ArrayList<>();
    }

    public GameMode getDefaultGameMode(Player player) {
        return defaultGameMode.apply(player);
    }

    public List<GameMode> getGameModes() {
        return Collections.unmodifiableList(gameModes);
    }

    public List<GameMode> getGameModes(Player player) {
        List<GameMode> gameModes = new ArrayList<>(this.gameModes.size());
        GameMode from = dataHandle.get(player).gameMode;
        for (GameMode to : this.gameModes) {
            if(hasPermission(player, from, to))
                gameModes.add(to);
        }

        return Collections.unmodifiableList(gameModes);
    }

    public void add(List<GameMode> gameModes) {
        for (GameMode gameMode : gameModes) add(gameMode);
    }

    private void add(GameMode gameMode) {
        gameModes.add(gameMode);
        transitions.put(gameMode, new HashMap<String, GameMode>());
    }

    public void addTransition(String command, GameMode from, GameMode to) {
        transitions.get(from).put(command, to);
    }

    public void addTransition(String command, GameMode to) {
        for (GameMode from : gameModes) {
           if(from == to)
               continue;
            addTransition(command, from, to);
        }
    }

    public GameMode get(Player player, String command) {
        GameMode from = dataHandle.get(player).gameMode;
        if(from == null)
            return null;

        GameMode to = transitions.get(from).get(command);
        if (hasPermission(player, from, to)) {
            return to;
        }
        return null;
    }

    private boolean hasPermission(Player player, GameMode from, GameMode to) {
        if (from == null || to == null) return false;
        if (player.hasPermission("pvpmanager.transition." + from.getName() + "." + to.getName())) return true;
        if (player.hasPermission("pvpmanager.transition.*." + to.getName())) return true;
        if (player.hasPermission("pvpmanager.transition." + from.getName() + ".*")) return true;
        return false;
    }

    private boolean hasPermission(Player player, GameMode to) {
        return hasPermission(player, dataHandle.get(player).gameMode, to);
    }

    public boolean transfer(Player player, String command) {
        GameMode to = get(player, command);
        if(to == null)
            return false;
        final UserData userData = dataHandle.get(player);
        if(userData.transitioning) {
            warn(player, "You are already switching game mode!");
            return true;
        }
        if(userData.gameMode == to) {
            warn(player, "You already are in " + to.getName());
            return true;
        }
        userManager.switchGameMode(player, to);
        return true;
    }

    public Command[] getCommands() {
        Set<String> commandNames = new HashSet<>();
        for (HashMap<String, GameMode> transition : transitions.values()) {
            commandNames.addAll(transition.keySet());
        }

        Command[] commands = new Command[commandNames.size()];
        int index = 0;
        for (String commandName : commandNames) {
            commands[index++] = new TransitionCommand(commandName);
        }
        return commands;
    }

    private class TransitionCommand extends PVPManagerCommand implements CommandExecutor {
        public TransitionCommand(String name) {
            super(null, name);
            setExecutor(this);
        }

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if(!Checker.isPlayer(commandSender))
                return true;
            return transfer((Player) commandSender, getName());
        }

        @Override
        public boolean testPermissionSilent(CommandSender target) {
            if(!(target instanceof Player))
                return super.testPermissionSilent(target);
            Player player = (Player) target;
            GameMode from = dataHandle.get(player).gameMode;
            for (Map.Entry<String, GameMode> transition : transitions.get(from).entrySet()) {
                GameMode to = transition.getValue();
                if(from == to) continue;
                if(!transition.getKey().equals(getName())) continue;
                if(hasPermission(player, from, to))
                    return true;
            }
            return false;
        }
    }
}
