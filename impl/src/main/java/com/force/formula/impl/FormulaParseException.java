/*
 * Created on Dec 29, 2004
 *
 */
package com.force.formula.impl;

import antlr.*;
import com.force.formula.FormulaException;
import com.force.formula.parser.gen.FormulaTokenTypes;
import com.force.formula.util.FormulaI18nUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dchasman
 * @since 140
 */
public class FormulaParseException extends FormulaException
{

    private static final long serialVersionUID = 1L;
    private static final Map<Integer, String> mapTokenToText = new HashMap<Integer, String>();

    static
    {
        mapTokenToText.put(FormulaTokenTypes.BEGIN_EMBEDDED_FORMULA, "{");
        mapTokenToText.put(FormulaTokenTypes.END_EMBEDDED_FORMULA, "}");

        mapTokenToText.put(FormulaTokenTypes.LPAREN, "(");
        mapTokenToText.put(FormulaTokenTypes.RPAREN, ")");

        mapTokenToText.put(FormulaTokenTypes.BANG, "!");
        mapTokenToText.put(FormulaTokenTypes.COMMA, ",");

        mapTokenToText.put(FormulaTokenTypes.LSQUAREBRACKET, "[");
        mapTokenToText.put(FormulaTokenTypes.RSQUAREBRACKET, "]");

        mapTokenToText.put(FormulaTokenTypes.EQUAL, "=");
    }

    private RecognitionException cause;
    private String offendingToken;

    public FormulaParseException(ANTLRException e)
    {
        super(createMessage(e));

        if (e instanceof RecognitionException)
        {
            cause = (RecognitionException) e;
        }
        else if (e instanceof TokenStreamRecognitionException)
        {
            cause = ((TokenStreamRecognitionException) e).recog;
        }

        if (e instanceof MismatchedCharException)
        {
            MismatchedCharException mce = (MismatchedCharException) e;
            offendingToken = String.valueOf(mce.foundChar);
        }
        else if (e instanceof NoViableAltException)
        {
            NoViableAltException nvae = (NoViableAltException) e;
            Token token = nvae.token;
            offendingToken = token.getText();
        }
        else if (e instanceof MismatchedTokenException)
        {
            MismatchedTokenException mte = (MismatchedTokenException) e;
            offendingToken = mte.token.getText();

        }
    }

    private static String createMessage(ANTLRException e)
    {
        if (e instanceof RecognitionException)
        {
            return handleException((RecognitionException) e);
        }
        else if (e instanceof TokenStreamRecognitionException)
        {
            return handleException(((TokenStreamRecognitionException) e).recog);
        }
        else
        {
            return FormulaI18nUtils.getLocalizer().getLabel("FormulaFieldExceptionMessages", "FormulaParseException" + "_UNHANDLED", e
                    .getMessage());
        }
    }

    /**
     * Build the found token based on the ANTLR exception thrown.
     *
     * @param e : RecognitionException throw from ANTLR
     * @return null if the found token is not applicable to the exception type,
     * otherwise the properly constructed found token.
     */
    public static String buildFoundToken(RecognitionException e)
    {
        if (e instanceof NoViableAltException)
        {
            // line 1:1: unexpected token: a
            NoViableAltException nvae = (NoViableAltException) e;

            Token token = nvae.token;
            return (token.getType() != FormulaTokenTypes.EOF) ? token.getText() : FormulaI18nUtils.getLocalizer().getLabel(
                    "FormulaFieldExceptionMessages", "EndOfFormula");
        }
        else if (e instanceof MismatchedTokenException)
        {
            MismatchedTokenException mte = (MismatchedTokenException) e;

            if (mte.expecting == FormulaTokenTypes.EOF)
            {
                String found = (mte.token.getType() != FormulaTokenTypes.EOF) ? mte.token.getText() : FormulaI18nUtils.getLocalizer().getLabel(
                        "FormulaFieldExceptionMessages", "EndOfFormula");
                if (mte.token.getType() != FormulaTokenTypes.IDENT)
                {
                    found = "'" + found + "'";
                }

                return found;
            }
        }
        return null;
    }

    private static String handleException(RecognitionException e)
    {
        return handleException(e, buildFoundToken(e));
    }

    /**
     * Builds the error message based on the ANTLR exception and the found token.
     *
     * @param e     : the handled ANTLR exception
     * @param found : the found token used to construct the the error message
     * @return constructed error message for this FormulaParseException
     */
    public static String handleException(RecognitionException e, String found)
    {
        if (e instanceof MismatchedCharException)
        {
            MismatchedCharException mce = (MismatchedCharException) e;

            return FormulaI18nUtils.getLocalizer().getLabel("FormulaFieldExceptionMessages", "FormulaParseException" + "_DETAILED",
                    mce.getLine(), mce.getColumn(), (char) mce.expecting, mce.foundChar);
        }
        else if (e instanceof NoViableAltException)
        {
            // line 1:1: unexpected token: a
            NoViableAltException nvae = (NoViableAltException) e;

            Token token = nvae.token;

            return FormulaI18nUtils.getLocalizer().getLabel("FormulaFieldExceptionMessages", "FormulaParseException"
                    + "_DETAILEDWITHTOKEN", token.getLine(), token.getColumn(), found == null ? "" : found);
        }
        else if (e instanceof MismatchedTokenException)
        {
            MismatchedTokenException mte = (MismatchedTokenException) e;

            if (mte.expecting != FormulaTokenTypes.EOF)
            {
                return FormulaI18nUtils.getLocalizer().getLabel("FormulaFieldExceptionMessages",
                        "FormulaParseException" + "_DETAILED", mte.getLine(), mte.getColumn(),
                        tokenTextFromId(mte.expecting));
            }
            else
            {
                return FormulaI18nUtils.getLocalizer().getLabel("FormulaFieldExceptionMessages", "FormulaParseException"
                        + "_DETAILEDWITHEXTRA", found == null ? "" : found);
            }
        }
        else
        {
            return FormulaI18nUtils.getLocalizer().getLabel("FormulaFieldExceptionMessages", "FormulaParseException", e.getLine(), e
                    .getColumn());
        }
    }

    private static Object tokenTextFromId(int token)
    {
        String tokenText = mapTokenToText.get(token);

        if (tokenText != null)
        {
            return "'" + tokenText + "'";
        }
        else if (token == FormulaTokenTypes.EOF)
        {
            return "'" + FormulaI18nUtils.getLocalizer().getLabel("FormulaFieldExceptionMessages", "EndOfFormula") + "'";
        }
        else if (token == FormulaTokenTypes.IDENT)
        {
            return FormulaI18nUtils.getLocalizer().getLabel("FormulaFieldExceptionMessages", "FieldReference");
        }
        else
        {
            return "Unknown token: " + token;
        }
    }

    public int getLocation()
    {
        return (cause != null) ? cause.getColumn() : -1;
    }

    public RecognitionException getCauseException()
    {
        return cause;
    }

    public String getOffendingToken()
    {
        return offendingToken;
    }

}
