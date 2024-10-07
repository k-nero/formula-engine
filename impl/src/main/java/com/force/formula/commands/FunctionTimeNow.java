package com.force.formula.commands;

import com.force.formula.*;
import com.force.formula.FormulaCommandType.AllowedContext;
import com.force.formula.FormulaCommandType.SelectorSection;
import com.force.formula.impl.FormulaAST;
import com.force.formula.impl.JsValue;
import com.force.formula.impl.TableAliasRegistry;
import com.force.formula.sql.SQLPair;
import com.force.formula.util.FormulaDateUtil;
import com.force.i18n.BaseLocalizer;

import java.util.Calendar;
import java.util.Deque;
import java.util.GregorianCalendar;

/**
 * Describe your class here.
 *
 * @author p
 * @since 140
 */
@AllowedContext(section = SelectorSection.DATE_TIME, nonFlowOnly = true)
public class FunctionTimeNow extends FormulaCommandInfoImpl
{

    public FunctionTimeNow()
    {
        super("TIMENOW", FormulaTime.class, new Class[]{});
    }

    @Override
    public FormulaCommand getCommand(FormulaAST node, FormulaContext context)
    {
        return new FunctionTimeNowCommand(this);
    }

    @Override
    public SQLPair getSQL(FormulaAST node, FormulaContext context, String[] args, String[] guards, TableAliasRegistry registry)
    {
        FormulaTime tt = FormulaEngine.getHooks().testHook_getCurrentTime();
        if (tt != null)
        {
            return new SQLPair("" + tt.getTimeInMillis(), null);
        }
        else
        {
            return new SQLPair(getSqlHooks(context).sqlTimeNow(), null);
        }
    }

    @Override
    public JsValue getJavascript(FormulaAST node, FormulaContext context, JsValue[] args) throws FormulaException
    {
        return new JsValue("new Date()", null, false);
    }

}

class FunctionTimeNowCommand extends AbstractFormulaCommand
{
    private static final long serialVersionUID = 1L;
    private static final String FUNCTIONTIMENOW_VALUE = "common.formula.commands.FunctionTimeNow.value";

    public FunctionTimeNowCommand(FormulaCommandInfo formulaCommandInfo)
    {
        super(formulaCommandInfo);
    }

    @Override
    public void execute(FormulaRuntimeContext context, Deque<Object> stack)
    {
        FormulaTime now = context.getProperty(FUNCTIONTIMENOW_VALUE);
        if (now == null)
        {
            Calendar cal = new GregorianCalendar(BaseLocalizer.GMT_TZ);
            now = FormulaEngine.getHooks().constructTime(FormulaDateUtil.millisecondOfDay(cal));
            context.setProperty(FUNCTIONTIMENOW_VALUE, now);
        }

        stack.push(now);
    }

    @Override
    public boolean isDeterministic(FormulaContext formulaContext)
    {
        return false;
    }
}
