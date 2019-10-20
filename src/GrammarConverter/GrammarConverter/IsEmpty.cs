namespace GrammarConverter
{
    public class IsEmpty
    {
        private bool isEmpty = true;
        private bool lol = true;

        public void setL(bool L)
        {
            this.lol = L;
        }

        public bool getL()
        {
            return lol;
        }
        
        public void setIs(bool isEmpty)
        {
            this.isEmpty = isEmpty;
        }

        public bool getIs()
        {
            return isEmpty;
        }

    }
}