using System;
using System.Collections.Generic;

namespace Core.model
{
    public class TableM
    {
        public Dictionary<String, Dictionary<String, String>> tableM;

        public TableM()
        {
            tableM = new Dictionary<string, Dictionary<string, string>>();
        }

        public void Insert(String noTerminal, String terminal, String value)
        {
            if (tableM.ContainsKey(noTerminal))
            {
                Dictionary<String, String> row = tableM[noTerminal];
                if (row.ContainsKey(terminal))
                {
                    row[terminal] = value;
                }
                else
                {
                    row.Add(terminal, value);
                }
            }
            else
            {
                Dictionary<String, String> newRow = new Dictionary<string, string>();
                newRow.Add(terminal, value);
                tableM.Add(noTerminal, newRow);
            }
        }

    }
}