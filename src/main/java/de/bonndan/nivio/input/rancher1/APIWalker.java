package de.bonndan.nivio.input.rancher1;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LabelProcessor;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import io.rancher.Rancher;
import io.rancher.service.ProjectService;
import io.rancher.service.ServiceService;
import io.rancher.service.StackService;
import io.rancher.type.Project;
import io.rancher.type.Service;
import io.rancher.type.Stack;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static de.bonndan.nivio.input.rancher1.ItemDescriptionFactoryRancher1API.API_ACCESS_KEY;
import static de.bonndan.nivio.input.rancher1.ItemDescriptionFactoryRancher1API.API_SECRET_KEY;

/**
 * Gathers projects, stacks and services from a Rancher 1.6 API
 */
class APIWalker {

    private Rancher rancher;
    private SourceReference reference;

    public APIWalker(SourceReference reference) {
        Rancher.Config config = getConfig(reference);
        this.rancher = new Rancher(config);
        this.reference = reference;
    }

    public List<ItemDescription> getDescriptions() {

        String projectName = (String) reference.getProperty("projectName");
        Project project = getProject(projectName)
                .orElseThrow(() -> new ProcessingException(reference.getLandscapeDescription(), "Project " + projectName + "not found"));
        String accountId = project.getId();


        Map<String, Stack> stacks = getStacksById(accountId);
        List<Service> services = getServices(accountId);

        return asDescriptions(services, stacks);
    }

    private List<Service> getServices(final String accountId) {
        ServiceService service = rancher.type(ServiceService.class);
        try {
            return service.list().execute().body().getData().stream()
                    .filter(service1 -> service1.getAccountId().equals(accountId))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ProcessingException(reference.getLandscapeDescription(), "Could not load services from Rancher API", e);
        }
    }

    private Map<String, Stack> getStacksById(String accountId) {
        StackService stackService = rancher.type(StackService.class);
        Map<String, Stack> stacks = new HashMap<>();
        try {
            stackService.list().execute().body().getData().stream()
                    .filter(stack -> stack.getAccountId().equals(accountId))
                    .forEach(stack -> stacks.put(stack.getId(), stack));
            return stacks;
        } catch (IOException e) {
            throw new ProcessingException(reference.getLandscapeDescription(), "Could not access Rancher API", e);
        }
    }

    private Optional<Project> getProject(String projectName) {
        ProjectService projectService = rancher.type(ProjectService.class);
        List<Project> projects = null;
        try {
            projects = projectService.list().execute().body().getData();
        } catch (IOException e) {
            throw new ProcessingException("Could not load projects", e);
        }
        return projects.stream()
                .filter(project -> StringUtils.isEmpty(projectName) || project.getName().equals(projectName))
                .findFirst();
    }

    private List<ItemDescription> asDescriptions(List<Service> data, final Map<String, Stack> stacks) {
        List<ItemDescription> descriptions = new ArrayList<>();

        data.forEach(service -> {
            ItemDescription item = new ItemDescription();
            item.setIdentifier(service.getId());
            item.setName(service.getName());
            item.setScale(String.valueOf(service.getScale()));
            Stack stack = stacks.get(service.getStackId());
            if (stack != null)
                item.setGroup(stack.getName());

            if (service.getLinkedServices() != null) {
                service.getLinkedServices().forEach((key, value) -> {
                    RelationDescription rd = new RelationDescription();
                    rd.setSource((String) value);
                    rd.setTarget(item.getIdentifier());
                    item.addRelation(rd);
                });
            }

            //copy all labels
            if (service.getLaunchConfig() != null) {
                if (service.getLaunchConfig().getLabels() != null) {
                    service.getLaunchConfig().getLabels().forEach((s, o) -> LabelProcessor.applyLabel(item, s, o));
                }
                if (service.getLaunchConfig().getEnvironment() != null) {
                    service.getLaunchConfig().getEnvironment().forEach((s, o) -> LabelProcessor.applyLabel(item, s, o));
                }
            }

            descriptions.add(item);
        });

        return descriptions;
    }

    private Rancher.Config getConfig(SourceReference reference) {
        Rancher.Config config = null;
        try {
            config = new Rancher.Config(
                    new URL(reference.getUrl()),
                    (String) reference.getProperty(API_ACCESS_KEY),
                    (String) reference.getProperty(API_SECRET_KEY)
            );
        } catch (MalformedURLException e) {
            throw new ProcessingException(reference.getLandscapeDescription(), "Could not configure rancher API: " + e.getMessage(), e);
        }
        return config;
    }
}
