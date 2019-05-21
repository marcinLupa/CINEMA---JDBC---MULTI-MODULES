package service;


import exceptions.ExceptionCode;
import exceptions.MyException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class UserDataService {

    private Scanner sc = new Scanner(System.in);

    public int getInt(int scale) {

        String text = sc.nextLine();
        if (!text.matches("\\d+")) {
            throw new MyException(ExceptionCode.VALIDATION, "VALUE IS NOT DIGIT: " + text);
        }
        if (Integer.parseInt(text) < 1 || Integer.parseInt(text) > scale) {
            throw new MyException(ExceptionCode.VALIDATION, "RANGE OUT OF BOUND " + text);
        }

        return Integer.parseInt(text);
    }
    public String getString() {


        String text = sc.nextLine();
        if (!text.matches("[A-Za-z]+")) {
            throw new MyException(ExceptionCode.VALIDATION, "VALUE IS NOT DIGIT: " + text);
        }
        return text;
    }
    public boolean getYesOrNo() {


        String text = sc.nextLine();
        if (!text.matches("TAK|NIE|tak|nie+")) {
            throw new MyException(ExceptionCode.VALIDATION, "NOT CORRECT AGREEMENT FORMAT " + text);
        }
        if (text.equalsIgnoreCase("TAK")) {
            return true;
        }
        if (text.equalsIgnoreCase("NIE")) {
            return false;
        }
        else {
            throw new MyException(ExceptionCode.VALIDATION, "INCORRECT VALUE " + text);
        }
    }

    public BigDecimal getPrice() {

        String text = sc.nextLine();
        if (!text.matches("\\d.+")) {
            throw new MyException(ExceptionCode.VALIDATION, "VALUE IS NOT BIG DECIMAL: " + text);
        }
        if (new BigDecimal(text).compareTo(BigDecimal.ZERO) < 0) {
            throw new MyException(ExceptionCode.VALIDATION, "VALUE UNDER ZERO " + text);
        }

        return new BigDecimal(text);
    }

    public String getEmail() {

        String text = sc.nextLine();
        if (!text.matches("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")) {
            throw new MyException(ExceptionCode.VALIDATION,"WRONG FORMAT OF E-MAIL "+text);
        }

        return text;
    }
    public LocalDateTime getLocalDateTime() {

        String text = sc.nextLine();
        if(!text.matches("[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]")){
            throw new MyException(ExceptionCode.VALIDATION,"WRONG FORMAT OF LOCAL DATE TIME "+text);
        }
        return  LocalDateTime.parse(
                text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    public void close() {
        if (sc != null) {
            sc.close();
            sc = null;
        }
    }
}
