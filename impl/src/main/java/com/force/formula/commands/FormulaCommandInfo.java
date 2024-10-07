/*
 * Created on Dec 10, 2004
 *
 */
package com.force.formula.commands;

import com.force.formula.FormulaCommand;
import com.force.formula.FormulaCommandType;
import com.force.formula.FormulaContext;
import com.force.formula.FormulaException;
import com.force.formula.impl.FormulaAST;
import com.force.formula.impl.JsValue;
import com.force.formula.sql.ITableAliasRegistry;
import com.force.formula.sql.SQLPair;

import java.lang.reflect.Type;

/**
 * Metadata for formula operations
 *
 * @author dchasman
 * @since 140
 */
public interface FormulaCommandInfo extends FormulaCommandType
{
    Type getReturnType(FormulaAST node, FormulaContext context) throws FormulaException;

    Type[] getArgumentTypes(FormulaAST node, FormulaContext context) throws FormulaException;

    FormulaCommand getCommand(FormulaAST node, FormulaContext context) throws FormulaException;

    SQLPair getSQL(FormulaAST node, FormulaContext context, String[] args, String[] guards, ITableAliasRegistry registry) throws FormulaException;

    JsValue getJavascript(FormulaAST node, FormulaContext context, JsValue[] args) throws FormulaException;
}
