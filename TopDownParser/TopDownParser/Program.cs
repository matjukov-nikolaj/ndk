using System;
using System.Collections.Generic;

namespace TopDownParser
{
    class Program
    {
        static void Main(string[] args)
        {
            List<String> inc = new List<String>();
            inc.Add("A->AaB");
            TopDownParser parser = new TopDownParser(inc);
            List<String> kek = parser.GetTerminals(inc);
            Console.WriteLine("1");
        }
    }
}