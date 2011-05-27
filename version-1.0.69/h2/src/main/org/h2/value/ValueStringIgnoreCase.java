/*
 * Copyright 2004-2008 H2 Group. Licensed under the H2 License, Version 1.0
 * (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.value;

import org.h2.constant.SysProperties;
import org.h2.util.MathUtils;
import org.h2.util.StringCache;
import org.h2.util.StringUtils;

/**
 * Implementation of the VARCHAR_IGNORECASE data type.
 */
public class ValueStringIgnoreCase extends ValueStringBase {

    private static final ValueStringIgnoreCase EMPTY = new ValueStringIgnoreCase("");
    private int hash;

    protected ValueStringIgnoreCase(String value) {
        super(value);
    }

    public int getType() {
        return Value.STRING_IGNORECASE;
    }

    protected int compareSecure(Value o, CompareMode mode) {
        ValueStringIgnoreCase v = (ValueStringIgnoreCase) o;
        return mode.compareString(value, v.value, true);
    }

    public boolean equals(Object other) {
        return other instanceof ValueStringBase && value.equalsIgnoreCase(((ValueStringBase) other).value);
    }

    public int hashCode() {
        if (hash == 0) {
            // this is locale sensitive
            hash = value.toUpperCase().hashCode();
        }
        return hash;
    }

    public String getSQL() {
        return "CAST(" + StringUtils.quoteStringSQL(value) + " AS VARCHAR_IGNORECASE)";
    }

    public static ValueStringIgnoreCase get(String s) {
        if (s.length() == 0) {
            return EMPTY;
        }
        ValueStringIgnoreCase obj = new ValueStringIgnoreCase(StringCache.get(s));
        if (s.length() > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return obj;
        }
        ValueStringIgnoreCase cache = (ValueStringIgnoreCase) Value.cache(obj);
        // the cached object could have the wrong case 
        // (it would still be 'equal', but we don't like to store it)
        if (cache.value.equals(s)) {
            return cache;
        } else {
            return obj;
        }
    }

    public Value convertPrecision(long precision) {
        if (precision == 0 || value.length() <= precision) {
            return this;
        }
        int p = MathUtils.convertLongToInt(precision);
        return ValueStringIgnoreCase.get(value.substring(0, p));
    }

}
