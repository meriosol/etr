package com.meriosol.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author meriosol
 * @version 0.1
 * @since 15/01/14
 */
public class UtilTest {
    @Test
    public void testJoinCollection() {
        List<String> list = new ArrayList<String>();
        list.add("a");
        list.add("b");
        list.add("c");
        String delimiter = ";";
        String joined = Util.join(list, delimiter);
        String expected = "a;b;c";
        assertEquals(joined, expected);
    }

    @Test
    public void testJoinArray() {
        String[] array = new String[]{"a", "b", "c"};
        String delimiter = ";";
        String joined = Util.join(array, delimiter);
        String expected = "a;b;c";
        assertEquals(joined, expected);
    }

    @Test
    public void testJoinOneElementArray() {
        String element1 = "a";
        String[] array = new String[]{element1};
        String delimiter = ";";
        String joined = Util.join(array, delimiter);
        String expected = element1;
        assertEquals(joined, expected);
    }

    @Test
    public void testJoinEmptyArray() {
        String[] array = new String[]{};
        String delimiter = ";";
        String joined = Util.join(array, delimiter);
        String expected = "";
        assertEquals(joined, expected);
    }

    @Test
    public void testJoinNullArray() {
        String[] array = null;
        String delimiter = ";";
        String joined = Util.join(array, delimiter);
        String expected = "";
        assertEquals(joined, expected);
    }

    @Test
    public void testJoinOneElementCollection() {
        List<Integer> list = new ArrayList<Integer>();
        String element1 = "1234";
        list.add(Integer.valueOf(element1));
        String delimiter = ",";
        String joined = Util.join(list, delimiter);
        String expected = element1;
        assertEquals(joined, expected);
    }

    @Test
    public void testJoinEmptyCollection() {
        Set<Integer> set = new HashSet<Integer>();
        String delimiter = " ";
        String joined = Util.join(set, delimiter);
        String expected = "";
        assertEquals(joined, expected);
    }

    @Test
    public void testJoinNullCollection() {
        Set<Integer> set = null;
        String delimiter = "\t\n";
        String joined = Util.join(set, delimiter);
        String expected = "";
        assertEquals(joined, expected);
    }
}
