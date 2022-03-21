package de.bonndan.nivio.model;

import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.dto.ProcessDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Used like a default component factory, but does not merge processes.
 */
public class ProcessFactory implements GraphNodeFactory<Process, ProcessDescription, Landscape> {

    public static final ProcessFactory INSTANCE = new ProcessFactory();


    @Override
    public Process merge(@NonNull final Process existing, @NonNull final Process added) {
        return added;
    }

    /**
     * @param identifier identifier of the process
     * @param parent     landscape
     * @param dto        process description
     * @return a validated process with branches containing existing or new relations
     * @throws NoSuchElementException if a branch node cannot be found
     * @throws ProcessingException    if the process has gaps
     */
    @Override
    @NonNull
    public Process createFromDescription(@NonNull final String identifier,
                                         @NonNull final Landscape parent,
                                         @Nullable final ProcessDescription dto
    ) {
        if (dto == null) {
            throw new IllegalArgumentException("foo");
        }

        FlexSearch<? extends GraphComponent, Item> itemFlexSearch = new FlexSearch<>(Item.class, parent.indexReadAccess);
        List<List<Item>> itemsPerBranch = dto.getBranches().stream()
                .map(
                        branchDescription -> branchDescription.getItems().stream()
                                .map(s -> itemFlexSearch.searchOne(s, null).orElseThrow(
                                                () -> new NoSuchElementException(String.format("No branch node found matching: %s", s))
                                        )
                                ).collect(Collectors.toList()))
                .collect(Collectors.toList());

        validateGraph(itemsPerBranch);

        var branches = itemsPerBranch.stream()
                .map(branchNodes -> createBranchWithRelations(parent.getWriteAccess(), branchNodes))
                .collect(Collectors.toList());

        return new Process(identifier,
                dto.getName(),
                dto.getOwner(),
                dto.getContact(),
                dto.getDescription(),
                dto.getType(),
                branches,
                parent);
    }

    /**
     * Creates a new branch containing the relations between the given items.
     *
     * Creates relations if absent.
     *
     * @param writeAccess
     * @param branchNodes items
     * @return a new branch
     */
    private static Branch createBranchWithRelations(GraphWriteAccess<GraphComponent> writeAccess, final List<Item> branchNodes) {
        List<Relation> relations = new ArrayList<>();
        for (int i = 0; i < branchNodes.size(); i++) {
            Item item = branchNodes.get(i);
            if (i == branchNodes.size() - 1) {
                break;
            }
            Item next = branchNodes.get(i + 1);
            Relation relation1 = item.getRelations().stream().filter(relation -> relation.getSource().equals(item) && relation.getTarget().equals(next))
                    .findFirst()
                    .orElseGet(() -> {
                        Relation created = createRelation(item, next);
                        writeAccess.addOrReplaceRelation(created);
                        return created;
                    });
            relations.add(relation1);
        }

        return new Branch(relations);
    }

    @NonNull
    private static Relation createRelation(Item item, Item next) {
        RelationDescription description = new RelationDescription();
        description.setType(RelationType.DATAFLOW);
        return RelationFactory.create(item, next, description);
    }

    /**
     * Ensures that each branch is connected to at least one node of the other branches.
     *
     * @param branches branches of the process
     */
    private static void validateGraph(final List<List<Item>> branches) {
        if (branches.size() < 2) {
            return;
        }

        for (List<Item> branch : branches) {
            final Item firstInBranch = branch.get(0);
            final Item lastInBranch = branch.get(branch.size() - 1);
            final Set<Item> allOtherItems = branches.stream()
                    .filter(branch1 -> branch1 != branch)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());

            if (!allOtherItems.contains(firstInBranch) && !allOtherItems.contains(lastInBranch)) {
                throw new ProcessingException(
                        String.format("Branch start %s and end %s are both not part of the process", firstInBranch, lastInBranch),
                        new NoSuchElementException("Start or end of branch not in process nodes")
                );
            }
        }
    }

}
