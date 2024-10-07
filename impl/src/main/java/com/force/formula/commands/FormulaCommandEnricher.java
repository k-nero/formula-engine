package com.force.formula.commands;

import com.force.formula.FormulaContext;
import com.force.formula.FormulaException;
import com.force.formula.FormulaProperties;
import com.force.formula.impl.FormulaAST;

/**
 * Describe your class here.
 *
 * @author djacobs
 * @since 140
 */
public interface FormulaCommandEnricher
{
    FormulaAST enrich(FormulaAST node, FormulaContext context, FormulaProperties properties) throws FormulaException;
}
