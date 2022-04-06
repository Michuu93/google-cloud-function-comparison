using Google.Cloud.Functions.Framework;
using Microsoft.AspNetCore.Http;
using System.Threading.Tasks;
using System;
using System.IO;

namespace dotnet3_heavy
{
    public class Function : IHttpFunction
    {
        public async Task HandleAsync(HttpContext context)
        {
            using TextReader reader = new StreamReader(context.Request.Body);
            string str = await reader.ReadLineAsync();
            string result = SortString(str);
            await context.Response.WriteAsync(result);
        }

        static string SortString(string input)
        {
            char[] characters = input.ToCharArray();
            Array.Sort(characters);
            return new string(characters);
        }
    }
}
