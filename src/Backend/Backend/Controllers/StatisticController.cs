using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Threading;
using Core;
using Core.model;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using StackExchange.Redis;

namespace Backend.Controllers
{
    [Route("api/[controller]")]
    public class StatisticController : Controller
    {
        private static Dictionary<string, string> properties = Configuration.GetParameters();

        [HttpGet("{statistic}")]
        public IActionResult Get()
        {
            try
            {
                ConnectionMultiplexer redis = ConnectionMultiplexer.Connect(properties["REDIS_SERVER"]);
                IDatabase statDb = redis.GetDatabase(Convert.ToInt32(properties["STATISTIC_DB"]));
                String accepted = "";
                String declined = "";
                accepted = statDb.StringGet("accepted");
                declined = statDb.StringGet("declined");
                StatisticResult statRes = new StatisticResult();
                
                JsonSerializerSettings settings = new JsonSerializerSettings();
                settings.ContractResolver = new DictionaryAsArrayResolver();
                
                if (accepted == null)
                {
                    Dictionary<List<String>, List<String>> empty = new Dictionary<List<string>, List<string>>();
                    statRes.result.Add("accepted", empty);
                }

                if (declined == null)
                {
                    Dictionary<List<String>, List<String>> empty = new Dictionary<List<string>, List<string>>();
                    statRes.result.Add("declined", empty);
                }

                if (!String.IsNullOrEmpty(accepted))
                {
                    Statistic stat = JsonConvert.DeserializeObject<Statistic>(accepted, settings);
                    List<string> keyList = new List<string>(stat.statistic.Keys);
                    IDatabase newGrammarDb = redis.GetDatabase(Convert.ToInt32(properties["NEW_GRAMMAR_DB"]));
                    Dictionary<List<String>, List<String>> result = new Dictionary<List<string>, List<string>>();
                    foreach (var grammarId in keyList)
                    {
                        string value = newGrammarDb.StringGet(grammarId);
                        NewGrammar newGrammar = JsonConvert.DeserializeObject<NewGrammar>(value);
                        List<String> production = newGrammar.productions;
                        result.Add(production, stat.statistic[grammarId]);
                    }
                    statRes.result.Add("accepted", result);
                }

                if (!String.IsNullOrEmpty(accepted))
                {
                    Statistic stat = JsonConvert.DeserializeObject<Statistic>(declined, settings);
                    List<string> keyList = new List<string>(stat.statistic.Keys);
                    IDatabase newGrammarDb = redis.GetDatabase(Convert.ToInt32(properties["NEW_GRAMMAR_DB"]));
                    Dictionary<List<String>, List<String>> result = new Dictionary<List<string>, List<string>>();
                    foreach (var grammarId in keyList)
                    {
                        string value = newGrammarDb.StringGet(grammarId);
                        NewGrammar newGrammar = JsonConvert.DeserializeObject<NewGrammar>(value);
                        List<String> production = newGrammar.productions;
                        result.Add(production, stat.statistic[grammarId]);
                    }
                    statRes.result.Add("declined", result);
                }

                string json = JsonConvert.SerializeObject(statRes, settings);
                
                return Ok(json);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                return new NotFoundResult();
            }
        }

        // POST api/statistic
        [HttpPost]
        public string Post([FromBody] string value)
        {
            return null;
        }

    }
}