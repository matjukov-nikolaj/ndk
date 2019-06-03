using System;
using System.Collections.Generic;
using Core;
using Core.model;
using Newtonsoft.Json;
using StackExchange.Redis;

namespace SequenceHandler
{
    class Program
    {
        private static Dictionary<string, string> properties = Configuration.GetParameters();
        static void Main(string[] args)
        {
            Console.WriteLine("Sequence handler is running.");
            try
            {
                ConnectionMultiplexer redisConnection = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"]);
                ISubscriber sub = redisConnection.GetSubscriber();
                sub.Subscribe("events", (channel, message) =>
                {
                    string id = message.ToString();
                    if (id.Contains("SEQUENCE_"))
                    {
                        int dbNumber = Convert.ToInt32(properties["SEQUENCE_DB"]);
                        IDatabase redisDb = redisConnection.GetDatabase(dbNumber);
                        string value = redisDb.StringGet(id);
                        Console.WriteLine("Event: " + id + "-" + value);
                        HandleSequence(value, redisConnection);
                    }
                });
                Console.ReadKey();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }

        private static void HandleSequence(string value, ConnectionMultiplexer redisConnection)
        {
            try
            {
                SequenceId sId = JsonConvert.DeserializeObject<SequenceId>(value);
                
                int grammarDbNumber = Convert.ToInt32(properties["GRAMMAR_DB"]);
                IDatabase grammarDb = redisConnection.GetDatabase(grammarDbNumber);

                string grammarStr = grammarDb.StringGet("GRAMMAR_" + sId.id);
                Grammar grammar = JsonConvert.DeserializeObject<Grammar>(grammarStr);
                
                int newGrammarDbNumber = Convert.ToInt32(properties["NEW_GRAMMAR_DB"]);
                IDatabase newGrammarDb = redisConnection.GetDatabase(newGrammarDbNumber);

                string newGrammarStr = newGrammarDb.StringGet("NEW_GRAMMAR_" + sId.id);
                NewGrammar newGrammar = JsonConvert.DeserializeObject<NewGrammar>(newGrammarStr);
                
                int mTableDbNumber = Convert.ToInt32(properties["TABLE_M_DB"]);
                IDatabase mTableDb = redisConnection.GetDatabase(mTableDbNumber);

                string mTableStr = mTableDb.StringGet("TABLE_M_" + sId.id);
                MTable mTable = JsonConvert.DeserializeObject<MTable>(mTableStr);
                
                SequenceHandler sequenceHandler = new SequenceHandler(grammar.startSymbol, newGrammar.terminals, newGrammar.noTerminals, mTable.mTable);
                sequenceHandler.Process(sId.sequence);
                
                IDatabase redisDb = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"])
                    .GetDatabase(Convert.ToInt32(properties["SEQUENCE_DB"]));

                Sequence sequence = sequenceHandler.GetSequence();
                sequence.grammarId = "NEW_GRAMMAR_" + sId.id;
                string json = JsonConvert.SerializeObject(sequence);
                String newId = "SEQUENCE_RESULT_" + sId.id;
                redisDb.StringSet(newId, json);
                Console.WriteLine(newId + ": " + json + " - saved to redis SEQUENCE_DB");

                MakeStatisticEvent(newId);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }

        private static void MakeStatisticEvent(String newId)
        {
            ConnectionMultiplexer redisConnection = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"]);
            ISubscriber sub = redisConnection.GetSubscriber();
            sub.Publish("events", $"{newId}");
        }
    }
}