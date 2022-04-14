package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.*;
import de.bonndan.nivio.model.IndexReadAccess;
import org.springframework.lang.NonNull;

/**
 * This class resolves all "contains" queries of a group description, i.e. the items are assigned dynamically to a group.
 *
 * TODO extend to {@link UnitDescription}, {@link ContextDescription}
 */
public class ContainsResolver implements Resolver {

    @NonNull
    @Override
    public LandscapeDescription resolve(@NonNull final LandscapeDescription input) {

        IndexReadAccess<ComponentDescription> indexReadAccess = input.getReadAccess();
        indexReadAccess.all(GroupDescription.class).forEach(group -> {
            // run the query against all landscape items which match the condition
            group.getContains().forEach(condition -> indexReadAccess.search(condition, ItemDescription.class)
                    .forEach(itemDescription -> itemDescription.setGroup(group.getIdentifier())));
        });

        return LandscapeDescriptionFactory.refreshedCopyOf(input);
    }
}
