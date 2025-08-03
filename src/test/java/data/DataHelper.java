package data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;

public class DataHelper {

    private DataHelper() {
    }

    private static final Faker faker = new Faker(new Locale("en"));
    private static final Random random = new Random();

    public static class CommonValues {

        /**
         * Метод для генерации буквенных значений.
         *
         * @param length - необходимое количество латинских букв.
         */
        public static String generateLetters(int length) {
            return faker.letterify("?".repeat(length)).toUpperCase();
        }


        /**
         * Общее невалидное значение - символы.
         *
         * @param lenght - необходимое количество символов.
         */
        public static String invalidValueSymbols(int lenght) {
            String symbols = "!@#$%^&*()_+={}[]|:;<>?,./";
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < lenght; i++) {
                int index = random.nextInt(symbols.length());
                result.append(symbols.charAt(index));
            }
            return result.toString();

        }


        /**
         * Общее невалидное значение - кириллица.
         *
         * @param lenght - необходимое количество символов.
         */
        public static String invalidValueCyrillic(int lenght) {
            String cyrillic = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < lenght; i++) {
                int index = random.nextInt(cyrillic.length());
                result.append(cyrillic.charAt(index));
            }
            return result.toString();
        }


        /**
         * Общее невалидное значение - пробелы.
         */
        public static String invalidValueSpace() {
            return "   ";
        }


        /**
         * Общее невалидное значение - пустое поле.
         */
        public static String invalidValueEmpty() {
            return "";
        }


        /**
         * Общий метод для генерации цифр.
         *
         * @param length - необходимое количество цифр.
         */
        public static String generateDigits(int length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(faker.number().randomDigit());
            }
            return sb.toString();
        }

    }


    public static class CardNumber {

        /**
         * Номер специальной карты APPROVED.
         */
        public static String numberApprovedCard() {
            return "1111222233334444";
        }


        /**
         * Номер специальной карты DECLINED.
         */
        public static String numberDeclinedCard() {
            return "5555666677778888";
        }


        /**
         * Невалидный номер карты - 0000 0000 0000 0000.
         */
        public static String invalidNumberCardAllZeros() {
            return "0000000000000000";
        }


        /**
         * Невалидный номер карты - 4111 1111 1111 1111.
         */
        public static String invalidNumberCardSpec() {
            return "4111111111111111";
        }

    }


    public static class Month {

        /**
         * Валидный месяц.
         */
        public static String validMonth() {
            return String.format("%02d", faker.number().numberBetween(1, 13));
        }


        /**
         * Валидный месяц - текущий месяц.
         */
        public static String validCurrentMonth() {
            return String.format("%02d", LocalDate.now().getMonthValue());
        }


        /**
         * Валидный месяц - 01.
         */
        public static String validMonth01() {
            return "01";
        }


        /**
         * Валидный месяц - 12.
         */
        public static String validMonth12() {
            return "12";
        }


        /**
         * Невалидный месяц для текущего года - текущий месяц минус 1.
         */
        public static String invalidMonthCurrentMonthMinus1() {
            LocalDate previousMonth = LocalDate.now().minusMonths(1);
            int month = previousMonth.getMonthValue();
            return String.format("%02d", month);
        }


        /**
         * /Невалидный месяц - 00.
         */
        public static String invalidMonth00() {
            return "00";
        }


        /**
         * Невалидный месяц - 13
         */
        public static String invalidMonth13() {
            return "13";
        }
    }


    public static class CardYear {

        /**
         * Генерация года с учётом смещения от текущего года.
         *
         * @param offset - количество лет относительно текущего года (может быть отрицательным).
         * @return последние 2 цифры нужного года.
         */
        public static String generateYearOffset(int offset) {
            return String.valueOf(Year.now().plusYears(offset).getValue()).substring(2);
        }
    }


    public static class Holder {

        /**
         * Генерация имени держателя карты.
         *
         * @param nameParts    - количество частей имени (1 — обычное имя, 2+ — множественные части)
         * @param separator    - разделитель между частями (например, "-", "'", " ")
         * @param withLastName - добавлять ли фамилию в конец
         * @return строка длиной не более 50 символов
         */
        private static String generateHolder(int nameParts, String separator, boolean withLastName) {
            StringBuilder holder = new StringBuilder();
            for (int i = 0; i < nameParts; i++) {
                holder.append(faker.name().firstName().toUpperCase());
                if (i < nameParts - 1) {
                    holder.append(separator);
                }
            }
            if (withLastName) {
                holder.append(" ").append(faker.name().lastName().toUpperCase());
            }
            String result = holder.toString();
            return result.length() > 50 ? result.substring(0, 50) : result;
        }


        /**
         * Валидный держатель - 2 слова на латинице через пробел.
         */
        public static String validHolder() {
            return generateHolder(1, "", true);
        }


        /**
         * Валидный держатель - имя на латинице через дефис.
         */
        public static String validHolderHyphenate() {
            return generateHolder(2, "-", true);
        }


        /**
         * Валидный держатель - имя на латинице через апостроф.
         */
        public static String validHolderApostrophe() {
            return generateHolder(2, "'", true);
        }


        /**
         * Валидный держатель - множественные части имени.
         */
        public static String validHolderMultipleNames() {
            return generateHolder(3, " ", true);
        }


        /**
         * Валидный держатель - минимальное валидное значение (2 буквы через пробел)
         */
        public static String validHolderTwoLettersAndSpace() {
            String letters = CommonValues.generateLetters(2);
            return letters.charAt(0) + " " + letters.charAt(1);

        }


        /**
         * Валидный держатель - минимальное валидное значение плюс 1 (3 буквы и один пробел)
         */
        public static String validHolderThreeLettersAndSpace() {
            String letters = CommonValues.generateLetters(3);
            return letters.charAt(0) + " " + letters.charAt(1) + letters.charAt(2);

        }


        /**
         * Невалидный держатель - кириллица.
         */
        public static String invalidHolderCyrillic() {
            Faker fakerRu = new Faker(new Locale("ru"));
            return fakerRu.name().fullName();
        }


        /**
         * Невалидный держатель - одно слово на латинице.
         */
        public static String invalidHolderOneWord() {
            return generateHolder(1, "", false);
        }
    }


    public static class CVC {

        /**
         * Валидный CVC - 000.
         */
        public static String validCVC000() {
            return "000";
        }


        /**
         * Валидный CVC - 999.
         */
        public static String validCVC999() {
            return "999";
        }
    }


}
