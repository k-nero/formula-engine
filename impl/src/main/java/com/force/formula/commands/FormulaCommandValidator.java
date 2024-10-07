package com.force.formula.commands;

import com.force.formula.FormulaContext;
import com.force.formula.FormulaException;
import com.force.formula.FormulaProperties;
import com.force.formula.impl.FormulaAST;

import java.lang.reflect.Type;

/**
 * Describe your class here.
 *
 * @author dchasman
 * @since 140
 */
public interface FormulaCommandValidator
{
    Type validate(FormulaAST node, FormulaContext context, FormulaProperties properties) throws FormulaException;
}
