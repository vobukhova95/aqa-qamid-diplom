package data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataHelper {

    private DataHelper() {
    }

    private static final Faker faker = new Faker(new Locale("en"));
    private static final Random random = new Random();

    public static class CommonValues {

        /**
         * Метод, который возвращает статус "APPROVED".
         */
        public static String getStatusApproved() {
            return "APPROVED";
        }


        /**
         * Метод, который возвращает статус "DECLINED".
         */
        public static String getStatusDeclined() {
            return "DECLINED";
        }


        /**
         * Метод, который возвращает стоимость путешествия.
         */
        public static int getCostTravel() {
            return 4_500_000;
        }


        /**
         * Метод, который возвращает текст ошибки "Неверный формат".
         */
        public static String getErrorTextIncorrectFormat(){
            return "Неверный формат";
        }


        /**
         * Метод, который возвращает текст ошибки "Поле обязательно для заполнения".
         */
        public static String getErrorTextRequiredField() {
            return "Поле обязательно для заполнения";
        }

        /**
         * Метод, который возвращает текст ошибки "Неверно указан срок действия карты".
         */
        public static String getErrorTextExpirationDateIsIncorrect() {
            return "Неверно указан срок действия карты";
        }

        /**
         * Метод, который возвращает текст ошибки "Истёк срок действия карты".
         */
        public static String getErrorTextCardExpired() {
            return "Истёк срок действия карты";
        }



        /**
         * Метод, который подготавливает ожидаемое значение и обрезает строку до максимальной валидной длины.
         * @param invalidValue - невалидное по количеству символов значение.
         * @param maxLength - максимальное допустимое количестов символов в строке.
         */
        public static String truncateToMaxLength (String invalidValue, int maxLength){
         return invalidValue.substring(0, maxLength);
        }


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
            String symbols = "!@#$%^&*()_+={}[]|:;<>?,./`'-";
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
        public static String getValueEmpty() {
            return "";
        }


        /**
         * Общий метод для генерации цифр.
         *
         * @param length - необходимое количество цифр.
         */
        public static String generateDigits(int length) {
            String pattern = "#".repeat(length);
            return faker.numerify(pattern);
        }

    }


    public static class CardNumber {

        /**
         * Номер специальной карты APPROVED.
         */
        public static String approvedCardNumber() {
            return "1111222233334444";
        }


        /**
         * Номер специальной карты DECLINED.
         */
        public static String declinedCardNumber() {
            return "5555666677778888";
        }


        /**
         * Невалидный номер карты - 0000 0000 0000 0000.
         */
        public static String invalidCardNumberAllZeros() {
            return "0000000000000000";
        }


        /**
         * Невалидный номер карты - 4111 1111 1111 1111.
         */
        public static String invalidCardNumberSpec() {
            return "4111111111111111";
        }

        public static String trimTo16Digits (String invalidCardNumber) {
            StringBuilder sb = new StringBuilder();
            sb.append(invalidCardNumber, 0, 4).append(" ");
            sb.append(invalidCardNumber, 4, 8).append(" ");
            sb.append(invalidCardNumber, 8, 12).append(" ");
            sb.append(invalidCardNumber, 12, 16);
            return sb.toString();
        }

    }


    public static class Month {

      private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM");

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
            return LocalDate.now().format(formatter);
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
            return LocalDate.now().minusMonths(1).format(formatter);
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
       private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy");

        /**
         * Генерация года с учётом смещения от текущего года.
         *
         * @param offset - количество лет относительно текущего года (может быть отрицательным).
         * @return последние 2 цифры нужного года.
         */
        public static String generateYearOffset(int offset) {
            return LocalDate.now().plusYears(offset).format(formatter);
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
