/*
 * Created on Dec 10, 2004
 *
 */
package com.force.formula.commands;

import com.force.formula.FormulaCommand;
import com.force.formula.FormulaCommandType;
import com.force.formula.FormulaCommandType.AllowedContext;
import com.force.formula.FormulaCommandType.SelectorSection;
import com.force.formula.FormulaContext;
import com.force.formula.FormulaException;
import com.force.formula.impl.FormulaAST;
import com.force.formula.impl.JsValue;
import com.force.formula.sql.ITableAliasRegistry;
import com.force.formula.sql.SQLPair;

import java.math.BigDecimal;

/**
 * Base class for binary math commands
 *
 * @author dchasman
 * @since 140
 */

@AllowedContext(section = SelectorSection.MATH)
public class BinaryMathCommandInfo implements FormulaCommandInfo
{
    private static final Class<?>[] argumentType = new Class<?>[]{BigDecimal.class, BigDecimal.class};
    private final String symbol;
    private final BinaryMathCommandBehavior behavior;

    public BinaryMathCommandInfo(String symbol, BinaryMathCommandBehavior behavior)
    {
        this.symbol = symbol;
        this.behavior = behavior;
    }

    @Override
    public String getName()
    {
        return symbol;
    }

    @Override
    public Class<?> getReturnType(FormulaAST node, FormulaContext context)
    {
        return BigDecimal.class;
    }

    @Override
    public Class<?>[] getArgumentTypes(FormulaAST node, FormulaContext context)
    {
        return BinaryMathCommandInfo.argumentType;
    }

    @Override
    public FormulaCommand getCommand(FormulaAST node, FormulaContext context)
    {
        return behavior.getCommand(this);
    }

    @Override
    public SQLPair getSQL(FormulaAST node, FormulaContext context, String[] args, String[] guards, ITableAliasRegistry registry)
    {
        return behavior.getSQL(node, context, args, guards);
    }

    @Override
    public JsValue getJavascript(FormulaAST node, FormulaContext context, JsValue[] args) throws FormulaException
    {
        return behavior.getJavascript(node, context, args);
    }

    @Override
    public FormulaCommandType.AllowedContext getAllowedContext()
    {
        FormulaCommandType.AllowedContext context = behavior.getClass().getAnnotation(FormulaCommandType.AllowedContext.class);
        return context == null ? BinaryMathCommandInfo.class.getAnnotation(FormulaCommandType.AllowedContext.class) : context;
    }
}
