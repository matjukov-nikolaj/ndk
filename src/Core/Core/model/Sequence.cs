using System;
using System.Collections.Generic;

namespace Core.model
{
    public class Sequence
    {
        public String grammarId;
        
        public String sequence;

        public Dictionary<String, List<String>> processTable;

        public bool result;

        public Sequence()
        {
            processTable = new Dictionary<string, List<string>>();
            processTable.Add("STATE", new List<string>());
            processTable.Add("SEQUENCE", new List<string>());
            processTable.Add("TRANSITION", new List<string>());
            sequence = "";
            result = false;
            grammarId = "";
        }

    }
}