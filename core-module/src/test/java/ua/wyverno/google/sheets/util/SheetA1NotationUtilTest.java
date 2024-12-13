package ua.wyverno.google.sheets.util;

import junit.framework.TestCase;

public class SheetA1NotationUtilTest extends TestCase {

    public void testLetterToColumnIndex() {
        assertEquals(0, SheetA1NotationUtil.letterToColumnIndex("A"));
        assertEquals(1, SheetA1NotationUtil.letterToColumnIndex("B"));
        assertEquals(2, SheetA1NotationUtil.letterToColumnIndex("C"));
        assertEquals(3,SheetA1NotationUtil.letterToColumnIndex("D"));
        assertEquals(26, SheetA1NotationUtil.letterToColumnIndex("AA"));
    }
}