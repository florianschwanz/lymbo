package de.interoberlin.lymbo.model.translate;

import java.util.ArrayList;
import java.util.List;

public class ParamHolder {
    private List<Param> params = new ArrayList<>();

    // --------------------
    // Methods
    // --------------------

    /**
     * Adds a parameter to the parameter holder
     *
     * @param param parameter to be added
     */
    public void add(Param param) {
        params.add(param);
    }

    /**
     * Returns complete parameter string
     *
     * @return parameter string
     */
    public String getParamString() {
        String paramString = "";

        // Append params
        for (Param p : params) {
            paramString += p.getKey() + "=" + p.getValue() + "&";
        }

        // Remove trailing ampersand
        if (paramString.length() > 0 && paramString.charAt(paramString.length() - 1) == '&') {
            paramString = paramString.substring(0, paramString.length() - 1);
        }

        return paramString;
    }
}
