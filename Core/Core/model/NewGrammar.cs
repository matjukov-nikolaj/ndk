using System;
using System.Collections.Generic;

namespace Core.model
{
    public class NewGrammar
    {
        public Dictionary<String,  List<String>> newGrammar;

        public Dictionary<String, List<String>> first;

        public Dictionary<String, List<String>> follow;

        public NewGrammar()
        {
            newGrammar = new Dictionary<string,  List<String>>();
            first = new Dictionary<string, List<string>>();
            follow = new Dictionary<string, List<string>>();
        }
        
        public void AddProduction(String noTerminal, String alternative)
        {
            if (newGrammar.ContainsKey(noTerminal))
            {
                newGrammar[noTerminal].Add(alternative);
            }
            else
            {
                List<String> alternatives = new List<string>();
                alternatives.Add(alternative);
                newGrammar.Add(noTerminal, alternatives);
            }
        }

        public void AddInFirst(String noTerminal, String terminal)
        {
            if (first.ContainsKey(noTerminal))
            {
                List<String> terminals = first[noTerminal];
                terminals.Add(terminal);
            }
            else
            {
                List<String> terminals = new List<string>();
                terminals.Add(terminal);
                first.Add(noTerminal, terminals);
            }
        }
        
        public void AddInFollow(String noTerminal, String terminal)
        {
            if (follow.ContainsKey(noTerminal))
            {
                List<String> terminals = first[noTerminal];
                terminals.Add(terminal);
            }
            else
            {
                List<String> terminals = new List<string>();
                terminals.Add(terminal);
                follow.Add(noTerminal, terminals);
            }
        }
    }
}