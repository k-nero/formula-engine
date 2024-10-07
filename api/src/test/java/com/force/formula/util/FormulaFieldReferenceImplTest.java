/**
 *
 */
package com.force.formula.util;

import com.force.formula.FormulaApiMocks;
import com.force.formula.FormulaFieldReferenceInfo;
import com.force.formula.FormulaSchema;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author stamm
 */
public class FormulaFieldReferenceImplTest
{


    @Test
    public void testFieldReferenceImpl()
    {
        FormulaFieldReferenceImpl ffr = new FormulaFieldReferenceImpl(null, "name");
        assertNull(ffr.getBase());
        assertEquals("name", ffr.getElement());
        assertFalse(ffr.isDynamic());
        assertFalse(ffr.isDynamicBase());
        assertEquals("name", ffr.toString());
    }

    @Test
    public void testFieldReferenceInfoImpl()
    {
        FormulaSchema.Field field = new FormulaApiMocks.MockField("foo", FormulaApiMocks.MockType.TEXT);
        FormulaFieldReferenceInfo ffri = new FormulaFieldReferenceInfoImpl(field);
        assertEquals(field, ffri.getFieldOrColumn());
        assertEquals("fid:foo domain: ", ffri.toString());
        assertEquals(ffri, new FormulaFieldReferenceInfoImpl(field));
    }

}
