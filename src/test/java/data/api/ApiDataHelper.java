package data.api;

import lombok.Value;

public class ApiDataHelper {

    private static String getCardNumberWithSpaces (String cardNumber) {
        if(cardNumber == null || cardNumber.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cardNumber, 0, 4).append(" ");
        stringBuilder.append(cardNumber, 4,8 ).append(" ");
        stringBuilder.append(cardNumber, 8, 12).append(" ");
        stringBuilder.append(cardNumber, 12, 16);
        return stringBuilder.toString();
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
