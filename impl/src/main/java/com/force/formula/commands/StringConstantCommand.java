/*
 * Created on Dec 10, 2004
 *
 */
package com.force.formula.commands;


import com.force.formula.FormulaRuntimeContext;

import java.util.Deque;

/**
 * @author dchasman
 * @since 140
 */
public class StringConstantCommand extends AbstractFormulaCommand
{
    private static final long serialVersionUID = 1L;
    private final Object value;

    public StringConstantCommand(FormulaCommandInfo info, Object value)
    {
        super(info);
        if ((value != null) && (value.equals("")))
        {
            this.value = null;
        }
        else
        {
            this.value = value;
        }
    }

    @Override
    public void execute(FormulaRuntimeContext context, Deque<Object> stack)
    {
        stack.push(value);
    }
}
