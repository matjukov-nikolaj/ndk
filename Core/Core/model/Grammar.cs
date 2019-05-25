using System;
using System.Collections.Generic;

namespace Core.model
{
    public class Grammar
    {

        public Dictionary<String, List<String>> grammar;

        public List<String> terminals;

        public List<String> noTerminals;

        public Grammar()
        {
            grammar = new Dictionary<string, List<String>>();
            terminals = new List<string>();
            noTerminals = new List<string>();
        }

        public void AddProduction(String noTerminal, String alternative)
        {
            if (grammar.ContainsKey(noTerminal))
            {
                grammar[noTerminal].Add(alternative);
            }
            else
            {
                List<String> alternatives = new List<string>();
                alternatives.Add(alternative);
                grammar.Add(noTerminal, alternatives);
            }
        }

        public void AddTerminal(String terminal)
        {
            terminals.Add(terminal);
        }

        public void AddNoTerminal(String noTerminal)
        {
            noTerminals.Add(noTerminal);
        }

    }
}