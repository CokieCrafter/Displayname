package de.senf.displayname;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Displayname extends JavaPlugin {

    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        RegisteredServiceProvider<LuckPerms> provider = getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            System.out.println("LuckPermsApi + Displayname plugin enabled");
        }

        getCommand("setdisplayname").setExecutor(new SetDisplayNameCommand());
    }

    public class SetDisplayNameCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /setdisplayname <Spieler> <Prefix> <Wort>");
                return false;
            }

            Player target = getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Spieler nicht gefunden!");
                return false;
            }

            String prefix = ChatColor.translateAlternateColorCodes('&', args[1]);
            String word = args[2];
            String displayName = prefix + " " + word;

            target.setDisplayName(displayName);
            target.setPlayerListName(displayName);
            sender.sendMessage(ChatColor.GREEN + "Displayname von " + target.getName() + " geändert zu: " + displayName);

            if (luckPerms != null) {
                User user = luckPerms.getUserManager().getUser(target.getUniqueId());
                if (user != null) {
                    user.data().add(Node.builder("prefix." + prefix).build());
                    luckPerms.getUserManager().saveUser(user);
                }
            }

            return true;
        }
    }
}