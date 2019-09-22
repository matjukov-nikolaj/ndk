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
    public class SLRSequenceController : Controller
    {
        private static Dictionary<string, string> properties = Configuration.GetParameters();
        
        // POST api/slrsequence
        [HttpPost]
        public IActionResult Post([FromBody] SequenceId value)
        {
            var sequenceId = Guid.NewGuid().ToString();
            try
            {
                string textKey = "SLR_SEQUENCE_" + sequenceId;
                this.SaveDataToRedis(value, textKey);
                this.makeEvent(ConnectionMultiplexer.Connect(properties["REDIS_SERVER"]), textKey);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
            ConnectionMultiplexer redis = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"]);
            IDatabase sequenceDb = redis.GetDatabase(Convert.ToInt32(properties["SLR_SEQUENCE_DB"]));

            string sequenceResult = null;

            for (short i = 0; i < 5; ++i)
            {
                sequenceResult = sequenceDb.StringGet("SLR_SEQUENCE_RESULT_" + sequenceId);
                if (sequenceResult == null)
                {
                    Thread.Sleep(200);
                }
                else
                {
                    return Ok(sequenceResult);
                }
            }

            return new StatusCodeResult(402);
        }

        private void SaveDataToRedis(SequenceId value, String id)
        {
            var redisDb = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"])
                .GetDatabase(Convert.ToInt32(properties["SLR_SEQUENCE_DB"]));
            string json = JsonConvert.SerializeObject(value);
            redisDb.StringSet(id, json);
            Console.WriteLine(id + ": " + value + " - saved to redis SLR_SEQUENCE_DB");
        }

        private void makeEvent(ConnectionMultiplexer redis, String id)
        {
            ISubscriber sub = redis.GetSubscriber();
            sub.Publish("events", $"{id}");
        }
    }
}