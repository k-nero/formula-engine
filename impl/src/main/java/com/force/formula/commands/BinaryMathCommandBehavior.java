/*
 * Created on Jan 26, 2005
 *
 */
package com.force.formula.commands;

import com.force.formula.FormulaCommand;
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
public abstract class BinaryMathCommandBehavior implements Serializable
{
    private static final long serialVersionUID = 1L;

    public abstract FormulaCommand getCommand(FormulaCommandInfo info);

    public abstract SQLPair getSQL(FormulaAST node, FormulaContext context, String[] args, String[] guards);

    public abstract JsValue getJavascript(FormulaAST node, FormulaContext context, JsValue[] args);
}
