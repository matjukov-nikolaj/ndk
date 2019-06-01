using System;
using System.Collections.Generic;
using Core;
using Core.model;
using Microsoft.Data.OData.Query.SemanticAst;
using Newtonsoft.Json;
using StackExchange.Redis;

namespace SequenceStatistic
{
    class Program
    {
        private static Dictionary<string, string> properties = Configuration.GetParameters();

        private const String ACCEPTED = "accepted";

        private const String DECLINE = "declined";

        static void Main(string[] args)
        {
            Console.WriteLine("Sequence statistic handler is running.");
            try
            {
                ConnectionMultiplexer redisConnection = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"]);
                ISubscriber sub = redisConnection.GetSubscriber();
                sub.Subscribe("events", (channel, message) =>
                {
                    string id = message.ToString();
                    if (id.Contains("SEQUENCE_RESULT_"))
                    {
                        int dbNumber = Convert.ToInt32(properties["SEQUENCE_DB"]);
                        IDatabase redisDb = redisConnection.GetDatabase(dbNumber);
                        string value = redisDb.StringGet(id);
                        Console.WriteLine("Event: " + id + "-" + value);
                        Sequence sequence = JsonConvert.DeserializeObject<Sequence>(value);
                        IDatabase newGrammarDb =
                            redisConnection.GetDatabase(Convert.ToInt32(properties["NEW_GRAMMAR_DB"]));

                        int statisticDbNumber = Convert.ToInt32(properties["STATISTIC_DB"]);
                        IDatabase statisticDb = redisConnection.GetDatabase(statisticDbNumber);
                        if (sequence.result)
                        {
                            SetStatistic(statisticDb, sequence, sequence.grammarId, ACCEPTED);
                        }
                        else
                        {
                            SetStatistic(statisticDb, sequence, sequence.grammarId, DECLINE);
                        }
                    }
                });
                Console.ReadKey();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }

        private static void SetStatistic(IDatabase statisticDb, Sequence sequence, String grammar, String key)
        {
            string statElement = key == ACCEPTED ? statisticDb.StringGet(ACCEPTED) : statisticDb.StringGet(DECLINE);
            if (statElement == null)
            {
                Statistic statistic = new Statistic();
                List<String> sequences = new List<string>();
                sequences.Add(sequence.sequence);
                statistic.statistic.Add(grammar, sequences);
                SetInDatabase(key, statistic, statisticDb);
            }
            else
            {
                JsonSerializerSettings settings = new JsonSerializerSettings();
                settings.ContractResolver = new DictionaryAsArrayResolver();
                Statistic statistic = JsonConvert.DeserializeObject<Statistic>(statElement, settings);
                if (statistic.statistic.ContainsKey(grammar))
                {
                    statistic.statistic[grammar].Add(sequence.sequence);
                }
                else
                {
                    List<String> sequences = new List<string>();
                    sequences.Add(sequence.sequence);
                    statistic.statistic.Add(grammar, sequences);
                }

                SetInDatabase(key, statistic, statisticDb);
            }
        }

        private static void SetInDatabase(string key, Statistic statistic, IDatabase statisticDb)
        {
            JsonSerializerSettings settings = new JsonSerializerSettings();
            settings.ContractResolver = new DictionaryAsArrayResolver();
            string json = JsonConvert.SerializeObject(statistic, settings);
            if (key == ACCEPTED)
            {
                statisticDb.StringSet(ACCEPTED, json);
                Console.WriteLine("SAVE: ");
                Console.WriteLine(ACCEPTED + " " + json);
                Console.WriteLine("");
            }
            else
            {
                statisticDb.StringSet(DECLINE, json);
                Console.WriteLine("SAVE: ");
                Console.WriteLine(DECLINE + " " + json);
                Console.WriteLine("");
            }
        }
    }
}