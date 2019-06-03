using System;
using System.Collections.Generic;
using System.Linq;
using Core;
using Core.model;
using Newtonsoft.Json;
using StackExchange.Redis;

namespace GrammarProcessor
{
    class Program
    {
        private static Dictionary<string, string> properties = Configuration.GetParameters();

        const string PROCESSOR_CHANNEL = "processor";
        const string PROCESSOR_QUEUE_NAME = "processor_queue";

        static void Main(string[] args)
        {
            Console.WriteLine("Grammar processor is running.");
            try
            {
                ConnectionMultiplexer redisConnection = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"]);
                ISubscriber sub = redisConnection.GetSubscriber();
                sub.Subscribe("events", (channel, message) =>
                {
                    string id = message.ToString();
                    if (id.Contains("INPUT_GRAMMAR_"))
                    {
                        int dbNumber = Convert.ToInt32(properties["INPUT_GRAMMAR_DB"]);
                        IDatabase redisDb = redisConnection.GetDatabase(dbNumber);
                        string value = redisDb.StringGet(id);
                        Console.WriteLine("Event: " + id + "\n" + value);
                        HandleGrammarProcessor(id, value, sub);
                    }
                });
                Console.ReadKey();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }

        private static void HandleGrammarProcessor(String id, string value, ISubscriber sub)
        {
            try
            {
                List<String> productions = value.Split("\n").ToList();
                if (productions.Contains(""))
                {
                    int index = productions.IndexOf("");
                    productions.RemoveAt(index);
                }

                GrammarProcessor grammarProcessor = new GrammarProcessor(productions);
                grammarProcessor.HandleInputGrammar();
                Grammar grammar = grammarProcessor.GetGrammar();
                String json = JsonConvert.SerializeObject(grammar);
                IDatabase redisDb = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"])
                    .GetDatabase(Convert.ToInt32(properties["GRAMMAR_DB"]));
                string newId = id.Replace("INPUT_GRAMMAR_", "GRAMMAR_");
                redisDb.StringSet(newId, json);
                Console.WriteLine(newId + ": " + json + " - saved to redis GRAMMAR_DB");
                
                
                
                // Queue
                IDatabase processorQueueDb = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"])
                    .GetDatabase(Convert.ToInt32(properties["PROCESSOR_QUEUE_DB"]));
                // put message to queue
                processorQueueDb.ListLeftPush(PROCESSOR_QUEUE_NAME, newId, flags: CommandFlags.FireAndForget);
                // and notify consumers
                processorQueueDb.Multiplexer.GetSubscriber().Publish(PROCESSOR_CHANNEL, "");
                
                
                grammarProcessor.Clear();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}