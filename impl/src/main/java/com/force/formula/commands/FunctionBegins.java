package com.force.formula.commands;


import com.force.formula.FormulaCommand;
import com.force.formula.FormulaCommandType.AllowedContext;
import com.force.formula.FormulaCommandType.SelectorSection;
import com.force.formula.FormulaContext;
import com.force.formula.FormulaException;
import com.force.formula.FormulaRuntimeContext;
import com.force.formula.impl.FormulaAST;
import com.force.formula.impl.JsValue;
import com.force.formula.impl.TableAliasRegistry;
import com.force.formula.sql.SQLPair;

import java.util.Deque;

/**
 * Describe your class here.
 *
 * @author djacobs
 * @since 140
 */
@AllowedContext(section = SelectorSection.TEXT, isOffline = true)
public class FunctionBegins extends FormulaCommandInfoImpl
{
    public FunctionBegins()
    {
        super("BEGINS", Boolean.class, new Class[]{String.class, String.class});
    }

    @Override
    public FormulaCommand getCommand(FormulaAST node, FormulaContext context)
    {
        return new FunctionBeginsCommand(this);
    }

    @Override
    public SQLPair getSQL(FormulaAST node, FormulaContext context, String[] args, String[] guards, TableAliasRegistry registry)
    {
        String sql = "((" + args[1] + " IS NULL) OR (" + getSqlHooks(context).sqlInstr2(args[0], args[1]) + " = 1))";
        String guard = SQLPair.generateGuard(guards, null);
        return new SQLPair(sql, guard);
    }

    @Override
    public JsValue getJavascript(FormulaAST node, FormulaContext context, JsValue[] args) throws FormulaException
    {
        String js = "!" + args[1] + "||" + args[0] + ".lastIndexOf(" + jsNvl(context, args[1].js, "''") + ",0)===0";
        return JsValue.forNonNullResult(js, args);
    }
}

class FunctionBeginsCommand extends AbstractFormulaCommand
{
    private static final long serialVersionUID = 1L;

    public FunctionBeginsCommand(FormulaCommandInfo formulaCommandInfo)
    {
        super(formulaCommandInfo);
    }

    @Override
    public void execute(FormulaRuntimeContext context, Deque<Object> stack)
    {
        String sub = checkStringType(stack.pop());
        String target = checkStringType(stack.pop());
        if ((sub == null) || (sub.isEmpty()))
        {
            stack.push(true);
        }
        else if ((target == null) || (target.isEmpty()))
        {
            stack.push(null);
        }
        else
        {
            stack.push(target.startsWith(sub));
        }
    }
}
