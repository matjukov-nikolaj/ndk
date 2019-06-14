using System;

namespace SlrParser
{
    class Program
    {
        static void Main(string[] args)
        {
            String input = "<S>+ -> real <idlist>\n<idlist> -> <id> , <idlist>\n<idlist> -> <id>\n<id> -> A\n<id> -> B\n<id> -> C";
            SlrParser slrParser = new SlrParser(input);
            slrParser.ProcessInputGrammar();
            Console.WriteLine("Hello!");
        }
    }
}