package com.force.formula.commands;

import com.force.formula.FormulaContext;
import com.force.formula.impl.FormulaAST;
import com.force.formula.impl.JsValue;
import com.force.formula.sql.SQLPair;

import java.io.Serializable;

/**
 * Describe your class here.
 *
 * @author dchasman
 * @since 140
 */
public abstract class UnaryMathCommandBehavior implements Serializable
{

    private static final long serialVersionUID = 1L;

    UnaryMathCommandBehavior()
    {
    }

    public abstract UnaryMathCommand getCommand(FormulaCommandInfo info);

    public abstract SQLPair getSQL(FormulaAST node, FormulaContext context, String[] args, String[] guards);

    public abstract JsValue getJavascript(FormulaAST node, FormulaContext context, JsValue[] args);
}
