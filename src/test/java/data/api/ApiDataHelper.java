package data.api;

import lombok.Value;

public class ApiDataHelper {

    private static String getCardNumberWithSpaces(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cardNumber.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                result.append(" ");
            }
            result.append(cardNumber.charAt(i));
        }

        return result.toString();
    }

    public static String getSQLInjection(){
        return "Robert'); DROP TABLE users;--";
    }

    public static String getXSSInjection(){
        return "R<script>alert(1)</script>";
    }


    public static CardInfo getCard(String cardNumber, String year, String month, String holder, String cvc) {
       String number = getCardNumberWithSpaces(cardNumber);
       return new CardInfo(number, year, month, holder, cvc);
    }


    @Value
    public static class CardInfo {
        String number;
        String year;
        String month;
        String holder;
        String cvc;
    }
}
