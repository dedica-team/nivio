package de.bonndan.nivio.config;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.types.ResolvedArrayType;
import com.fasterxml.classmate.types.ResolvedObjectType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.PluginRegistry;
import springfox.documentation.schema.DefaultTypeNameProvider;
import springfox.documentation.schema.ModelNameContext;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;
import springfox.documentation.spi.schema.TypeNameProviderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.DefaultsProviderPlugin;
import springfox.documentation.spi.service.ResourceGroupingStrategy;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;
import springfox.documentation.spring.web.SpringGroupingStrategy;
import springfox.documentation.spring.web.plugins.DefaultConfiguration;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.lang.reflect.Type;

import static com.google.common.base.Optional.fromNullable;
import static springfox.documentation.schema.Collections.containerType;
import static springfox.documentation.schema.Collections.isContainerType;
import static springfox.documentation.schema.Types.typeNameFor;

/**
 * Compatibility between Swagger 2 and Spring Plugins 2 as workaround for
 * https://github.com/springfox/springfox/issues/2932.
 */
@SuppressWarnings("deprecation")
@Configuration
public class SwaggerCompatibility {

    /**
     * Compatible {@link DocumentationPluginsManager} implementation.
     */
    public static class CompatibleDocumentationPluginsManager extends DocumentationPluginsManager {

        private @Autowired @Qualifier("defaultsProviderPluginRegistry") PluginRegistry<DefaultsProviderPlugin, DocumentationType> defaultsProviders;
        private @Autowired @Qualifier("resourceGroupingStrategyRegistry") PluginRegistry<ResourceGroupingStrategy, DocumentationType> resourceGroupingStrategies;

        @Override
        public DocumentationContextBuilder createContextBuilder(DocumentationType documentationType, DefaultConfiguration defaultConfiguration) {
            return defaultsProviders.getPluginOrDefaultFor(documentationType, defaultConfiguration).create(documentationType)
                    .withResourceGroupingStrategy(resourceGroupingStrategy(documentationType));
        }

        @Override
        public ResourceGroupingStrategy resourceGroupingStrategy(DocumentationType documentationType) {
            return resourceGroupingStrategies.getPluginOrDefaultFor(documentationType, new SpringGroupingStrategy());
        }
    }

    /**
     * Compatible {@link TypeNameExtractor} implementation.
     */
    public static class CompatibleTypeNameExtractor extends TypeNameExtractor {

        private final TypeResolver typeResolver;
        private final PluginRegistry<TypeNameProviderPlugin, DocumentationType> typeNameProviders;
        private final EnumTypeDeterminer enumTypeDeterminer;

        public @Autowired CompatibleTypeNameExtractor(TypeResolver typeResolver,
                                                      @Qualifier("typeNameProviderPluginRegistry") PluginRegistry<TypeNameProviderPlugin, DocumentationType> typeNameProviders,
                                                      EnumTypeDeterminer enumTypeDeterminer) {
            super(typeResolver, typeNameProviders, enumTypeDeterminer);
            this.typeResolver = typeResolver;
            this.typeNameProviders = typeNameProviders;
            this.enumTypeDeterminer = enumTypeDeterminer;
        }

        @Override
        public String typeName(ModelContext context) {
            ResolvedType type = asResolved(context.getType());
            if (isContainerType(type)) {
                return containerType(type);
            }
            return innerTypeName(type, context);
        }

        private ResolvedType asResolved(Type type) {
            return typeResolver.resolve(type);
        }

        private String genericTypeName(ResolvedType resolvedType, ModelContext context) {
            Class<?> erasedType = resolvedType.getErasedType();
            GenericTypeNamingStrategy namingStrategy = context.getGenericNamingStrategy();
            ModelNameContext nameContext = new ModelNameContext(resolvedType.getErasedType(), context.getDocumentationType());
            String simpleName = fromNullable(typeNameFor(erasedType)).or(typeName(nameContext));
            StringBuilder sb = new StringBuilder(String.format("%s%s", simpleName, namingStrategy.getOpenGeneric()));
            boolean first = true;
            for (int index = 0; index < erasedType.getTypeParameters().length; index++) {
                ResolvedType typeParam = resolvedType.getTypeParameters().get(index);
                if (first) {
                    sb.append(innerTypeName(typeParam, context));
                    first = false;
                } else {
                    sb.append(String.format("%s%s", namingStrategy.getTypeListDelimiter(), innerTypeName(typeParam, context)));
                }
            }
            sb.append(namingStrategy.getCloseGeneric());
            return sb.toString();
        }

        private String innerTypeName(ResolvedType type, ModelContext context) {
            if ((type.getTypeParameters().size() > 0) && (type.getErasedType().getTypeParameters().length > 0)) {
                return genericTypeName(type, context);
            }
            return simpleTypeName(type, context);
        }

        private String simpleTypeName(ResolvedType type, ModelContext context) {
            Class<?> erasedType = type.getErasedType();
            if (type instanceof ResolvedPrimitiveType) {
                return typeNameFor(erasedType);
            } else if (enumTypeDeterminer.isEnum(erasedType)) {
                return "string";
            } else if (type instanceof ResolvedArrayType) {
                GenericTypeNamingStrategy namingStrategy = context.getGenericNamingStrategy();
                return String.format("Array%s%s%s", namingStrategy.getOpenGeneric(), simpleTypeName(type.getArrayElementType(), context),
                        namingStrategy.getCloseGeneric());
            } else if (type instanceof ResolvedObjectType) {
                String typeName = typeNameFor(erasedType);
                if (typeName != null) {
                    return typeName;
                }
            }
            return typeName(new ModelNameContext(type.getErasedType(), context.getDocumentationType()));
        }

        private String typeName(ModelNameContext context) {
            TypeNameProviderPlugin selected = typeNameProviders.getPluginOrDefaultFor(context.getDocumentationType(), new DefaultTypeNameProvider());
            return selected.nameFor(context.getType());
        }
    }

    /**
     * Produce the {@link BeanFactoryPostProcessor} to substitute compatible bean types.
     *
     * @return {@link BeanFactoryPostProcessor}
     */
    @Bean
    public static BeanFactoryPostProcessor substituteCompatibleBeanTypes() {
        return beanFactory -> {
            beanFactory.getBeanDefinition("documentationPluginsManager").setBeanClassName(CompatibleDocumentationPluginsManager.class.getName());
            beanFactory.getBeanDefinition("typeNameExtractor").setBeanClassName(CompatibleTypeNameExtractor.class.getName());
        };
    }
}