package su.nightexpress.excellenteconomy.migration.impl;

import com.earth2me.essentials.Essentials;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellenteconomy.EconomyPlugin;
import su.nightexpress.excellenteconomy.api.currency.ExcellentCurrency;
import su.nightexpress.excellenteconomy.hook.HookPlugin;
import su.nightexpress.excellenteconomy.migration.Migrator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EssentialsMigrator extends Migrator {

    public EssentialsMigrator(@NotNull EconomyPlugin plugin) {
        super(plugin, HookPlugin.ESSENTIALS);
    }

    @Override
    public boolean canMigrate(@NotNull ExcellentCurrency currency) {
        return true;
    }

    @Override
    @NotNull
    public Map<OfflinePlayer, Double> getBalances(@NotNull ExcellentCurrency currency) {
        Map<OfflinePlayer, Double> balances = new HashMap<>();

        java.io.File essentialsFolder = new java.io.File(this.plugin.getDataFolder().getParentFile(), "Essentials");
        java.io.File userdataFolder = new java.io.File(essentialsFolder, "userdata");
        if (userdataFolder.exists() && userdataFolder.isDirectory()) {
            java.io.File[] files = userdataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null) {
                for (java.io.File file : files) {
                    try {
                        String nameWithoutExt = file.getName().substring(0, file.getName().length() - 4);
                        UUID uuid = UUID.fromString(nameWithoutExt);
                        org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);
                        Object moneyObj = config.get("money");
                        if (moneyObj != null) {
                            double money;
                            if (moneyObj instanceof Number) {
                                money = ((Number) moneyObj).doubleValue();
                            } else {
                                money = Double.parseDouble(moneyObj.toString().replace(",", ""));
                            }
                            OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(uuid);
                            balances.put(offlinePlayer, money);
                        }
                    } catch (IllegalArgumentException ignored) {
                    } catch (Exception exception) {
                        this.plugin.error("Could not convert Essentials balance from file '" + file.getName() + "'!");
                        exception.printStackTrace();
                    }
                }
            }
        }

        Essentials essentials = (Essentials) this.getBackend();
        if (essentials == null) return balances;

        for (UUID uuid : essentials.getUserMap().getAllUniqueUsers()) {
            try {
                OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(uuid);
                com.earth2me.essentials.User user = essentials.getUserMap().load(offlinePlayer);
                if (user != null) {
                    BigDecimal money = user.getMoney();
                    if (money != null) {
                        balances.put(offlinePlayer, money.doubleValue());
                    }
                }
            } catch (Exception exception) {
                this.plugin.error("Could not convert Essentials balance for '" + uuid + "'!");
                exception.printStackTrace();
            }
        }
        return balances;
    }
}