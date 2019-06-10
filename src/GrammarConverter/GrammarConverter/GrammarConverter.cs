using System;
using System.Collections.Generic;
using System.Linq;
using Core.model;

namespace GrammarConverter
{
    public class GrammarConverter
    {
        private List<String> terminals;

        private List<String> noTerminals;

        private int[] recNoTerminalStatus;

        private String startSymbol;

        private List<String> productions;

        private List<String> newProductions;

        private List<List<String>> first;

        private List<List<String>> follow;

        public GrammarConverter(Grammar grammar)
        {
            terminals = grammar.terminals;
            noTerminals = grammar.noTerminals;
            startSymbol = grammar.startSymbol;

            productions = grammar.productions;
            newProductions = new List<string>();

            first = new List<List<string>>();
            follow = new List<List<string>>();
        }

        public List<String> GetProductions()
        {
            return productions;
        }

        public void SetNoTerminals(List<String> newNoTerminals)
        {
            this.noTerminals = newNoTerminals;
        }

        public List<String> GetTerminals()
        {
            return terminals;
        }

        public List<String> GetNoTerminals()
        {
            return noTerminals;
        }

        public void Clear()
        {
            terminals = new List<string>();
            noTerminals = new List<string>();
            startSymbol = null;

            productions = new List<string>();
            newProductions = new List<string>();

            first = new List<List<string>>();
            follow = new List<List<string>>();
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

                    for (int j = 0; j < productions.Count; j++)
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
                    String alternative = production[3] + "'" + "->" + production.Substring(4, production.Length - 4) +
                                         production[3] + "'";
                    productions[i] = alternative;

                    SetStateNoTerminal(production[0].ToString());
                }
            }

            ManageLeftRecursion();
            DeleteRecStates();
            productions.AddRange(newProductions);
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
                        String production = productions[j];
                        String noTerminal = production[0].ToString();
                        String nextSymbolNoTerminal = production[1].ToString();
                        if (noTerminal.Equals(noTerminalRec) && !nextSymbolNoTerminal.Equals("'"))
                        {
                            String alternative = noTerminal + "->" + production.Substring(3, production.Length - 3) +
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

        public void First()
        {
            FillFirst();
            //функця, которая возвращает список всех не терминалов, которые будут пустые.
            //пока результат(список) не равен списку текущего прогона, если они одинаковые, значит мы нашли тот список
            //
            List<string> result = new List<string>();
            List<string> current = new List<string>();

            do
            {
                result.AddRange(GetEmptyNotTerminals(current)); 
            } while (current != result);
            
            for (int i = 0; i < productions.Count; i++)
            {
                String production = productions[i];
                List<String> firstTemp = new List<string>();
                if (IsApostrophe(production))
                {
                    String alternative = production.Substring(4, production.Length - 4);
                    int altLength = alternative.Length;
                    String alt = alternative;
                    IsEmpty isEmpty = new IsEmpty();
                    for (int j = 0; j < altLength; j++)
                    {
                        if (!isEmpty.getIs())
                        {
                            break;
                        }
                        else
                        {
                            isEmpty.setIs(false);
                        }

                        if (j == altLength - 1)
                        {
                            alternative = alt.Substring(j, 1);
                        }
                        else
                        {
                            alternative = alt.Substring(j, altLength - j);
                        }

                        firstTemp.AddRange(GetLineOfFirstSet(alternative, isEmpty));
                    }
                }
                else
                {
                    String alternative = production.Substring(3, production.Length - 3);
                    IsEmpty isEmpty = new IsEmpty();
                    int altLength = alternative.Length;
                    String alt = alternative;
                    for (int j = 0; j < altLength; j++)
                    {
                        if (!isEmpty.getIs())
                        {
                            break;
                        }
                        else
                        {
                            isEmpty.setIs(false);
                        }

                        if (j == altLength - 1)
                        {
                            alternative = alt.Substring(j, 1);
                        }
                        else
                        {
                            alternative = alt.Substring(j, altLength - j);
                        }

                        firstTemp.AddRange(GetLineOfFirstSet(alternative, isEmpty));
                    }
                }

                firstTemp = firstTemp.Distinct().ToList();
                for (int j = 0; j < firstTemp.Count; j++)
                {
                    String item = firstTemp[j];
                    if (item.Equals("&"))
                    {
                        firstTemp.RemoveAt(j);
                        firstTemp.Add("&");
                        break;
                    }
                }

                String noTerminal = production.Substring(0, 2);
                String lol = production.Substring(1, 1);
                if (production.Substring(1, 1).Equals("-"))
                {
                    noTerminal = production.Substring(0, 1);
                }

                for (int j = 0; j < noTerminals.Count; j++)
                {
                    if (noTerminal.Equals(noTerminals[j]))
                    {
                        List<String> temp = first[j];
                        temp.AddRange(firstTemp);
                        first[j] = temp;
                    }
                }
            }
        }

        private List<string> GetEmptyNotTerminals(String production, List<string> current)
        {
            List<string> result = new List<string>();

            for (int i = 0; i < noTerminals.Count; i++)
            {
                String noTerminal = noTerminals[i];
                if (noTerminal.Length == 1)
                {
                    for (int j = 0; j < productions.Count; j++)
                    {
                        if (productions[j] == "&" || (current.Contains(productions[j])))
                        {
                            result.Add(noTerminals[i]);
                        }
                    }
                }
            }

            return result;
        }

        private List<string> GetLineOfFirstSet(String production, IsEmpty isEmpty)
        {
            List<string> result = new List<string>();

            if (production.Equals("\'"))
            {
                return result;
            }

            if (production.Length == 1 && production[0].Equals('&'))
            {
                isEmpty.setIs(true);
            }

            if (!Char.IsUpper(production[0]))
            {
                result.Add(production[0].ToString());
            }
            else
            {
                List<string> temp = new List<string>();
                List<string> firstSet = new List<string>();
                if (IsApostrophe(production))
                {
                    temp = GetProductionsForFirst(production[0].ToString(), true);
                    for (int i = 0; i < temp.Count; i++)
                    {
                        String alternative = temp[i];
                        int altLength = alternative.Length;
                        IsEmpty isEmptyNew = new IsEmpty();
                        String alt = alternative;
                        for (int j = 0; j < altLength; j++)
                        {
                            if (!isEmptyNew.getIs())
                            {
                                isEmpty.setIs(true);
                                break;
                            }
                            else
                            {
                                isEmptyNew.setIs(false);
                            }

                            if (j == altLength - 1)
                            {
                                alternative = alt.Substring(j, 1);
                            }
                            else
                            {
                                alternative = alt.Substring(j, altLength - j);
                            }

                            firstSet.AddRange(GetLineOfFirstSet(alternative, isEmptyNew));
                        }
                    }
                }
                else
                {
                    temp = GetProductionsForFirst(production[0].ToString(), false);
                    for (int i = 0; i < temp.Count; i++)
                    {
                        String alternative = temp[i];
                        int altLength = alternative.Length;
                        IsEmpty isEmptyNew = new IsEmpty();
                        String alt = alternative;
                        for (int j = 0; j < altLength; j++)
                        {
                            if (!isEmptyNew.getIs())
                            {
                                isEmpty.setIs(true);
                                break;
                            }
                            else
                            {
                                isEmptyNew.setIs(false);
                            }

                            if (j == altLength - 1)
                            {
                                alternative = alt.Substring(j, 1);
                            }
                            else
                            {
                                alternative = alt.Substring(j, altLength - j);
                            }

                            firstSet.AddRange(GetLineOfFirstSet(alternative, isEmptyNew));
                        }
                    }
                }

                result.AddRange(firstSet);
            }

            return result;
        }

        public List<List<String>> GetFirst()
        {
            return first;
        }

        public List<List<String>> GetFollow()
        {
            return follow;
        }

        public void Follow()
        {
            FillFollow();
            List<String> followSetTemp = new List<string>();
            for (int i = 0; i < productions.Count; i++)
            {
                String prod = productions[i];
                List<String> followSet = new List<string>();
                int startProd = 3;
                String noTerminal = prod[0].ToString();
                if (prod[1].Equals('\''))
                {
                    startProd = 4;
                    noTerminal += "'";
                }

                List<string> noTerminalsSet = GetNoTerminalsSet(prod, startProd);
                if (noTerminalsSet.Count == 0)
                {
                }
                else
                {
                    for (int j = 0; j < noTerminalsSet.Count; j++)
                    {
                        String alternative = prod.Substring(startProd, prod.Length - startProd);

                        followSet.AddRange(Rotate(noTerminalsSet[j], alternative, noTerminal));
                    }

                    followSetTemp.AddRange(followSet);
                    AddNewElementToFollowSet(followSet);
                }
            }

            PassageFollowSet(followSetTemp);
            PassageFollowSet(followSetTemp);
        }

        private void PassageFollowSet(List<String> followSetTemp)
        {
            for (int i = 0; i < followSetTemp.Count; i++)
            {
                String temp = followSetTemp[i];
                if (temp[0].Equals('º'))
                {
                    String nt1 = temp[1].ToString();
                    String nt2 = "";
                    if (followSetTemp[i][2].Equals('\''))
                    {
                        nt1 += "'";
                    }

                    if (temp.Length == 4)
                    {
                        if (followSetTemp[i][2].Equals('\''))
                        {
                            nt2 = temp[3].ToString();
                        }
                        else
                        {
                            nt2 = temp[2] + "'";
                        }
                    }
                    else
                    {
                        if (temp.Length == 5)
                        {
                            nt2 = temp[3] + "'";
                        }
                        else
                        {
                            nt2 = temp[2].ToString();
                        }
                    }

                    int posBefore = GetPosition(noTerminals, nt1);
                    int posAfter = GetPosition(noTerminals, nt2);

                    SetPositionBeforeAndAfter(posBefore, posAfter);
                }
            }
        }

        private void SetPositionBeforeAndAfter(int posBefore, int posAfter)
        {
            for (int i = 0; i < follow[posAfter].Count; i++)
            {
                String temp = follow[posAfter][i];
                if (temp.Length != 2 && !Char.IsUpper(temp[0]))
                {
                    if (!follow[posBefore].Contains(temp))
                    {
                        follow[posBefore].Add(follow[posAfter][i]);
                    }
                }
            }
        }

        private int GetPosition(List<string> arr, String noTerminal)
        {
            int result = -1;
            for (int i = 0; i < arr.Count; i++)
            {
                String term = noTerminal[0].ToString();
                String termInArr = arr[i][0].ToString();
                if (noTerminal.Length == 2 && arr[i].Length == 2)
                {
                    if (term.Equals(termInArr))
                    {
                        result = i;
                    }
                }
                else
                {
                    if (arr[i].Length == 1 && noTerminal.Length == 1)
                    {
                        if (term.Equals(termInArr))
                        {
                            result = i;
                        }
                    }
                }
            }

            return result;
        }

        private void AddNewElementToFollowSet(List<String> newElements)
        {
            for (int j = 0; j < newElements.Count; j++)
            {
                for (int k = 0; k < follow.Count; k++)
                {
                    if (newElements[j][0].ToString().Equals(follow[k][0]))
                    {
                        if (!follow[k].Contains(newElements[j][2].ToString()))
                        {
                            follow[k].Add(newElements[j][2].ToString());
                        }
                    }
                }
            }
        }

        private List<String> Rotate(String noTerminalSetEl, String production, String noTerminal)
        {
            List<String> result = new List<string>();

            String left = ProductionLeftOrRight(noTerminalSetEl, production, true);
            String right = ProductionLeftOrRight(noTerminalSetEl, production, false);

            if (String.IsNullOrEmpty(right))
            {
                result.Add("º" + noTerminalSetEl + noTerminal);
            }
            else
            {
                if (!Char.IsUpper(right[0]) && !right[0].Equals('\''))
                {
                    result.Add(noTerminalSetEl + " " + right[0]);
                }
                else
                {
                    String nt = right[0].ToString();
                    if (right.Length > 1)
                    {
                        if (right[1].Equals('\''))
                        {
                            nt += "'";
                        }
                    }

                    for (int i = 0; i < noTerminals.Count; i++)
                    {
                        if (nt.Equals(first[i][0]))
                        {
                            for (int j = 1; j < first[i].Count; j++)
                            {
                                if (first[i][j].Equals("&"))
                                {
                                    result.Add("º" + noTerminalSetEl + noTerminal);
                                }
                                else
                                {
                                    result.Add(noTerminalSetEl + " " + first[i][j]);
                                }
                            }
                        }
                    }
                }
            }

            return result;
        }

        private String ProductionLeftOrRight(String noTerminal, String prod, bool isLeft)
        {
            String result = null;
            String symbol = noTerminal[0].ToString();
            int posBefore = 0;
            int posAfter = 0;

            for (int i = 0; i < prod.Length; i++)
            {
                if (prod[i].Equals(noTerminal[0]))
                {
                    posBefore = i - 1;
                    if (noTerminal.Length == 2)
                    {
                        posAfter = i + 2;
                    }
                    else
                    {
                        posAfter = i + 1;
                    }
                }
            }

            if (isLeft)
            {
                Char[] prodArr = prod.ToCharArray();
                String lol = "";
                for (int i = 0; i < posBefore + 1; i++)
                {
                    lol += prodArr[i];
                }

                result = lol;
            }
            else
            {
                result = prod.Substring(posAfter, prod.Length - posAfter);
            }

            return result;
        }

        private List<String> GetNoTerminalsSet(String prod, int start)
        {
            List<String> result = new List<string>();
            for (int i = start; i < prod.Length; i++)
            {
                if (Char.IsUpper(prod[i]))
                {
                    String ch = prod[i].ToString();
                    if (i < prod.Length - 1)
                    {
                        if (prod[i + 1].Equals('\''))
                        {
                            result.Add(ch + "'");
                        }
                        else
                        {
                            result.Add(ch);
                        }
                    }
                    else
                    {
                        result.Add(ch);
                    }
                }
            }

            return result;
        }

        private void FillFollow()
        {
            for (int i = 0; i < noTerminals.Count; i++)
            {
                List<String> temp = new List<string>();
                temp.Add(noTerminals[i]);
                if (noTerminals[i].Equals(startSymbol))
                {
                    temp.Add("$");
                }

                follow.Add(temp);
            }
        }

        private List<String> GetProductionsForFirst(String noTerminal, bool isApostrophe)
        {
            List<String> result = new List<string>();
            for (int i = 0; i < productions.Count; i++)
            {
                String production = productions[i];
                String firstTemp = production[0].ToString();
                if (firstTemp.Equals(noTerminal) && !isApostrophe && !IsApostrophe(production))
                {
                    result.Add(production.Substring(3, production.Length - 3));
                }
                else if (firstTemp.Equals(noTerminal) && isApostrophe && IsApostrophe(production))
                {
                    result.Add(production.Substring(4, production.Length - 4));
                }
            }

            return result;
        }

        private bool IsApostrophe(String word)
        {
            if (word.Length > 1)
            {
                return word[1].Equals('\'') ? true : false;
            }

            return false;
        }

        private void FillFirst()
        {
            for (int i = 0; i < noTerminals.Count; i++)
            {
                List<String> temp = new List<string>();
                temp.Add(noTerminals[i]);
                first.Add(temp);
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
                    String next = production.Substring(3, production.Length - 3 + line.Length);
                    if (next.Equals(line))
                    {
                        String alternative = null;
                        if (production.Length == next.Length + 3)
                        {
                            alternative = noTerminal + "'->&";
                            productions[i] = alternative;
                        }
                        else
                        {
                            String right = production.Substring(line.Length + 3, production.Length - line.Length + 3);
                            alternative = noTerminal + "'->" + right;
                            productions[i] = alternative;
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

            return result.Substring(0, result.Length - end);
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
                    result.Add(production.Substring(3, production.Length - 3));
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