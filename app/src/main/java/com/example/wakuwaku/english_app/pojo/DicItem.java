package com.example.wakuwaku.english_app.pojo;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by wakuwaku on 2015/08/20.
 */
@Root(name = "DicItemTitle", strict = false)
public class DicItem {

    @Element(name = "ItemID")
    private String itemId;
    @Element(name = "Title")
    private String title;

    public void setTitle(String  title) {
        this.title = title;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public String getItemId() {
        return itemId;
    }

    @Override
    public String toString() {
        if (title == null) {
            return itemId;
        }
        return title;
    }



}
