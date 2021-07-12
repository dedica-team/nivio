package de.bonndan.nivio.input.kubernetes;

/*
@EnableKubernetesMockClient(crud = true, https = false)
class DeploymentItemTest {
    Deployment deployment;
    DeploymentItem deploymentItem;
    KubernetesClient kubernetesClient;

    @BeforeEach
    void setUp() {
        deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName("test")
                .withNamespace("test")
                .withUid("1234")
                .withLabels(Map.of("release", "testgroup"))
                .endMetadata()
                .withSpec(new DeploymentSpecBuilder()
                        .build())
                .withStatus(new DeploymentStatusBuilder()
                        .build())
                .build();
        kubernetesClient.apps().deployments().create(deployment);

        deploymentItem = new DeploymentItem("test", "1234", ItemType.DEPLOYMENT, deployment, new LevelDecorator(4));
    }

    @Test
    void testGetWrappedItem() {
        HasMetadata result = deploymentItem.getWrappedItem();
        assertThat(result).isEqualTo(deployment);
    }

    @Test
    void testGetDeploymentItems() {
        List<Item> result = DeploymentItem.getDeploymentItems(kubernetesClient);
        assertThat(result.size()).isEqualTo(Collections.singletonList(new DeploymentItem("test", "1234", ItemType.VOLUME, deployment, new LevelDecorator(4))).size());
        result.forEach(item -> assertThat(item).isEqualToComparingFieldByField(deploymentItem));
    }

    @Test
    void testAddOwner() {
        var owner = new DeploymentItem("test", "1234", ItemType.DEPLOYMENT, null, new LevelDecorator(4));
        deploymentItem.addOwner(owner);
        assertThat(deploymentItem.getOwner()).isEqualTo(Collections.singletonList(owner));
    }

    @Test
    void testAddOwnerNull() {
        assertThatThrownBy(() -> deploymentItem.addOwner(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddRelation() {
        var relation = new RelationDescription("source", "target");
        deploymentItem.addRelation(relation);
        assertThat(deploymentItem.getRelationDescriptionList()).isEqualTo(Collections.singletonList(relation));
    }

    @Test
    void testAddRelationNull() {
        assertThatThrownBy(() -> deploymentItem.addRelation(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddStatusGreen() {
        deploymentItem.addStatus("key", "true");
        assertThat(deploymentItem.getStatus()).isEqualTo(Collections.singletonMap("key", "green"));
    }

    @Test
    void testAddStatusRed() {
        deploymentItem.addStatus("key", "false");
        assertThat(deploymentItem.getStatus()).isEqualTo(Collections.singletonMap("key", "red"));
    }

    @Test
    void testAddStatusNull() {
        assertThatThrownBy(() -> deploymentItem.addStatus(null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> deploymentItem.addStatus("null", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> deploymentItem.addStatus(null, "null")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testGetGroup() {
        var owner = new DeploymentItem("test", "1234", ItemType.DEPLOYMENT, null, new LevelDecorator(4));
        deploymentItem.addOwner(owner);
        String result = deploymentItem.getGroup();
        assertThat(result).isEqualTo("test");
    }

    @Test
    void testSetOwners() {
        List<Item> owner = Collections.singletonList(new DeploymentItem("test", "1234", ItemType.DEPLOYMENT, null, new LevelDecorator(4)));
        deploymentItem.setOwners(owner);
        assertThat(deploymentItem.getOwner()).isEqualTo(owner);
    }

    @Test
    void testSetOwnersNull() {
        assertThatThrownBy(() -> deploymentItem.setOwners(null)).isInstanceOf(NullPointerException.class);
    }
}
*/
