package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.*;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * Finds the parent for a {@link GraphComponent}.
 *
 * Creates new default parent instances if necessary.
 *
 */
public class ParentResolver {
    @NonNull
    private final IndexReadAccess<GraphComponent> readAccess;
    @NonNull
    private final GraphWriteAccess<GraphComponent> writeAccess;
    @NonNull
    private final String defaultUnit;
    @NonNull
    private final String defaultContext;

    public ParentResolver(@NonNull final IndexReadAccess<GraphComponent> readAccess,
                          @NonNull final GraphWriteAccess<GraphComponent> writeAccess,
                          @NonNull final String defaultUnit,
                          @NonNull final String defaultContext

    ) {
        this.readAccess = Objects.requireNonNull(readAccess);
        this.writeAccess = Objects.requireNonNull(writeAccess);
        this.defaultUnit = Objects.requireNonNull(defaultUnit);
        this.defaultContext = Objects.requireNonNull(defaultContext);
    }

    /**
     * Returns or creates the parent for a given {@link ComponentDescription}.
     *
     * @param dto input dto
     * @param pClass parent class
     */
    public <P extends GraphComponent, D extends ComponentDescription> P getParent(@NonNull final D dto,
                                                                                  @NonNull final Class<P> pClass
    ) {
        final String parentIdentifier = getParentIdentifier(dto);
        return readAccess.matchOneByIdentifiers(parentIdentifier, null, pClass)
                .orElseGet(() -> createParentInstantly(parentIdentifier, pClass));
    }

    /**
     * When a parent for an item or context is absent,
     *
     * @param parentIdentifier identifier of the parent
     * @param parentClass      type
     * @return a new graph component
     * @throws IllegalArgumentException if instant creation is not possible
     */
    @NonNull
    <P extends GraphComponent> P createParentInstantly(@NonNull final String parentIdentifier,
                                                       @NonNull final Class<P> parentClass
    ) {
        if (Group.class.equals(parentClass)) {
            return (P) getParentForItemDescription(parentIdentifier);
        } else if (Context.class.equals(parentClass)) {
            return (P) getParentForGroup(parentIdentifier);
        } else if (Unit.class.equals(parentClass)) {
            return (P) getParentForContext(parentIdentifier);
        } else if (Landscape.class.equals(parentClass)) {
            return (P) readAccess.getRoot();
        }

        throw new IllegalArgumentException(String.format("Cannot determine default parent for %s", parentClass));
    }

    private Group getParentForItemDescription(String groupIdentifier) {

        Group group;
        try {
            group = readAccess.matchOneByIdentifiers(groupIdentifier, null, Group.class).orElseThrow();
        } catch (NoSuchElementException e) {
            group = GroupFactory.INSTANCE.createFromDescription(groupIdentifier, getDefaultContext(readAccess.all(Context.class)), null);
            writeAccess.addOrReplaceChild(group);
        }
        return group;
    }

    private Context getParentForGroup(String identifier) {

        Context context;
        try {
            context = readAccess.matchOneByIdentifiers(identifier, null, Context.class).orElseThrow();
        } catch (NoSuchElementException e) {
            context = ContextFactory.INSTANCE.createFromDescription(identifier, getDefaultUnit(readAccess.all(Unit.class)), null);
            writeAccess.addOrReplaceChild(context);
        }
        return context;
    }

    private Context getDefaultContext(Set<Context> contexts) {
        return contexts.stream()
                .filter(context -> context.identifier.equals(defaultContext))
                .findFirst()
                .orElseGet(
                        () -> {

                            Context newDefaultContext = ContextFactory.INSTANCE.createFromDescription(
                                    defaultContext,
                                    getParentForContext(defaultContext),
                                    new ContextDescription());
                            writeAccess.addOrReplaceChild(newDefaultContext);
                            return newDefaultContext;
                        }
                );
    }

    private Unit getDefaultUnit(Set<Unit> units) {
        return units.stream()
                .filter(unit -> unit.identifier.equals(defaultUnit))
                .findFirst()
                .orElseGet(() -> {
                            Unit newDefaultUnit = UnitFactory.INSTANCE.createFromDescription(
                                    defaultUnit,
                                    (Landscape) readAccess.getRoot(),
                                    new UnitDescription());
                            writeAccess.addOrReplaceChild(newDefaultUnit);
                            return newDefaultUnit;
                        }
                );
    }

    private Unit getParentForContext(String parentIdentifier) {

        Unit unit;
        try {
            unit = readAccess.matchOneByIdentifiers(parentIdentifier, null, Unit.class).orElseThrow();
        } catch (NoSuchElementException e) {
            unit = UnitFactory.INSTANCE.createFromDescription(parentIdentifier, (Landscape) readAccess.getRoot(), null);
            writeAccess.addOrReplaceChild(unit);
        }
        return unit;
    }

    private String getParentIdentifier(ComponentDescription dto) {
        if (dto instanceof ItemDescription) {

            String groupIdentifier = ((ItemDescription) dto).getGroup();
            if (FullyQualifiedIdentifier.isUndefined(groupIdentifier)) {
                groupIdentifier = ((ItemDescription) dto).getLayer();
            }
            if (FullyQualifiedIdentifier.isUndefined(groupIdentifier)) {
                groupIdentifier = Layer.domain.name();
            }
            return groupIdentifier;
        }

        if (StringUtils.hasLength(dto.getParentIdentifier())) {
            return dto.getParentIdentifier();
        }

        if (dto instanceof GroupDescription) {
            return defaultContext;
        }

        if (dto instanceof ContextDescription) {
            return defaultUnit;
        }

        throw new IllegalArgumentException(String.format("Cannot determine parent identifier for dto %s", dto.getClass()));
    }
}
