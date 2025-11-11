package controller;

import model.Card;
import model.CardType;
import model.TariffKind;
import model.TurnstileStats;
import service.Registry;

import java.io.IOException;
import java.util.Map;
import java.util.EnumMap;
import java.util.Optional;

public class TurnstileController {
    private final Registry registry;
    private final TurnstileStats stats = new TurnstileStats();

    public TurnstileController(Registry registry) {
        this.registry = registry;
    }

    public Card issue(CardType type, TariffKind tariff) {
        return registry.issueCard(type, tariff);
    }

    public boolean topUpStoredValue(String id, float amount) {
        return registry.topUp(id, amount);
    }

    public boolean tryPass(String cardId) {
        Optional<Card> maybeCard = registry.readCard(cardId);
        if (maybeCard.isEmpty()) {
            stats.incDenied(CardType.REGULAR);
            return false;
        }
        Card card = maybeCard.get();
        boolean ok = validateAndCharge(card);
        if (ok) stats.incAllowed(card.getType());
        else stats.incDenied(card.getType());
        return ok;
    }

    private boolean validateAndCharge(Card c) {
        switch (c.getTariff()) {
            case MONTH, TEN_DAYS -> {
                var today = java.time.LocalDate.now();
                return c.getValidUntil() != null && !today.isAfter(c.getValidUntil());
            }
            case TRIPS_5, TRIPS_10 -> {
                if (c.getRemainingTrips() <= 0) return false;
                c.setRemainingTrips(c.getRemainingTrips() - 1);
                return true;
            }
            case STORED_VALUE -> {
                float fare = Registry.FARE;
                if (c.getBalance() < fare) return false;
                c.setBalance(c.getBalance() - fare);
                return true;
            }
        }
        return false;
    }

    public long getAllowedTotal() {
        return stats.getAllowedTotal();
    }

    public long getDeniedTotal() {
        return stats.getDeniedTotal();
    }

    public Map<CardType, long[]> getStatsByType() {
        Map<CardType, long[]> m = new EnumMap<>(CardType.class);
        for (CardType t : CardType.values()) {
            m.put(t, new long[]{
                    stats.getAllowedByType().get(t),
                    stats.getDeniedByType().get(t)
            });
        }
        return m;
    }

    public Map<String, Card> dumpRegistry() {
        return registry.snapshot();
    }

    public void save(String path) throws IOException {
        registry.save(path);
    }

    public void load(String path) throws IOException, ClassNotFoundException {
        registry.load(path);
    }

    public Optional<Card> findById(String id) {
        return registry.readCard(id);
    }

}
