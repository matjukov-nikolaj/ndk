using System;
using System.Collections.Generic;

namespace Core.model
{
    public class NewGrammar
    {
        public List<String> productions;

        public List<List<String>> first;

        public List<List<String>> follow;
        
        public List<String> terminals;

        public List<String> noTerminals;
        
        public List<String> emptyNoTerminals;

        public NewGrammar()
        {
            productions = new List<string>();
            first = new List<List<String>>();
            follow = new List<List<String>>();
            terminals = new List<string>();
            noTerminals = new List<string>();
            emptyNoTerminals = new List<string>();
        }
        
    }
}