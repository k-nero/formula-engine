/*
 * Created on Dec 10, 2004
 */
package com.force.formula.commands;

import com.force.formula.FormulaCommand;
import com.force.formula.FormulaContext;
import com.force.formula.FormulaException;
import com.force.formula.FormulaRuntimeContext;
import com.force.formula.impl.FormulaAST;
import com.force.formula.impl.JsValue;
import com.force.formula.impl.TableAliasRegistry;
import com.force.formula.sql.SQLPair;
import com.force.formula.util.BigDecimalHelper;

import java.math.BigDecimal;
import java.util.Deque;

/**
 * Push the command's associated numeric value onto the stack
 *
 * @author dchasman
 * @since 140
 */
public class ConstantNumber extends ConstantBase
{
    public ConstantNumber()
    {
        super("NUMBER", BigDecimal.class, new Class[0]);
    }

    @Override
    public FormulaCommand getCommand(final FormulaAST node, FormulaContext context)
    {
        return new NumberConstantCommand(this, new BigDecimal(node.getText(), BigDecimalHelper.MC_PRECISION_INTERNAL));
    }

    @Override
    public SQLPair getSQL(FormulaAST node, FormulaContext context, String[] args, String[] guards, TableAliasRegistry registry)
    {
        return new SQLPair(node.getText(), null);
    }

    @Override
    public JsValue getJavascript(FormulaAST node, FormulaContext context, JsValue[] args) throws FormulaException
    {
        if (context.useHighPrecisionJs())
        {
            return new JsValue("new " + context.getJsEngMod() + ".Decimal('" + node.getText() + "')", null, false);
        }
        return new JsValue(node.getText(), null, false);
    }


    public static class NumberConstantCommand extends AbstractFormulaCommand
    {
        private static final long serialVersionUID = 1L;
        private final BigDecimal value;

        public NumberConstantCommand(FormulaCommandInfo info, BigDecimal value)
        {
            super(info);
            this.value = value;
        }

        public BigDecimal getValue()
        {
            return value;
        }

        @Override
        public void execute(FormulaRuntimeContext context, Deque<Object> stack)
        {
            stack.push(value);
        }
    }
}
