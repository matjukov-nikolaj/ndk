using System;
using System.Collections.Generic;

namespace Core.model
{
    public class StatisticResult
    {
        public Dictionary<String, Dictionary<List<String>, List<String>>> result;

        public StatisticResult()
        {
            result = new Dictionary<string, Dictionary<List<string>, List<string>>>();
        }
    }
}