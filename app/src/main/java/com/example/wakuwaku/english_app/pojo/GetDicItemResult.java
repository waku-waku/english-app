package com.example.wakuwaku.english_app.pojo;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by shunhosaka on 15/08/21.
 * Whats:
 * When :
 */
@Root(name = "GetDicItemResult", strict = false)
public class GetDicItemResult {
    public static final String TAG = GetDicItemResult.class.getSimpleName();


    //xml内の検索したいtag
    @Element(name = "Head")
    private String head;
    @Element(name = "Body")
    private String body;

    public String getHead() {
        return head;
    }

    public String getBody() {
        return body;
    }
}
