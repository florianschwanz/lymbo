package de.interoberlin.lymbo.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.core.model.v1.objects.TagObject;

public class TagUtil {
    // --------------------
    // Methods
    // --------------------

    static public ArrayList<String> getDistinctValues(List<Tag> tags) {
        ArrayList<String> names = new ArrayList<>();

        for (Tag t : tags) {
            if (t != null)
                names.add(t.getValue());
        }

        Collections.sort(names);
        return names;
    }

    static public List<Tag> getTagList(List<TagObject> tags) {
        List<Tag> tagList = new ArrayList<>();

        for (TagObject t : tags) {
            if (t != null)
                tagList.add((Tag) t);
        }

        return tagList;
    }
}
