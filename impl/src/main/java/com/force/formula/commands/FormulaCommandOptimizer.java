package com.force.formula.commands;

import com.force.formula.FormulaContext;
import com.force.formula.FormulaException;
import com.force.formula.impl.FormulaAST;

public interface FormulaCommandOptimizer
{
    // optimize is allowed to modify the parsetree *and* the ast node
    FormulaAST optimize(FormulaAST node, FormulaContext context) throws FormulaException;
}
