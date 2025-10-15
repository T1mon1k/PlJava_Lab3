package view;

import model.Card;
import model.CardType;
import model.TariffKind;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public final class Pretty {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DecimalFormat MONEY;

    static {
        DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.forLanguageTag("uk-UA"));
        sym.setDecimalSeparator(',');
        sym.setGroupingSeparator(' ');
        MONEY = new DecimalFormat("#,##0.00", sym);
    }

    private Pretty() {}

    public static String typeName(CardType t) {
        return switch (t) {
            case PUPIL -> "Учнівська";
            case STUDENT -> "Студентська";
            case REGULAR -> "Звичайна";
        };
    }

    public static String tariffName(TariffKind k) {
        return switch (k) {
            case MONTH -> "на місяць";
            case TEN_DAYS -> "на 10 днів";
            case TRIPS_5 -> "5 поїздок";
            case TRIPS_10 -> "10 поїздок";
            case STORED_VALUE -> "Накопичувальна";
        };
    }

    public static String money(float uah) {
        return MONEY.format(uah) + " грн";
    }

    public static String dateOrDash(java.time.LocalDate d) {
        return d == null ? "-" : DATE.format(d);
    }

    public static String cardRow(Card c) {
        String fullId = c.getId();
        String trips = c.getRemainingTrips() > 0 ? String.valueOf(c.getRemainingTrips()) : "-";
        String balance = c.getTariff() == TariffKind.STORED_VALUE ? money(c.getBalance()) : "-";
        String valid = (c.getTariff() == TariffKind.MONTH || c.getTariff() == TariffKind.TEN_DAYS)
                ? dateOrDash(c.getValidUntil()) : "-";
        return String.format(
                "│ %-11s │ %-12s │ %-14s │ %-7s │ %-20s │ %-20s│",
                fullId, typeName(c.getType()), tariffName(c.getTariff()), trips, balance, valid
        );
    }

    public static String cardDetails(Card c) {
        String trips   = c.getRemainingTrips() > 0 ? String.valueOf(c.getRemainingTrips()) : "-";
        String balance = c.getTariff() == TariffKind.STORED_VALUE ? money(c.getBalance()) : "-";
        String valid   = (c.getTariff() == TariffKind.MONTH || c.getTariff() == TariffKind.TEN_DAYS)
                ? dateOrDash(c.getValidUntil()) : "-";

        return String.format(
                        "│ ID:                  %-28s│%n" +
                        "│ Тип:                 %-28s│%n" +
                        "│ Тариф:               %-28s│%n" +
                        "│ Поїздок лишилось:    %-28s│%n" +
                        "│ Баланс:              %-28s│%n" +
                        "│ Дійсна до (включно): %-28s│%n" +
                        "╰──────────────────────────────────────────────────╯",
                c.getId(), typeName(c.getType()), tariffName(c.getTariff()), trips, balance, valid
        );
    }

    public static String formatSummary(long allowed, long denied) {
        return String.format(
                "╭─ ЗВІТ (Сумарно)──────────────────────────────────╮\n" +
                "│ Дозволено: %-38s│%n" +
                "│ Відмовлено: %-37s│%n" +
                "╰──────────────────────────────────────────────────╯",
                allowed, denied
        );
    }

    public static String formatByType(Map<CardType, long[]> statsByType) {
        String title = "ЗВІТ (По типах карток)";
        String[] lines = new String[CardType.values().length];
        int i = 0;
        for (CardType t : CardType.values()) {
            long[] vals = statsByType.get(t);
            lines[i++] = String.format("%-11s : дозв=%-11d  відм=%-11d", typeName(t), vals[0], vals[1]);
        }
        int width = title.length();
        for (String l : lines) if (l.length() > width) width = l.length();
        String hor = "─".repeat(width + 2);
        StringBuilder sb = new StringBuilder();
        sb.append("╭─ ").append(title).append(" ").append("─".repeat(width - title.length() - 1)).append("╮\n");
        for (String l : lines) {
            sb.append("│ ").append(String.format("%-" + width + "s", l)).append(" │\n");
        }
        sb.append("╰").append(hor).append("╯");
        return sb.toString();
    }

    public static String formatPassResult(boolean ok) {
        String msg = ok ? "✅ Прохід ДОЗВОЛЕНО" : "❌ Прохід ЗАБОРОНЕНО";
        int width = msg.length();
        String hor = "─".repeat(width + 3);
        return String.format(
                "╭%1$s╮%n" +
                "│ %2$s │%n" +
                "╰%1$s╯",
                hor, msg
        );
    }

    public static String formatTopUpResult(String id, float amount) {
        String msg = String.format("✅ Баланс картки %s поповнено на %.2f грн", id, amount);
        int width = msg.length();
        String hor = "─".repeat(width + 3);
        return String.format(
                "╭%1$s╮%n" +
                "│ %2$s │%n" +
                "╰%1$s╯",
                hor, msg
        );
    }
}
