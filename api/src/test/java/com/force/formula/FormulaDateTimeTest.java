/**
 *
 */
package com.force.formula;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author stamm
 *
 */

public class FormulaDateTimeTest
{

    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void testWrapper()
    {
        Date d = Calendar.getInstance().getTime();
        FormulaDateTime fdt = new FormulaDateTime(d);
        assertEquals(d, fdt.getDate());
        assertEquals(d.getTime(), fdt.getDate().getTime());
        assertNotEquals(d, fdt);
        assertNotEquals(fdt, d);
        assertEquals(0, new FormulaDateTime(d).compareTo(fdt));
        assertEquals(d.toString(), fdt.toString());

        assertEquals(d, FormulaDateTime.unwrap(fdt));
        assertEquals(d, FormulaDateTime.unwrap(d));

        assertEquals("null", new FormulaDateTime(null).toString());

    }


}
