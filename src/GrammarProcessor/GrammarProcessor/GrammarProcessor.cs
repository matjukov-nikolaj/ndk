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
            String row = inputGrammar[0];
            grammar.startSymbol = row.Split(" -> ")[0];
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
                String alternative = inputGrammar[i].Split(" -> ")[1];
                String[] alts = alternative.Split(" ");
                for (int j = 0; j < alts.Length; j++)
                {
                    String element = alts[j];
                    if (element.Length >= 3)
                    {
                        if (element[0] != '<' && element[element.Length - 1] != '>')
                        {
                            if (!result.Contains(element))
                            {
                                result.Add(element);
                            }
                        }
                    }
                    else
                    {
                        if (!result.Contains(element))
                        {
                            result.Add(element);
                        }
                    }
                }
            }

            return result;
        }

        private List<String> GetNoTerminals(List<String> inputGrammar)
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



    }
}