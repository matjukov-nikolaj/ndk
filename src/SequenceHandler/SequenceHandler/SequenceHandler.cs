using System;
using System.Collections.Generic;
using Core.model;

namespace SequenceHandler
{
    public class SequenceHandler
    {
        private String startSymbol;
        private List<String> terminals;
        private List<String> noTerminals;
        private List<List<String>> mTable;

        private Sequence sequenceModel;
        
        public SequenceHandler(String startSymbol, List<String> terminals, List<String> noTerminals, List<List<String>> mTable)
        {
            this.startSymbol = startSymbol;
            this.terminals = terminals;
            this.noTerminals = noTerminals;
            this.mTable = mTable;
            
            this.sequenceModel = new Sequence();
            
        }

        public Sequence GetSequence()
        {
            return sequenceModel;
        }

        public void Process(String inStr)
        {
            sequenceModel.sequence = inStr;
            List<String> result = new List<string>();
            try
            {
                String sequence = inStr + "$";
                String stack = "$" + startSymbol;
                String record = sequence;
                String exit = GetFromTable(startSymbol,inStr[0].ToString());

                while (!record.Equals("$") || !stack.Equals("$"))
                {
                    if (exit.Equals("error"))
                    {
                        break;
                    }

                    String noTerminal = "";
                    String terminal = "";

                    if (stack.Length > 1)
                    {
                        noTerminal = stack[stack.Length - 1].ToString();
                        terminal = record[0].ToString();

                    }
                    else
                    {
                        noTerminal = stack;
                        terminal = record;
                    }

                    if (noTerminal.Equals("&"))
                    {
                        stack = stack.Substring(0, stack.Length - 1);
                        noTerminal = stack[stack.Length - 1].ToString();
                    }

                    if (noTerminal.Equals("'"))
                    {
                        noTerminal = stack[stack.Length - 2] + noTerminal;
                    }


                    if (Char.IsUpper(noTerminal[0]))
                    {
                        exit = GetFromTable(noTerminal, terminal);
                        if (exit.Equals(""))
                        {
                            exit = "error";
                        }
                        else
                        {
                            sequenceModel.processTable["STATE"].Add(stack);
                            sequenceModel.processTable["SEQUENCE"].Add(record);
                            sequenceModel.processTable["TRANSITION"].Add(exit);

                            String exitNoTerm = "";
                            String exitTerm = "";
                            if (IsApostrophe(exit))
                            {
                                exitNoTerm = exit.Substring(0, 2);
                                exitTerm = exit.Substring(4, 1);

                                if (IsApostrophe(exitTerm))
                                {
                                    exitTerm += "'";
                                }
                            }
                            else
                            {
                                exitNoTerm = exit.Substring(0, 1);
                                exitTerm = exit.Substring(3, 1);

                                if (exit.Length > 4)
                                {
                                    if (IsApostrophe(exitTerm))
                                    {
                                        exitTerm += "'";
                                    }
                                }

                            }

                            if (stack.Substring(stack.Length - 1, 1).Equals("'"))
                            {
                                stack = stack.Substring(0, stack.Length - 2);
                            }
                            else
                            {
                                if (!stack.Equals("$"))
                                {
                                    stack = stack.Substring(0, stack.Length - 1);
                                }
                            }

                            String term = "";

                            for (int j = 0; j < exit.Length; j++)
                            {
                                int posExit = exit.Length - j;
                                if (exit.Substring(posExit - 1, 1).Equals(">"))
                                {
                                    break;
                                }
                                else
                                {
                                    string lol = exit.Substring(posExit - 1, 1);
                                    if (!exit.Substring(posExit - 1, 1).Equals("'")) {
                                        if (j != 0)
                                        {
                                            if (exit.Substring(posExit, 1).Equals("'"))
                                            {
                                                term += exit.Substring(posExit - 1, 2);
                                                
                                            }
                                            else
                                            {
                                                term += exit.Substring(posExit - 1, 1);
                                            }
                                        }
                                        else
                                        {
                                            term += exit.Substring(posExit - 1, 1);
                                        }
                                    }
                                }
                            }

                            stack += term;

                        }

                        noTerminal = "";
                        terminal = "";

                        if (stack.Length > 1)
                        {
                            noTerminal = stack[stack.Length - 1].ToString();
                            terminal = record[0].ToString();
                        }
                        else
                        {
                            noTerminal = stack;
                            terminal = record;
                        }

                        if (noTerminal.Equals("&"))
                        {
                            stack = stack.Substring(0, stack.Length - 1);
                            noTerminal = stack[stack.Length - 1].ToString();
                            
                        }

                        if (noTerminal.Equals("'"))
                        {
                            noTerminal = stack[stack.Length - 2] + noTerminal;
                        }

                        if (noTerminal.Equals(terminal))
                        {
                            exit = "";
                            if (!stack.Equals("$") && !record.Equals("$"))
                            {
                                sequenceModel.processTable["STATE"].Add(stack);
                                sequenceModel.processTable["SEQUENCE"].Add(record);
                                sequenceModel.processTable["TRANSITION"].Add(exit);


                                stack = stack.Substring(0, stack.Length - 1);
                                record = record.Substring(1, record.Length - 1);
                            }
                        }
                        else
                        {
                            if (!Char.IsUpper(noTerminal[0]))
                            {
                                exit = "error";
                            }
                        }

                        if (record.Equals("$") && stack.Equals("$"))
                        {
                            sequenceModel.result = true;
                        }
                        if (record.Equals("$") && stack.Equals("$"))
                        {
                            sequenceModel.processTable["STATE"].Add(stack);
                            sequenceModel.processTable["SEQUENCE"].Add(record);
                            sequenceModel.processTable["TRANSITION"].Add(exit);
                        }
                    }
                }
                if (record.Equals("$") && stack.Equals("$"))
                {
                    sequenceModel.result = true;
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
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
        
        private string GetFromTable(String noTerminal, String terminal)
        {
            String result = "";
            int posNoTerminal = -1;
            for (int i = 1; i <= noTerminals.Count; i++)
            {
                if (mTable[i][0].Equals(noTerminal))
                {
                    posNoTerminal = i;
                }
            }

            int posTerminal = -1;
            for (int i = 1; i <= terminals.Count + 1; i++)
            {
                if (mTable[0][i].Equals(terminal))
                {
                    posTerminal = i;
                }
            }

            result = mTable[posNoTerminal][posTerminal];
            return result;
        }

    }
}