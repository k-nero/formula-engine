/*
 * Created on Dec 10, 2004
 *
 */
package com.force.formula.commands;

import com.force.formula.FormulaCommand;
import com.force.formula.FormulaContext;
import com.force.formula.FormulaException;
import com.force.formula.impl.FormulaAST;
import com.force.formula.impl.JsValue;
import com.force.formula.impl.TableAliasRegistry;
import com.force.formula.sql.SQLPair;
import com.force.formula.util.FormulaTextUtil;
import com.google.common.base.CharMatcher;

import java.io.Serializable;

/**
 * Push the command's associated string value onto the stack
 *
 * @author dchasman
 * @since 140
 */
public class ConstantString extends ConstantBase
{
    // Special value used to represent a Null String on the evaluation stack.  This is used with dynamic references to
    // allow comparison operations to know if an operand is a String type, even if the value is null.
    public static final StringWrapper NullString = new StringWrapper(null);
    private final boolean unescape;

    public ConstantString(String name, boolean unescape)
    {
        super(name, String.class, new Class[0]);

        this.unescape = unescape;
    }

    public static String getStringValue(FormulaAST node, boolean unescape)
    {
        String result = node.getText();

        if (result != null && unescape)
        {
            // Remove double quotes of literal string value
            result = result.substring(1, result.length() - 1);

            // Un-escape double quotes and backslashes
            result = FormulaTextUtil.replaceSimple(result, new String[]{"\\\"", "\\\\"}, new String[]{"\"", "\\"});
        }

        return result;
    }

    @Override
    public FormulaCommand getCommand(FormulaAST node, FormulaContext context)
    {
        return new StringConstantCommand(this, getStringValue(node, unescape));
    }

    @Override
    public SQLPair getSQL(FormulaAST node, FormulaContext context, String[] args, String[] guards, TableAliasRegistry registry)
    {
        String value = getStringValue(node, unescape);
        if (value != null)
        {
            // Escape single quotes
            value = CharMatcher.is('\'').replaceFrom(value, "''");

            // Use NULL instead of '' for postgres
            if (context.getSqlStyle().isPostgresStyle() && "".equals(value))
            {
                value = "NULL";
            }
            else
            {
                // Wrap with single quotes
                String result = "'" + value +
                        "'";
                value = result;
            }

        }

        return new SQLPair(value, null);
    }

    @Override
    public JsValue getJavascript(FormulaAST node, FormulaContext context, JsValue[] args) throws FormulaException
    {
        if (node.getText() == null)
        {
            return new JsValue(null, null, true);
        }
        return new JsValue('"' + FormulaTextUtil.escapeForJavascriptString(getStringValue(node, true)) + '"', null, false);
    }

    public static class StringWrapper implements Serializable
    {

        private static final long serialVersionUID = 1L;
        private final String value;

        protected StringWrapper()
        {
            this.value = null;
        }

        protected StringWrapper(String value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return value;
        }
    }

}

