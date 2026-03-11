package su.nightexpress.coinsengine.currency.placeholder;

import org.jspecify.annotations.NonNull;
import su.nightexpress.excellenteconomy.api.currency.ExcellentCurrency;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.currency.CurrencyRegistry;
import su.nightexpress.coinsengine.util.placeholder.PlaceholderRegistryCompat;
import su.nightexpress.nightcore.bridge.placeholder.PlaceholderProvider;
import su.nightexpress.nightcore.bridge.placeholder.PlaceholderRegistry;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.text.night.NightMessage;

public class PlayerBalancePlaceholders implements PlaceholderProvider {

    private final CurrencyRegistry currencyRegistry;
    private final CurrencyManager manager;

    public PlayerBalancePlaceholders(@NonNull CurrencyRegistry currencyRegistry, @NonNull CurrencyManager manager) {
        this.currencyRegistry = currencyRegistry;
        this.manager = manager;
    }

    @Override
    public void addPlaceholders(@NonNull PlaceholderRegistry registry) {
        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "payments_state", (player, currency) -> {
            return CoreLang.STATE_ENABLED_DISALBED.get(this.manager.getPaymentsState(player, currency));
        });

        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "balance_short_clean", (player, currency) -> {
            return NightMessage.stripTags(currency.formatCompact(this.manager.getBalance(player, currency)));
        });

        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "balance_short_legacy", (player, currency) -> {
            return NightMessage.asLegacy(currency.formatCompact(this.manager.getBalance(player, currency)));
        });

        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "balance_short", (player, currency) -> {
            return currency.formatCompact(this.manager.getBalance(player, currency));
        });

        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "balance_clean", (player, currency) -> {
            return NightMessage.stripTags(currency.format(this.manager.getBalance(player, currency)));
        });

        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "balance_legacy", (player, currency) -> {
            return NightMessage.asLegacy(currency.format(this.manager.getBalance(player, currency)));
        });

        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "balance_raw", (player, currency) -> {
            return currency.formatRaw(this.manager.getBalance(player, currency));
        });

        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, "balance", (player, currency) -> {
            return currency.format(this.manager.getBalance(player, currency));
        });
    }
}
