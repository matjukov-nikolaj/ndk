using System;
using System.Collections.Generic;
using Core;
using Core.model;
using Newtonsoft.Json;
using StackExchange.Redis;

namespace GrammarConverter
{
    class Program
    {
        private static Dictionary<string, string> properties = Configuration.GetParameters();

        static void Main(string[] args)
        {
            Console.WriteLine("Grammar converter is running.");
            try
            {
                ConnectionMultiplexer redisConnection = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"]);
                ISubscriber sub = redisConnection.GetSubscriber();
                sub.Subscribe("events", (channel, message) =>
                {
                    string id = message.ToString();
                    if (id.Contains("GRAMMAR_"))
                    {
                        int dbNumber = Convert.ToInt32(properties["GRAMMAR_DB"]);
                        IDatabase redisDb = redisConnection.GetDatabase(dbNumber);
                        string value = redisDb.StringGet(id);
                        Console.WriteLine("Event: " + id + "-" + value);
                        HandleGrammarConverter(id, value, sub);
                    }
                });
                Console.ReadKey();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }

        static void HandleGrammarConverter(string id, string value, ISubscriber sub)
        {
            try
            {
                Grammar grammar = JsonConvert.DeserializeObject<Grammar>(value);
                GrammarConverter grammarConverter = new GrammarConverter(grammar);
                grammarConverter.DeleteLeftRecursion();
                grammarConverter.Factorization();
                grammarConverter.SortGrammar();
                            
                grammarConverter.SetNoTerminals(grammarConverter.GetNoTerminals(grammarConverter.GetProductions()));
                            
                grammarConverter.First();
                            
                grammarConverter.Follow();
                            
                NewGrammar newGrammar = new NewGrammar();

                newGrammar.productions = grammarConverter.GetProductions();

                newGrammar.first = grammarConverter.GetFirst();

                newGrammar.follow = grammarConverter.GetFollow();

                newGrammar.terminals = grammarConverter.GetTerminals();

                newGrammar.noTerminals = grammarConverter.GetNoTerminals();

                String json = JsonConvert.SerializeObject(newGrammar);
                            
                IDatabase redisDb = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"])
                    .GetDatabase(Convert.ToInt32(properties["NEW_GRAMMAR_DB"]));
                string newId = id.Replace("GRAMMAR_", "NEW_GRAMMAR_");
                redisDb.StringSet(newId, json);
                Console.WriteLine(newId + ": " + json + " - saved to redis NEW_GRAMMAR_DB");
                sub.Publish("events", $"{newId}");
                grammarConverter.Clear();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}