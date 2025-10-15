package model;

import java.util.EnumMap;
import java.util.Map;

public class TurnstileStats {
    private long allowedTotal = 0;
    private long deniedTotal = 0;
    private final Map<CardType, Long> allowedByType = new EnumMap<>(CardType.class);
    private final Map<CardType, Long> deniedByType = new EnumMap<>(CardType.class);

    public TurnstileStats() {
        for (CardType t : CardType.values()) {
            allowedByType.put(t, 0L);
            deniedByType.put(t, 0L);
        }
    }

    public void incAllowed(CardType type) {
        allowedTotal++;
        allowedByType.put(type, allowedByType.get(type) + 1);
    }

    public void incDenied(CardType type) {
        deniedTotal++;
        deniedByType.put(type, deniedByType.get(type) + 1);
    }

    public long getAllowedTotal() { return allowedTotal; }
    public long getDeniedTotal() { return deniedTotal; }
    public Map<CardType, Long> getAllowedByType() { return allowedByType; }
    public Map<CardType, Long> getDeniedByType() { return deniedByType; }
}
