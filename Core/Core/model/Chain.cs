using System;
using System.Collections.Generic;

namespace Core.model
{
    public class Chain
    {
        private static String STACK = "STACK";

        private static String RECORD = "RECORD";
        
        private static String EXIT = "EXIT";
        
        public String chain;

        public Dictionary<String, List<String>> processTable;

        public bool result;

        public Chain()
        {
            processTable = new Dictionary<string, List<string>>();
            processTable.Add(STACK, new List<string>());
            processTable.Add(RECORD, new List<string>());
            processTable.Add(EXIT, new List<string>());
            chain = "";
            result = false;
        }

        public void AddProcessLine(String stack, String record, String exit)
        {
            processTable[STACK].Add(stack);
            processTable[RECORD].Add(record);
            processTable[EXIT].Add(exit);
        }

        public Dictionary<String, List<String>> GetProcessTable()
        {
            return processTable;
        }

        public void SetChain(String chain)
        {
            this.chain = chain;
        }

        public void SetResult(bool result)
        {
            this.result = result;
        }

    }
}