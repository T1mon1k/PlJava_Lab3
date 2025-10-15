package service;

import model.Card;
import model.CardType;
import model.TariffKind;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;

public class Registry {
    private final Map<String, Card> store = new LinkedHashMap<>();

    public static final float FARE = 8.00f;

    public Card issueCard(CardType type, TariffKind tariff) {
        if (tariff == TariffKind.STORED_VALUE && type != CardType.REGULAR) {
            throw new IllegalArgumentException("Накопичувальна картка не може бути учнівська чи студентська.");
        }
        Card c = new Card(type, tariff);
        switch (tariff) {
            case MONTH -> {
                c.setIssueDate(LocalDate.now());
                c.setValidUntil(LocalDate.now().plusMonths(1).minusDays(1));
            }
            case TEN_DAYS -> {
                c.setIssueDate(LocalDate.now());
                c.setValidUntil(LocalDate.now().plusDays(10).minusDays(1));
            }
            case TRIPS_5 -> c.setRemainingTrips(5);
            case TRIPS_10 -> c.setRemainingTrips(10);
            case STORED_VALUE -> c.setBalance(0.0f);
        }
        store.put(c.getId(), c);
        return c;
    }

    public Optional<Card> readCard(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public boolean topUp(String id, float amount) {
        Card c = store.get(id);
        if (c == null || c.getTariff() != TariffKind.STORED_VALUE) return false;
        c.setBalance(c.getBalance() + amount);
        return true;
    }

    public Map<String, Card> snapshot() {
        return Collections.unmodifiableMap(store);
    }

}
