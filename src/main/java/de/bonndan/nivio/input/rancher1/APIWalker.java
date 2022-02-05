package de.bonndan.nivio.input.rancher1;

import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.SourceReference;
import de.bonndan.nivio.input.rancher1.patches.ProjectServices;
import de.bonndan.nivio.input.rancher1.patches.ProjectStacks;
import de.bonndan.nivio.input.rancher1.patches.Service;
import de.bonndan.nivio.model.Label;
import io.rancher.Rancher;
import io.rancher.base.TypeCollection;
import io.rancher.service.ProjectService;
import io.rancher.type.Project;
import io.rancher.type.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gathers projects, stacks and services from a Rancher 1.6 API
 */
class APIWalker {

    private static final Logger LOGGER = LoggerFactory.getLogger(APIWalker.class);

    private final Rancher rancher;
    private final SourceReference reference;

    public APIWalker(SourceReference reference, Rancher.Config config) {
        this.rancher = new Rancher(config);
        this.reference = reference;
    }

    public List<ItemDescription> getDescriptions() {

        String projectName = (String) reference.getProperty("projectName");
        Project project = getProject(reference, projectName)
                .orElseThrow(() -> new ProcessingException(reference, String.format("Error while reading rancher API: project %s not found", projectName)));
        String accountId = project.getId();

        try {
            Map<String, Stack> stacks = getStacks(accountId);
            List<Service> services = getServices(accountId);
            LOGGER.info("Found {} services in project {}", services.size(), project.getName());
            return asDescriptions(services, stacks);

        } catch (IOException e) {
            throw new ProcessingException(reference, String.format("Failed to load data: %s", e.getMessage()), e);
        }
    }

    private List<Service> getServices(final String accountId) throws IOException {
        ProjectServices service = rancher.type(ProjectServices.class);
        TypeCollection<Service> body;
        body = service.getServices(accountId).execute().body();


        if (body == null) {
            LOGGER.warn("No services found in project {}", accountId);
            return new ArrayList<>();
        }

        return body.getData().stream()
                .filter(this::hasInstances)
                .collect(Collectors.toList());
    }

    /**
     * Ignoring services which do not have instances (probably db orphans).
     */
    private boolean hasInstances(Service service) {
        if (service == null)
            return false;

        return service.getInstanceIds() != null && !service.getInstanceIds().isEmpty();
    }

    private Map<String, Stack> getStacks(String accountId) throws IOException {
        ProjectStacks stackService = rancher.type(ProjectStacks.class);
        Map<String, Stack> stacks = new HashMap<>();

        TypeCollection<Stack> body;

        body = stackService.getStacks(accountId).execute().body();

        if (body == null) {
            LOGGER.warn("Could not load stacks from project {}", accountId);
            return new HashMap<>(0);
        }
        body.getData().stream()
                .filter(stack -> stack.getAccountId().equals(accountId))
                .forEach(stack -> stacks.put(stack.getId(), stack));
        return stacks;
    }

    private Optional<Project> getProject(SourceReference reference, String projectName) {
        ProjectService projectService = rancher.type(ProjectService.class);
        List<Project> projects;
        try {
            Response<TypeCollection<Project>> response = projectService.list().execute();
            TypeCollection<Project> body = response.body();
            if (!response.isSuccessful() || body == null) {
                throw new ProcessingException(reference, "No projects found: code " + response.code());
            }
            projects = body.getData();
        } catch (IOException | NullPointerException e) {
            throw new ProcessingException(reference, "Could not load projects" + projectService.toString(), e);
        }
        return projects.stream()
                .filter(project -> !StringUtils.hasLength(projectName) || project.getName().equals(projectName))
                .findFirst();
    }

    private List<ItemDescription> asDescriptions(List<Service> data, final Map<String, Stack> stacks) {
        List<ItemDescription> descriptions = new ArrayList<>();

        data.forEach(service -> {
            ItemDescription item = new ItemDescription();
            item.setIdentifier(service.getId());
            item.setName(service.getName());
            item.setLabel(Label.scale, String.valueOf(service.getScale()));
            Stack stack = stacks.get(service.getStackId());
            if (stack != null) {
                item.setGroup(stack.getName());
            } else {
                LOGGER.warn("Rancher 1 Service {} has no stack or references unknown stack '{}', cannot set group",
                        service.getId(), service.getStackId()
                );
            }

            if (service.getLinkedServices() != null) {
                service.getLinkedServices().forEach((key, value) -> {
                    RelationDescription rd = new RelationDescription();
                    rd.setSource((String) value);
                    rd.setTarget(item.getIdentifier());
                    item.addOrReplaceRelation(rd);
                });
            }

            if (service.getLaunchConfig() != null) {
                item.setLabel(Label.version, service.getLaunchConfig().getImageUuid());
            }

            //copy all labels
            if (service.getLaunchConfig() != null) {
                if (service.getLaunchConfig().getLabels() != null) {
                    service.getLaunchConfig().getLabels().forEach((s, o) -> item.setLabel(s, String.valueOf(o)));
                }
                if (service.getLaunchConfig().getEnvironment() != null) {
                    service.getLaunchConfig().getEnvironment().forEach((s, o) -> item.setLabel(s, String.valueOf(o)));
                }
            }

            descriptions.add(item);
        });

        return descriptions;
    }

}
