package com.schaex.days;

import java.io.File;

final class DaysUtil {
    private DaysUtil() {}

    public static File resource(String name) {
        return new File(DaysUtil.class.getResource(name).getFile());
    }
}
