package io.micronaut.jimmer.repository.sourcegen.generator.java;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.inject.ast.Element;
import io.micronaut.inject.processing.ProcessingException;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.sourcegen.bytecode.ByteCodeWriter;
import io.micronaut.sourcegen.generator.SourceGenerator;
import io.micronaut.sourcegen.model.ClassTypeDef;
import io.micronaut.sourcegen.model.ObjectDef;
import java.io.OutputStream;
import java.io.Writer;
import java.util.LinkedList;

public final class ByteCodeGenerator implements SourceGenerator {

    private static final ByteCodeWriter BYTE_CODE_WRITER = new ByteCodeWriter(false, true);

    @Override
    public VisitorContext.Language getLanguage() {
        return VisitorContext.Language.JAVA;
    }

    @Override
    public void write(ObjectDef objectDef, Writer writer) {
        throw new IllegalStateException(
                "ByteCode generator doesn't support writing using `java.io.Writer`");
    }

    @Override
    public void write(ObjectDef objectDef, VisitorContext context, Element... originatingElements) {
        LinkedList<InnerDef> innerTypes = new LinkedList<>();
        write(objectDef, null, context, innerTypes, originatingElements);
        while (!innerTypes.isEmpty()) {
            InnerDef innerType = innerTypes.removeFirst();
            write(innerType.inner, innerType.outer, context, innerTypes, originatingElements);
        }
    }

    private void write(
            ObjectDef objectDef,
            @Nullable ClassTypeDef outerType,
            VisitorContext context,
            LinkedList<InnerDef> innerTypes,
            Element[] originatingElements) {
        String className = objectDef.getName();
        try (OutputStream os = context.visitClass(className, originatingElements)) {
            os.write(BYTE_CODE_WRITER.write(objectDef, outerType));
            for (ObjectDef innerType : objectDef.getInnerTypes()) {
                innerTypes.add(new InnerDef(objectDef.asTypeDef(), innerType));
            }
        } catch (Exception e) {
            Element element = originatingElements.length > 0 ? originatingElements[0] : null;
            throw new ProcessingException(
                    element, "Failed to generate '" + className + "': " + e.getMessage(), e);
        }
    }

    private record InnerDef(ClassTypeDef outer, ObjectDef inner) {}
}
