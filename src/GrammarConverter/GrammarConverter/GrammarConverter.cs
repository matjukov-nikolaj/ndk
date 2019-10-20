using System;
using System.Collections.Generic;
using System.Linq;
using Core.model;
using Microsoft.AspNetCore.Routing.Tree;
using Microsoft.VisualBasic.CompilerServices;

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
                if (noTerminal[noTerminal.Length - 1].ToString() != "'")
                {
                    for (int j = 0; j < productions.Count; j++)
                    {
                        String production = productions[j];
                        String[] noTermAlt = production.Split(" -> ");
                        String ср = noTermAlt[0][noTermAlt[0].Length - 1].ToString();
                        if (noTermAlt[0].Equals(noTerminal) &&
                            !noTermAlt[0][noTermAlt[0].Length - 1].ToString().Equals("'") &&
                            !newGrammar.Contains(production))
                        {
                            newGrammar.Add(production);
                        }
                    }

                    for (int j = 0; j < productions.Count; j++)
                    {
                        String production = productions[j];
                        String[] noTermAlt = production.Split(" -> ");
                        String noTerm = noTermAlt[0];
                        if (noTerm[noTerm.Length - 1].ToString() == "'")
                        {
                            noTerm = noTerm.Substring(0, noTerm.Length - 1);
                        }

                        if (noTerm.Equals(noTerminal) && !newGrammar.Contains(production))
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
                    String[] noTermAlt = production.Split(" -> ");

                    String[] altsArr = noTermAlt[1].Split(" ");
                    List<string> alts = altsArr.OfType<string>().ToList();
                    List<String> nextRange = alts.GetRange(1, alts.Count - 1);

                    String alternative = alts[0] + "'" + " ->";
                    for (int j = 0; j < nextRange.Count; j++)
                    {
                        alternative += " " + nextRange[j];
                    }

                    alternative += " " + alts[0] + "'";
                    productions[i] = alternative;

                    SetStateNoTerminal(noTermAlt[0]);
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
                String[] noTermAlt = line.Split(" -> ");
                if (!result.Contains(noTermAlt[0]))
                {
                    result.Add(noTermAlt[0]);
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
                        String[] noTermAlt = production.Split(" -> ");
                        String noTerminal = noTermAlt[0];
                        String nextSymbolNoTerminal = noTerminal[noTerminal.Length - 1].ToString();
                        if (noTerminal.Equals(noTerminalRec) && !nextSymbolNoTerminal.Equals("'"))
                        {
                            String[] altsArr = noTermAlt[1].Split(" ");
                            List<string> alts = altsArr.OfType<string>().ToList();
                            List<String> nextRange = alts.GetRange(0, alts.Count);
                            String alternative = noTerminal + " ->";
                            for (int k = 0; k < nextRange.Count; k++)
                            {
                                alternative += " " + nextRange[k];
                            }

                            alternative += " " + noTerminal + "'";
                            newProductions.Add(alternative);
                            count++;
                        }
                    }

                    if (count == 0)
                    {
                        String alternative = noTerminals[i] + " -> " + noTerminals[i] + "'";
                        newProductions.Add(alternative);
                    }

                    String alternativeEpsilon = noTerminals[i] + "' -> &";
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
                //TODO continue work with factorization
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
                                String alternative = noTerminal + " -> " + subLine + noTerminal + "'";
                                this.productions.Add(alternative);
                            }
                        }
                    }
                }
            }
        }

        private List<String> GetProductionsForFactorization(String noTerminal)
        {
            List<String> result = new List<string>();
            bool leftRec = false;
            for (int i = 0; i < productions.Count; i++)
            {
                String production = productions[i];
                String[] noTermAlt = production.Split(" -> ");
                String firstSymbol = noTermAlt[0];
                if (noTermAlt[0][noTermAlt[0].Length - 1].ToString().Equals("'"))
                {
                    firstSymbol = firstSymbol.Replace("'", "");
                }

                String nextSymbol = noTermAlt[0][noTermAlt[0].Length - 1].ToString();
                if (firstSymbol.Equals(noTerminal))
                {
                    result.Add(noTermAlt[1]);
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

        public void First()
        {
            FillFirst();
            emptyNoTerminals = GetAllEmptyNoTerminals();

            for (int i = 0; i < productions.Count; i++)
            {
                String production = productions[i];
                List<String> firstTemp = new List<string>();
                String[] noTermAltArr = production.Split(" -> ");
                List<String> alternative = noTermAltArr[1].Split(" ").OfType<string>().ToList();
                List<String> alt = alternative;
                int altLength = alt.Count;
                List<String> altSymbols = new List<String>();
                for (int j = 0; j < alt.Count; j++)
                {
                    if (j == alt.Count - 1)
                    {
                        altSymbols = alt.GetRange(j, 1);
                    }
                    else
                    {
                        altSymbols = alt.GetRange(j, altLength - j);
                    }

                    firstTemp.AddRange(GetLineOfFirstSet(altSymbols));
                    if (!emptyNoTerminals.Contains(altSymbols[0]))
                    {
                        break;
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

                String noTerminal = production.Split(" -> ")[0];

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

        private List<string> GetLineOfFirstSet(List<String> production)
        {
            List<string> result = new List<string>();
//            if (production.Equals("\'"))
//            {
//                return result;
//            }

            if (terminals.Contains(production[0]))
            {
                result.Add(production[0]);
            }
            else
            {
                List<string> temp = new List<string>();

                List<string> firstSet = new List<string>();
                    temp = GetProductionsForFirst(production[0]);
                    for (int i = 0; i < temp.Count; i++)
                    {
                        String[] noTermAltArr = temp[i].Split(" -> ");
                        List<String> alternative = noTermAltArr[1].Split(" ").OfType<string>().ToList();
                        List<String> alt = alternative;
                        int altLength = alt.Count;
                        List<String> altSymbols = new List<String>();
                        for (int j = 0; j < alt.Count; j++)
                        {
                            if (j == alt.Count - 1)
                            {
                                altSymbols = alt.GetRange(j, 1);
                            }
                            else
                            {
                                altSymbols = alt.GetRange(j, altLength - j);
                            }

                            firstSet.AddRange(GetLineOfFirstSet(altSymbols));
                            if (!emptyNoTerminals.Contains(altSymbols[0]))
                            {
                                break;
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
                String[] noTermAltArr = production.Split(" -> ");
                
                List<String> alternative =  noTermAltArr[1].Split(" ").OfType<String>().ToList(); ;
                if (alternative.Count > 1 && terminals.Contains(alternative[0]))
                {
                    continue;
                }

                String noTermAlt = noTermAltArr[0];
                if (alternative.Count >= 1 && alternative[0] == "&")
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

            return current;
        }

        private bool IsTerm(String alt)
        {
            return (alt.Length > 1 && Char.IsLower(alt[0]));
        }

        private List<string> GetNoTerminal(List<string> alt)
        {
            List<string> result = new List<string>();
            for (int i = 0; i < alt.Count; i++)
            {
                String item = alt[i];
                if (noTerminals.Contains(item))
                {
                    result.Add(item);
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
                String[] noTermAltArr = prod.Split(" -> ");
                String noTerminal = noTermAltArr[0];
                
                List<string> noTerminalsSet = GetNoTerminalsSet(noTermAltArr[1]);
                
                if (noTerminalsSet.Count == 0)
                {
                }
                else
                {
                    for (int j = 0; j < noTerminalsSet.Count; j++)
                    {
                        String alternative = noTermAltArr[1];
                        // TODO Rotate bug here
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

        private List<String> Rotate(String noTerminalSetEl, String production, String noTerminal)
        {
            List<String> result = new List<string>();
            String right = ProductionLeftOrRight(noTerminalSetEl, production, false);
            List<String> rightArr = right.Split(" ").OfType<String>().ToList();
            if (String.IsNullOrEmpty(right))
            {
                result.Add("º " + noTerminalSetEl + " " + noTerminal);
            }
            else
            {
                if (!noTerminals.Contains(rightArr[0]) && !rightArr[0][rightArr[0].Length - 1].Equals('\''))
                {
                    result.Add(noTerminalSetEl + " " + rightArr[0]);
                }
                else
                {
                    List<String> listRight = rightArr;
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
                                        result.Add("º " + noTerminalSetEl + " " + noTerminal);
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
        
        private void PassageFollowSet(List<String> followSetTemp)
        {
            for (int i = 0; i < followSetTemp.Count; i++)
            {
                List<String> temp = followSetTemp[i].Split(" ").OfType<String>().ToList();
                if (temp[0].Equals("º"))
                {
                    String nt1 = temp[1];
                    String nt2 = temp[2];

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
                if (!noTerminals.Contains(temp))
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
                if (arr[i] == noTerminal)
                {
                    result = i;
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
                    if (newElements[j].Split(" ")[0].Equals(follow[k][0]))
                    {
                        if (!follow[k].Contains(newElements[j].Split(" ")[1]))
                        {
                            follow[k].Add(newElements[j].Split(" ")[1]);
                        }
                    }
                }
            }
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
            int posBefore = 0;
            int posAfter = 0;
            String[] prods = prod.Split(" ");
            for (int i = 0; i < prods.Length; i++)
            {
                if (prods[i].Equals(noTerminal))
                {
                    posAfter = i + 1;
                }
            }
            
            List<String> resultArr = new List<string>();
            
                resultArr = (prods.OfType<String>().ToList()).GetRange(posAfter, prods.Length - posAfter);
                if (resultArr.Count == 0)
                {
                    return "";
                }

                if (resultArr.Count > 1)
            {
                for (int i = 0; i < resultArr.Count; i++)
                {
                    if (i != resultArr.Count - 1)
                    {
                        result += resultArr[i] + " ";
                    }
                    else
                    {
                        result += resultArr[i];
                    }
                }
            }
            else
            {
                result += resultArr[0];
            }
            
            return result;
        }

        private List<String> GetNoTerminalsSet(String alt)
        {
            List<String> result = new List<string>();
            List<String> alts = alt.Split(" ").OfType<String>().ToList();

            for (int i = 0; i < alts.Count; i++)
            {
                String item = alts[i];
                if (noTerminals.Contains(item))
                {
                    result.Add(item);
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

        private List<String> GetProductionsForFirst(String noTerminal)
        {
            List<String> result = new List<string>();
            for (int i = 0; i < productions.Count; i++)
            {
                String production = productions[i];
                String[] noTermAltArr = production.Split(" -> ");

                String nt = noTermAltArr[0];
                if (nt.Equals(noTerminal))
                {
                    result.Add(production);
                }
            }

            return result;
        }

        private bool IsApostrophe(String word)
        {
            return word[word.Length - 1].ToString() == "'";
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
                String[] noTermAlt = production.Split(" -> ");
                String noTerminal = noTermAlt[0];
                List<String> tempAlts = noTermAlt[1].Split(" ").OfType<String>().ToList();
                List<String> tempProd = new List<string>();
                tempProd.Add(noTerminal);
                if (noTerminal[noTerminal.Length - 1].ToString().Equals("'"))
                {
                    tempProd.Add("'");
                }

                tempProd.Add("-");
                tempProd.Add(">");
                tempProd.AddRange(tempAlts);
//                    ints.OfType<int>().ToList();

                List<String> lines = line.Split(" ").OfType<String>().ToList();
                if (tempProd.Count >= 3 + lines.Count)
                {
                    List<String> next = tempProd.GetRange(3, line.Length);
                    if (IsEqualsList(next, lines))
                    {
                        String alternative = null;
                        if (tempProd.Count == next.Count + 3)
                        {
                            alternative = noTerminal + "' -> &";
                            productions[i] = alternative;
                        }
                        else
                        {
                            List<String> rightArr =
                                tempProd.GetRange(lines.Count + 3, tempProd.Count - (lines.Count + 3));
                            String right = "";
                            if (rightArr.Count > 1)
                            {
                                for (int j = 0; j < rightArr.Count; j++)
                                {
                                    if (j != rightArr.Count - 1)
                                    {
                                        right += rightArr[j] + " ";
                                    }
                                    else
                                    {
                                        right += rightArr[j];
                                    }
                                }
                            }
                            else
                            {
                                right += rightArr[0];
                            }

                            alternative = noTerminal + "' -> " + right;
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

            String[] altsArr = lines[0].Split(" ");
            List<String> alts = altsArr.OfType<string>().ToList();
            List<String> result = alts;

            for (int i = 1; i < lines.Count; i++)
            {
                altsArr = lines[i].Split(" ");
                alts = altsArr.OfType<string>().ToList();
                if (alts.Count < result.Count)
                {
                    result = alts;
                }
            }

            int end = result.Count;
            for (int i = 0; i < lines.Count; i++)
            {
                int j;
                for (j = 0; j < end; j++)
                {
                    String[] altsArr1 = lines[i].Split(" ");
                    List<String> alts1 = altsArr1.OfType<string>().ToList();
                    if (result[j] != alts1[j])
                    {
                        break;
                    }
                }

                if (j < end)
                {
                    end = j;
                }
            }

            String res = "";
            result = result.GetRange(0, result.Count - end);
            if (result.Count > 1)
            {
                for (int i = 0; i < result.Count; i++)
                {
                    if (i != result.Count - 1)
                    {
                        res += result[i] + " ";
                    }
                    else
                    {
                        res += result[i];
                    }
                }
            }
            else
            {
                res += result[0];
            }

            return res;
        }

        private List<String> LeaveAppearence(List<String> productions, String ch)
        {
            List<String> result = new List<string>();
            int count = 0;
            for (int i = 0; i < productions.Count; i++)
            {
                String[] altsArr = productions[i].Split(" ");
                List<String> alts = altsArr.OfType<String>().ToList();
                if (ch.Equals(alts[0]))
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
                String[] altArr = productions[i].Split(" ");
                result.Add(altArr[0]);
            }

            return RemoveDuplicates(result);
        }

        private List<String> RemoveDuplicates(List<String> items)
        {
            return new List<string>(new HashSet<string>(items));
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
                        String[] noTermAlt = production.Split(" -> ");
                        String noTerminal = noTermAlt[0];
                        String nextSymbolNoTerminal = noTerminal[noTerminal.Length - 1].ToString();
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
            String[] noTermAlt = production.Split(" -> ");
            String[] alts = noTermAlt[1].Split(" ");
            return noTermAlt[0] == alts[0] ? true : false;
        }
    }
}