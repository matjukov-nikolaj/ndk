using System;
using System.Collections.Generic;

namespace SlrSequence
{

    public enum CompletedStates
    {
        EMPTY = -1,
        ERROR = 0,
        NUMBER = 1,
        NUMBER16 = 2,
        NUMBER8 = 3,
        NUMBER2 = 4,
        DOUBLE = 5,
        EXP = 6,
        ID = 7,
        SINGLE_COMMENT = 8,
        MULTIPLE_COMMENT = 9,
        EXCLAMATION = 10,
        LESS = 11,
        GREATER = 12,
        OPEN_BRACKET = 13,
        CLOSE_BRACKET = 14,
        BRACE_OPEN_BRACKET = 15,
        BRACE_CLOSE_BRACKET = 16,
        SQUARE_OPEN_BRACKET = 17,
        SQUARE_CLOSE_BRACKET = 18,
        COMMA = 19,
        SEMICOLON = 20,
        PLUS = 21,
        MINUS = 22,
        EQUALLY = 23

    }

    public class CompletedStatesMethods
    {

        public CompletedStatesMethods()
        {
        }

        private IDictionary<int, CompletedStates> getCompletedState()
        {
            IDictionary<int, CompletedStates> map = new Dictionary<int, CompletedStates>();
            map[(int) CompletedStates.EMPTY] = CompletedStates.EMPTY;
            map[(int) CompletedStates.ERROR] = CompletedStates.ERROR;
            map[(int) CompletedStates.NUMBER] = CompletedStates.NUMBER;
            map[(int) CompletedStates.NUMBER16] = CompletedStates.NUMBER16;
            map[(int) CompletedStates.NUMBER8] = CompletedStates.NUMBER8;
            map[(int) CompletedStates.NUMBER2] = CompletedStates.NUMBER2;
            map[(int) CompletedStates.DOUBLE] = CompletedStates.DOUBLE;
            map[(int) CompletedStates.EXP] = CompletedStates.EXP;
            map[(int) CompletedStates.ID] = CompletedStates.ID;
            map[(int) CompletedStates.SINGLE_COMMENT] = CompletedStates.SINGLE_COMMENT;
            map[(int) CompletedStates.MULTIPLE_COMMENT] = CompletedStates.MULTIPLE_COMMENT;
            map[(int) CompletedStates.EXCLAMATION] = CompletedStates.EXCLAMATION;
            map[(int) CompletedStates.LESS] = CompletedStates.LESS;
            map[(int) CompletedStates.GREATER] = CompletedStates.GREATER;
            map[(int) CompletedStates.OPEN_BRACKET] = CompletedStates.OPEN_BRACKET;
            map[(int) CompletedStates.CLOSE_BRACKET] = CompletedStates.CLOSE_BRACKET;
            map[(int) CompletedStates.BRACE_OPEN_BRACKET] = CompletedStates.BRACE_OPEN_BRACKET;
            map[(int) CompletedStates.BRACE_CLOSE_BRACKET] = CompletedStates.BRACE_CLOSE_BRACKET;
            map[(int) CompletedStates.SQUARE_OPEN_BRACKET] = CompletedStates.SQUARE_OPEN_BRACKET;
            map[(int) CompletedStates.SQUARE_CLOSE_BRACKET] = CompletedStates.SQUARE_CLOSE_BRACKET;
            map[(int) CompletedStates.COMMA] = CompletedStates.COMMA;
            map[(int) CompletedStates.SEMICOLON] = CompletedStates.SEMICOLON;
            map[(int) CompletedStates.PLUS] = CompletedStates.PLUS;
            map[(int) CompletedStates.MINUS] = CompletedStates.MINUS;
            map[(int) CompletedStates.EQUALLY] = CompletedStates.EQUALLY;
            return map;
        }

        private static IDictionary<CompletedStates, String> enumToString()
        {
            IDictionary<CompletedStates, String> map = new Dictionary<CompletedStates, string>();
            map[CompletedStates.EMPTY] = "EMPTY";
            map[CompletedStates.ERROR] = "ERROR";
            map[CompletedStates.NUMBER] = "NUMBER";
            map[CompletedStates.NUMBER16] = "NUMBER16";
            map[CompletedStates.NUMBER8] = "NUMBER8";
            map[CompletedStates.NUMBER2] = "NUMBER2";
            map[CompletedStates.DOUBLE] = "DOUBLE";
            map[CompletedStates.EXP] = "EXP";
            map[CompletedStates.ID] = "ID";
            map[CompletedStates.SINGLE_COMMENT] = "SINGLE_COMMENT";
            map[CompletedStates.MULTIPLE_COMMENT] = "MULTIPLE_COMMENT";
            map[CompletedStates.EXCLAMATION] = "EXCLAMATION";
            map[CompletedStates.LESS] = "LESS";
            map[CompletedStates.GREATER] = "GREATER";
            map[CompletedStates.OPEN_BRACKET] = "OPEN_BRACKET";
            map[CompletedStates.CLOSE_BRACKET] = "CLOSE_BRACKET";
            map[CompletedStates.BRACE_OPEN_BRACKET] = "BRACE_OPEN_BRACKET";
            map[CompletedStates.BRACE_CLOSE_BRACKET] = "BRACE_CLOSE_BRACKET";
            map[CompletedStates.SQUARE_OPEN_BRACKET] = "SQUARE_OPEN_BRACKET";
            map[CompletedStates.SQUARE_CLOSE_BRACKET] = "SQUARE_CLOSE_BRACKET";
            map[CompletedStates.COMMA] = "COMMA";
            map[CompletedStates.SEMICOLON] = "SEMICOLON";
            map[CompletedStates.PLUS] = "PLUS";
            map[CompletedStates.MINUS] = "MINUS";
            map[CompletedStates.EQUALLY] = "EQUALLY";
            return map;
        }

        public String getStr(CompletedStates state)
        {

            return enumToString()[state];
        }

        public CompletedStates createFromInteger(int mode)
        {
            return getCompletedState()[mode];
        }
    }

}