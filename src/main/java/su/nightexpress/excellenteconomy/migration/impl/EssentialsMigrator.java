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
        Essentials essentials = (Essentials) this.getBackend();
        if (essentials == null) return balances;

        for (UUID uuid : essentials.getUserMap().getAllUniqueUsers()) {
            try {
                com.earth2me.essentials.User user = essentials.getUser(uuid);
                if (user != null) {
                    BigDecimal money = user.getMoney();
                    if (money != null) {
                        OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(uuid);
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
