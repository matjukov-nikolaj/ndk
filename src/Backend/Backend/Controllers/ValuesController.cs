using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Concurrent;
using StackExchange.Redis;
using System.Threading;
using Core;
using Core.model;
using Newtonsoft.Json;

namespace Backend.Controllers
{
    [Route("api/[controller]")]
    public class ValuesController : Controller
    {
        private static Dictionary<string, string> properties = Configuration.GetParameters();
        static readonly ConcurrentDictionary<string, string> _data = new ConcurrentDictionary<string, string>();
        // POST api/values
        [HttpPost]
        public IActionResult Post([FromBody] string value)
        {
            var id = Guid.NewGuid().ToString();
            Console.WriteLine(id);
            try
            {
                string textKey = "INPUT_GRAMMAR_" + id;
                this.SaveDataToRedis(value, textKey);
                this.makeEvent(ConnectionMultiplexer.Connect(properties["REDIS_SERVER"]), textKey);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
            ConnectionMultiplexer redis = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"]);
            IDatabase grammarDb = redis.GetDatabase(Convert.ToInt32(properties["GRAMMAR_DB"]));
            IDatabase newGrammarDb = redis.GetDatabase(Convert.ToInt32(properties["NEW_GRAMMAR_DB"]));
            IDatabase tableMDb = redis.GetDatabase(Convert.ToInt32(properties["TABLE_M_DB"]));

            Result result = new Result();
            result.id = id;

            string grammar = null;
            string newGrammar = null;
            string table = null;

            for (short i = 0; i < 5; ++i)
            {
                grammar = grammarDb.StringGet("GRAMMAR_" + id);
                if (grammar == null)
                {
                    Thread.Sleep(200);
                }
                else
                {
                    result.grammar = grammar;
                    break;
                }
            }

            for (short i = 0; i < 5; ++i)
            {
                newGrammar = newGrammarDb.StringGet("NEW_GRAMMAR_" + id);
                if (newGrammar == null)
                {
                    Thread.Sleep(200);
                }
                else
                {
                    result.newGrammar = newGrammar;
                    break;
                }
            }

            for (short i = 0; i < 5; ++i)
            {
                table = tableMDb.StringGet("TABLE_M_" + id);
                if (table == null)
                {
                    Thread.Sleep(200);
                }
                else
                {
                    result.table = table;
                    break;
                }
            }

            if (!String.IsNullOrEmpty(result.grammar) && !String.IsNullOrEmpty(result.newGrammar) &&
                !String.IsNullOrEmpty(result.table))
            {
                String json = JsonConvert.SerializeObject(result);
                return Ok(json);
            }

            return new StatusCodeResult(402);
        }

        private void SaveDataToRedis(String value, String id)
        {
            var redisDb = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"])
                .GetDatabase(Convert.ToInt32(properties["INPUT_GRAMMAR_DB"]));
            redisDb.StringSet(id, value);
            Console.WriteLine(id + ": " + value + " - saved to redis INPUT_GRAMMAR_DB");
        }

        private void makeEvent(ConnectionMultiplexer redis, String id)
        {
            ISubscriber sub = redis.GetSubscriber();
            sub.Publish("events", $"{id}");
        }
    }
}