package com.force.formula.commands;

import com.force.formula.*;
import com.force.formula.FormulaCommandType.AllowedContext;
import com.force.formula.FormulaCommandType.SelectorSection;
import com.force.formula.impl.*;
import com.force.formula.sql.SQLPair;
import com.force.i18n.LabelRef;
import com.force.i18n.LabelReference;
import com.google.common.base.Joiner;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;

/**
 * Function to instantiate a LabelReference for use in doing complex formatting
 * <p>
 * LABEL(section, key, [arg1,...]);
 *
 * @author stamm
 * @see FunctionFormat
 * @since 0.2.0
 */
@AllowedContext(section = SelectorSection.TEXT, isSql = false, displayOnly = true)
public class FunctionLabel extends FormulaCommandInfoImpl implements FormulaCommandValidator
{
    public FunctionLabel()
    {
        // label constructor Section, Key.... plus optional arguments that will autorender with MessageFormat.
        super("LABEL", LabelReference.class, new Class[]{String.class, String.class});
    }

    @Override
    public FormulaCommand getCommand(FormulaAST node, FormulaContext context)
    {
        return new LabelCommand(this, node.getNumberOfChildren());
    }

    @Override
    public SQLPair getSQL(FormulaAST node, FormulaContext context, String[] args, String[] guards, TableAliasRegistry registry)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> validate(FormulaAST node, FormulaContext context, FormulaProperties properties) throws FormulaException
    {

        int kids = node.getNumberOfChildren();
        if (kids < 2)
        {
            throw new WrongNumberOfArgumentsException(node.getText(), 2, node);
        }

        FormulaAST sectionNode = (FormulaAST) node.getFirstChild();
        FormulaAST keyNode = (FormulaAST) sectionNode.getNextSibling();

        if (!FormulaTypeUtils.isTypeText(sectionNode.getDataType()))
        {
            throw new WrongArgumentTypeException(node.getText(), new Type[]{String.class}, sectionNode);
        }
        if (!FormulaTypeUtils.isTypeText(keyNode.getDataType()))
        {
            throw new WrongArgumentTypeException(node.getText(), new Type[]{String.class}, keyNode);
        }

        // Note: we allow "any" type for the rest of these as they'll be string converted if need be.
        return LabelReference.class;
    }

    @Override
    public JsValue getJavascript(FormulaAST node, FormulaContext context, JsValue[] args) throws FormulaException
    {
        String restOfArgs = "";
        if (args.length > 2)
        {
            restOfArgs = "," + Joiner.on(',').join(Arrays.asList(args).subList(2, args.length));
        }
        return JsValue.forNonNullResult("Grammaticus.getLabel(" + args[0].js + "+'.'+" + args[1].js + ", null " + restOfArgs + ")", args);
    }

    static class LabelCommand extends AbstractFormulaCommand
    {
        private static final long serialVersionUID = 1L;
        private final int numNodes;

        LabelCommand(FormulaCommandInfo info, int numNodes)
        {
            super(info);
            this.numNodes = numNodes;
        }

        @Override
        public void execute(FormulaRuntimeContext context, Deque<Object> stack)
        {
            // Pop off all the arguments
            Object[] args = new Object[numNodes - 2];
            for (int i = 2; i < numNodes; i++)
            {
                Object value = stack.pop();
                if (value == null)
                {
                    value = "";
                }
                else if (value instanceof FormulaDateTime)
                {
                    // Unwrap FormulaDateTime if doing formatting
                    value = ((FormulaDateTime) value).getDate();
                }
                else if (!(value instanceof Date || value instanceof Number || value instanceof Boolean))
                {
                    value = FormulaEngine.getHooks().convertToString(value);
                }
                args[numNodes - i - 1] = value;
                // TODO: Convert the various arguments if needed
            }
            // Get Key and section
            String key = checkStringType(stack.pop());
            String section = checkStringType(stack.pop());

            stack.push(new LabelRef(section, key, args));
        }
    }
}
