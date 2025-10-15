package model;

import java.time.LocalDate;
import java.util.Objects;
import java.security.SecureRandom;

public class Card {
    private static final SecureRandom RND = new SecureRandom();
    private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private static String generateId() {
        StringBuilder sb = new StringBuilder(11);
        for (int i = 0; i < 10; i++) sb.append(RND.nextInt(10));
        sb.append(LETTERS[RND.nextInt(LETTERS.length)]);
        return sb.toString();
    }

    private final String id;
    private final CardType type;
    private final TariffKind tariff;

    private LocalDate issueDate;
    private LocalDate validUntil;
    private int remainingTrips;
    private float balance;

    public Card(CardType type, TariffKind tariff) {
        this.id = generateId();
        this.type = type;
        this.tariff = tariff;
    }

    public String getId() { return id; }
    public CardType getType() { return type; }
    public TariffKind getTariff() { return tariff; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public int getRemainingTrips() { return remainingTrips; }
    public void setRemainingTrips(int remainingTrips) { this.remainingTrips = remainingTrips; }

    public float getBalance() { return balance; }
    public void setBalance(float balance) { this.balance = balance; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card card)) return false;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
