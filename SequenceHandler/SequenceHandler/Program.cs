using System;
using System.Collections.Generic;
using Core;
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
                        HandleSequence(id, value, sub);
                    }
                });
                Console.ReadKey();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }

        private static void HandleSequence(String id, string value, ISubscriber sub)
        {
            try
            {
                
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}