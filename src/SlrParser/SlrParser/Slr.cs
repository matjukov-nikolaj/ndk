using System;

namespace SlrParser
{
    public class Slr
    {
        public int x;
        public int y;
        public string name;
        public bool isNoTerminal;

        public Slr(String name, int y, int x, bool isNoTerminal)
        {
            this.name = name;
            this.x = x;
            this.y = y;
            this.isNoTerminal = isNoTerminal;
        }
    }
}