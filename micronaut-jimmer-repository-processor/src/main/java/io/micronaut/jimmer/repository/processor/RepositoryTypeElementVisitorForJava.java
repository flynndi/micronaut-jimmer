package io.micronaut.jimmer.repository.processor;

import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.inject.ast.ClassElement;
import io.micronaut.inject.ast.ElementQuery;
import io.micronaut.inject.ast.MethodElement;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.jimmer.repository.annotation.Repository;
import io.micronaut.jimmer.repository.parser.Context;
import io.micronaut.jimmer.repository.parser.QueryMethod;
import io.micronaut.jimmer.repository.support.JRepositoryImpl;
import io.micronaut.jimmer.repository.support.QueryExecutors;
import io.micronaut.jimmer.util.HashUtil;
import io.micronaut.sourcegen.generator.SourceGenerator;
import io.micronaut.sourcegen.generator.SourceGenerators;
import io.micronaut.sourcegen.model.*;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.lang.reflect.Method;
import java.util.*;
import javax.lang.model.element.Modifier;
import org.babyfish.jimmer.Specification;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;

final class RepositoryTypeElementVisitorForJava {

    RepositoryTypeElementVisitorForJava() {}

    public void visitClass(ClassElement element, VisitorContext context) {
        Collection<ClassElement> interfaces = element.getInterfaces();
        if (interfaces.isEmpty()) {
            return;
        }

        List<ClassElement> interfaceList = new ArrayList<>(interfaces);
        ClassElement classElement = interfaceList.get(0);
        List<? extends ClassElement> boundGenericTypes = classElement.getBoundGenericTypes();
        if (boundGenericTypes.size() != 2) {
            throw new IllegalStateException();
        }

        ClassElement entityClassElement = boundGenericTypes.get(0);
        ClassElement idClassElement = boundGenericTypes.get(1);
        SourceGenerator sourceGenerator =
                SourceGenerators.findByLanguage(context.getLanguage()).orElse(null);
        if (sourceGenerator == null) {
            return;
        }
        String builderClassName =
                element.getName() + "_" + HashUtil.sha1(element.getName()) + "Impl";
        ClassTypeDef currentGeneratedClassTypeDef = ClassTypeDef.of(builderClassName);

        ClassTypeDef jRepositoryImplClassTypeDef = ClassTypeDef.of(JRepositoryImpl.class);
        TypeDef entityTypeDef = TypeDef.of(entityClassElement);
        TypeDef idTypeDef = TypeDef.of(idClassElement);
        ClassTypeDef.Parameterized parameterizedType =
                new ClassTypeDef.Parameterized(
                        jRepositoryImplClassTypeDef, List.of(entityTypeDef, idTypeDef));

        ClassDef.ClassDefBuilder classDefBuilder =
                ClassDef.builder(builderClassName)
                        .addAnnotation(Singleton.class)
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(parameterizedType)
                        .addSuperinterface(TypeDef.of(element));

        String dataSourceName =
                element.getAnnotationMetadata()
                        .getAnnotation(Repository.class)
                        .stringValue("dataSourceName")
                        .orElse("default");

        ParameterDef parameterDef =
                ParameterDef.builder("var1", TypeDef.of(JSqlClient.class))
                        .addAnnotation(
                                AnnotationDef.builder(Named.class)
                                        .addMember("value", dataSourceName)
                                        .build())
                        .build();

        ExpressionDef.Constant classLiteral =
                ExpressionDef.constant(ClassTypeDef.of(entityClassElement));

        MethodDef methodDef =
                MethodDef.constructor()
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Inject.class)
                        .addParameter(parameterDef)
                        .addStatement(
                                (aThis, methodParameters) ->
                                        aThis.superRef()
                                                .invokeConstructor(
                                                        List.of(
                                                                methodParameters.get(0),
                                                                classLiteral)))
                        .build();
        classDefBuilder.addMethod(methodDef);

        List<StatementDef> staticBlockList = new ArrayList<>();
        // Context var0 = new Context(); start
        StatementDef defineContext =
                new StatementDef.DefineAndAssign(
                        new VariableDef.Local("var0", TypeDef.of(Context.class)),
                        ClassTypeDef.of(Context.class).instantiate());
        staticBlockList.add(defineContext);
        // Context var0 = new Context(); end

        // ImmutableType var1 = ImmutableType.get(x.class); start
        ExpressionDef.InvokeStaticMethod immutableTypeGet =
                ClassTypeDef.of(ImmutableType.class)
                        .invokeStatic(
                                "get",
                                TypeDef.of(ImmutableType.class),
                                ExpressionDef.constant(ClassTypeDef.of(entityClassElement)));
        StatementDef.DefineAndAssign defineImmutableType =
                new StatementDef.DefineAndAssign(
                        new VariableDef.Local("var1", TypeDef.of(ImmutableType.class)),
                        immutableTypeGet);
        staticBlockList.add(defineImmutableType);
        // ImmutableType var1 = ImmutableType.get(x.class); end

        List<MethodElement> methods =
                element.getEnclosedElements(
                        ElementQuery.of(MethodElement.class).onlyDeclared().onlyAbstract());

        for (MethodElement method : methods) {
            // private static final QueryMethod methodName start
            String generatorMethodName =
                    "QUERY_METHOD_" + method.getName().toUpperCase(Locale.ROOT);
            FieldDef fieldDef =
                    FieldDef.builder(generatorMethodName)
                            .ofType(TypeDef.of(QueryMethod.class))
                            .addModifiers(
                                    List.of(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC))
                            .build();
            classDefBuilder.addField(fieldDef);
            // private static final QueryMethod methodName end

            // QUERY_METHOD start
            VariableDef.Local var0 = new VariableDef.Local("var0", TypeDef.of(Context.class));
            VariableDef.Local var1 = new VariableDef.Local("var1", TypeDef.of(ImmutableType.class));

            // currentClass expression
            ExpressionDef currentClassLiteral = ExpressionDef.constant(ClassTypeDef.of(element));

            List<ExpressionDef> methodCallArgs = new ArrayList<>();
            methodCallArgs.add(ExpressionDef.constant(method.getName()));

            // Obtain the list of parameter types of the method and convert it to Class<? Literal
            // expression
            List<ExpressionDef> parametersTypes = new ArrayList<>();
            if (Objects.nonNull(method.getParameters()) && method.getParameters().length != 0) {
                Arrays.stream(method.getParameters())
                        .forEach(
                                arg -> {
                                    TypeDef typeDef = TypeDef.of(arg.getType());
                                    // For primitive types, we need to use their wrapper class for
                                    // Class<?> literals
                                    if (typeDef instanceof TypeDef.Primitive primitive) {
                                        parametersTypes.add(
                                                ExpressionDef.constant(primitive.clazz()));
                                    } else {
                                        parametersTypes.add(ExpressionDef.constant(typeDef));
                                    }
                                });
            }

            // parametersTypes
            methodCallArgs.add(TypeDef.array(TypeDef.of(Class.class)).instantiate(parametersTypes));

            // .class.getMethod("findByRoleId", String.class)
            ExpressionDef.InvokeInstanceMethod getMethodCall =
                    currentClassLiteral.invoke(
                            "getMethod", TypeDef.of(Method.class), methodCallArgs);

            // QueryMethod.of(...)
            List<ExpressionDef> queryMethodOfArgs = new ArrayList<>();
            queryMethodOfArgs.add(var0);
            queryMethodOfArgs.add(var1);
            queryMethodOfArgs.add(getMethodCall);

            // QueryMethod.of(...)
            ExpressionDef assignmentExpression =
                    ClassTypeDef.of(QueryMethod.class)
                            .invokeStatic("of", TypeDef.of(QueryMethod.class), queryMethodOfArgs);

            VariableDef.StaticField staticFieldVariable =
                    new VariableDef.StaticField(
                            currentGeneratedClassTypeDef, fieldDef.getName(), fieldDef.getType());
            StatementDef.PutStaticField putStaticField =
                    staticFieldVariable.put(assignmentExpression);
            staticBlockList.add(putStaticField);
            // QUERY_METHOD end

            ClassTypeDef queryExecutorsType = ClassTypeDef.of(QueryExecutors.class);

            ClassTypeDef sqlClientImplementorType = ClassTypeDef.of(JSqlClientImplementor.class);
            TypeDef paginationType = TypeDef.of(Pageable.class);
            TypeDef sortType = TypeDef.of(Sort.class);
            TypeDef specificationType = TypeDef.of(Specification.class);
            TypeDef fetcherType = TypeDef.of(Fetcher.class);
            TypeDef classType = TypeDef.of(Class.class);

            MethodDef newMethod =
                    MethodDef.override(method)
                            .build(
                                    (aThis, methodParameters) -> {
                                        ExpressionDef pageableArg =
                                                ExpressionDef.nullValue().cast(paginationType);
                                        ExpressionDef sortArg =
                                                ExpressionDef.nullValue().cast(sortType);
                                        ExpressionDef specificationArg =
                                                ExpressionDef.nullValue().cast(specificationType);
                                        ExpressionDef fetcherArg =
                                                ExpressionDef.nullValue().cast(fetcherType);
                                        ExpressionDef classTypeArg =
                                                ExpressionDef.nullValue().cast(classType);

                                        if (Objects.nonNull(methodParameters)) {
                                            for (VariableDef.MethodParameter param :
                                                    methodParameters) {
                                                if (isTypeCompatible(
                                                        param.type(), paginationType)) {
                                                    pageableArg = param;
                                                } else if (isTypeCompatible(
                                                        param.type(), sortType)) {
                                                    sortArg = param;
                                                } else if (isTypeCompatible(
                                                        param.type(), specificationType)) {
                                                    specificationArg = param;
                                                } else if (isTypeCompatible(
                                                        param.type(), fetcherType)) {
                                                    fetcherArg = param;
                                                } else if (isTypeCompatible(
                                                        param.type(), classType)) {
                                                    classTypeArg = param;
                                                }
                                            }
                                        }

                                        List<VariableDef.MethodParameter> filteredParameters =
                                                methodParameters.stream()
                                                        .filter(
                                                                param ->
                                                                        !isTypeCompatible(
                                                                                param.type(),
                                                                                paginationType))
                                                        .filter(
                                                                param ->
                                                                        !isTypeCompatible(
                                                                                param.type(),
                                                                                sortType))
                                                        .filter(
                                                                param ->
                                                                        !isTypeCompatible(
                                                                                param.type(),
                                                                                specificationType))
                                                        .filter(
                                                                param ->
                                                                        !isTypeCompatible(
                                                                                param.type(),
                                                                                fetcherType))
                                                        .filter(
                                                                param ->
                                                                        !isTypeCompatible(
                                                                                param.type(),
                                                                                classType))
                                                        .toList();

                                        ExpressionDef newObjectArray =
                                                TypeDef.array(TypeDef.OBJECT)
                                                        .instantiate(filteredParameters);
                                        List<ExpressionDef> executeArgs = new ArrayList<>();
                                        executeArgs.add(
                                                new VariableDef.Field(
                                                        aThis.superRef(),
                                                        "sqlClient",
                                                        sqlClientImplementorType));
                                        executeArgs.add(immutableTypeGet);
                                        executeArgs.add(staticFieldVariable);
                                        executeArgs.add(pageableArg);
                                        executeArgs.add(sortArg);
                                        executeArgs.add(specificationArg);
                                        executeArgs.add(fetcherArg);
                                        executeArgs.add(classTypeArg);
                                        executeArgs.add(newObjectArray);
                                        // QueryExecutors.execute()
                                        ExpressionDef executeCall =
                                                queryExecutorsType.invokeStatic(
                                                        "execute",
                                                        TypeDef.of(Object.class),
                                                        executeArgs);
                                        return executeCall
                                                .cast(TypeDef.of(method.getReturnType()))
                                                .returning();
                                    });

            classDefBuilder.addMethod(newMethod);
        }

        // static code start
        classDefBuilder.addStaticInitializer(StatementDef.multi(staticBlockList));
        // static code end
        sourceGenerator.write(classDefBuilder.build(), context, element);
    }

    private boolean isTypeCompatible(TypeDef actualType, TypeDef expectedType) {
        if (actualType.equals(expectedType)) {
            return true;
        }
        if (actualType instanceof ClassTypeDef.Parameterized actualParam
                && expectedType instanceof ClassTypeDef.Parameterized expectedParam) {
            return actualParam.rawType().equals(expectedParam.rawType());
        }
        if (actualType instanceof ClassTypeDef.Parameterized actualParam
                && expectedType instanceof ClassTypeDef expectedClass) {
            return actualParam.rawType().equals(expectedClass);
        }
        if (actualType instanceof ClassTypeDef actualClass
                && expectedType instanceof ClassTypeDef.Parameterized expectedParam) {
            return actualClass.equals(expectedParam.rawType());
        }
        if (actualType instanceof ClassTypeDef actualClass
                && expectedType instanceof ClassTypeDef expectedClass) {
            return actualClass.getName().equals(expectedClass.getName());
        }

        return false;
    }
}
