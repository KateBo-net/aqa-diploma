package data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class DataHelper {

    private static final String approvedCardNumber = "4444 4444 4444 4441";
    private static final String declinedCardNumber = "4444 4444 4444 4442";
    static final String dateFormat = "yy";
    private static final Faker fakerEN = new Faker(Locale.ENGLISH);
    public static final Faker fakerRU = new Faker(new Locale("ru-RU"));

    public static String generateMonth(int bound) {
        return "0" + (new Random().nextInt(bound) + 1);
    }

    public static String generateYear(int shift) {
        return LocalDate.now().plusYears(shift).format(DateTimeFormatter.ofPattern(dateFormat));
    }

    public static String generateName() {
        return fakerEN.name().firstName();
    }

    public static String generateLastName() {
        return fakerEN.name().lastName();
    }

    public static String generateOwner() {
        return generateName() + " " + generateLastName();
    }

    public static String generateCyrillicOwner() {
        return fakerRU.name().firstName() + " " + fakerRU.name().lastName();
    }

    public static String generateNumber(int count) {
        StringBuilder sb = new StringBuilder();
        for (int number = 0; number < count; number++) {
            sb.append("#");
        }
        return fakerEN.numerify(sb.toString());
    }

    public static String generateSymbolString(int count) {
        StringBuilder sb = new StringBuilder();
        String[] symbols = {"<", "(", "[", "{", "^", "-", "=", "$", "!", "|", "]", "}", ")", "?", "*", "+", ".", ">"};
        for (int i = 0; i < count; i++) {
            sb.append(symbols[new Random().nextInt(symbols.length)]);
        }
        return sb.toString();
    }

    public static CardInfo generateValidCardInfo(int status) {
        if (status == 0) {
            return new CardInfo(approvedCardNumber, generateMonth(12), generateYear(3), generateOwner(), generateNumber(3));

        }
        return new CardInfo(declinedCardNumber, generateMonth(12), generateYear(3), generateOwner(), generateNumber(3));
    }

}
