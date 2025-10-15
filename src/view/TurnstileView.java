package view;

import controller.TurnstileController;
import model.Card;
import model.CardType;
import model.TariffKind;

import java.util.Map;
import java.util.Scanner;

public class TurnstileView {
    private final TurnstileController controller;
    private final Scanner sc = new Scanner(System.in);
    private static final String ID_PATTERN = "\\d{10}[A-Z]";

    public TurnstileView(TurnstileController controller) {
        this.controller = controller;
    }

    public void run() {
        while (true) {
            printMenu();
            String input = sc.nextLine().trim();

            if (input.matches(ID_PATTERN)) {
                boolean ok = controller.tryPass(input);
                System.out.println(Pretty.formatPassResult(ok));
                continue;
            }

            switch (input) {
                case "1" -> issueCardFlow();
                case "2" -> statisticsFlow();
                case "3" -> listCards();
                case "4" -> topUpFlow();
                case "0" -> {
                    System.out.println("Вихід... До побачення!");
                    return;
                }
                default -> System.out.println("Невідома команда або неправильний ID. Спробуйте ще раз.");
            }
        }
    }

    private void printMenu() {
        System.out.println("""

                ╭─ СИСТЕМА КЕРУВАННЯ ТУРНІКЕТОМ ───────────────────╮
                │ 1. Випустити картку                              │
                │ 2. Статистика                                    │
                │ 3. Перелік усіх карток                           │
                │ 4. Поповнити накопичувальну (грн)                │
                │ 0. Вихід                                         │
                ╰──────────────────────────────────────────────────╯
                 АБО введіть ID (10 цифр + літера) для проходу:"""
        );
    }

    private void statisticsFlow() {
        try {
            System.out.println("""

                    ╭─ СТАТИСТИКА ─────────────────────────────────────╮
                    │ 1. Звіт сумарний                                 │
                    │ 2. Звіт по типах                                 │
                    │ 0. Повернутись в меню                            │
                    ╰──────────────────────────────────────────────────╯"""
            );

            String pick = sc.nextLine().trim();
            long allowed = controller.getAllowedTotal();
            long denied  = controller.getDeniedTotal();
            Map<CardType, long[]> byType = controller.getStatsByType();
            switch (pick) {
                case "1" -> System.out.println(Pretty.formatSummary(allowed, denied));
                case "2" -> System.out.println(Pretty.formatByType(byType));
                case "0" -> {}
                default -> throw new IllegalArgumentException("Невірний вибір команди, спробуйте ще раз.");
            }
        } catch (Exception ex) {
            System.out.println("Помилка: " + ex.getMessage());
        }
    }

    private void issueCardFlow() {
        try {
            System.out.println("""

                    ╭─ Оберіть тип картки ─────────────────────────────╮
                    │ 1. Учнівська                                     │
                    │ 2. Студентська                                   │
                    │ 3. Звичайна                                      │
                    ╰──────────────────────────────────────────────────╯"""
            );

            String line = sc.nextLine().trim();
            int t;
            try {
                t = Integer.parseInt(line);
            } catch (NumberFormatException ex) {
                System.out.println("Невірний вибір: очікується 1, 2 або 3.");
                return;
            }
            CardType type = switch (t) {
                case 1 -> CardType.PUPIL;
                case 2 -> CardType.STUDENT;
                case 3 -> CardType.REGULAR;
                default -> throw new IllegalArgumentException("Невірний вибір типу.");
            };

            System.out.println("""

                    ╭─ Оберіть тип картки ─────────────────────────────╮
                    │ 1. На місяць                                     │
                    │ 2. На 10 днів                                    │
                    │ 3. 5 поїздок                                     │
                    │ 4. 10 поїздок                                    │
                    │ 5. Накопичувальна (лише для звичайної)           │
                    ╰──────────────────────────────────────────────────╯"""
            );

            line = sc.nextLine().trim();
            int k;
            try {
                k = Integer.parseInt(line);
            } catch (NumberFormatException ex) {
                System.out.println("Невірний вибір: очікується 1, 2 або 3.");
                return;
            }
            TariffKind tariff = switch (k) {
                case 1 -> TariffKind.MONTH;
                case 2 -> TariffKind.TEN_DAYS;
                case 3 -> TariffKind.TRIPS_5;
                case 4 -> TariffKind.TRIPS_10;
                case 5 -> TariffKind.STORED_VALUE;
                default -> {
                    System.out.println("Невірний вибір тарифу.");
                    yield null;
                }
            };
            if (tariff == null) return;

            Card c = controller.issue(type, tariff);
            System.out.println("╭─ Картку випущено ────────────────────────────────╮");
            System.out.println(Pretty.cardDetails(c));
        } catch (Exception ex) {
            System.out.println("Сталася помилка: " + ex.getMessage());
        }
    }

    private void topUpFlow() {
        System.out.print("Введіть id картки: ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("Помилка: порожній рядок.");
            return;
        }
        float amount;
        while (true) {
            System.out.print("Сума поповнення (грн, напр. 50.00): ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("Помилка: порожній рядок. Введіть число у форматі 50.00.");
                continue;
            }
            try {
                amount = Float.parseFloat(line);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Невірний формат. Спробуйте ще раз (наприклад, 20.50).");
            }
        }

        boolean ok = controller.topUpStoredValue(id, amount);
        if (ok) {
            System.out.println(Pretty.formatTopUpResult(id, amount));
        } else {
            System.out.println("Помилка поповнення (перевірте ID і тип картки).");
        }
    }

    private void listCards() {
        Map<String, Card> all = controller.dumpRegistry();
        if (all.isEmpty()) {
            System.out.println("Реєстр порожній.");
            return;
        }
        System.out.println("╭─── Видані ──┬─── картки ───┬────────────────┬─────────┬──────────────────────┬─────────────────────╮");
        String header = String.format(
                "| %-11s │ %-12s │ %-14s │ %-7s │ %-20s │ %-20s|",
                "ID", "Тип", "Тариф", "Поїздок", "Баланс", "Дійсна до (включно)"
        );
        String line = "├─────────────┼──────────────┼────────────────┼─────────┼──────────────────────┼─────────────────────┤";
        System.out.println(header);
        System.out.println(line);
        all.values().forEach(c -> System.out.println(Pretty.cardRow(c)));
        System.out.println("╰─────────────┴──────────────┴────────────────┴─────────┴──────────────────────┴─────────────────────╯");
    }
}
