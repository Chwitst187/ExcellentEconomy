package su.nightexpress.coinsengine.tops.placeholder;

import org.jspecify.annotations.NonNull;
import su.nightexpress.coinsengine.tops.TopManager;
import su.nightexpress.coinsengine.util.placeholder.PlaceholderRegistryCompat;
import su.nightexpress.coinsengine.currency.CurrencyRegistry;
import su.nightexpress.excellenteconomy.api.currency.ExcellentCurrency;
import su.nightexpress.nightcore.bridge.placeholder.PlaceholderProvider;
import su.nightexpress.nightcore.bridge.placeholder.PlaceholderRegistry;
import su.nightexpress.nightcore.util.text.night.NightMessage;

public class ServerBalancePlaceholders implements PlaceholderProvider {

    private final TopManager topManager;
    private final CurrencyRegistry currencyRegistry;

    public ServerBalancePlaceholders(@NonNull CurrencyRegistry currencyRegistry, @NonNull TopManager topManager) {
        this.currencyRegistry = currencyRegistry;
        this.topManager = topManager;
    }

    @Override
    public void addPlaceholders(@NonNull PlaceholderRegistry registry) {
        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "server_balance_short_clean", (player, currency) -> {
            return NightMessage.stripTags(currency.formatCompact(this.topManager.getTotalBalance(currency)));
        });

        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "server_balance_short_legacy", (player, currency) -> {
            return NightMessage.asLegacy(currency.formatCompact(this.topManager.getTotalBalance(currency)));
        });

        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "server_balance_short", (player, currency) -> {
            return currency.formatCompact(this.topManager.getTotalBalance(currency));
        });

        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "server_balance_clean", (player, currency) -> {
            return NightMessage.stripTags(currency.format(this.topManager.getTotalBalance(currency)));
        });

        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "server_balance_legacy", (player, currency) -> {
            return NightMessage.asLegacy(currency.format(this.topManager.getTotalBalance(currency)));
        });

        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "server_balance_raw", (player, currency) -> {
            return currency.formatRaw(this.topManager.getTotalBalance(currency));
        });

        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "server_balance", (player, currency) -> {
            return currency.format(this.topManager.getTotalBalance(currency));
        });
    }
}
