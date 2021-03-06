using System;
using System.Collections.Generic;
using System.Reflection.Metadata;
using Core.model;
using Microsoft.VisualBasic;

namespace TableMGenerator
{
    public class TableMGenerator
    {
        private List<List<String>> mTable;
        
        private List<String> productions;
        private List<List<String>> first;
        private List<List<String>> follow;

        private List<String> terminals;
        private List<String> noTerminals;
        

        public TableMGenerator(NewGrammar convertedGrammar)
        {
            mTable = new List<List<string>>();
            productions = convertedGrammar.productions;
            first = convertedGrammar.first;
            follow = convertedGrammar.follow;
            terminals = convertedGrammar.terminals;
            noTerminals = convertedGrammar.noTerminals;
        }

        public List<List<String>> GetTable()
        {
            return mTable;
        }

        public void GenerateTable()
        {
            InitTable();

            for (int i = 0; i < productions.Count; i++)
            {
                String production = productions[i];
                String noTerminal = "";
                String alternative = "";
                if (IsApostrophe(production))
                {
                    noTerminal = production.Substring(0, 2);
                    alternative = production.Substring(4, 1);
                    if (IsApostrophe(alternative))
                    {
                        alternative += "'";
                    }
                }
                else
                {
                    noTerminal = production.Substring(0, 1);
                    alternative = production.Substring(3, 1);
                    if (production.Length > 4)
                    {
                        if (IsApostrophe(alternative))
                        {
                            alternative += "'";
                        }
                    }
                }

                GenerateFromFirstAndFollow(alternative, noTerminal, production);

            }
        }

        public void Clear()
        {
            mTable = new List<List<string>>();
            productions = new List<string>();
            first = new List<List<String>>();
            follow = new List<List<String>>();
            terminals = new List<string>();
            noTerminals = new List<string>();
        }

        private void GenerateFromFirstAndFollow(String alternative, String noTerminal, String production)
        {
            List<String> temp = new List<string>();
            if (!Char.IsUpper(alternative[0]))
            {
                if (alternative[0].Equals('&'))
                {
                    int posNoTerminal = -1;
                    for (int j = 0; j < noTerminals.Count; j++)
                    {
                        if (noTerminals[j].Equals(noTerminal))
                        {
                            posNoTerminal = j;
                        }
                    }

                    temp = follow[posNoTerminal];
                }
                else
                {
                    temp.Add(alternative);
                }
            }
            else
            {
                int posNoTerminal = -1;
                for (int j = 0; j < noTerminals.Count; j++)
                {
                    if (noTerminals[j].Equals(alternative))
                    {
                        posNoTerminal = j;
                    }
                }

                temp = first[posNoTerminal];
            }

            for (int l = 0; l < temp.Count; l++)
            {
                for (int j = 0; j < (noTerminals.Count + 1); j++)
                {
                    if (mTable[j][0].Equals(noTerminal))
                    {
                        for (int k = 0; k < (terminals.Count + 2); k++)
                        {
                            if (mTable[0][k].Equals(temp[l]))
                            {
                                mTable[j][k] = production;
                            }
                        }
                    }
                }
            }
        }

        private void InitTable()
        {
            for (int i = 0; i < noTerminals.Count + 1; i++)
            {
                List<String> row = new List<string>();
                for (int j = 0; j < terminals.Count + 2; j++)
                {
                    row.Add("");
                }
                mTable.Add(row);
            }

            mTable[0][terminals.Count + 1] = "$";
            for (int i = 1; i <= noTerminals.Count; i++) {
                mTable[i][0] = noTerminals[i - 1];
            }
            for (int i = 1; i <= terminals.Count; i++) {
                mTable[0][i] = terminals[i - 1];
            }
        }

        private bool IsApostrophe(String word)
        {
            if (word.Length > 1)
            {
                return word[1].Equals('\'') ? true : false;
            }

            return false;
        }
    }
}