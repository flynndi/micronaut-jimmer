package io.micronaut.jimmer.repository.sourcegen.generator.kotlin

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toKClassName
import com.squareup.kotlinpoet.javapoet.toKTypeName
import io.micronaut.core.annotation.Nullable
import io.micronaut.core.reflect.ClassUtils
import io.micronaut.inject.visitor.VisitorContext
import io.micronaut.sourcegen.generator.SourceGenerator
import io.micronaut.sourcegen.model.AnnotationDef
import io.micronaut.sourcegen.model.ClassDef
import io.micronaut.sourcegen.model.ClassTypeDef
import io.micronaut.sourcegen.model.EnumDef
import io.micronaut.sourcegen.model.EnumDef.EnumConstantDef
import io.micronaut.sourcegen.model.ExpressionDef
import io.micronaut.sourcegen.model.ExpressionDef.And
import io.micronaut.sourcegen.model.ExpressionDef.Cast
import io.micronaut.sourcegen.model.ExpressionDef.ComparisonOperation
import io.micronaut.sourcegen.model.ExpressionDef.Constant
import io.micronaut.sourcegen.model.ExpressionDef.EqualsReferentially
import io.micronaut.sourcegen.model.ExpressionDef.EqualsStructurally
import io.micronaut.sourcegen.model.ExpressionDef.GetPropertyValue
import io.micronaut.sourcegen.model.ExpressionDef.IfElse
import io.micronaut.sourcegen.model.ExpressionDef.InvokeGetClassMethod
import io.micronaut.sourcegen.model.ExpressionDef.InvokeHashCodeMethod
import io.micronaut.sourcegen.model.ExpressionDef.InvokeInstanceMethod
import io.micronaut.sourcegen.model.ExpressionDef.InvokeStaticMethod
import io.micronaut.sourcegen.model.ExpressionDef.IsFalse
import io.micronaut.sourcegen.model.ExpressionDef.IsNotNull
import io.micronaut.sourcegen.model.ExpressionDef.IsNull
import io.micronaut.sourcegen.model.ExpressionDef.IsTrue
import io.micronaut.sourcegen.model.ExpressionDef.MathBinaryOperation
import io.micronaut.sourcegen.model.ExpressionDef.MathUnaryOperation
import io.micronaut.sourcegen.model.ExpressionDef.NewArrayInitialized
import io.micronaut.sourcegen.model.ExpressionDef.NewArrayOfSize
import io.micronaut.sourcegen.model.ExpressionDef.NewInstance
import io.micronaut.sourcegen.model.ExpressionDef.NotEqualsReferentially
import io.micronaut.sourcegen.model.ExpressionDef.NotEqualsStructurally
import io.micronaut.sourcegen.model.ExpressionDef.Or
import io.micronaut.sourcegen.model.ExpressionDef.Switch
import io.micronaut.sourcegen.model.ExpressionDef.SwitchYieldCase
import io.micronaut.sourcegen.model.FieldDef
import io.micronaut.sourcegen.model.InterfaceDef
import io.micronaut.sourcegen.model.MethodDef
import io.micronaut.sourcegen.model.ObjectDef
import io.micronaut.sourcegen.model.ParameterDef
import io.micronaut.sourcegen.model.PropertyDef
import io.micronaut.sourcegen.model.RecordDef
import io.micronaut.sourcegen.model.StatementDef
import io.micronaut.sourcegen.model.StatementDef.Assign
import io.micronaut.sourcegen.model.StatementDef.DefineAndAssign
import io.micronaut.sourcegen.model.StatementDef.PutField
import io.micronaut.sourcegen.model.StatementDef.Return
import io.micronaut.sourcegen.model.TypeDef
import io.micronaut.sourcegen.model.VariableDef
import java.io.IOException
import java.io.Writer
import java.lang.reflect.Array
import java.util.function.Consumer
import javax.lang.model.element.Modifier
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.iterator
import kotlin.reflect.KClass

class KotlinPoetSourceGenerator : SourceGenerator {
    override fun getLanguage(): VisitorContext.Language = VisitorContext.Language.KOTLIN

    @Throws(IOException::class)
    override fun write(
        objectDef: ObjectDef,
        writer: Writer,
    ) {
        when (objectDef) {
            is ClassDef -> {
                writeClass(writer, objectDef)
            }

            is RecordDef -> {
                writeRecordDef(writer, objectDef)
            }

            is InterfaceDef -> {
                writeInterface(writer, objectDef)
            }

            is EnumDef -> {
                writeEnumDef(writer, objectDef)
            }

            else -> {
                throw IllegalStateException("Unknown object definition: $objectDef")
            }
        }
    }

    @Throws(IOException::class)
    private fun writeInterface(
        writer: Writer,
        interfaceDef: InterfaceDef,
    ) {
        val interfaceBuilder = getInterfaceBuilder(interfaceDef)
        FileSpec
            .builder(interfaceDef.packageName, interfaceDef.simpleName + ".kt")
            .addType(interfaceBuilder.build())
            .build()
            .writeTo(writer)
    }

    private fun getInterfaceBuilder(interfaceDef: InterfaceDef): TypeSpec.Builder {
        val interfaceBuilder = TypeSpec.interfaceBuilder(interfaceDef.simpleName)
        if (interfaceDef.annotations.any { it.type.name.equals(FunctionalInterface::class.qualifiedName) }) {
            interfaceBuilder.addModifiers(KModifier.FUN)
        }
        interfaceBuilder.addModifiers(asKModifiers(interfaceDef.modifiers))
        interfaceDef.typeVariables
            .stream()
            .map { tv: TypeDef.TypeVariable -> asTypeVariable(tv, interfaceDef) }
            .forEach { typeVariable: TypeVariableName -> interfaceBuilder.addTypeVariable(typeVariable) }
        interfaceDef.superinterfaces
            .stream()
            .map { typeDef: TypeDef -> asType(typeDef, interfaceDef) }
            .forEach { it: TypeName ->
                interfaceBuilder.addSuperinterface(
                    it,
                )
            }
        interfaceDef.javadoc.forEach(Consumer { format: String -> interfaceBuilder.addKdoc(format) })
        interfaceDef.annotations
            .stream()
            .map { annotationDef: AnnotationDef -> asAnnotationSpec(annotationDef) }
            .forEach { annotationSpec: AnnotationSpec -> interfaceBuilder.addAnnotation(annotationSpec) }

        var companionBuilder: TypeSpec.Builder? = null
        for (property in interfaceDef.properties) {
            val propertySpec =
                if (property.type.isNullable) {
                    buildProperty(
                        property.name,
                        property.type.makeNullable(),
                        property.modifiers,
                        property.annotations,
                        property.javadoc,
                        null,
                        interfaceDef,
                    )
                } else {
                    buildConstructorProperty(
                        property.name,
                        property.type,
                        property.modifiers,
                        property.annotations,
                        property.javadoc,
                        interfaceDef,
                    )
                }
            interfaceBuilder.addProperty(
                propertySpec,
            )
        }
        for (method in interfaceDef.methods) {
            var modifiers = method.modifiers
            if (modifiers.contains(Modifier.STATIC)) {
                if (companionBuilder == null) {
                    companionBuilder = TypeSpec.companionObjectBuilder()
                }
                modifiers = stripStatic(modifiers)
                companionBuilder.addFunction(
                    buildFunction(null, method, modifiers),
                )
            } else {
                interfaceBuilder.addFunction(
                    buildFunction(interfaceDef, method, modifiers),
                )
            }
        }
        if (companionBuilder != null) {
            interfaceBuilder.addType(companionBuilder.build())
        }
        addInnerTypes(interfaceDef.innerTypes, interfaceBuilder, isInterface = true)
        return interfaceBuilder
    }

    @Throws(IOException::class)
    private fun writeClass(
        writer: Writer,
        classDef: ClassDef,
    ) {
        val classBuilder = getClassBuilder(classDef)
        FileSpec
            .builder(classDef.packageName, classDef.simpleName + ".kt")
            .addType(classBuilder.build())
            .build()
            .writeTo(writer)
    }

    private fun getClassBuilder(classDef: ClassDef): TypeSpec.Builder {
        val classBuilder = TypeSpec.classBuilder(classDef.simpleName)
        classBuilder.addModifiers(asKModifiers(classDef.modifiers))
        classDef.typeVariables
            .stream()
            .map { tv: TypeDef.TypeVariable -> asTypeVariable(tv, classDef) }
            .forEach { typeVariable: TypeVariableName -> classBuilder.addTypeVariable(typeVariable) }
        classDef.superinterfaces
            .stream()
            .map { typeDef: TypeDef -> asType(typeDef, classDef) }
            .forEach { it: TypeName ->
                classBuilder.addSuperinterface(
                    it,
                )
            }
        classDef.javadoc.forEach(Consumer { format: String -> classBuilder.addKdoc(format) })
        if (classDef.superclass != null) {
            classBuilder.superclass(asType(classDef.superclass, classDef))
        }
        classDef.annotations
            .stream()
            .map { annotationDef: AnnotationDef -> asAnnotationSpec(annotationDef) }
            .forEach { annotationSpec: AnnotationSpec -> classBuilder.addAnnotation(annotationSpec) }

        var companionBuilder: TypeSpec.Builder? = null
        buildProperties(classDef, classBuilder)
        companionBuilder = buildFields(classDef, companionBuilder, classBuilder)

        classDef.staticInitializer?.let { staticInitializerDef ->
            val currentCompanion =
                companionBuilder ?: TypeSpec.companionObjectBuilder().also {
                    companionBuilder = it
                }
            currentCompanion.addInitializerBlock(
                renderStatementCodeBlock(classDef, MethodDef.builder("<clinit>").build(), staticInitializerDef),
            )
        }

        for (method in classDef.methods) {
            var modifiers = method.modifiers
            if (modifiers.contains(Modifier.STATIC)) {
                val currentCompanion =
                    companionBuilder ?: TypeSpec.companionObjectBuilder().also {
                        companionBuilder = it
                    }
                modifiers = stripStatic(modifiers)
                currentCompanion.addFunction(
                    buildFunction(null, method, modifiers),
                )
            } else if (method.name == "<init>") {
                val superCallStatement =
                    method.statements.firstOrNull {
                        it is InvokeInstanceMethod && it.instance is VariableDef.Super && it.method.name == "<init>"
                    } as? InvokeInstanceMethod
                if (superCallStatement != null) {
                    val superArgsCodeBlock = CodeBlock.builder()
                    for ((index, arg) in superCallStatement.values.withIndex()) {
                        superArgsCodeBlock.add(renderExpressionCode(classDef, method, arg))
                        if (index < superCallStatement.values.size - 1) {
                            superArgsCodeBlock.add(", ")
                        }
                    }
                    val constructorFunSpecBuilder =
                        FunSpec
                            .constructorBuilder()
                            .addModifiers(asKModifiers(method, modifiers))
                            .addParameters(
                                method.parameters
                                    .stream()
                                    .map { param: ParameterDef ->
                                        val paramBuilder =
                                            ParameterSpec.builder(
                                                param.name,
                                                asType(param.type, classDef),
                                            )
                                        param.annotations.forEach { annotation ->
                                            paramBuilder.addAnnotation(asAnnotationSpec(annotation))
                                        }

                                        paramBuilder.build()
                                    }.toList(),
                            )
                    classBuilder.superclassConstructorParameters.add(superArgsCodeBlock.build())
                    classBuilder.primaryConstructor(constructorFunSpecBuilder.build())
                } else {
                    classBuilder.addFunction(
                        buildFunction(classDef, method, modifiers),
                    )
                }
            } else {
                classBuilder.addFunction(
                    buildFunction(classDef, method, modifiers),
                )
            }
        }
        companionBuilder?.let {
            classBuilder.addType(it.build())
        }
        addInnerTypes(classDef.innerTypes, classBuilder)
        return classBuilder
    }

    @Throws(IOException::class)
    private fun writeRecordDef(
        writer: Writer,
        recordDef: RecordDef,
    ) {
        val classBuilder = getRecordBuilder(recordDef)
        FileSpec
            .builder(recordDef.packageName, recordDef.simpleName + ".kt")
            .addType(classBuilder.build())
            .build()
            .writeTo(writer)
    }

    private fun getRecordBuilder(recordDef: RecordDef): TypeSpec.Builder {
        val classBuilder = TypeSpec.classBuilder(recordDef.simpleName)
        classBuilder.addModifiers(KModifier.DATA)
        classBuilder.addModifiers(asKModifiers(recordDef.modifiers))
        recordDef.typeVariables
            .stream()
            .map { tv: TypeDef.TypeVariable -> asTypeVariable(tv, recordDef) }
            .forEach { typeVariable: TypeVariableName -> classBuilder.addTypeVariable(typeVariable) }
        recordDef.superinterfaces
            .stream()
            .map { typeDef: TypeDef -> asType(typeDef, recordDef) }
            .forEach { it: TypeName ->
                classBuilder.addSuperinterface(
                    it,
                )
            }
        recordDef.javadoc.forEach(Consumer { format: String -> classBuilder.addKdoc(format) })
        recordDef.annotations
            .stream()
            .map { annotationDef: AnnotationDef -> asAnnotationSpec(annotationDef) }
            .forEach { annotationSpec: AnnotationSpec -> classBuilder.addAnnotation(annotationSpec) }

        var companionBuilder: TypeSpec.Builder? = null
        val constructorProperties: MutableList<PropertyDef> = ArrayList()
        for (property in recordDef.properties) {
            constructorProperties.add(property)
            classBuilder.addProperty(
                buildConstructorProperty(
                    property.name,
                    property.type,
                    extendModifiers(property.modifiers, Modifier.FINAL),
                    property.annotations,
                    property.javadoc,
                    recordDef,
                ),
            )
        }
        if (constructorProperties.isNotEmpty()) {
            classBuilder.primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addModifiers(KModifier.PUBLIC)
                    .addParameters(
                        constructorProperties
                            .stream()
                            .map { prop: PropertyDef ->
                                ParameterSpec
                                    .builder(
                                        prop.name,
                                        asType(prop.type, recordDef),
                                    ).build()
                            }.toList(),
                    ).build(),
            )
        }

        for (method in recordDef.methods) {
            var modifiers = method.modifiers
            if (modifiers.contains(Modifier.STATIC)) {
                if (companionBuilder == null) {
                    companionBuilder = TypeSpec.companionObjectBuilder()
                }
                modifiers = stripStatic(modifiers)
                companionBuilder.addFunction(
                    buildFunction(null, method, modifiers),
                )
            } else {
                classBuilder.addFunction(
                    buildFunction(recordDef, method, modifiers),
                )
            }
        }
        if (companionBuilder != null) {
            classBuilder.addType(companionBuilder.build())
        }
        addInnerTypes(recordDef.innerTypes, classBuilder)
        return classBuilder
    }

    @Throws(IOException::class)
    private fun writeEnumDef(
        writer: Writer,
        enumDef: EnumDef,
    ) {
        val enumBuilder = getEnumBuilder(enumDef)
        FileSpec
            .builder(enumDef.packageName, enumDef.simpleName + ".kt")
            .addType(enumBuilder.build())
            .build()
            .writeTo(writer)
    }

    private fun getEnumBuilder(enumDef: EnumDef): TypeSpec.Builder {
        val enumBuilder = TypeSpec.enumBuilder(enumDef.simpleName)
        enumBuilder.addModifiers(asKModifiers(enumDef.modifiers))
        enumDef.superinterfaces
            .stream()
            .map { typeDef: TypeDef -> asType(typeDef, enumDef) }
            .forEach { it: TypeName -> enumBuilder.addSuperinterface(it) }
        enumDef.javadoc.forEach(Consumer { format: String -> enumBuilder.addKdoc(format) })
        enumDef.annotations
            .stream()
            .map { annotationDef: AnnotationDef -> asAnnotationSpec(annotationDef) }
            .forEach { annotationSpec: AnnotationSpec -> enumBuilder.addAnnotation(annotationSpec) }

        enumDef.enumConstants.forEach { enumConstant: EnumConstantDef ->
            if (enumConstant.constructorArgs != null && enumConstant.constructorArgs.isNotEmpty()) {
                val exps = enumConstant.constructorArgs
                val expBuilder: CodeBlock.Builder = CodeBlock.builder()
                for (i in exps.indices) {
                    expBuilder.add(
                        renderExpressionCode(
                            null,
                            MethodDef.builder("").returns(TypeDef.VOID).build(),
                            exps[i],
                        ),
                    )
                    if (i < exps.size - 1) {
                        expBuilder.add(", ")
                    }
                }
                enumBuilder.addEnumConstant(
                    enumConstant.name,
                    TypeSpec
                        .companionObjectBuilder()
                        .addSuperclassConstructorParameter(expBuilder.build())
                        .build(),
                )
            } else {
                enumBuilder.addEnumConstant(enumConstant.name)
            }
        }

        var companionBuilder: TypeSpec.Builder? = null
        buildProperties(enumDef, enumBuilder)
        companionBuilder = buildFields(enumDef, companionBuilder, enumBuilder)

        for (method in enumDef.methods) {
            var modifiers = method.modifiers
            if (modifiers.contains(Modifier.STATIC)) {
                if (companionBuilder == null) {
                    companionBuilder = TypeSpec.companionObjectBuilder()
                }
                modifiers = stripStatic(modifiers)
                companionBuilder.addFunction(
                    buildFunction(null, method, modifiers),
                )
            } else {
                enumBuilder.addFunction(
                    buildFunction(enumDef, method, modifiers),
                )
            }
        }
        if (companionBuilder != null) {
            enumBuilder.addType(companionBuilder.build())
        }
        addInnerTypes(enumDef.innerTypes, enumBuilder)
        return enumBuilder
    }

    fun addInnerTypes(
        objectDefs: List<ObjectDef>,
        classBuilder: TypeSpec.Builder,
        isInterface: Boolean = false,
    ) {
        for (objectDef in objectDefs) {
            var innerBuilder: TypeSpec.Builder
            when (objectDef) {
                is ClassDef -> {
                    innerBuilder = getClassBuilder(objectDef)
                }

                is RecordDef -> {
                    innerBuilder = getRecordBuilder(objectDef)
                }

                is InterfaceDef -> {
                    innerBuilder = getInterfaceBuilder(objectDef)
                }

                is EnumDef -> {
                    innerBuilder = getEnumBuilder(objectDef)
                }

                else -> {
                    throw IllegalStateException("Unknown object definition: $objectDef")
                }
            }
            if (isInterface) {
                innerBuilder.addModifiers(KModifier.PUBLIC)
            }
            classBuilder.addType(innerBuilder.build())
        }
    }

    private fun buildProperties(
        objectDef: ObjectDef,
        builder: TypeSpec.Builder,
    ) {
        val notNullProperties: MutableList<PropertyDef> = ArrayList()
        for (property in objectDef.properties) {
            var propertySpec: PropertySpec
            if (property.type.isNullable) {
                propertySpec =
                    buildProperty(
                        property.name,
                        property.type.makeNullable(),
                        property.modifiers,
                        property.annotations,
                        property.javadoc,
                        null,
                        objectDef,
                    )
            } else {
                propertySpec =
                    buildConstructorProperty(
                        property.name,
                        property.type,
                        property.modifiers,
                        property.annotations,
                        property.javadoc,
                        objectDef,
                    )
                notNullProperties.add(property)
            }
            builder.addProperty(
                propertySpec,
            )
        }
        if (notNullProperties.isNotEmpty()) {
            builder.primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addModifiers(KModifier.PUBLIC)
                    .addParameters(
                        notNullProperties
                            .stream()
                            .map { prop: PropertyDef ->
                                ParameterSpec
                                    .builder(
                                        prop.name,
                                        asType(prop.type, objectDef),
                                    ).build()
                            }.toList(),
                    ).build(),
            )
        }
    }

    private fun buildFields(
        objectDef: ObjectDef,
        companionBuilder: TypeSpec.Builder?,
        builder: TypeSpec.Builder,
    ): TypeSpec.Builder? {
        var companionBuilderTmp = companionBuilder
        var fields: List<FieldDef>
        if (objectDef is ClassDef) {
            fields = objectDef.fields
        } else if (objectDef is EnumDef) {
            fields = objectDef.fields
        } else {
            return builder
        }

        for (field in fields) {
            val modifiers = field.modifiers
            if (modifiers.contains(Modifier.STATIC)) {
                if (companionBuilderTmp == null) {
                    companionBuilderTmp = TypeSpec.companionObjectBuilder()
                }
                companionBuilderTmp.addProperty(
                    buildProperty(field, stripStatic(modifiers), field.javadoc, objectDef),
                )
            } else {
                if (field.type.isNullable) {
                    builder.addProperty(
                        buildProperty(field, modifiers, field.javadoc, objectDef),
                    )
                } else {
                    builder.addProperty(
                        buildProperty(field, modifiers, field.javadoc, objectDef),
                    )
                }
            }
        }
        return companionBuilderTmp
    }

    private fun buildProperty(
        name: String,
        typeDef: TypeDef,
        modifiers: Set<Modifier>,
        annotations: List<AnnotationDef>,
        docs: List<String>,
        initializer: ExpressionDef?,
        objectDef: ObjectDef?,
    ): PropertySpec {
        val propertyBuilder =
            PropertySpec.builder(
                name,
                asType(typeDef, objectDef),
                asKModifiers(modifiers),
            )
        docs.forEach(Consumer { format: String -> propertyBuilder.addKdoc(format) })

        if (!modifiers.contains(Modifier.FINAL)) {
            propertyBuilder.mutable(true)
        }
        for (annotation in annotations) {
            propertyBuilder.addAnnotation(
                asAnnotationSpec(annotation),
            )
        }
        if (initializer != null) {
            if (initializer is Constant) {
                propertyBuilder.initializer(
                    CodeBlock.of(
                        "%L",
                        initializer.value,
                    ),
                )
            }
        }
        if (typeDef.isNullable) {
            propertyBuilder.initializer("null")
        }
        return propertyBuilder.build()
    }

    private fun buildConstructorProperty(
        name: String,
        typeDef: TypeDef,
        modifiers: Set<Modifier>,
        annotations: List<AnnotationDef>,
        docs: List<String>,
        objectDef: ObjectDef?,
    ): PropertySpec {
        val propertyBuilder =
            PropertySpec.builder(
                name,
                asType(typeDef, objectDef),
                asKModifiers(modifiers),
            )
        docs.forEach(Consumer { format: String -> propertyBuilder.addKdoc(format) })
        if (!modifiers.contains(Modifier.FINAL)) {
            propertyBuilder.mutable(true)
        }
        for (annotation in annotations) {
            propertyBuilder.addAnnotation(
                asAnnotationSpec(annotation),
            )
        }
        return propertyBuilder
            .initializer(name)
            .build()
    }

    private fun buildProperty(
        field: FieldDef,
        modifiers: Set<Modifier>,
        docs: List<String>,
        objectDef: ObjectDef?,
    ): PropertySpec =
        buildProperty(
            field.name,
            field.type,
            modifiers,
            field.annotations,
            docs,
            field.initializer.orElse(null),
            objectDef,
        )

    private fun buildFunction(
        objectDef: ObjectDef?,
        method: MethodDef,
        modifiers: Set<Modifier>,
    ): FunSpec {
        var funBuilder =
            if (method.name == "<init>") {
                FunSpec.constructorBuilder()
            } else {
                FunSpec.builder(method.name).returns(asType(method.returnType, objectDef))
            }
        funBuilder =
            funBuilder
                .addModifiers(asKModifiers(method, modifiers))
                .addParameters(
                    method.parameters
                        .stream()
                        .map { param: ParameterDef ->
                            val paramBuilder =
                                ParameterSpec.builder(
                                    param.name,
                                    asType(param.type, objectDef),
                                )
                            param.annotations.forEach { annotation ->
                                paramBuilder.addAnnotation(asAnnotationSpec(annotation))
                            }
                            paramBuilder.build()
                        }.toList(),
                )
        if (method.isOverride) {
            funBuilder.modifiers += KModifier.OVERRIDE
        }
        for (annotation in method.annotations) {
            funBuilder.addAnnotation(
                asAnnotationSpec(annotation),
            )
        }
        if (method.throwTypes.isNotEmpty()) {
            funBuilder.addAnnotation(
                AnnotationSpec
                    .builder(Throws::class)
                    .addMember(
                        method.throwTypes.joinToString { "%T::class" },
                        *method.throwTypes.map { asType(it, objectDef) }.toTypedArray(),
                    ).build(),
            )
        }
        method.statements
            .stream()
            .map { st: StatementDef -> renderStatementCodeBlock(objectDef, method, st) }
            .forEach(funBuilder::addCode)
        method.javadoc.forEach(Consumer { format: String -> funBuilder.addKdoc(format) })
        return funBuilder.build()
    }

    companion object {
        private fun stripStatic(modifiers: MutableSet<Modifier>): MutableSet<Modifier> {
            val mutable = HashSet(modifiers)
            mutable.remove(Modifier.STATIC)
            return mutable
        }

        private fun extendModifiers(
            modifiers: MutableSet<Modifier>,
            modifier: Modifier,
        ): Set<Modifier> {
            if (modifiers.contains(modifier)) {
                return modifiers
            }
            val mutable = HashSet(modifiers)
            mutable.add(modifier)
            return mutable
        }

        @OptIn(KotlinPoetJavaPoetPreview::class)
        private fun asClassName(classType: ClassTypeDef): ClassName {
            val packageName = classType.packageName
            var simpleName = classType.simpleName
            if (classType.isEnum) {
                simpleName = simpleName.substringAfter("$")
            }
            val result: ClassName =
                com.squareup.javapoet.ClassName
                    .get(packageName, simpleName)
                    .toKClassName()
            if (result.isNullable) {
                return asNullable(result) as ClassName
            }
            return result
        }

        private fun asNullable(kClassName: TypeName): TypeName = kClassName.copy(true, kClassName.annotations, kClassName.tags)

        private fun asKModifiers(
            methodDef: MethodDef,
            modifier: Collection<Modifier>,
        ): List<KModifier> {
            val modifiers = asKModifiers(modifier)
            if (methodDef.isOverride) {
                val mutableList = modifiers.toMutableList()
                mutableList.add(KModifier.OVERRIDE)
                return mutableList
            }
            return modifiers
        }

        private fun asKModifiers(modifier: Collection<Modifier>): List<KModifier> =
            modifier
                .stream()
                .map { m: Modifier ->
                    when (m) {
                        Modifier.PUBLIC -> KModifier.PUBLIC
                        Modifier.PROTECTED -> KModifier.PROTECTED
                        Modifier.PRIVATE -> KModifier.PRIVATE
                        Modifier.ABSTRACT -> KModifier.ABSTRACT
                        Modifier.SEALED -> KModifier.SEALED
                        Modifier.FINAL -> KModifier.FINAL
                        else -> throw IllegalStateException("Not supported modifier: $m")
                    }
                }.toList()

        @OptIn(KotlinPoetJavaPoetPreview::class)
        private fun asType(
            typeDef: TypeDef,
            objectDef: ObjectDef?,
        ): TypeName = asType(typeDef, objectDef, null)

        @OptIn(KotlinPoetJavaPoetPreview::class)
        private fun asType(
            typeDef: TypeDef,
            objectDef: ObjectDef?,
            methodDef: MethodDef?,
        ): TypeName {
            val result: TypeName =
                if (typeDef == TypeDef.THIS) {
                    if (objectDef == null) {
                        throw java.lang.IllegalStateException("This type is used outside of the instance scope!")
                    }
                    asType(objectDef.asTypeDef(), null)
                } else if (typeDef is TypeDef.Array) {
                    asArray(typeDef, objectDef)
                } else if (typeDef is ClassTypeDef.Parameterized) {
                    asClassName(typeDef.rawType).parameterizedBy(
                        typeDef.typeArguments.map { v: TypeDef -> this.asType(v, objectDef) },
                    )
                } else if (typeDef is TypeDef.Primitive) {
                    when (typeDef.name()) {
                        "void" -> UNIT
                        "byte" ->
                            com.squareup.javapoet.TypeName.BYTE
                                .toKTypeName()
                        "short" ->
                            com.squareup.javapoet.TypeName.SHORT
                                .toKTypeName()
                        "char" ->
                            com.squareup.javapoet.TypeName.CHAR
                                .toKTypeName()
                        "int" ->
                            com.squareup.javapoet.TypeName.INT
                                .toKTypeName()
                        "long" ->
                            com.squareup.javapoet.TypeName.LONG
                                .toKTypeName()
                        "float" ->
                            com.squareup.javapoet.TypeName.FLOAT
                                .toKTypeName()
                        "double" ->
                            com.squareup.javapoet.TypeName.DOUBLE
                                .toKTypeName()
                        "boolean" ->
                            com.squareup.javapoet.TypeName.BOOLEAN
                                .toKTypeName()
                        else -> throw IllegalStateException("Unrecognized primitive name: " + typeDef.name())
                    }
                } else if (typeDef is ClassTypeDef) {
                    asClassName(typeDef)
                } else if (typeDef is ClassTypeDef.AnnotatedClassTypeDef) {
                    asType(typeDef.typeDef, objectDef).copy(
                        typeDef.typeDef.isNullable,
                        typeDef.annotations
                            .stream()
                            .map { asAnnotationSpec(it) }
                            .toList(),
                    )
                } else if (typeDef is TypeDef.Wildcard) {
                    if (typeDef.lowerBounds.isNotEmpty()) {
                        WildcardTypeName.consumerOf(
                            asType(
                                typeDef.lowerBounds[0],
                                objectDef,
                            ),
                        )
                    } else {
                        WildcardTypeName.producerOf(
                            asType(
                                typeDef.upperBounds[0],
                                objectDef,
                            ),
                        )
                    }
                } else if (typeDef is TypeDef.TypeVariable) {
                    if (isVariablePartOfTheDefinition(typeDef.name, objectDef, methodDef)) {
                        return asTypeVariable(typeDef, objectDef)
                    }
                    if (typeDef.bounds.isEmpty()) {
                        return asType(TypeDef.OBJECT, objectDef)
                    }
                    return asType(typeDef.bounds.get(0), objectDef)
                } else if (typeDef is TypeDef.Annotated && typeDef is TypeDef.AnnotatedTypeDef) {
                    return asType(typeDef.typeDef, objectDef).copy(
                        typeDef.typeDef.isNullable,
                        typeDef.annotations
                            .stream()
                            .map { asAnnotationSpec(it) }
                            .toList(),
                    )
                } else {
                    throw IllegalStateException("Unrecognized type definition $typeDef")
                }
            if (typeDef.isNullable) {
                return asNullable(result)
            }
            return result
        }

        private fun isVariablePartOfTheDefinition(
            variableName: String,
            objectDef: ObjectDef?,
            methodDef: MethodDef?,
        ): Boolean {
            if (methodDef != null &&
                methodDef.typeVariables.stream().anyMatch { v: TypeDef.TypeVariable -> v.name == variableName }
            ) {
                return true
            }
            if (objectDef != null) {
                if (objectDef is ClassDef) {
                    return objectDef.typeVariables
                        .stream()
                        .anyMatch { tv: TypeDef.TypeVariable -> tv.name == variableName }
                }
                if (objectDef is InterfaceDef) {
                    return objectDef.typeVariables
                        .stream()
                        .anyMatch { tv: TypeDef.TypeVariable -> tv.name == variableName }
                }
            }
            return false
        }

        private fun asTypeVariable(
            tv: TypeDef.TypeVariable,
            objectDef: ObjectDef?,
        ): TypeVariableName =
            TypeVariableName(
                tv.name,
                tv.bounds
                    .stream()
                    .map { v: TypeDef -> asType(v, objectDef) }
                    .toList(),
            )

        private fun asArray(
            classType: TypeDef.Array,
            objectDef: ObjectDef?,
        ): TypeName {
            var newDef =
                ClassTypeDef.Parameterized(
                    ClassTypeDef.of("kotlin.Array"),
                    listOf(classType.componentType),
                )
            for (i in 2..classType.dimensions) {
                newDef = ClassTypeDef.Parameterized(ClassTypeDef.of("kotlin.Array"), listOf(newDef))
            }
            return asType(newDef, objectDef)
        }

        private fun renderStatementCodeBlock(
            objectDef: @Nullable ObjectDef?,
            methodDef: MethodDef,
            statementDef: StatementDef,
        ): CodeBlock {
            if (statementDef is StatementDef.Multi) {
                val builder: CodeBlock.Builder =
                    CodeBlock.builder()
                for (statement in statementDef.statements) {
                    builder.add(renderStatementCodeBlock(objectDef, methodDef, statement))
                }
                return builder.build()
            }
            if (statementDef is StatementDef.If) {
                val builder: CodeBlock.Builder =
                    CodeBlock.builder()
                builder.add("if (")
                builder.add(renderExpressionCode(objectDef, methodDef, statementDef.condition))
                builder.add(") {\n")
                builder.indent()
                builder.add(renderStatementCodeBlock(objectDef, methodDef, statementDef.statement))
                builder.unindent()
                builder.add("}\n")
                return builder.build()
            }
            if (statementDef is StatementDef.IfElse) {
                val builder: CodeBlock.Builder = CodeBlock.builder()
                builder.add("if (")
                builder.add(renderExpressionCode(objectDef, methodDef, statementDef.condition))
                builder.add(") {\n")
                builder.indent()
                builder.add(renderStatementCodeBlock(objectDef, methodDef, statementDef.statement))
                builder.unindent()
                builder.add("} else {\n")
                builder.indent()
                builder.add(renderStatementCodeBlock(objectDef, methodDef, statementDef.elseStatement))
                builder.unindent()
                builder.add("}\n")
                return builder.build()
            }
            if (statementDef is StatementDef.Switch) {
                val builder: CodeBlock.Builder =
                    CodeBlock.builder()
                builder.add("when (")
                builder.add(renderExpressionCode(objectDef, methodDef, statementDef.expression))
                builder.add(") {\n")
                builder.indent()
                for ((key, statement) in statementDef.cases) {
                    builder.add(renderConstantExpression(key, methodDef))
                    builder.add("-> {\n")
                    builder.indent()
                    builder.add(renderStatementCodeBlock(objectDef, methodDef, statement))
                    builder.unindent()
                    builder.add("}\n")
                }
                if (statementDef.defaultCase != null) {
                    builder.add("else -> {\n")
                    builder.indent()
                    builder.add(renderStatementCodeBlock(objectDef, methodDef, statementDef.defaultCase))
                    builder.unindent()
                    builder.add("}\n")
                }
                builder.unindent()
                builder.add("}\n")
                return builder.build()
            }
            if (statementDef is StatementDef.While) {
                val builder: CodeBlock.Builder =
                    CodeBlock.builder()
                builder.add("while (")
                builder.add(renderExpressionCode(objectDef, methodDef, statementDef.expression))
                builder.add(") {\n")
                builder.indent()
                builder.add(renderStatementCodeBlock(objectDef, methodDef, statementDef.statement))
                builder.unindent()
                builder.add("}\n")
                return builder.build()
            }
            return CodeBlock
                .builder()
                .addStatement("%L", renderStatement(objectDef, methodDef, statementDef))
                .build()
        }

        private fun renderStatement(
            objectDef: ObjectDef?,
            methodDef: MethodDef,
            statementDef: StatementDef,
        ): CodeBlock {
            if (statementDef is StatementDef.Throw) {
                return CodeBlock
                    .builder()
                    .add("throw ")
                    .add(renderExpressionCode(objectDef, methodDef, statementDef.expression))
                    .build()
            }
            if (statementDef is StatementDef.Return) {
                val codeBlock =
                    renderExpressionWithNotNullAssertion(
                        objectDef,
                        methodDef,
                        statementDef.expression,
                        methodDef.returnType,
                    )
                return CodeBlock
                    .builder()
                    .add("return ")
                    .add(codeBlock)
                    .build()
            }
            if (statementDef is PutField) {
                val field = statementDef.field
                val variableExp = renderVariable(objectDef, methodDef, field)
                val codeBuilder = variableExp.toBuilder()
                codeBuilder.add(" = ")
                codeBuilder.add(
                    renderExpressionCode(
                        objectDef,
                        methodDef,
                        statementDef.expression,
                        field.type(),
                    ),
                )
                return codeBuilder.build()
            }
            if (statementDef is Assign) {
                val variableExp = renderVariable(objectDef, methodDef, statementDef.variable)
                val codeBuilder = variableExp.toBuilder()
                codeBuilder.add(" = ")
                codeBuilder.add(
                    renderExpressionCode(
                        objectDef,
                        methodDef,
                        statementDef.expression,
                        statementDef.variable.type(),
                    ),
                )
                return codeBuilder.build()
            }
            if (statementDef is DefineAndAssign) {
                return CodeBlock
                    .builder()
                    .add("var %L:%T", statementDef.variable.name, asType(statementDef.variable.type, objectDef))
                    .add(" = ")
                    .add(
                        renderExpressionCode(
                            objectDef,
                            methodDef,
                            statementDef.expression,
                            statementDef.variable.type,
                        ),
                    ).build()
            }
            if (statementDef is StatementDef.PutStaticField) {
                val field = statementDef.field
                val variableExp = renderVariable(objectDef, methodDef, field)
                val codeBuilder = variableExp.toBuilder()
                codeBuilder.add(" = ")
                codeBuilder.add(
                    renderExpressionCode(
                        objectDef,
                        methodDef,
                        statementDef.expression,
                        field.type,
                    ),
                )
                return codeBuilder.build()
            }
            if (statementDef is ExpressionDef) {
                return renderExpressionCode(objectDef, methodDef, statementDef)
            }

            throw IllegalStateException("Unrecognized statement: $statementDef")
        }

        private fun renderYield(
            builder: CodeBlock.Builder,
            methodDef: MethodDef,
            statementDef: StatementDef,
            objectDef: ObjectDef?,
        ) {
            if (statementDef is StatementDef.Return) {
                builder.addStatement(
                    "%L",
                    CodeBlock
                        .builder()
                        .add("return ")
                        .add(renderExpressionCode(objectDef, methodDef, statementDef.expression, methodDef.returnType))
                        .build(),
                )
            } else {
                throw java.lang.IllegalStateException("The last statement of SwitchYieldCase should be a return. Found: $statementDef")
            }
        }

        private fun renderExpressionCode(
            objectDef: ObjectDef?,
            methodDef: MethodDef,
            expressionDef: ExpressionDef,
            expectedType: TypeDef,
        ): CodeBlock {
            val codeBlock = renderExpressionCode(objectDef, methodDef, expressionDef)
            val builder = codeBlock.toBuilder()
            if (!expectedType.isNullable && expressionDef.type().isNullable) {
                builder.add("!!")
            }
            return builder.build()
        }

        private fun renderExpressionCode(
            objectDef: ObjectDef?,
            methodDef: MethodDef,
            expressionDef: ExpressionDef,
        ): CodeBlock {
            if (expressionDef is NewInstance) {
                val codeBuilder = CodeBlock.builder()
                codeBuilder.add("%T(", asClassName(expressionDef.type))
                for ((index, parameter) in expressionDef.values.withIndex()) {
                    codeBuilder.add(renderExpressionCode(objectDef, methodDef, parameter))
                    if (index != expressionDef.values.size - 1) {
                        codeBuilder.add(", ")
                    }
                }
                codeBuilder.add(")")
                return codeBuilder.build()
            }
            if (expressionDef is InvokeInstanceMethod) {
                val instanceExp = renderExpressionCode(objectDef, methodDef, expressionDef.instance)
                val codeBuilder = CodeBlock.builder()
                if (expressionDef.method.name == "<init>") {
                    codeBuilder.add(instanceExp)
                    codeBuilder.add("(")
                } else {
                    codeBuilder.add(instanceExp)
                    if (expressionDef.instance is InvokeInstanceMethod) {
                        codeBuilder.add("\n")
                    }
                    codeBuilder.add(".%N(", expressionDef.method.name)
                }
                for ((index, parameter) in expressionDef.values.withIndex()) {
                    codeBuilder.add(renderExpressionCode(objectDef, methodDef, parameter))
                    if (index != expressionDef.values.size - 1) {
                        codeBuilder.add(", ")
                    }
                }
                codeBuilder.add(")")
                return codeBuilder.build()
            }
            if (expressionDef is GetPropertyValue) {
                val instanceExp = renderExpressionCode(objectDef, methodDef, expressionDef.instance)
                val codeBuilder = instanceExp.toBuilder()
                codeBuilder.add(".%L", expressionDef.propertyElement.name)
                return codeBuilder.build()
            }
            if (expressionDef is InvokeStaticMethod) {
                val codeBuilder = CodeBlock.builder()
                codeBuilder.add("%T.%N(", asClassName(expressionDef.classDef), expressionDef.method.name)
                for ((index, parameter) in expressionDef.values.withIndex()) {
                    codeBuilder.add(renderExpressionCode(objectDef, methodDef, parameter))
                    if (index != expressionDef.values.size - 1) {
                        codeBuilder.add(", ")
                    }
                }
                codeBuilder.add(")")
                return codeBuilder.build()
            }
            if (expressionDef is Cast) {
                if (expressionDef.type == expressionDef.expressionDef.type()) {
                    return renderExpressionCode(objectDef, methodDef, expressionDef.expressionDef)
                }
                val codeBuilder = CodeBlock.builder()
                codeBuilder.add(
                    renderExpressionCode(
                        objectDef,
                        methodDef,
                        expressionDef.expressionDef,
                        expressionDef.type,
                    ),
                )
                codeBuilder.add(" as %T", asType(expressionDef.type, objectDef))
                return codeBuilder.build()
            }
            if (expressionDef is VariableDef) {
                return renderVariable(objectDef, methodDef, expressionDef)
            }
            if (expressionDef is Constant) {
                return renderConstantExpression(expressionDef, methodDef)
            }
            if (expressionDef is And) {
                return CodeBlock
                    .builder()
                    .add(renderCondition(objectDef, methodDef, expressionDef.left))
                    .add(" && ")
                    .add(renderCondition(objectDef, methodDef, expressionDef.right))
                    .build()
            }
            if (expressionDef is Or) {
                return CodeBlock
                    .builder()
                    .add(renderCondition(objectDef, methodDef, expressionDef.left))
                    .add(" || ")
                    .add(renderCondition(objectDef, methodDef, expressionDef.right))
                    .build()
            }
            if (expressionDef is IfElse) {
                return CodeBlock
                    .builder()
                    .add("if (")
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.condition, TypeDef.Primitive.BOOLEAN))
                    .add(") ")
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.ifExpression, expressionDef.type()))
                    .add(" else ")
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.elseExpression, expressionDef.type()))
                    .build()
            }
            if (expressionDef is Switch) {
                val builder: CodeBlock.Builder = CodeBlock.builder()
                builder.add("when (")
                builder.add(
                    renderExpressionCode(
                        objectDef,
                        methodDef,
                        expressionDef.expression,
                        TypeDef.Primitive.BOOLEAN,
                    ),
                )
                builder.add(") {\n")
                builder.indent()
                for ((key, value) in expressionDef.cases) {
                    builder.add(renderExpressionCode(objectDef, methodDef, key))
                    builder.add(" -> ")
                    builder.add(renderExpressionCode(objectDef, methodDef, value))
                    if (value is SwitchYieldCase) {
                        builder.add("\n")
                    } else {
                        builder.add(";\n")
                    }
                }
                if (expressionDef.defaultCase != null) {
                    builder.add("else -> ")
                    builder.add(renderExpressionCode(objectDef, methodDef, expressionDef.defaultCase))
                }
                builder.unindent()
                builder.add("}")
                return builder.build()
            }
            if (expressionDef is SwitchYieldCase) {
                val builder: CodeBlock.Builder = CodeBlock.builder()
                builder.add("{\n")
                builder.indent()
                val statement = expressionDef.statement
                val flatten = statement.flatten()
                check(!flatten.isEmpty()) { "SwitchYieldCase did not return any statements" }
                val last = flatten[flatten.size - 1]
                val rest: List<StatementDef> = flatten.subList(0, flatten.size - 1)
                for (statementDef in rest) {
                    builder.add(renderStatementCodeBlock(objectDef, methodDef, statementDef))
                }
                renderYield(builder, methodDef, last, objectDef)
                builder.unindent()
                builder.add("}")
                val str: String = builder.build().toString()
                // Render the body to prevent nested statements
                return CodeBlock.of(str)
            }
            if (expressionDef is IsNull) {
                return CodeBlock
                    .builder()
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.expression))
                    .add(" == null")
                    .build()
            }
            if (expressionDef is IsNotNull) {
                return CodeBlock
                    .builder()
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.expression))
                    .add(" != null")
                    .build()
            }
            if (expressionDef is IsTrue) {
                return renderExpressionCode(objectDef, methodDef, expressionDef.expression)
            }
            if (expressionDef is IsFalse) {
                return CodeBlock
                    .builder()
                    .add("!")
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.expression))
                    .build()
            }
            if (expressionDef is MathBinaryOperation) {
                return CodeBlock
                    .builder()
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.left))
                    .add(getMathOp(expressionDef.opType))
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.right))
                    .build()
            }
            if (expressionDef is MathUnaryOperation) {
                return CodeBlock
                    .builder()
                    .add(getMathOp(expressionDef.opType))
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.expression))
                    .build()
            }
            if (expressionDef is ComparisonOperation) {
                return CodeBlock
                    .builder()
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.left))
                    .add(getOpType(expressionDef.opType))
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.right))
                    .build()
            }
            if (expressionDef is NewArrayOfSize) {
                return CodeBlock.of(
                    "arrayOfNulls<%T>(%L)",
                    asType(expressionDef.type.componentType, objectDef),
                    expressionDef.size,
                )
            }
            if (expressionDef is NewArrayInitialized) {
                if (isInVarargContext(expressionDef)) {
                    val builder: CodeBlock.Builder = CodeBlock.builder()
                    val iterator: Iterator<ExpressionDef> = expressionDef.expressions.iterator()
                    while (iterator.hasNext()) {
                        val expression = iterator.next()
                        builder.add(renderExpressionCode(objectDef, methodDef, expression))
                        if (iterator.hasNext()) {
                            builder.add(", ")
                        }
                    }
                    return builder.build()
                } else {
                    val builder: CodeBlock.Builder = CodeBlock.builder()
                    builder.add("arrayOf<%T>(", asType(expressionDef.type.componentType, objectDef))
                    val iterator: Iterator<ExpressionDef> = expressionDef.expressions.iterator()
                    while (iterator.hasNext()) {
                        val expression = iterator.next()
                        builder.add(renderExpressionCode(objectDef, methodDef, expression))
                        if (iterator.hasNext()) {
                            builder.add(",")
                        }
                    }
                    builder.add(")")
                    return builder.build()
                }
            }
            if (expressionDef is InvokeGetClassMethod) {
                val instanceExp = renderExpressionCode(objectDef, methodDef, expressionDef.instance)
                return instanceExp.toBuilder().add(".javaClass").build()
            }
            if (expressionDef is InvokeHashCodeMethod) {
                val instanceExp = renderExpressionCode(objectDef, methodDef, expressionDef.instance)
                val type = expressionDef.instance.type()
                if (type.isArray) {
                    if (type is TypeDef.Array && type.dimensions > 1) {
                        return instanceExp.toBuilder().add(".contentDeepHashCode()").build()
                    }
                    return instanceExp.toBuilder().add(".contentHashCode()").build()
                }
                return instanceExp.toBuilder().add(".hashCode()").build()
            }
            if (expressionDef is EqualsStructurally) {
                val type = expressionDef.instance.type()
                if (type.isArray) {
                    if (type is TypeDef.Array && type.dimensions > 1) {
                        return CodeBlock
                            .builder()
                            .add(renderExpressionCode(objectDef, methodDef, expressionDef.instance))
                            .add(".contentDeepEquals(")
                            .add(renderExpressionCode(objectDef, methodDef, expressionDef.other))
                            .add(")")
                            .build()
                    }
                    return CodeBlock
                        .builder()
                        .add(renderExpressionCode(objectDef, methodDef, expressionDef.instance))
                        .add(".contentEquals(")
                        .add(renderExpressionCode(objectDef, methodDef, expressionDef.other))
                        .add(")")
                        .build()
                }
                return CodeBlock
                    .builder()
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.instance))
                    .add(" == ")
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.other))
                    .build()
            }
            if (expressionDef is NotEqualsStructurally) {
                val type = expressionDef.instance.type()
                if (type.isArray) {
                    if (type is TypeDef.Array && type.dimensions > 1) {
                        return CodeBlock
                            .builder()
                            .add("!")
                            .add(renderExpressionCode(objectDef, methodDef, expressionDef.instance))
                            .add(".contentDeepEquals(")
                            .add(renderExpressionCode(objectDef, methodDef, expressionDef.other))
                            .add(")")
                            .build()
                    }
                    return CodeBlock
                        .builder()
                        .add("!")
                        .add(renderExpressionCode(objectDef, methodDef, expressionDef.instance))
                        .add(".contentEquals(")
                        .add(renderExpressionCode(objectDef, methodDef, expressionDef.other))
                        .add(")")
                        .build()
                }
                return CodeBlock
                    .builder()
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.instance))
                    .add(" != ")
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.other))
                    .build()
            }
            if (expressionDef is EqualsReferentially) {
                return CodeBlock
                    .builder()
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.instance))
                    .add(" === ")
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.other))
                    .build()
            }
            if (expressionDef is NotEqualsReferentially) {
                return CodeBlock
                    .builder()
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.instance))
                    .add(" !== ")
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.other))
                    .build()
            }
            if (expressionDef is ExpressionDef.Lambda) {
                val builder =
                    CodeBlock
                        .builder()
                        .add("%T ", asType(expressionDef.type, objectDef))
                        .add("{")
                val parameter: Iterator<ParameterDef> = expressionDef.implementation.parameters.iterator()
                if (!parameter.hasNext()) {
                    builder.add("()")
                }
                while (parameter.hasNext()) {
                    val param = parameter.next()
                    builder.add("%L: %T", param.name, asType(param.type, objectDef))
                    if (parameter.hasNext()) {
                        builder.add(", ")
                    }
                }
                builder.add(" -> ")
                val statements: List<StatementDef> = expressionDef.implementation.statements
                if (statements.size == 1 && statements[0] is Return) {
                    val returnStatement = statements[0] as Return
                    builder.add(renderExpressionCode(objectDef, expressionDef.implementation, returnStatement.expression))
                } else {
                    builder.add("{")
                    for (statement in statements) {
                        builder.add(renderStatementCodeBlock(objectDef, expressionDef.implementation, statement))
                    }
                    builder.unindent()
                }
                return builder.add("}").build()
            }
            if (expressionDef is ExpressionDef.StringConcatenation) {
                var left: ExpressionDef = expressionDef.left()
                if (left.type() != TypeDef.STRING && !(expressionDef.right().type().equals(TypeDef.STRING))) {
                    left = TypeDef.STRING.invokeStatic("valueOf", TypeDef.STRING, left)
                }
                return CodeBlock
                    .builder()
                    .add(renderExpressionCode(objectDef, methodDef, left))
                    .add(" + ")
                    .add(renderExpressionCode(objectDef, methodDef, expressionDef.right))
                    .build()
            }
            throw IllegalStateException("Unrecognized expression: $expressionDef")
        }

        private fun getMathOp(opType: MathBinaryOperation.OpType): String =
            when (opType) {
                MathBinaryOperation.OpType.ADDITION -> " + "
                MathBinaryOperation.OpType.SUBTRACTION -> " - "
                MathBinaryOperation.OpType.MULTIPLICATION -> " * "
                MathBinaryOperation.OpType.DIVISION -> " / "
                MathBinaryOperation.OpType.MODULUS -> " % "
                MathBinaryOperation.OpType.BITWISE_AND -> " & "
                MathBinaryOperation.OpType.BITWISE_OR -> " | "
                MathBinaryOperation.OpType.BITWISE_XOR -> " ^ "
                MathBinaryOperation.OpType.BITWISE_LEFT_SHIFT -> " << "
                MathBinaryOperation.OpType.BITWISE_RIGHT_SHIFT -> " >> "
                MathBinaryOperation.OpType.BITWISE_UNSIGNED_RIGHT_SHIFT -> " >>> "
            }

        private fun getMathOp(opType: MathUnaryOperation.OpType): String =
            when (opType) {
                MathUnaryOperation.OpType.NEGATE -> "-"
            }

        private fun getOpType(opType: ComparisonOperation.OpType): String =
            when (opType) {
                ComparisonOperation.OpType.EQUAL_TO -> " == "
                ComparisonOperation.OpType.NOT_EQUAL_TO -> " != "
                ComparisonOperation.OpType.GREATER_THAN -> " > "
                ComparisonOperation.OpType.LESS_THAN -> " < "
                ComparisonOperation.OpType.GREATER_THAN_OR_EQUAL -> " >= "
                ComparisonOperation.OpType.LESS_THAN_OR_EQUAL -> " <= "
            }

        private fun renderCondition(
            objectDef: @Nullable ObjectDef?,
            methodDef: MethodDef,
            expressionDef: ExpressionDef,
        ): CodeBlock {
            val needsParentheses = expressionDef is And || expressionDef is Or
            val rendered = renderExpressionCode(objectDef, methodDef, expressionDef)
            if (needsParentheses) {
                return CodeBlock
                    .builder()
                    .add("(")
                    .add(rendered)
                    .add(")")
                    .build()
            }
            return rendered
        }

        private fun renderConstantExpression(
            constant: Constant,
            methodDef: MethodDef,
        ): CodeBlock {
            val type = constant.type
            val value = constant.value ?: return CodeBlock.of("null")
            if (value is TypeDef) {
                return CodeBlock.of("%T::class.java", asType(value, null))
            }
            if (type is ClassTypeDef && type.isEnum) {
                return renderExpressionCode(
                    null,
                    methodDef,
                    VariableDef.StaticField(
                        type,
                        if (value is Enum<*>) value.name else value.toString(),
                        type,
                    ),
                )
            }
            if (type is TypeDef.Primitive) {
                return when (type.name()) {
                    "long" -> CodeBlock.of(value.toString() + "l")
                    "float" -> CodeBlock.of(value.toString() + "f")
                    "double" -> CodeBlock.of(value.toString() + "d")
                    else -> CodeBlock.of("%L", value)
                }
            } else if (type is TypeDef.Array) {
                if (value.javaClass.isArray) {
                    val array = value
                    val builder = CodeBlock.builder()
                    val length =
                        java.lang.reflect.Array
                            .getLength(array)
                    val componentType = type.componentType
                    for (i in 0..length) {
                        builder.add(
                            renderConstantExpression(
                                Constant(componentType, Array.get(array, i)),
                                methodDef,
                            ),
                        )
                        if (i + 1 != length) {
                            builder.add(",")
                        }
                    }
                    val values = builder.build()
                    val typeName: String =
                        if (componentType is ClassTypeDef) {
                            componentType.simpleName
                        } else if (componentType is TypeDef.Primitive) {
                            componentType.name()
                        } else {
                            throw java.lang.IllegalStateException("Unrecognized expression: $constant")
                        }
                    return CodeBlock
                        .builder()
                        .add("new %N[] {", typeName)
                        .add(values)
                        .add("}")
                        .build()
                }
            } else if (type is ClassTypeDef) {
                val name = type.name
                return if (ClassUtils.isJavaLangType(name)) {
                    when (name) {
                        "java.lang.Long" -> CodeBlock.of(value.toString() + "l")
                        "java.lang.Float" -> CodeBlock.of(value.toString() + "f")
                        "java.lang.Double" -> CodeBlock.of(value.toString() + "d")
                        "java.lang.String" -> CodeBlock.of("%S", value)
                        else -> CodeBlock.of("%L", value)
                    }
                } else {
                    CodeBlock.of("%L", value)
                }
            }
            throw IllegalStateException("Unrecognized expression: $constant")
        }

        private fun renderVariable(
            objectDef: ObjectDef?,
            methodDef: MethodDef?,
            variableDef: VariableDef,
        ): CodeBlock {
            if (variableDef is VariableDef.MethodParameter) {
                checkNotNull(methodDef) { "Accessing method parameters is not available" }
                methodDef.getParameter(variableDef.name) // Check if exists
                return CodeBlock.of("%N", variableDef.name)
            }
            if (variableDef is VariableDef.Field) {
                checkNotNull(objectDef) { "Field 'this' is not available" }
                if (objectDef is ClassDef) {
                    if (!objectDef.hasField(variableDef.name)) {
                        throw IllegalStateException("Field '${variableDef.name}' is not available in [$objectDef]:${objectDef.fields}")
                    }
                } else if (objectDef is EnumDef) {
                    if (!objectDef.hasField(variableDef.name)) {
                        throw IllegalStateException("Field '${variableDef.name}' is not available in [$objectDef]:${objectDef.properties}")
                    }
                } else {
                    throw IllegalStateException("Field access not supported on the object definition: $objectDef")
                }
                checkNotNull(methodDef) { "Accessing field is not available" }
                val codeBlock = renderExpressionCode(objectDef, methodDef, variableDef.instance)
                val builder = codeBlock.toBuilder()
                if (variableDef.instance.type().isNullable) {
                    builder.add("!!")
                }
                builder.add(". %N", variableDef.name)
                return builder.build()
            }
            if (variableDef is VariableDef.StaticField) {
                return if (objectDef != null &&
                    variableDef.ownerType.name == objectDef.asTypeDef().name
                ) {
                    // Within same class - use just the field name
                    CodeBlock.of("%L", variableDef.name)
                } else {
                    // Different class - use fully qualified name
                    CodeBlock.of(
                        "%T.%L",
                        asType(variableDef.ownerType, objectDef),
                        variableDef.name,
                    )
                }
            }
            if (variableDef is VariableDef.This) {
                checkNotNull(objectDef) { "Accessing 'this' is not available" }
                return CodeBlock.of("this")
            }
            if (variableDef is VariableDef.Local) {
                return CodeBlock.of("%L", variableDef.name)
            }
            if (variableDef is VariableDef.Super) {
                checkNotNull(objectDef) { "Accessing 'super' is not available" }
                if (variableDef.type() !== TypeDef.SUPER) {
                    return CodeBlock.of("super<%T>", asType(variableDef.type, objectDef))
                }
                return CodeBlock.of("super")
            }
            throw IllegalStateException("Unrecognized variable: $variableDef")
        }

        private fun renderExpressionWithNotNullAssertion(
            objectDef: ObjectDef?,
            methodDef: MethodDef,
            expressionDef: ExpressionDef,
            result: TypeDef,
        ): CodeBlock {
            val codeBlock = renderExpressionCode(objectDef, methodDef, expressionDef)
            val builder = codeBlock.toBuilder()
            if (!result.isNullable && expressionDef.type().isNullable) {
                builder.add("!!")
            }
            return builder.build()
        }

        private fun asAnnotationSpec(annotationDef: AnnotationDef): AnnotationSpec {
            var annName: String =
                if (annotationDef.type.name.contains("$")) {
                    annotationDef.type.name.replace("$", ".")
                } else {
                    annotationDef.type.name
                }
            var builder = AnnotationSpec.builder(ClassName.bestGuess(annName))
            for ((memberName, value) in annotationDef.values) {
                builder = addAnnotationValue(builder, memberName, value)
            }
            return builder.build()
        }

        private fun addAnnotationValue(
            builder: AnnotationSpec.Builder,
            memberName: String,
            value: Any,
        ): AnnotationSpec.Builder =
            when (value) {
                // Note: Class values skip both Class<*> and KClass<*> entries
                is Class<*> -> {
                    builder.addMember("$memberName = %T::class", value)
                }

                is KClass<*> -> {
                    builder.addMember("$memberName = %T::class", value)
                }

                is ClassTypeDef -> {
                    builder.addMember("$memberName = %L::class", value.getSimpleName())
                }

                is Enum<*> -> {
                    // Enum values gets represented as a Static Variable and does not enter here
                    builder.addMember("$memberName = %T.%L", value.javaClass, value.name)
                }

                is String -> {
                    builder.addMember("$memberName = %S", value)
                }

                is Float -> {
                    builder.addMember("$memberName = %Lf", value)
                }

                is Char -> {
                    builder.addMember(
                        "$memberName = '%L'",
                        characterLiteralWithoutSingleQuotes(
                            value,
                        ),
                    )
                }

                is VariableDef -> {
                    builder.addMember("$memberName = %L", renderVariable(null, null, value))
                }

                is AnnotationDef -> {
                    val spec = asAnnotationSpec(value)
                    builder.addMember("$memberName = %L", spec.toString().substring(1))
                }

                is Collection<*> -> {
                    value.forEach(Consumer { v: Any? -> addAnnotationValue(builder, memberName, v!!) })
                    val listItems = builder.members.filter { it.isNotEmpty() && it.toString().contains(memberName) }
                    builder.members.removeAll(listItems)
                    val listStr: String = listItems.map { it.toString().substringAfter("= ") }.joinToString(separator = ",\n")
                    builder.addMember("$memberName = [%L]", listStr)
                }

                else -> {
                    builder.addMember("$memberName = %L", value)
                }
            }

        // Copy from com.squareup.javapoet.Util
        private fun characterLiteralWithoutSingleQuotes(c: Char): String {
            // see https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6
            return when (c) {
                '\b' -> "\\b"
                '\t' -> "\\t"
                '\n' -> "\\n"
                '\u000c' -> "\\f"
                '\r' -> "\\r"
                '\"' -> "\""
                '\'' -> "\\'"
                '\\' -> "\\\\"
                else -> if (Character.isISOControl(c)) String.format("\\u%04x", c.code) else c.toString()
            }
        }

        private fun isInVarargContext(arrayExpression: NewArrayInitialized): Boolean {
            val componentType = arrayExpression.type.componentType
            return componentType is ClassTypeDef &&
                (componentType.name == "java.lang.Class" || componentType.simpleName == "Class")
        }
    }
}
