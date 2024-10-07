/**
 *
 */
package com.force.formula.template.commands;

import com.force.formula.*;
import com.force.formula.FormulaSchema.Entity;
import com.force.formula.FormulaSchema.Field;
import com.force.formula.FormulaSchema.FieldOrColumn;
import com.force.formula.FormulaTypeWithDomain.IdType;
import com.force.formula.commands.FieldReferenceCommandInfo;
import com.force.formula.commands.FormulaCommandInfo;
import com.force.formula.commands.FunctionFormat;
import com.force.formula.impl.*;
import com.force.i18n.BaseLocalizer;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Test dynamic references in templates
 *
 * @author stamm
 * @see <a href="https://developer.salesforce.com/docs/atlas.en-us.pages.meta/pages/pages_dynamic_vf_sample_standard.htm">Dynamic Visualforce Documentation</a>
 */
public class DynamicReferenceTest extends BaseCustomizableParserTest
{

    static final FormulaFactoryImpl TEST_FACTORY;

    static
    {
        List<FormulaCommandInfo> types = new ArrayList<>(FormulaCommandTypeRegistryImpl.getDefaultCommands());
        // Test format and template parsing
        types.add(new FunctionFormat());
        types.add(new FunctionTemplate());
        types.add(new FunctionMap());
        types.add(new DynamicReference());
        types.add(new DynamicFieldSelector());
        types.add(new FieldReferenceCommandInfo());
        TEST_FACTORY = new FormulaFactoryImpl(new FormulaCommandTypeRegistryImpl(types));
    }

    private FormulaEngineHooks oldHooks = null;
    private FormulaFactory oldFactory = null;

    public DynamicReferenceTest(String name)
    {
        super(name);
    }

    @Override
    protected MockFormulaType getFormulaType()
    {
        return MockFormulaType.DYNAMIC;
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        oldFactory = FormulaEngine.getFactory();
        FormulaEngine.setFactory(TEST_FACTORY);
        oldHooks = FormulaEngine.getHooks();
        FormulaEngine.setHooks(new FormulaValidationHooks()
        {
            @Override
            public BaseLocalizer getLocalizer()
            {
                return new MockLocalizerContext.MockLocalizer();
            }

            @Override
            public Type parseHook_getFieldReturnTypeOverride(FormulaDataType columnType, FormulaAST node,
                    FormulaContext context, Entity[] domain)
            {
                if (columnType == MockFormulaDataType.LIST)
                {
                    return List.class;
                }
                else if (columnType == MockFormulaDataType.MAP)
                {
                    return Map.class;
                }
                else
                {
                    return FormulaValidationHooks.super.parseHook_getFieldReturnTypeOverride(columnType, node, context, domain);
                }
            }

            @Override
            public Object getFieldReferenceValue(ContextualFormulaFieldInfo fieldInfo, FormulaDataType dataType,
                    FormulaRuntimeContext context, FormulaFieldReference fieldReference, boolean useUnderlyingType)
                    throws FormulaException
            {
                if (dataType == MockFormulaDataType.MAP)
                {
                    return context.getObject(fieldReference);
                }
                else if (dataType == MockFormulaDataType.LIST)
                {
                    return context.getObject(fieldReference);
                }
                return FormulaValidationHooks.super.getFieldReferenceValue(fieldInfo, dataType, context, fieldReference,
                        useUnderlyingType);
            }
        });
    }

    @Override
    protected void tearDown() throws Exception
    {
        FormulaEngine.setHooks(oldHooks);
        FormulaEngine.setFactory(oldFactory);
    }

    /**
     * Test List references using substrings from the TestAccount
     *
     * @throws Exception
     */
    public void testListReference() throws Exception
    {
        assertEquals("First", evaluateString("List[0]"));
        assertEquals("Second", evaluateString("List[1]"));
        try
        {
            evaluateString("List[2]");
        }
        catch (FormulaEvaluationException x)
        {
            assertEquals("Subscript value 2 not valid.  Must be between 0 and 1", x.getMessage());
        }
    }

    /**
     * Test Map references using [] from the TestAccount
     *
     * @throws Exception
     */
    public void testMapReference() throws Exception
    {
        assertEquals(new BigDecimal("1"), evaluateBigDecimal("Map['Foo']"));
        try
        {
            evaluateString("Map['Baz']");
        }
        catch (FormulaEvaluationException x)
        {
            assertEquals("Map key Baz not found in map.", x.getMessage());
        }
    }

    /**
     * Test the MAP() function
     *
     * @throws Exception
     */
    public void testMapFunction() throws Exception
    {
        // Map is StringString...
        // Note: you can only use [] on field references as of 2021.  Grammar issue
        assertEquals(ImmutableMap.of("First", "1", "Second", "2"),
                evaluate("MAP('First',1,'Second',2)", MockFormulaDataType.MAP));
    }

    static class MockIdFormulaType implements IdType
    {
        private final Entity[] domain;

        public MockIdFormulaType(Entity[] domain)
        {
            this.domain = domain;
        }

        public MockIdFormulaType(FieldOrColumn field, boolean isSobjectRow)
        {
            this(((Field) field).getFormulaForeignKeyDomains());
        }

        @Override
        public boolean isApplicable(Entity[] targetDomains)
        {
            return false;
        }

        @Override
        public Type[] getActualTypeArguments()
        {
            return null;
        }

        @Override
        public Type getRawType()
        {
            return MockIdFormulaType.class;
        }

        @Override
        public Type getOwnerType()
        {
            return null;
        }

        @Override
        public Entity[] getDomains()
        {
            return domain;
        }

        @Override
        public IdType addToDomain(Entity[] additionalDomains)
        {
            return null;
        }
    }
}
