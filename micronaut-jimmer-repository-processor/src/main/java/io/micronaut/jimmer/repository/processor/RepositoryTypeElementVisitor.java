package io.micronaut.jimmer.repository.processor;

import io.micronaut.core.annotation.Internal;
import io.micronaut.inject.ast.ClassElement;
import io.micronaut.inject.visitor.TypeElementVisitor;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.jimmer.repository.annotation.Repository;
import java.util.*;

@Internal
public final class RepositoryTypeElementVisitor implements TypeElementVisitor<Repository, Object> {

    public RepositoryTypeElementVisitor() {}

    @Override
    public Set<String> getSupportedAnnotationNames() {
        return Set.of(Repository.class.getName());
    }

    @Override
    public void visitClass(ClassElement element, VisitorContext context) {
        if (element.hasDeclaredStereotype(Repository.class)) {
            if (context.getLanguage().equals(VisitorContext.Language.KOTLIN)) {
                RepositoryTypeElementVisitorForKotlin repositoryTypeElementVisitorForKotlin =
                        new RepositoryTypeElementVisitorForKotlin();
                repositoryTypeElementVisitorForKotlin.visitClass(element, context);
            } else {
                RepositoryTypeElementVisitorForJava repositoryTypeElementVisitorForJava =
                        new RepositoryTypeElementVisitorForJava();
                repositoryTypeElementVisitorForJava.visitClass(element, context);
            }
        }
    }
}
