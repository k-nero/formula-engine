/*
 * Copyright, 2010, salesforce.com All Rights Reserved Company Confidential
 */
package com.force.formula.template.commands;

import com.force.formula.FormulaCommand;
import com.force.formula.FormulaCommandType.AllowedContext;
import com.force.formula.FormulaCommandType.SelectorSection;
import com.force.formula.FormulaContext;
import com.force.formula.FormulaException;
import com.force.formula.FormulaRuntimeContext;
import com.force.formula.commands.AbstractFormulaCommand;
import com.force.formula.commands.FormulaCommandInfo;
import com.force.formula.commands.FormulaCommandInfoImpl;
import com.force.formula.commands.FormulaCommandInfoRegistry;
import com.force.formula.impl.FormulaAST;
import com.force.formula.impl.JsValue;
import com.force.formula.impl.TableAliasRegistry;
import com.force.formula.sql.SQLPair;

import java.util.Deque;

/**
 * Formula Command Info/Command for a dynamic reference field element; i.e. in an expression like a[b].c, the .c part.
 *
 * @author aballard
 * @since 168
 */
@AllowedContext(section = SelectorSection.ADVANCED, isSql = false, isJavascript = false)
public class DynamicFieldSelector extends FormulaCommandInfoImpl
{

    public final static String DYNAMIC_REF_IDENT = FormulaCommandInfoRegistry.DYNAMIC_REF_IDENT;

    public DynamicFieldSelector()
    {
        super(DYNAMIC_REF_IDENT, String.class, new Class[]{});
    }

    @Override
    public FormulaCommand getCommand(FormulaAST node, FormulaContext context)
    {
        return new DynamicFieldSelectorCommand(this, node.getText());
    }

    @Override
    public SQLPair getSQL(FormulaAST node, FormulaContext context, String[] args, String[] guards, TableAliasRegistry registry)
            throws FormulaException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsValue getJavascript(FormulaAST node, FormulaContext context, JsValue[] args) throws FormulaException
    {
        throw new UnsupportedOperationException();
    }
}

class DynamicFieldSelectorCommand extends AbstractFormulaCommand
{
    private static final long serialVersionUID = 1L;
    private final String fieldName;

    DynamicFieldSelectorCommand(FormulaCommandInfo info, String fieldName)
    {
        super(info);
        this.fieldName = fieldName;
    }

    // The name is the value.
    @Override
    public void execute(FormulaRuntimeContext context, Deque<Object> stack)
    {
        stack.push(fieldName);
    }

}
