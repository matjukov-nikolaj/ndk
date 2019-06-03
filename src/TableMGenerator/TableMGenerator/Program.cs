using System;
using System.Collections.Generic;
using Core;
using Core.model;
using Newtonsoft.Json;
using StackExchange.Redis;

namespace TableMGenerator
{
    class Program
    {
        private static Dictionary<string, string> properties = Configuration.GetParameters();

        const string NEW_GRAMMAR_CHANNEL = "new_grammar";
        const string NEW_GRAMMAR_QUEUE_NAME = "new_grammar_queue";

        static void Main(string[] args)
        {
            Console.WriteLine("Table M Generator is running.");
            try
            {
                ConnectionMultiplexer redisConnection = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"]);
                ISubscriber sub = redisConnection.GetSubscriber();
                sub.Subscribe(NEW_GRAMMAR_CHANNEL, delegate
                {
                    IDatabase newGrammarQueueDb = redisConnection.GetDatabase(Convert.ToInt32(properties["NEW_GRAMMAR_QUEUE_DB"]));
                    string msg = newGrammarQueueDb.ListRightPop(NEW_GRAMMAR_QUEUE_NAME);
                    while (msg != null && msg != "")
                    {
                        string id = msg.ToString();
                        int dbNumber = Convert.ToInt32(properties["NEW_GRAMMAR_DB"]);
                        IDatabase redisDb = redisConnection.GetDatabase(dbNumber);
                        string value = redisDb.StringGet(id);
                        Console.WriteLine("Event: " + id + "\r\n" + value);

                        GenerateTable(id, value, sub);
                        msg = newGrammarQueueDb.ListRightPop(NEW_GRAMMAR_QUEUE_NAME);
                    }
                });
                Console.ReadKey();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }

        private static void GenerateTable(String id, string value, ISubscriber sub)
        {
            try
            {
                NewGrammar newGrammar = JsonConvert.DeserializeObject<NewGrammar>(value);
                TableMGenerator generator = new TableMGenerator(newGrammar);
                generator.GenerateTable();
                MTable table = new MTable();
                table.mTable = generator.GetTable();
                String json = JsonConvert.SerializeObject(table);

                IDatabase redisDb = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"])
                    .GetDatabase(Convert.ToInt32(properties["TABLE_M_DB"]));
                string newId = id.Replace("NEW_GRAMMAR_", "TABLE_M_");
                redisDb.StringSet(newId, json);
                Console.WriteLine(newId + ": " + json + " - saved to redis TABLE_M_DB");
                generator.Clear();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}