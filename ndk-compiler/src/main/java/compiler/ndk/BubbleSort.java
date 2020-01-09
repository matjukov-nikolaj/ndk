package compiler.ndk;

import java.util.ArrayList;

public class BubbleSort {

    ArrayList<String> first;
    ArrayList<String> second;

    public BubbleSort() {
    }

    public void main() {
        this.first = new ArrayList();
        this.first.add("о");
        this.first.add("н");
        this.first.add("е");
        this.first.add("с");
        this.first.add("т");
        this.first.add("с");
        this.first.add("е");
        this.first.add("н");
        this.first.add("1");
        this.second = new ArrayList();
        this.second.add("");
        this.second.add("");
        this.second.add("");
        this.second.add("");
        this.second.add("");
        this.second.add("");
        this.second.add("");
        this.second.add("");
        this.second.add("");
        byte size = 9;
        int i = size;

        for(int j = 0; i != 0; --i) {
            this.second.set(j, ((String)this.first.get(i - 1)).toString());
            ++j;
        }

        boolean isEquals = true;

        for(i = 0; i < size; ++i) {
            if (((String)this.second.get(i)).toString() != ((String)this.first.get(i)).toString()) {
                isEquals = false;
            }
        }

        if (isEquals) {
            System.out.println("Одинаковые!");
        } else {
            System.out.println("Нет");
        }
    }

}
