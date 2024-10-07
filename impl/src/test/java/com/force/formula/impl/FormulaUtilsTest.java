package com.force.formula.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FormulaUtilsTest
{

    private static String generateStringWithBackslashes(int count)
    {
        return "\\".repeat(Math.max(0, count)) +
                "'";
    }

    @Test
    public void testNumberOfPrecedingBackslashesZero()
    {
        String input = "abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < input.length(); i++)
        {
            assertEquals(0, FormulaUtils.numberOfPrecedingBackslashes(input, i));
        }
    }

    @Test
    public void testNumberOfPrecedingBackslashesNonZero()
    {
        for (int i = 1; i < 10; i++)
        {
            String input = generateStringWithBackslashes(i);
            assertEquals(FormulaUtils.numberOfPrecedingBackslashes(input, input.length() - 1), i);
        }
    }

    @Test
    public void testNumberOfPrecedingBackslashesCornerCases()
    {
        String input = "abcdefghijklmnopqrstuvwxyz";
        assertEquals(0, FormulaUtils.numberOfPrecedingBackslashes(input, -1));
        assertEquals(0, FormulaUtils.numberOfPrecedingBackslashes(input, input.length() + 1));


        input = "abc\\";
        assertEquals(1, FormulaUtils.numberOfPrecedingBackslashes(input, input.length()));
    }

    @Test
    public void testNumberOfPrecedingBackslashesNotIncludingIndex()
    {
        String input = generateStringWithBackslashes(4); // "\\\\'"

        for (int i = 0; i < input.length() - 1; i++)
        {
            assertEquals(FormulaUtils.numberOfPrecedingBackslashes(input, i), i);
        }
    }

}
