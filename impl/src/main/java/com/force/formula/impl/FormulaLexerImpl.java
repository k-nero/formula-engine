/*
 * Copyright, 2006, SALESFORCE.com All Rights Reserved Company Confidential
 */

package com.force.formula.impl;

import com.force.formula.parser.gen.FormulaLexer;

import java.io.StringReader;

/**
 * @author dchasman
 * @since 140
 */
public class FormulaLexerImpl extends FormulaLexer
{

    public FormulaLexerImpl(StringReader reader, boolean template)
    {
        super(reader);

        setInFormula(!template);
        setTabSize(1);
    }

}
