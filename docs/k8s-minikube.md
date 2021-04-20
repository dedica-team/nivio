# Nivio Minikube
 
The complete instructions can be found at https://docs.bitnami.com/kubernetes/get-started-kubernetes/


## Install (on Ubuntu Linux)

* virtualbox https://download.virtualbox.org/virtualbox/6.1.8/virtualbox-6.1_6.1.8-137981~Ubuntu~eoan_amd64.deb
* kubectl https://kubernetes.io/docs/tasks/tools/install-kubectl/#install-kubectl-on-linux
* minikube https://kubernetes.io/docs/tasks/tools/install-minikube/
* helm 
```
curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get-helm-3 > get_helm.sh
chmod 700 get_helm.sh
./get_helm.sh
```

If you run into problems starting minikube: https://www.linuxuprising.com/2019/12/how-to-upgrade-ubuntu-repositories.html

## Run

```
minikube start --driver=virtualbox
```

## Optionally install K8s dashboard

https://www.replex.io/blog/how-to-install-access-and-add-heapster-metrics-to-the-kubernetes-dashboard

* install the k8s dashboard: 
```
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.0.0/aio/deploy/recommended.yaml
``` 

* create a token 
```
kubectl create serviceaccount dashboard-admin-sa
kubectl create clusterrolebinding dashboard-admin-sa --clusterrole=cluster-admin --serviceaccount=default:dashboard-admin-sa
kubectl get secrets
kubectl describe secret dashboard-admin-sa-token-*****
``` 
* enter the k8s dashboard: go to http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/.


## Install example Helm Charts 
* helm repo add bitnami https://charts.bitnami.com/bitnami
* helm install redis bitnami/redis --set serviceType=NodePort
* helm install nivio-wordpress bitnami/wordpress

## Start nivio
```
SEED=$(pwd)./src/test/resources/example/example_k8s.yml java -jar target/nivio.jar
``` 
