package com.meriosol.etr.dao.impl;

import java.util.Random;

/**
 * @author meriosol
 * @version 0.1
 * @since 09/02/14
 */
class IdGenerator {
    private static final Random random = new Random();

    static Long generateNewId() {
        return Math.abs(random.nextLong());
    }
}
