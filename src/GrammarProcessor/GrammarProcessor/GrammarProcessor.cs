using System;
using System.Collections.Generic;
using System.Linq;
using Core.model;

namespace GrammarProcessor
{
    public class GrammarProcessor
    {
        private Grammar grammar;

        private List<String> inputGrammar;

        public GrammarProcessor(List<String> input)
        {
            grammar = new Grammar();
            this.inputGrammar = input;
            grammar.startSymbol = inputGrammar[0][0].ToString();
        }

        public Grammar GetGrammar()
        {
            return grammar;
        }

        public void Clear()
        {
            inputGrammar.Clear();
            grammar.productions.Clear();
            grammar.terminals.Clear();
            grammar.noTerminals.Clear();
        }

        public void HandleInputGrammar()
        {
            grammar.productions.AddRange(inputGrammar);
            grammar.terminals.AddRange(GetTerminals(inputGrammar));
            grammar.noTerminals.AddRange(GetNoTerminals(inputGrammar));
        }
        
        private List<String> GetTerminals(List<String> inputGrammar)
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

        private List<String> GetNoTerminals(List<String> inputGrammar)
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



    }
}