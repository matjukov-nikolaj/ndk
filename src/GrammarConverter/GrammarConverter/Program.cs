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

        const string PROCESSOR_CHANNEL = "processor";
        const string PROCESSOR_QUEUE_NAME = "processor_queue";

        const string NEW_GRAMMAR_CHANNEL = "new_grammar";
        const string NEW_GRAMMAR_QUEUE_NAME = "new_grammar_queue";

        static void Main(string[] args)
        {
            Console.WriteLine("Grammar converter is running.");
            try
            {
                ConnectionMultiplexer redisConnection = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"]);
                ISubscriber sub = redisConnection.GetSubscriber();

                sub.Subscribe(PROCESSOR_CHANNEL, delegate
                {
                    IDatabase queueDb = redisConnection.GetDatabase(Convert.ToInt32(properties["PROCESSOR_QUEUE_DB"]));
                    string msg = queueDb.ListRightPop(PROCESSOR_QUEUE_NAME);

                    while (msg != null && msg != "")
                    {
                        string id = msg.ToString();
                        int dbNumber = Convert.ToInt32(properties["GRAMMAR_DB"]);
                        IDatabase redisDb = redisConnection.GetDatabase(dbNumber);
                        string value = redisDb.StringGet(id);
                        Console.WriteLine("Get from processor queue: " + id + "-" + value);
                        HandleGrammarConverter(id, value, sub);
                        msg = queueDb.ListRightPop(PROCESSOR_QUEUE_NAME);
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

                newGrammar.emptyNoTerminals = grammarConverter.GetEmptyNoTerminals();

                String json = JsonConvert.SerializeObject(newGrammar);

                IDatabase redisDb = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"])
                    .GetDatabase(Convert.ToInt32(properties["NEW_GRAMMAR_DB"]));
                string newId = id.Replace("GRAMMAR_", "NEW_GRAMMAR_");
                redisDb.StringSet(newId, json);
                Console.WriteLine(newId + ": " + json + " - saved to redis NEW_GRAMMAR_DB");
                
                
                // Queue
                IDatabase newGrammarQueueDb = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"])
                    .GetDatabase(Convert.ToInt32(properties["NEW_GRAMMAR_QUEUE_DB"]));
                // put message to queue
                newGrammarQueueDb.ListLeftPush(NEW_GRAMMAR_QUEUE_NAME, newId, flags: CommandFlags.FireAndForget);
                // and notify consumers
                newGrammarQueueDb.Multiplexer.GetSubscriber().Publish(NEW_GRAMMAR_CHANNEL, "");
                
                
                grammarConverter.Clear();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}