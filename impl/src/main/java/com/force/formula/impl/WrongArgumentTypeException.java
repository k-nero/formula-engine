package com.force.formula.impl;

import antlr.Token;
import com.force.formula.FormulaDataType;
import com.force.formula.FormulaException;
import com.force.formula.util.FormulaI18nUtils;

import java.lang.reflect.Type;

/**
 * Describe your class here.
 *
 * @author dchasman
 * @since 140
 */
public class WrongArgumentTypeException extends FormulaException
{

    private static final long serialVersionUID = 1L;
    private final int location;
    private final String text;
    private final int type;

    public WrongArgumentTypeException(String function, Type[] expectedInputTypes, FormulaAST actual)
    {
        super(createErrorMessage(function, expectedInputTypes, actual));

        Token token = actual.getToken();
        if (token != null)
        {
            location = token.getColumn();
            text = token.getText();
            type = token.getType();
        }
        else
        {
            location = 0;
            text = "";
            type = 0;
        }
    }

    public WrongArgumentTypeException(String function, Type[] expectedInputTypes, FormulaAST actual, FormulaDataType columnType)
    {
        super(createErrorMessage(function, expectedInputTypes, columnType));

        Token token = actual.getToken();
        if (token != null)
        {
            location = token.getColumn();
            text = token.getText();
            type = token.getType();
        }
        else
        {
            location = 0;
            text = "";
            type = 0;
        }
    }

    private static String createErrorMessage(String function, Type[] expectedInputTypes, FormulaAST actual)
    {
        Type actualInputType = actual.getDataType();

        StringBuffer expected = new StringBuffer();
        for (Type expectedInputType : expectedInputTypes)
        {
            // Avoid nonsense error messages like "Error: Incorrect parameter for function -. Expected Number, Date, DateTime, received Date"
            if (actualInputType == expectedInputType)
            {
                continue;
            }

            if (expected.length() > 0)
            {
                expected.append(", ");
            }

            expected.append(FormulaTypeUtils.getTypeLabel(expectedInputType));
        }

        String actualMessage = FormulaTypeUtils.getTypeLabel(actualInputType);

        return FormulaI18nUtils.getLocalizer().getLabel("FormulaFieldExceptionMessages", "WrongArgumentTypeException",
                getDescription(function),
                expected.toString(), actualMessage);
    }

    public static String getDescription(String tokenText)
    {
        String label;
        if (tokenText == null || tokenText.length() == 0 || Character.isLetter(tokenText.charAt(0)))
        {
            label = "function";
        }
        else if ("[]".equals(tokenText))
        {
            label = "subscript";
        }
        else
        {
            label = "operator";
        }
        return FormulaI18nUtils.getLocalizer().getLabel("FormulaFieldExceptionMessages", label, tokenText);
    }

    private static String createErrorMessage(String function, Type[] expectedInputTypes, FormulaDataType columnType)
    {
        StringBuffer expected = new StringBuffer();
        for (Type expectedInputType : expectedInputTypes)
        {
            if (expected.length() > 0)
            {
                expected.append(", ");
            }

            expected.append(FormulaTypeUtils.getTypeLabel(expectedInputType));
        }

        return FormulaI18nUtils.getLocalizer().getLabel("FormulaFieldExceptionMessages", "WrongArgumentTypeException", function,
                expected.toString(), columnType.getLabel());
    }

    public int getLocation()
    {
        return location;
    }

    public String getTokenText()
    {
        return text;
    }

    public int getTokenType()
    {
        return type;
    }
}
