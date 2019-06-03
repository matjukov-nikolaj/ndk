using System;
using System.Collections.Generic;

namespace Core.model
{
    public class Statistic
    {
        public Dictionary<String, List<String>> statistic;
        
        public Statistic() {
            statistic = new Dictionary<String, List<string>>();
        }
    }

}