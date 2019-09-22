using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using System.Security.Cryptography.X509Certificates;

namespace SlrSequence
{
    public class Lexer
    {
        private List<List<int>> table;

        public const String IDENTIFIER = "#IDENTIFIER#";

        public const String NUMBER = "#NUMBER#";

        private List<String> identifiers;

        private List<String> numbers;
            
        private IDictionary<char, int> idByChar;

        private IDictionary<int, char> charById;

        private IDictionary<int, CompletedStates> finalStates;

        private int currState;

        private String currStr;

        private ISet<String> keyWords;

        private CompletedStatesMethods csMethods;

        private String result;

        public Lexer()
        {
            this.table = new List<List<int>>();
            this.idByChar = new SortedDictionary<char, int>();
            this.charById = new SortedDictionary<int, char>();
            this.finalStates = new SortedDictionary<int, CompletedStates>();
            this.keyWords = new SortedSet<string>();
            this.currState = 0;
            this.currStr = "";
            this.result = "";
            this.fillFields();
            this.csMethods = new CompletedStatesMethods();
            identifiers = new List<string>();
            numbers = new List<string>();
        }
        
        public void clean() {
            this.currState = 0;
            this.currStr = "";
            this.result = "";
            identifiers = new List<string>();
            numbers = new List<string>();
        }

        private void fillFields()
        {
            this.fillTable();
            this.fillCharById();
            this.fillIdByChar();
            this.fillFinalStates();
            this.fillKeyWords();
        }

        private void fillTable()
        {
            List<int> row0 = new List<int>(new []{ 1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 20, 20, 20, 20, 20, 20, 20, 0, 0, 0, 20,
                -1, 22, -1, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 39, 40, 28, 29, 30,
                31, 32, 33, 34, 35, 36, 37, 38, 41});
            List<int> row1 = new List<int>(new []{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, -1, 10, -1, -1, 16, -1, 2, 6, 6, 6, 7, 13,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1});
                
            List<int> row2 = new List<int>(new []{3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1});
                
            List<int> row3 = new List<int>(new []{3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, -1, 4, 4, 4, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1});
                
            List<int> row4 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            List<int> row5 = new List<int>(new []{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, -1, -1, -1, -1, 16, -1, -1, 6, 6, 6, -1,
                13, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1});
            List<int> row6 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row7 = new List<int>(new []{8, 8, 8, 8, 8, 8, 8, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row8 = new List<int>(new []{8, 8, 8, 8, 8, 8, 8, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, 9, 9, 9, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1});

            List<int> row9 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});

            List<int> row10 = new List<int>(new []{11, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row11 = new List<int>(new []{11, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12,
                12, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row12 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row13 = new List<int>(new []{14, 14, 14, 14, 14, 14, 14, 14, 14, 14, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row14 = new List<int>(new []{14, 14, 14, 14, 14, 14, 14, 14, 14, 14, -1, -1, -1, -1, 16, -1, -1, 15,
                15, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row15 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
                
            List<int> row16 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 17, 17,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});

            List<int> row17 = new List<int>(new []{18, 18, 18, 18, 18, 18, 18, 18, 18, 18, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row18 = new List<int>(new []{18, 18, 18, 18, 18, 18, 18, 18, 18, 18, -1, -1, -1, -1, -1, -1, -1, 19,
                19, 19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});

            List<int> row19 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
                
            List<int> row20 = new List<int>(new []{20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 21,
                21, 21, 20, -1, -1, -1, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});

            List<int> row21 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row22 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, 23, 25, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row23 = new List<int>(new []{23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
                24, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});

            List<int> row24 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});

            List<int> row25 = new List<int>(new []{25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25,
                25, 25, 25, 25, 25, 26, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
                
            List<int> row26 = new List<int>(new []{25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25,
                25, 25, 25, 25, 27, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row27 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row28 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});

            List<int> row29 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row30 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row31 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row32 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row33 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row34 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row35 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});

            List<int> row36 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row37 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
            List<int> row38 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
               
                
            List<int> row39 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
                
            List<int> row40 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
            
               
                
            List<int> row41 = new List<int>(new []{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
                
            this.table.Add(row0);
            this.table.Add(row1);
            this.table.Add(row2);
            this.table.Add(row3);
            this.table.Add(row4);
            this.table.Add(row5);
            this.table.Add(row6);
            this.table.Add(row7);
            this.table.Add(row8);
            this.table.Add(row9);
            this.table.Add(row10);
            this.table.Add(row11);
            this.table.Add(row12);
            this.table.Add(row13);
            this.table.Add(row14);
            this.table.Add(row15);
            this.table.Add(row16);
            this.table.Add(row17);
            this.table.Add(row18);
            this.table.Add(row19);
            this.table.Add(row20);
            this.table.Add(row21);
            this.table.Add(row22);
            this.table.Add(row23);
            this.table.Add(row24);
            this.table.Add(row25);
            this.table.Add(row26);
            this.table.Add(row27);
            this.table.Add(row28);
            this.table.Add(row29);
            this.table.Add(row30);
            this.table.Add(row31);
            this.table.Add(row32);
            this.table.Add(row33);
            this.table.Add(row34);
            this.table.Add(row35);
            this.table.Add(row36);
            this.table.Add(row37);
            this.table.Add(row38);
            this.table.Add(row39);
            this.table.Add(row40);
            this.table.Add(row41);
        }

        private void fillCharById()
        {
            this.charById[0] = '0';
            this.charById[1] = '1';
            this.charById[2] = '2';
            this.charById[3] = '3';
            this.charById[4] = '4';
            this.charById[5] = '5';
            this.charById[6] = '6';
            this.charById[7] = '7';
            this.charById[8] = '8';
            this.charById[9] = '9';
            this.charById[10] = 'a';
            this.charById[11] = 'b';
            this.charById[12] = 'c';
            this.charById[13] = 'd';
            this.charById[14] = 'e';
            this.charById[15] = 'f';
            this.charById[16] = 'x';
            this.charById[17] = ' ';
            this.charById[18] = '\n';
            this.charById[19] = '\t';
            this.charById[20] = 'o';
            this.charById[21] = '.';
            this.charById[22] = '/';
            this.charById[23] = '*';
            this.charById[24] = 'g';
            this.charById[25] = 'h';
            this.charById[26] = 'i';
            this.charById[27] = 'j';
            this.charById[28] = 'k';
            this.charById[29] = 'l';
            this.charById[30] = 'm';
            this.charById[31] = 'n';
            this.charById[32] = 'p';
            this.charById[33] = 'q';
            this.charById[34] = 'r';
            this.charById[35] = 's';
            this.charById[36] = 't';
            this.charById[37] = 'u';
            this.charById[38] = 'v';
            this.charById[39] = 'w';
            this.charById[40] = 'y';
            this.charById[41] = 'z';
            this.charById[42] = '+';
            this.charById[43] = '-';
            this.charById[44] = '!';
            this.charById[45] = '<';
            this.charById[46] = '>';
            this.charById[47] = '(';
            this.charById[48] = ')';
            this.charById[49] = '{';
            this.charById[50] = '}';
            this.charById[51] = '[';
            this.charById[52] = ']';
            this.charById[53] = ',';
            this.charById[54] = ';';
            this.charById[55] = '=';
        }

        private void fillIdByChar()
        {
            this.idByChar['0'] = 0;
            this.idByChar['1'] = 1;
            this.idByChar['2'] = 2;
            this.idByChar['3'] = 3;
            this.idByChar['4'] = 4;
            this.idByChar['5'] = 5;
            this.idByChar['6'] = 6;
            this.idByChar['7'] = 7;
            this.idByChar['8'] = 8;
            this.idByChar['9'] = 9;
            this.idByChar['a'] = 10;
            this.idByChar['b'] = 11;
            this.idByChar['c'] = 12;
            this.idByChar['d'] = 13;
            this.idByChar['e'] = 14;
            this.idByChar['f'] = 15;
            this.idByChar['x'] = 16;
            this.idByChar[' '] = 17;
            this.idByChar['\n'] = 18;
            this.idByChar['\t'] = 19;
            this.idByChar['o'] = 20;
            this.idByChar['.'] = 21;
            this.idByChar['/'] = 22;
            this.idByChar['*'] = 23;
            this.idByChar['g'] = 24;
            this.idByChar['h'] = 25;
            this.idByChar['i'] = 26;
            this.idByChar['j'] = 27;
            this.idByChar['k'] = 28;
            this.idByChar['l'] = 29;
            this.idByChar['m'] = 30;
            this.idByChar['n'] = 31;
            this.idByChar['p'] = 32;
            this.idByChar['q'] = 33;
            this.idByChar['r'] = 34;
            this.idByChar['s'] = 35;
            this.idByChar['t'] = 36;
            this.idByChar['u'] = 37;
            this.idByChar['v'] = 38;
            this.idByChar['w'] = 39;
            this.idByChar['y'] = 40;
            this.idByChar['z'] = 41;
            this.idByChar['+'] = 42;
            this.idByChar['-'] = 43;
            this.idByChar['!'] = 44;
            this.idByChar['<'] = 45;
            this.idByChar['>'] = 46;
            this.idByChar['('] = 47;
            this.idByChar[')'] = 48;
            this.idByChar['{'] = 49;
            this.idByChar['}'] = 50;
            this.idByChar['['] = 51;
            this.idByChar[']'] = 52;
            this.idByChar[','] = 53;
            this.idByChar[';'] = 54;
            this.idByChar['='] = 55;
        }

        private void fillFinalStates()
        {
            this.finalStates[4] = CompletedStates.NUMBER16;
            this.finalStates[6] = CompletedStates.NUMBER;
            this.finalStates[8] = CompletedStates.NUMBER8;
            this.finalStates[12] = CompletedStates.NUMBER2;
            this.finalStates[15] = CompletedStates.DOUBLE;
            this.finalStates[19] = CompletedStates.EXP;
            this.finalStates[21] = CompletedStates.ID;
            this.finalStates[24] = CompletedStates.SINGLE_COMMENT;
            this.finalStates[27] = CompletedStates.MULTIPLE_COMMENT;
            this.finalStates[28] = CompletedStates.EXCLAMATION;
            this.finalStates[29] = CompletedStates.LESS;
            this.finalStates[30] = CompletedStates.GREATER;
            this.finalStates[31] = CompletedStates.OPEN_BRACKET;
            this.finalStates[32] = CompletedStates.CLOSE_BRACKET;
            this.finalStates[33] = CompletedStates.BRACE_OPEN_BRACKET;
            this.finalStates[34] = CompletedStates.BRACE_CLOSE_BRACKET;
            this.finalStates[35] = CompletedStates.SQUARE_OPEN_BRACKET;
            this.finalStates[36] = CompletedStates.SQUARE_CLOSE_BRACKET;
            this.finalStates[37] = CompletedStates.COMMA;
            this.finalStates[38] = CompletedStates.SEMICOLON;
            this.finalStates[39] = CompletedStates.PLUS;
            this.finalStates[40] = CompletedStates.MINUS;
            this.finalStates[41] = CompletedStates.EQUALLY;
        }

        private void fillKeyWords()
        {
            this.keyWords.Add("abstract");
            this.keyWords.Add("assert");
            this.keyWords.Add("boolean");
            this.keyWords.Add("break");
            this.keyWords.Add("byte");
            this.keyWords.Add("case");
            this.keyWords.Add("catch");
            this.keyWords.Add("char");
            this.keyWords.Add("class");
            this.keyWords.Add("const");
            this.keyWords.Add("continue");
            this.keyWords.Add("default");
            this.keyWords.Add("do");
            this.keyWords.Add("double");
            this.keyWords.Add("else");
            this.keyWords.Add("enum");
            this.keyWords.Add("extends");
            this.keyWords.Add("final");
            this.keyWords.Add("finally");
            this.keyWords.Add("float");
            this.keyWords.Add("for");
            this.keyWords.Add("goto");
            this.keyWords.Add("if");
            this.keyWords.Add("implements");
            this.keyWords.Add("import");
            this.keyWords.Add("instanceof");
            this.keyWords.Add("int");
            this.keyWords.Add("interface");
            this.keyWords.Add("long");
            this.keyWords.Add("native");
            this.keyWords.Add("new");
            this.keyWords.Add("package");
            this.keyWords.Add("private");
            this.keyWords.Add("protected");
            this.keyWords.Add("public");
            this.keyWords.Add("return");
            this.keyWords.Add("short");
            this.keyWords.Add("static");
            this.keyWords.Add("strictfp");
            this.keyWords.Add("super");
            this.keyWords.Add("switch");
            this.keyWords.Add("synchronized");
            this.keyWords.Add("this");
            this.keyWords.Add("throw");
            this.keyWords.Add("throws");
            this.keyWords.Add("transient");
            this.keyWords.Add("try");
            this.keyWords.Add("void");
            this.keyWords.Add("volatile");
            this.keyWords.Add("while");
            this.keyWords.Add("true");
            this.keyWords.Add("false");
        }

        public void goToState(char ch)
        {
            try
            {
                this.currStr += ch.ToString();
                int id = idByChar[ch];
                this.currState = this.table[currState][id];
                if (this.currState == -1)
                {
                    String resStr = "=====ERROR=====> " + this.currStr;
                    result += resStr + "\n";
                    this.initCurrentFields();
                    return;
                }

                this.currentStateHandler();
            }
            catch (Exception e)
            {
                String resStr = "Unsupported symbol: '" + ch + "'";
                result += resStr + "\n";
                Console.WriteLine(resStr);
                this.initCurrentFields();
            }
        }

        private void currentStateHandler()
        {
            if (!this.finalStates.ContainsKey(this.currState))
            {
                return;
            }

            this.currStr = this.currStr.Replace(" ", "");
            CompletedStates state = this.finalStates[this.currState];
            if (state == CompletedStates.ID && this.keyWords.Contains(this.currStr))
            {
                String resStr = "KEY WORD =====> " + this.currStr;
                result += resStr + "\n";
                Console.WriteLine(resStr);
            }
            else if (state != CompletedStates.SINGLE_COMMENT && state != CompletedStates.MULTIPLE_COMMENT)
            {
                if (this.finalStates[this.currState] == CompletedStates.ID)
                {
                    this.identifiers.Add(this.currStr);
                }
                
                if (this.finalStates[this.currState] == CompletedStates.NUMBER 
                    || this.finalStates[this.currState] == CompletedStates.NUMBER16
                    || this.finalStates[this.currState] == CompletedStates.NUMBER8
                    || this.finalStates[this.currState] == CompletedStates.NUMBER2
                    || this.finalStates[this.currState] == CompletedStates.DOUBLE
                    || this.finalStates[this.currState] == CompletedStates.EXP)
                {
                    this.numbers.Add(this.currStr);
                }
                
                String resStr = "ID =====> " + csMethods.getStr(this.finalStates[this.currState]) +
                                " VALUE: " + this.currStr;
                result += resStr + "\n";
                Console.WriteLine(resStr);
            }

            this.initCurrentFields();
        }

        public String getResult()
        {
            return result;
        }

        public List<String> getIdentifiers()
        {
            return this.identifiers;
        }

        public List<String> getNumbers()
        {
            return this.numbers;
        }

        private void initCurrentFields()
        {
            this.currStr = "";
            this.currState = 0;
        }
    }
}