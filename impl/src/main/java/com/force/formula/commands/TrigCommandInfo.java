package com.force.formula.commands;

import com.force.formula.FormulaCommand;
import com.force.formula.FormulaCommandType;
import com.force.formula.FormulaCommandType.AllowedContext;
import com.force.formula.FormulaCommandType.SelectorSection;
import com.force.formula.FormulaContext;
import com.force.formula.FormulaException;
import com.force.formula.impl.*;
import com.force.formula.sql.SQLPair;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import java.math.BigDecimal;

/**
 * Base class for trigonometric math, allowing overrides for SQL in FormulaSqlHooks.
 *
 * @author stamm
 * @since 0.2.0
 */
@AllowedContext(section = SelectorSection.MATH, isOffline = true)
public class TrigCommandInfo extends FormulaCommandInfoImpl
{
    private final FormulaTrigFunction trigFunction;

    public TrigCommandInfo(FormulaTrigFunction function)
    {
        super(function.name(), BigDecimal.class, new Class[]{BigDecimal.class});
        this.trigFunction = function;
    }

    @Override
    public FormulaCommand getCommand(FormulaAST node, FormulaContext context)
    {
        return new UnaryMathCommand(this)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected BigDecimal execute(BigDecimal arg)
            {
                return trigFunction.getJavaFunction().apply(arg);
            }
        };
    }

    @Override
    public SQLPair getSQL(FormulaAST node, FormulaContext context, String[] args, String[] guards, TableAliasRegistry registry)
    {
        String sql = trigFunction.getSqlFunction() + "(" + args[0] + ")";
        FormulaSqlHooks hooks = (FormulaSqlHooks) context.getSqlStyle();
        sql = hooks.sqlTrigConvert(sql);

        String guard = null;
        Range<BigDecimal> range = trigFunction.getRange();
        if (range != null)
        {
            // If there's a range on inputs, put in a SQL guard to prevent errors
            guard = SQLPair.generateGuard(guards, args[0] + (range.lowerBoundType() == BoundType.CLOSED ? "<" : "<=") + range.lowerEndpoint() + " OR " + args[0] +
                    (range.lowerBoundType() == BoundType.CLOSED ? ">" : ">=") + range.upperEndpoint());
        }
        else
        {
            SQLPair.generateGuard(guards, null);
        }

        return new SQLPair(sql, guard);
    }

    @Override
    public JsValue getJavascript(FormulaAST node, FormulaContext context, JsValue[] args) throws FormulaException
    {
        return JsValue.forNonNullResult(jsMathPkg(context) + "." + trigFunction.getJavascriptFunction() + "(" + args[0] + ")", args);
    }

    @Override
    protected FormulaCommandType.AllowedContext getDefaultContext()
    {
        return TrigCommandInfo.class.getAnnotation(FormulaCommandType.AllowedContext.class);
    }

    @Override
    public FormulaCommandType.AllowedContext getAllowedContext()
    {
        return getDefaultContext();
    }
}
