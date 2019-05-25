using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.Reflection.Metadata.Ecma335;
using System.Threading.Tasks.Dataflow;
using Microsoft.VisualBasic.CompilerServices;

namespace TopDownParser
{
    public class TopDownParser
    {
        public List<String> terminals;

        public List<String> noTerminals;

        public int[] recNoTerminalStatus;

        public String startSymbol;

        public List<String> productions;

        public List<String> newProductions;

        public List<String> newProductionsFact;

        public List<List<String>> first;

        public List<List<String>> follow;

        public String[][] mTab;

        public String[] mTabColumn;

        public TopDownParser(List<String> inputGrammar)
        {
        }

        public List<String> GetTerminals(List<String> inputGrammar)
        {
            List<String> result = new List<string>();

            for (int i = 0; i < inputGrammar.Count; i++)
            {
                String line = inputGrammar[i];
                for (int j = 0; j < line.Length; j++)
                {
                    char ch = line[j];
                    char chBefore = ' ';
                    char chAfter = ' ';
                    if (j < line.Length - 2)
                    {
                        chBefore = line[j + 1];
                    }

                    if (j > 0)
                    {
                        chAfter = line[j - 1];
                    }

                    HandleTerminalsInProductions(ch, result, chBefore, chAfter);
                }
            }

            return result;
        }

        private void HandleTerminalsInProductions(char ch, List<String> result, char chBefore, char chAfter)
        {
            if (!Char.IsUpper(ch) && !result.Contains(ch.ToString()) && ch != '\'' && ch != '&')
            {
                if (ch == '-')
                {
                    if (chBefore != '>')
                    {
                        result.Add(ch.ToString());
                    }
                }
                else
                {
                    if (ch == '>')
                    {
                        if (chAfter != '-')
                        {
                            result.Add(ch.ToString());
                        }
                    }
                    else
                    {
                        result.Add(ch.ToString());
                    }
                }
            }
        }

        public List<String> GetNoTerminals(List<String> inputGrammar)
        {
            List<String> result = new List<string>();

            for (int i = 0; i < inputGrammar.Count; i++)
            {
                String line = inputGrammar[i];
                char ch = line[0];
                String noTerminal = ch.ToString();
                char nextCh = line[1];
                if (nextCh == '\'')
                {
                    noTerminal += "'";
                }

                if (!result.Contains(noTerminal))
                {
                    result.Add(noTerminal);
                }
            }

            return result;
        }

        public String GetStartSymbol(List<String> inputGrammar)
        {
            return inputGrammar[0][0].ToString();
        }

        public void clear()
        {
            terminals = new List<string>();
            noTerminals = new List<string>();
            startSymbol = null;
            
            productions = new List<string>();
            newProductions = new List<string>();
            newProductionsFact = new List<string>();
            
            first = new List<List<string>>();
            follow = new List<List<string>>();

            mTab = null;
            mTabColumn = null;
        }

        public void SortGrammar()
        {
            List<string> newGrammar = new List<string>();

            for (int i = 0; i < noTerminals.Count; i++)
            {
                String noTerminal = noTerminals[i];
                if (noTerminal.Length == 1)
                {
                    for (int j = 0; j < productions.Count; j++)
                    {
                        String production = productions[j];
                        if (production[0].Equals(noTerminal[0]) && !production[1].ToString().Equals("'") &&
                            !newGrammar.Contains(production))
                        {
                            newGrammar.Add(production);
                        }
                    }

                    for (int j = 0; j < productions.Count; i++)
                    {
                        String production = productions[j];
                        if (production[0].Equals(noTerminal[0]) && !newGrammar.Contains(production))
                        {
                            newGrammar.Add(production);
                        }
                    }

                }
            }

            productions = newGrammar;
        }

        public void DeleteLeftRecursion()
        {
            recNoTerminalStatus = new int[noTerminals.Count];

            for (int i = 0; i < recNoTerminalStatus.Length; i++)
            {
                recNoTerminalStatus[i] = 0;
            }

            for (int i = 0; i < productions.Count; i++)
            {
                String production = productions[i];
                if (IsLeftRecursion(production))
                {
                    String alternative = production[3] + "\'" + "->" + production.Substring(4, production.Length) +
                                  production[3] + "'";
                    productions.Insert(i, alternative);
                    
                    SetStateNoTerminal(production[0].ToString());
                }
            }

            ManageLeftRecursion();
            DeleteRecStates();
            productions.AddRange(newProductions);

        }

        private void ManageLeftRecursion()
        {
            for (int i = 0; i < recNoTerminalStatus.Length; i++)
            {
                int count = 0;
                if (recNoTerminalStatus[i] == 1)
                {
                    String noTerminalRec = noTerminals[i];
                    for (int j = 0; j < productions.Count; j++)
                    {
                        String production = productions[i];
                        String noTerminal = production[0].ToString();
                        String nextSymbolNoTerminal = production[1].ToString();
                        if (noTerminal.Equals(noTerminalRec) && !nextSymbolNoTerminal.Equals("'"))
                        {
                            String alternative = noTerminal + "->" + production.Substring(3, production.Length) +
                                                 noTerminal + "'";
                            newProductions.Add(alternative);
                            count++;
                        }

                    }

                    if (count == 0)
                    {
                        String alternative = noTerminals[i][0] + "->" + noTerminals[i][0] + "'";
                        newProductions.Add(alternative);
                    }

                    String alternativeEpsilon = noTerminals[i][0] + "'->&";
                    newProductions.Add(alternativeEpsilon);
                }
            }
        }

        public void Factorization()
        {
            for (int i = 0; i < noTerminals.Count; i++)
            {
                String noTerminal = noTerminals[i];
                List<String> productions = GetProductionsForFactorization(noTerminal);
                if (productions != null && productions.Count > 1)
                {
                    List<String> onlyFirstChar = GetFirstLetterArray(productions);
                    for (int j = 0; j < onlyFirstChar.Count; j++)
                    {
                        List<string> productionsWithChar = LeaveAppearence(productions, onlyFirstChar[j]);
                        if (productionsWithChar.Count > 1)
                        {
                            String subLine = LongestCommonPrefix(productionsWithChar);
                            if (!subLine.Equals(null))
                            {
                                ChangePosition(subLine);
                                String alternative = noTerminal + "->" + subLine + noTerminal + "'";
                                productions.Add(alternative);
                            }
                        }
                    }
                }
            }
        }

        private void ChangePosition(String line)
        {
            for (int i = 0; i < productions.Count; i++)
            {
                String production = productions[i];
                String noTerminal = production[0].ToString();
                if (production.Length >= 3 + line.Length)
                {
                    String next = production.Substring(3, 3 + line.Length);
                    if (next.Equals(line))
                    {
                        String alternative = null;
                        if (production.Length == next.Length + 3)
                        {
                            alternative = noTerminal + "'->&";
                            productions.Insert(i, alternative);
                        }
                        else
                        {
                            String right = production.Substring(line.Length + 3, production.Length);
                            alternative = noTerminal + "'->" + right;
                            production.Insert(i, alternative);
                        }
                    }
                }
            }
        }

        private String LongestCommonPrefix(List<String> lines)
        {
            if (lines.Count == 0)
            {
                return "";
            }

            String result = lines[0];
            for (int i = 1; i < lines.Count; i++)
            {
                if (lines[i].Length < result.Length)
                {
                    result = lines[i];
                }
            }

            int end = result.Length;
            for (int i = 0; i < lines.Count; i++)
            {
                int j;
                for (j = 0; j < end; j++)
                {
                    if (result[j] != lines[i][j])
                    {
                        break;
                    }
                }

                if (j < end)
                {
                    end = j;
                }
            }

            return result.Substring(0, end);

        }

        private List<String> LeaveAppearence(List<String> productions, String ch)
        {
            List<String> result = new List<string>();
            int count = 0;

            for (int i = 0; i < productions.Count; i++)
            {
                if (ch.Equals(productions[i][0]))
                {
                    result.Add(productions[i]);
                    count++;
                }
            }

            return result;
        }

        private List<String> GetFirstLetterArray(List<String> productions)
        {
            List<String> result = new List<string>();

            for (int i = 0; i < productions.Count; i++)
            {
                String ch = productions[i][0].ToString();
                result.Add(ch);
            }

            return RemoveDuplicates(result);
        }

        private List<String> RemoveDuplicates(List<String> items)
        {
            return new List<string>(new HashSet<string>(items));
        }

        private List<String> GetProductionsForFactorization(String noTerminal)
        {
            List<String> result = new List<string>();
            bool leftRec = false;
            for (int i = 0; i < productions.Count; i++)
            {
                String production = productions[i];
                String firstSymbol = production[0].ToString();
                String nextSymbol = production[1].ToString();
                if (firstSymbol.Equals(noTerminal))
                {
                    result.Add(production.Substring(3, production.Length));
                    if (nextSymbol.Equals("\'"))
                    {
                        leftRec = true;
                    }
                }
            }

            if (leftRec)
            {
                result = null;
            }

            return result;
        }

        private void DeleteRecStates()
        {
            for (int i = 0; i < recNoTerminalStatus.Length; i++)
            {
                if (recNoTerminalStatus[i] == 1)
                {
                    String noTerminalRec = noTerminals[i];
                    int count = 0;
                    while (count < productions.Count)
                    {
                        String production = productions[count];
                        String noTerminal = production[0].ToString();
                        String nextSymbolNoTerminal = production[1].ToString();
                        if (noTerminal.Equals(noTerminalRec) && !nextSymbolNoTerminal.Equals("'"))
                        {
                            productions.RemoveAt(count);
                        }
                        else
                        {
                            count++;
                        }

                    }
                }
            }
        }

        private void SetStateNoTerminal(String noTerminal)
        {
            for (int i = 0; i < noTerminals.Count; i++)
            {
                if (noTerminals[i].Equals(noTerminal))
                {
                    recNoTerminalStatus[i] = 1;
                }
            }
        }

        private bool IsLeftRecursion(String production)
        {
            return production[0] == production[3] ? true : false;
        }



    }
}