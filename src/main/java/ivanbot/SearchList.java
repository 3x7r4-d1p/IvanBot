package ivanbot;

import java.util.ArrayList;
import java.util.List;

public class SearchList {

    private List<String> list = new ArrayList<>();

    public void searchListAdd(List<String> l){
        list.addAll(l);
    }

    public void clearList(){
        list.clear();
    }

    public String getInfo(int i){
        return list.get(i);
    }
}
