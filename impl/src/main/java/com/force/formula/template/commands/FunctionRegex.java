package com.force.formula.template.commands;

import com.force.formula.*;
import com.force.formula.FormulaCommandType.AllowedContext;
import com.force.formula.FormulaCommandType.SelectorSection;
import com.force.formula.commands.*;
import com.force.formula.impl.*;
import com.force.formula.parser.gen.FormulaTokenTypes;
import com.force.formula.sql.SQLPair;
import com.force.formula.template.commands.AccessCountedCharSequence.AccessCountExceededException;

import java.lang.reflect.Type;
import java.util.Deque;
import java.util.regex.Pattern;

/**
 * REGEX(Text, RegEx_Text)
 * Returns TRUE if Text matches the regular expression RegEx_Text. Otherwise, it returns FALSE.
 * Matches the entire string of Text, not just part, and null RegEx Text treated as "^$" and null
 * Text treated as "".
 */
@AllowedContext(section = SelectorSection.ADVANCED, changeOnly = true)
public class FunctionRegex extends FormulaCommandInfoImpl implements FormulaCommandValidator
{
    // TODO: Move this limit to a hook or context variable.
    public static int FORMULA_LIMIT = 1000000; // limits the runtime to ~ 50ms on average machine

    public FunctionRegex()
    {
        super("REGEX");
    }

    @Override
    public Class<?> validate(FormulaAST node, FormulaContext context, FormulaProperties properties)
            throws FormulaException
    {
        if (node.getNumberOfChildren() != 2)
        {
            throw new WrongNumberOfArgumentsException(node.getText(), 2, node);
        }

        FormulaAST firstArg = (FormulaAST) node.getFirstChild();
        FormulaAST secondArg = (FormulaAST) firstArg.getNextSibling();
        Type firstType = firstArg.getDataType();
        Type secondType = secondArg.getDataType();

        if ((!FormulaTypeUtils.isTypeText(firstType) && firstType != RuntimeType.class)
                || (!FormulaTypeUtils.isTypeText(secondType) && secondType != RuntimeType.class))
        {
            throw new IllegalArgumentTypeException(node.getText());
        }

        if (firstType == RuntimeType.class || secondType == RuntimeType.class)
        {
            return RuntimeType.class;
        }

        return Boolean.class;
    }

    @Override
    public FormulaCommand getCommand(FormulaAST node, FormulaContext context) throws FormulaException
    {
        Pattern p = null;
        FormulaAST regexNode = (FormulaAST) node.getFirstChild().getNextSibling();
        if (regexNode.getType() == FormulaTokenTypes.STRING_LITERAL)
        {
            p = Pattern.compile(ConstantString.getStringValue(regexNode, true));
        }
        return new FunctionRegexCommand(this, p);
    }

    @Override
    public SQLPair getSQL(FormulaAST node, FormulaContext context, String[] args, String[] guards, TableAliasRegistry registry)
            throws FormulaException
    {
        FormulaSqlHooks hooks = getSqlHooks(context);
        // Add in ^ and $ to force whole string matching.
        String regex = String.format(hooks.sqlConcat(false), String.format(hooks.sqlConcat(false), "'^'", args[1]), "'$'");
        String sql = hooks.sqlRegexpLike(args[0], regex);
        String guard = SQLPair.generateGuard(guards, null);
        return new SQLPair(sql, guard);
    }

    @Override
    public JsValue getJavascript(FormulaAST node, FormulaContext context, JsValue[] args) throws FormulaException
    {
        // If the regexp is null, it should be empty string test (i.e. '^$').  
        String js = "new RegExp(" + FormulaCommandInfoImpl.jsNvl2(context, args[1], "'^'+" + args[1].js + "+'$'", "'^$'") + ").test(" + FormulaCommandInfoImpl.jsNvl(context, args[0].js, "''") + ")";
        return JsValue.generate(js, args, false);
    }


    static class FunctionRegexCommand extends AbstractFormulaCommand
    {
        private static final long serialVersionUID = 1L;
        private final Pattern constantPattern;

        public FunctionRegexCommand(FormulaCommandInfo info, Pattern pattern)
        {
            super(info);
            constantPattern = pattern;
        }

        @Override
        public void execute(FormulaRuntimeContext context, Deque<Object> stack) throws FormulaException
        {
            Object regex = stack.pop();
            Object input = stack.pop();
            input = (input == null) ? "" : input;
            regex = (regex == null) ? "" : regex;

            try
            {
                // use constant pattern if available
                Pattern p = constantPattern;
                if (p == null)
                {
                    // otherwise compile
                    p = Pattern.compile(checkStringType(regex));
                }
                stack.push(p.matcher((new AccessCountedCharSequence(checkStringType(input), FunctionRegex.FORMULA_LIMIT))).matches());
            }
            catch (AccessCountExceededException x)
            {
                throw new RegexTooComplicatedException((String) regex); // NOPMD
            }
        }
    }
}
