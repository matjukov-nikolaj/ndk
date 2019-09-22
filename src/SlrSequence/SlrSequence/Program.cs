using System;
using System.Collections.Generic;
using Core;
using Core.model;
using Newtonsoft.Json;
using StackExchange.Redis;

namespace SlrSequence
{
    class Program
    {
        private static Dictionary<string, string> properties = Configuration.GetParameters();

        static void Main(string[] args)
        {
            Console.WriteLine("Lexer is running.");
            try
            {
                ConnectionMultiplexer redisConnection = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"]);
                ISubscriber sub = redisConnection.GetSubscriber();
                sub.Subscribe("events", (channel, message) =>
                {
                    string id = message.ToString();
                    if (id.Contains("SLR_SEQUENCE_"))
                    {
                        int dbNumber = Convert.ToInt32(properties["SLR_SEQUENCE_DB"]);
                        IDatabase redisDb = redisConnection.GetDatabase(dbNumber);
                        string value = redisDb.StringGet(id);
                        Console.WriteLine("Event: " + id + "-" + value);
                        HandleSequence(value, redisConnection, id.Replace("SLR_SEQUENCE_", "SLR_SEQUENCE_RESULT_"));
                    }
                });
                Console.ReadKey();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
        
        private static void HandleSequence(string value, ConnectionMultiplexer redisConnection, String idToSave)
        {
            try
            {
                SequenceId sId = JsonConvert.DeserializeObject<SequenceId>(value);
                String strToHandle = sId.sequence;
                strToHandle = strToHandle.Replace("\n", " ");
                strToHandle = strToHandle.Replace("\t", " ");
                Lexer lexer = new Lexer();
                foreach (var ch in strToHandle.ToLower().ToCharArray())
                {
                    lexer.goToState(ch);
                }

                String lexerResult = lexer.getResult();
                strToHandle = strToHandle.ToLower();

                List<String> arr = new List<string>(strToHandle.Split(" "));
                
                foreach (var element in lexer.getIdentifiers())
                {
                    arr[arr.FindIndex(ind=>ind.Equals(element))] =  Lexer.IDENTIFIER;
                }
                
                foreach (var element in lexer.getNumbers())
                {
                    arr[arr.FindIndex(ind=>ind.Equals(element))] =  Lexer.NUMBER;
                }
                String resultStr = String.Join(" ", arr.ToArray());
                lexer.clean();
                
                LexerResult res = new LexerResult();
                
                res.input = sId.sequence;
                res.convertedInput = resultStr;
                res.lexerResults = lexerResult;
                
                int dbNumber = Convert.ToInt32(properties["SLR_SEQUENCE_DB"]);
                IDatabase redisDb = redisConnection.GetDatabase(dbNumber);
                
                String saveToRedisStr = JsonConvert.SerializeObject(res);
                redisDb.StringSet(idToSave, saveToRedisStr);


            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }

    }
}