/*
 * Created on Dec 8, 2004
 */
package com.force.formula.impl;

import com.force.formula.FormulaException;
import com.force.formula.util.FormulaI18nUtils;

/**
 * Thrown when a reference is encountered to an unknown  function
 *
 * @author dchasman
 * @since 144
 */
public class InvalidFunctionReferenceException extends FormulaException
{

    private static final long serialVersionUID = 1L;
    private final String function;
    private final int location;

    public InvalidFunctionReferenceException(String function, int location)
    {
        super(FormulaI18nUtils.getLocalizer().getLabel("FormulaFieldExceptionMessages", "InvalidFunctionReferenceException", function));

        this.function = function;
        this.location = location;
    }

    public String getFunction()
    {
        return function;
    }

    public int getlocation()
    {
        return location;
    }
}
