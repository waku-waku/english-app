package com.example.wakuwaku.english_app.pojo;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wakuwaku on 2015/08/20.
 */
@Root(strict = false)
public class SearchDicItemResult {

    @ElementList(name="TitleList", required = false)
    private List<DicItem> dicItemList = new ArrayList<>();

    public List<DicItem> getDicItemList() {
        return dicItemList;
    }
}
