/**
 *
 */
package com.force.formula.commands;

import com.force.formula.*;
import com.force.formula.impl.FormulaAST;
import com.force.formula.impl.JsValue;
import com.force.formula.impl.TableAliasRegistry;
import com.force.formula.sql.SQLPair;
import junit.framework.TestCase;

import java.util.List;

/**
 * Test that the standard formulas pass the invariants.  Currently tests only mutability,
 * but can be extended to valiate allowedContext
 *
 * @author stamm
 */
public class FormulaCommandInvariantTest extends TestCase
{
    public FormulaCommandInvariantTest(String name)
    {
        super(name);
    }

    public void testAllCommandInfosAreImmutable()
    {
        // Iterate over all registered command infos and verify that they contain only final fields
        StringBuilder results = new StringBuilder();
        List<? extends FormulaCommandType> commandInfos = FormulaEngine.getFactory().getTypeRegistry().getCommands();
        for (FormulaCommandType commandInfo : commandInfos)
        {
            if ("REGEX".equals(commandInfo.getName()))
            {
                continue;  // Ignore FunctionRegex because it has a mutable constant for testing
            }
            FormulaCommandInvariants.validateCommandImmutable(results, commandInfo);
        }

        assertEquals(results.toString(), 0, results.length());

        // Test the validation code itself
        StringBuilder badResults = new StringBuilder();
        FormulaCommandInvariants.validateCommandImmutable(badResults, new BadCommandInfo());
        FormulaCommandInvariants.validateCommandImmutable(badResults, new ExtendedBadCommandInfo());
        String className = getClass().getName();
        assertEquals(
                "BadCommandInfo(s) should not have passed validateCommandInfo()",
                "CommandInfo[BadCommandInfo, " + className + "$BadCommandInfo] contains mutable field 'publicMutableField'\n"
                        + "CommandInfo[BadCommandInfo, " + className + "$BadCommandInfo] contains mutable field 'mutableField'\n"
                        + "CommandInfo[ExtendedBadCommandInfo, " + className + "$BadCommandInfo] contains mutable field 'publicMutableField'\n"
                        + "CommandInfo[ExtendedBadCommandInfo, " + className + "$BadCommandInfo] contains mutable field 'mutableField'\n",
                badResults.toString());
    }

    static class BadCommandInfo extends FormulaCommandInfoImpl
    {
        public final String immutablePublicField = "I am also a good field (other than I am public which is crazy-bad coding)";
        final String immutableField = "I am a good field";
        public String publicMutableField;
        String mutableField;

        public BadCommandInfo()
        {
            this("BadCommandInfo");
        }

        public BadCommandInfo(String name)
        {
            super(name);
            mutableField = "Mothra was here!";
            publicMutableField = "Hey, she's been here too";
        }

        @Override
        public FormulaCommand getCommand(FormulaAST node, FormulaContext context) throws FormulaException
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public SQLPair getSQL(FormulaAST node, FormulaContext context, String[] args, String[] guards, TableAliasRegistry registry)
                throws FormulaException
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public JsValue getJavascript(FormulaAST node, FormulaContext context, JsValue[] args) throws FormulaException
        {
            throw new UnsupportedOperationException();
        }
    }

    static class ExtendedBadCommandInfo extends BadCommandInfo
    {
        public ExtendedBadCommandInfo()
        {
            super("ExtendedBadCommandInfo");
        }
    }

}
