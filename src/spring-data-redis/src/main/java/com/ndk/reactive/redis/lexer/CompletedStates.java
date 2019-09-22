package com.taafl;

import java.util.HashMap;
import java.util.Map;

public enum CompletedStates {
    EMPTY(-1),
    ERROR(0),
    NUMBER(1),
    NUMBER16(2),
    NUMBER8(3),
    NUMBER2(4),
    DOUBLE(5),
    EXP(6),
    ID(7),
    SINGLE_COMMENT(8),
    MULTIPLE_COMMENT(9),
    EXCLAMATION(10),
    LESS(11),
    GREATER(12),
    OPEN_BRACKET(13),
    CLOSE_BRACKET(14),
    BRACE_OPEN_BRACKET(15),
    BRACE_CLOSE_BRACKET(16),
    SQUARE_OPEN_BRACKET(17),
    SQUARE_CLOSE_BRACKET(18),
    COMMA(19),
    SEMICOLON(20),
    PLUS(21),
    MINUS(22),
    EQUALLY(23);



    private final Integer value;

    CompletedStates(final Integer value) {
        this.value = value;
    }

    private static Map<Integer, CompletedStates> getCompletedState() {
        HashMap<Integer, CompletedStates> map = new HashMap<>();
        map.put(EMPTY.value, EMPTY);
        map.put(ERROR.value, ERROR);
        map.put(NUMBER.value, NUMBER);
        map.put(NUMBER16.value, NUMBER16);
        map.put(NUMBER8.value, NUMBER8);
        map.put(NUMBER2.value, NUMBER2);
        map.put(DOUBLE.value, DOUBLE);
        map.put(EXP.value, EXP);
        map.put(ID.value, ID);
        map.put(SINGLE_COMMENT.value, SINGLE_COMMENT);
        map.put(MULTIPLE_COMMENT.value, MULTIPLE_COMMENT);
        map.put(EXCLAMATION.value, EXCLAMATION);
        map.put(LESS.value, LESS);
        map.put(GREATER.value, GREATER);
        map.put(OPEN_BRACKET.value, OPEN_BRACKET);
        map.put(CLOSE_BRACKET.value, CLOSE_BRACKET);
        map.put(BRACE_OPEN_BRACKET.value, BRACE_OPEN_BRACKET);
        map.put(BRACE_CLOSE_BRACKET.value, BRACE_CLOSE_BRACKET);
        map.put(SQUARE_OPEN_BRACKET.value, SQUARE_OPEN_BRACKET);
        map.put(SQUARE_CLOSE_BRACKET.value, SQUARE_CLOSE_BRACKET);
        map.put(COMMA.value, COMMA);
        map.put(SEMICOLON.value, SEMICOLON);
        map.put(PLUS.value, PLUS);
        map.put(MINUS.value, MINUS);
        map.put(EQUALLY.value, EQUALLY);
        return map;
    }

    private static Map<CompletedStates, String> enumToString() {
        HashMap<CompletedStates, String> map = new HashMap<>();
        map.put(EMPTY, "EMPTY");
        map.put(ERROR, "ERROR");
        map.put(NUMBER, "NUMBER");
        map.put(NUMBER16, "NUMBER16");
        map.put(NUMBER8, "NUMBER8");
        map.put(NUMBER2, "NUMBER2");
        map.put(DOUBLE, "DOUBLE");
        map.put(EXP, "EXP");
        map.put(ID, "IDENTIFIER");
        map.put(SINGLE_COMMENT, "SINGLE_COMMENT");
        map.put(MULTIPLE_COMMENT, "MULTIPLE_COMMENT");
        map.put(EXCLAMATION, "EXCLAMATION");
        map.put(LESS, "LESS");
        map.put(GREATER, "GREATER");
        map.put(OPEN_BRACKET, "OPEN_BRACKET");
        map.put(CLOSE_BRACKET, "CLOSE_BRACKET");
        map.put(BRACE_OPEN_BRACKET, "BRACE_OPEN_BRACKET");
        map.put(BRACE_CLOSE_BRACKET, "BRACE_CLOSE_BRACKET");
        map.put(SQUARE_OPEN_BRACKET, "SQUARE_OPEN_BRACKET");
        map.put(SQUARE_CLOSE_BRACKET, "SQUARE_CLOSE_BRACKET");
        map.put(COMMA, "COMMA");
        map.put(SEMICOLON, "SEMICOLON");
        map.put(PLUS, "PLUS");
        map.put(MINUS, "MINUS");
        map.put(EQUALLY, "EQUALLY");
        return map;
    }

    public static String getStr(CompletedStates state) {

        return enumToString().get(state);
    }

    public static CompletedStates createFromInteger(Integer mode) {
        return getCompletedState().get(mode);
    }
}
