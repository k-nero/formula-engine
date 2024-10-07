package com.force.formula.impl;

import com.force.formula.*;
import com.force.formula.sql.FormulaSqlStyle;
import com.force.formula.util.NullFormulaContext;

/**
 * A formula context that returns a single constant.
 */
public class ConstantFormulaContext extends NullFormulaContext
{
    private final FormulaContext outerContext;

    public ConstantFormulaContext(FormulaContext context)
    {
        super(context);
        this.outerContext = context;
    }

    @Override
    public String getName()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public FormulaReturnType getFormulaReturnType()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toDurableName(String name) throws InvalidFieldReferenceException, UnsupportedTypeException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String fromDurableName(String reference) throws InvalidFieldReferenceException, UnsupportedTypeException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFunctionSupported(FormulaCommandType function)
    {
        return outerContext.isFunctionSupported(function);
    }

    @Override
    public boolean isNew()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClone()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public FormulaRuntimeContext getOriginalValuesContext() throws FormulaException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String name)
    {
        return outerContext.getProperty(name);
    }

    @Override
    public void setProperty(String name, Object value)
    {
        outerContext.setProperty(name, value);
    }

    @Override
    public FormulaSqlStyle getSqlStyle()
    {
        return outerContext.getSqlStyle();
    }

}

