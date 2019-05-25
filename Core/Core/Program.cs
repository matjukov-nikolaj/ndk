using System;
using Core.model;
using Newtonsoft.Json;

namespace Core
{
    class Program
    {
        static void Main(string[] args)
        {
//      GRAMMAR            
//            A->AaB
//            A->B
//            B->BkC
//            B->C
//            C->y
//            C->xC
//            C->nDi
//            D->&
//            D->oAz

            Grammar grammar = new Grammar();
            grammar.AddProduction("A", "AaB");
            grammar.AddProduction("A", "B");
            grammar.AddProduction("B", "BkC");
            grammar.AddProduction("B", "C");
            grammar.AddProduction("C", "y");
            grammar.AddProduction("C", "xC");
            grammar.AddProduction("C", "nDi");
            grammar.AddProduction("D", "&");
            grammar.AddProduction("D", "oAz");
            
            grammar.AddTerminal("a");
            grammar.AddTerminal("k");
            grammar.AddTerminal("y");
            grammar.AddTerminal("x");
            grammar.AddTerminal("n");
            grammar.AddTerminal("i");
            grammar.AddTerminal("o");
            grammar.AddTerminal("z");
            
            grammar.AddNoTerminal("A");
            grammar.AddNoTerminal("A\'");
            grammar.AddNoTerminal("B");
            grammar.AddNoTerminal("B\'");
            grammar.AddNoTerminal("C");
            grammar.AddNoTerminal("D");
            
            
            NewGrammar newGrammar = new NewGrammar();
            newGrammar.AddProduction("A", "BA\'");
            newGrammar.AddProduction("A\'", "aBA\'");
            newGrammar.AddProduction("B\'", "&");
            newGrammar.AddProduction("B", "CB\'");
            newGrammar.AddProduction("B\'", "kCB\'");
            newGrammar.AddProduction("B\'", "&");
            newGrammar.AddProduction("C", "y");
            newGrammar.AddProduction("C", "xC");
            newGrammar.AddProduction("C", "nDi");
            newGrammar.AddProduction("D", "&");
            newGrammar.AddProduction("D", "oAz");
            
            // First
            newGrammar.AddInFirst("A", "y");
            newGrammar.AddInFirst("A", "x");
            newGrammar.AddInFirst("A", "n");
            
            newGrammar.AddInFirst("A\'", "a");
            newGrammar.AddInFirst("A\'", "&");
            
            newGrammar.AddInFirst("B", "y");
            newGrammar.AddInFirst("B", "x");
            newGrammar.AddInFirst("B", "n");
            
            newGrammar.AddInFirst("B\'", "k");
            newGrammar.AddInFirst("B\'", "&");
            
            newGrammar.AddInFirst("C", "y");
            newGrammar.AddInFirst("C", "x");
            newGrammar.AddInFirst("C", "n");
            
            newGrammar.AddInFirst("D", "&");
            newGrammar.AddInFirst("D", "o");
            
            //Follow
            newGrammar.AddInFollow("A", "$");
            newGrammar.AddInFollow("A", "z");
            
            newGrammar.AddInFollow("A\'", "$");
            newGrammar.AddInFollow("A\'", "z");
            
            newGrammar.AddInFollow("B", "a");
            newGrammar.AddInFollow("B", "$");
            newGrammar.AddInFollow("B", "z");
            
            newGrammar.AddInFollow("B\'", "a");
            newGrammar.AddInFollow("B\'", "$");
            newGrammar.AddInFollow("B\'", "z");
            
            newGrammar.AddInFollow("C", "k");
            newGrammar.AddInFollow("C", "a");
            newGrammar.AddInFollow("C", "$");
            newGrammar.AddInFollow("C", "z");
            
            newGrammar.AddInFollow("D", "i");
            
            
            TableM tableM = new TableM();
            tableM.Insert("A", "a", "");
            tableM.Insert("A", "k", "");
            tableM.Insert("A", "y", "A->BA\'");
            tableM.Insert("A", "x", "A->BA\'");
            tableM.Insert("A", "n", "A->BA\'");
            tableM.Insert("A", "i", "");
            tableM.Insert("A", "o", "");
            tableM.Insert("A", "z", "");
            tableM.Insert("A", "&", "");
            
            tableM.Insert("A\'", "a", "A\'->aBA\'");
            tableM.Insert("A\'", "k", "");
            tableM.Insert("A\'", "y", "");
            tableM.Insert("A\'", "x", "");
            tableM.Insert("A\'", "n", "");
            tableM.Insert("A\'", "i", "");
            tableM.Insert("A\'", "o", "");
            tableM.Insert("A\'", "z", "A\'->&");
            tableM.Insert("A\'", "&", "A\'->&");
            
            tableM.Insert("B", "a", "");
            tableM.Insert("B", "k", "");
            tableM.Insert("B", "y", "B->CB\'");
            tableM.Insert("B", "x", "B->CB\'");
            tableM.Insert("B", "n", "B->CB\'");
            tableM.Insert("B", "i", "");
            tableM.Insert("B", "o", "");
            tableM.Insert("B", "z", "");
            tableM.Insert("B", "&", "");
            
            tableM.Insert("B\'", "a", "B\'->&");
            tableM.Insert("B\'", "k", "B\'->kCB\'");
            tableM.Insert("B\'", "y", "");
            tableM.Insert("B\'", "x", "");
            tableM.Insert("B\'", "n", "");
            tableM.Insert("B\'", "i", "");
            tableM.Insert("B\'", "o", "");
            tableM.Insert("B\'", "z", "B\'->&");
            tableM.Insert("B\'", "&", "B\'->&");
            
            tableM.Insert("C", "a", "");
            tableM.Insert("C", "k", "");
            tableM.Insert("C", "y", "C->y");
            tableM.Insert("C", "x", "C->xC");
            tableM.Insert("C", "n", "C->nDi");
            tableM.Insert("C", "i", "");
            tableM.Insert("C", "o", "");
            tableM.Insert("C", "z", "");
            tableM.Insert("C", "&", "");
            
            tableM.Insert("D", "a", "");
            tableM.Insert("D", "k", "");
            tableM.Insert("D", "y", "");
            tableM.Insert("D", "x", "");
            tableM.Insert("D", "n", "");
            tableM.Insert("D", "i", "D->&");
            tableM.Insert("D", "o", "D->oAz");
            tableM.Insert("D", "z", "");
            tableM.Insert("D", "&", "");
            
            Chain chain = new Chain();
            chain.SetChain("y");
            chain.SetResult(true);
            
            chain.AddProcessLine("$A", "y&", "A->BA\'");
            chain.AddProcessLine("$A\'B", "y&", "B->CB\'");
            chain.AddProcessLine("$A\'B\'C", "y&", "C->y");
            chain.AddProcessLine("$A\'B\'y", "y&", "");
            chain.AddProcessLine("$A\'B\'", "&", "B\'->&");
            chain.AddProcessLine("$A\'", "&", "A\'->&");
            chain.AddProcessLine("$", "&", "");
            
            Console.WriteLine(JsonConvert.SerializeObject(grammar));
            Console.WriteLine("\n");
            Console.WriteLine(JsonConvert.SerializeObject(newGrammar));
            Console.WriteLine("\n");
            Console.WriteLine(JsonConvert.SerializeObject(tableM));
            Console.WriteLine("\n");
            Console.WriteLine(JsonConvert.SerializeObject(chain));
            Console.WriteLine("\n");
            
        }
    }
}