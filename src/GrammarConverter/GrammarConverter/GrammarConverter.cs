using System;
using System.Collections.Generic;
using System.Linq;
using Core.model;
using Microsoft.AspNetCore.Routing.Tree;

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

        private List<String> emptyNoTerminals;

        public GrammarConverter(Grammar grammar)
        {
            terminals = grammar.terminals;
            noTerminals = grammar.noTerminals;
            startSymbol = grammar.startSymbol;

            productions = grammar.productions;
            newProductions = new List<string>();

            first = new List<List<string>>();
            follow = new List<List<string>>();
            emptyNoTerminals = new List<string>();
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

        public List<String> GetEmptyNoTerminals()
        {
            return emptyNoTerminals;
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
                                this.productions.Add(alternative);
                            }
                        }
                    }
                }
            }
        }

        public void First()
        {
            FillFirst();
            emptyNoTerminals = GetAllEmptyNoTerminals();

            for (int i = 0; i < productions.Count; i++)
            {
                String production = productions[i];
                List<String> firstTemp = new List<string>();
                if (IsApostrophe(production))
                {
                    String alternative = production.Substring(4, production.Length - 4);
                    String alt = alternative;
                    List<String> alternativeSymbols = GetNoTerminalAndTerminals(alt);
                    int altLength = alternativeSymbols.Count;
                    List<String> altSymbols = new List<String>();
                    for (int j = 0; j < alternativeSymbols.Count; j++)
                    {
                        if (j == alternativeSymbols.Count - 1)
                        {
                            altSymbols = alternativeSymbols.GetRange(j, 1);
                        }
                        else
                        {
                            altSymbols = alternativeSymbols.GetRange(j, altLength - j);
                        }

                        firstTemp.AddRange(GetLineOfFirstSet(altSymbols));
                        if (!emptyNoTerminals.Contains(altSymbols[0]))
                        {
                            break;
                        }
                    }
                }
                else
                {
                    String alternative = production.Substring(3, production.Length - 3);
                    String alt = alternative;
                    List<String> alternativeSymbols = GetNoTerminalAndTerminals(alt);
                    int altLength = alternativeSymbols.Count;
                    List<String> altSymbols = new List<String>();
                    for (int j = 0; j < alternativeSymbols.Count; j++)
                    {
                        if (j == alternativeSymbols.Count - 1)
                        {
                            altSymbols = alternativeSymbols.GetRange(j, 1);
                        }
                        else
                        {
                            altSymbols = alternativeSymbols.GetRange(j, altLength - j);
                        }

                        firstTemp.AddRange(GetLineOfFirstSet(altSymbols));
                        if (!emptyNoTerminals.Contains(altSymbols[0]))
                        {
                            break;
                        }
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
                        temp = temp.Distinct().ToList();
                        for (int k = 0; k < temp.Count; k++)
                        {
                            String item = temp[k];
                            if (item.Equals("&"))
                            {
                                temp.RemoveAt(k);
                                temp.Add("&");
                                break;
                            }
                        }

                        first[j] = temp;
                    }
                }
            }
        }

        private List<string> GetLineOfFirstSet(List<String> production)
        {
            List<string> result = new List<string>();
            if (production.Equals("\'"))
            {
                return result;
            }

            if (!Char.IsUpper(production[0].ToString()[0]))
            {
                result.Add(production[0].ToString());
            }
            else
            {
                List<string> temp = new List<string>();

                List<string> firstSet = new List<string>();
                if (IsApostropheInList(production))
                {
                    temp = GetProductionsForFirst(production[0].ToString(), true);
                    for (int i = 0; i < temp.Count; i++)
                    {
                        String alternative = temp[i];
                        List<String> alternativeSymbols = GetNoTerminalAndTerminals(alternative);
                        int altLength = alternativeSymbols.Count;
                        List<String> altSymbols = new List<String>();
                        for (int j = 0; j < alternativeSymbols.Count; j++)
                        {
                            if (j == alternativeSymbols.Count - 1)
                            {
                                altSymbols = alternativeSymbols.GetRange(j, 1);
                            }
                            else
                            {
                                altSymbols = alternativeSymbols.GetRange(j, altLength - j);
                            }

                            firstSet.AddRange(GetLineOfFirstSet(altSymbols));
                            if (!emptyNoTerminals.Contains(altSymbols[0]))
                            {
                                break;
                            }
                        }
                    }
                }
                else
                {
                    temp = GetProductionsForFirst(production[0].ToString(), false);
                    for (int i = 0; i < temp.Count; i++)
                    {
                        String alternative = temp[i];
                        List<String> alternativeSymbols = GetNoTerminalAndTerminals(alternative);
                        int altLength = alternativeSymbols.Count;
                        List<String> altSymbols = new List<String>();
                        for (int j = 0; j < alternativeSymbols.Count; j++)
                        {
                            if (j == alternativeSymbols.Count - 1)
                            {
                                altSymbols = alternativeSymbols.GetRange(j, 1);
                            }
                            else
                            {
                                altSymbols = alternativeSymbols.GetRange(j, altLength - j);
                            }

                            firstSet.AddRange(GetLineOfFirstSet(altSymbols));
                            if (!emptyNoTerminals.Contains(altSymbols[0]))
                            {
                                break;
                            }
                        }
                    }
                }

                result.AddRange(firstSet);
            }

            return result;
        }

        private bool IsApostropheInList(List<String> word)
        {
            if (word[0].Length > 1)
            {
                return word[0][1].Equals('\'') ? true : false;
            }

            return false;
        }

        private List<string> GetAllEmptyNoTerminals()
        {
            List<string> result = new List<string>();
            List<string> current = new List<string>();

            do
            {
                result.Clear();
                result.AddRange(current);
                current.Clear();
                current.AddRange(GetEmptyNotTerminals(result));
            } while (!IsEqualsList(result, current));

            return result;
        }

        private bool IsEqualsList(List<String> list1, List<String> list2)
        {
            list1.Sort();
            list2.Sort();
            if (list1.Count != list2.Count)
            {
                return false;
            }

            List<bool> boolList = new List<bool>();
            for (int i = 0; i < list1.Count; i++)
            {
                if (list1[i] == list2[i])
                {
                    boolList.Add(true);
                }
                else
                {
                    boolList.Add(false);
                }
            }

            for (int i = 0; i < boolList.Count; i++)
            {
                if (!boolList[i])
                {
                    return false;
                }
            }

            return true;
        }

        private List<string> GetEmptyNotTerminals(List<string> result)
        {
            List<string> current = new List<string>();

            for (int i = 0; i < productions.Count; i++)
            {
                String production = productions[i];
                if (IsApostrophe(production))
                {
                    String alternative = production.Substring(4, production.Length - 4);
                    if (IsTerm(alternative))
                    {
                        continue;
                    }

                    String noTermAlt = production.Substring(0, 2);
                    if (alternative.Length >= 1 && alternative[0] == '&')
                    {
                        current.Add(noTermAlt);
                    }

                    List<string> noTerms = GetNoTerminal(alternative);
                    for (int j = 0; j < noTerms.Count; j++)
                    {
                        String noTerm = noTerms[j];
                        if (!result.Contains(noTerm))
                        {
                            break;
                        }

                        if ((j == noTerms.Count - 1) && result.Contains(noTerm))
                        {
                            current.Add(noTermAlt);
                        }
                    }
                }
                else
                {
                    String alternative = production.Substring(3, production.Length - 3);
                    if (IsTerm(alternative))
                    {
                        continue;
                    }

                    String noTermAlt = production.Substring(0, 1);
                    if (alternative.Length >= 1 && alternative[0] == '&')
                    {
                        current.Add(noTermAlt);
                    }

                    List<string> noTerms = GetNoTerminal(alternative);
                    for (int j = 0; j < noTerms.Count; j++)
                    {
                        String noTerm = noTerms[j];
                        if (!result.Contains(noTerm))
                        {
                            break;
                        }

                        if ((j == noTerms.Count - 1) && result.Contains(noTerm))
                        {
                            current.Add(noTermAlt);
                        }
                    }
                }
            }

            return current;
        }

        private bool IsTerm(String alt)
        {
            return (alt.Length > 1 && Char.IsLower(alt[0]));
        }

        private List<string> GetNoTerminal(string prod)
        {
            List<string> result = new List<string>();
            char ch = ' ';

            char nextCh = ' ';
            for (int i = 0;
                i < prod.Length;
                i++)
            {
                if (i != prod.Length - 1)
                {
                    nextCh = prod[i + 1];
                }

                ch = prod[i];
                if (Char.IsUpper(ch) && nextCh == '\'')
                {
                    result.Add((ch + nextCh).ToString());
                }
                else if (Char.IsUpper(ch))
                {
                    result.Add(ch.ToString());
                }
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

            for (int i = 0; i < follow.Count; i++)
            {
                List<String> row = follow[i];
                row.Remove("$");
                row.Add("$");
            }
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
                    if (posBefore != -1 && posAfter != -1)
                    {
                        SetPositionBeforeAndAfter(posBefore, posAfter);
                    }
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
                    List<String> listRight = GetNoTerminalAndTerminals(right);
                    for (int k = 0; k < listRight.Count; k++)
                    {
                        String nt = listRight[k];

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

                        if (!emptyNoTerminals.Contains(nt))
                        {
                            break;
                        }
                    }
                }
            }

            return result;
        }

        private List<string> GetNoTerminalAndTerminals(string prod)
        {
            List<string> result = new List<string>();
            char ch = ' ';

            char nextCh = ' ';
            for (int i = 0; i < prod.Length; i++)
            {
                if (i != prod.Length - 1)
                {
                    nextCh = prod[i + 1];
                }

                ch = prod[i];
                if (Char.IsUpper(ch) && nextCh == '\'')
                {
                    result.Add(ch.ToString() + "'");
                }
                else if (Char.IsUpper(ch))
                {
                    result.Add(ch.ToString());
                }
                else
                {
                    result.Add(ch.ToString());
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
                    String next = production.Substring(3, line.Length);
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
                            String right = production.Substring(line.Length + 3,
                                (production.Length - (line.Length + 3)));
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
                if (ch.Equals(productions[i][0].ToString()))
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