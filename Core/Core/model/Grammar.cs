using System;
using System.Collections.Generic;

namespace Core.model
{
    public class Grammar
    {

        public List<String> productions;

        public List<String> terminals;

        public List<String> noTerminals;

        public String startSymbol;

        public Grammar()
        {
            productions = new List<String>();
            terminals = new List<string>();
            noTerminals = new List<string>();
            startSymbol = "";
        }

    }
}