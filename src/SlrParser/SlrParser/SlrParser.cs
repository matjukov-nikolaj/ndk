using System;
using System.Collections.Generic;
using System.Linq;

namespace SlrParser
{
    public class SlrParser
    {
        private string input;

        public List<String> rules;

        private List<String> noTerminals;

        private List<String> terminals;

        private List<String> alphabet;

        public SlrParser(string input)
        {
            this.input = input;
            rules = new List<String>();
            noTerminals = new List<String>();
            terminals = new List<String>();
            alphabet = new List<string>();
        }

        private void InitializeRulesAndAlphabetAndNonterminals()
        {
            String[] lines = this.input.Split("\n");
            for (int i = 0; i < lines.Length; i++)
            {
                
            }
        }

        private void Rule(String text)
        {
            
        }

    }
}