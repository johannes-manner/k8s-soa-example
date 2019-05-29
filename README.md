# Kubernetes example as part of SOA lecture

## Installing environment

We use [minikube](https://github.com/kubernetes/minikube) as a local K8s environment to get familiar with the K8s concepts. See all the installation details and information on the linked GitHub page.

You need also a running docker environment to do this hands-on. So please install docker as well. We recommend to use the [Docker Toolbox](https://docs.docker.com/toolbox/overview/), since Minikube works best with VirtualBox. Hyper-V causes problems under Windows when using Docker and Minikube in combination.

## Running the cat service

### Starting minikube and building *cats* image

Start your docker engine first.

Then open a bash with administrator permission, otherwise a few pre-create actions will fail. Starting minikube environment may take a few seconds up to minutes. Keep calm :)

```shell
$ minikube start
```

You can use the provided images via DockerHub or build local images by yourself. If you want to use the provided images by us, please skip the next part.
For local images, minikube needs a docker environment to *pull* images from. So we set the environment variable for minikube to reuse the docker daemon.

```shell
$ eval $(minikube docker-env)
```

After this, we can build the *frontend* and *backend* images. Thanks to [Stefan](https://github.com/stefan-kolb/jaxrs-samples) for providing the example.

```shell
$ docker build services/backend/. -t jmnnr/soa-k8s-backend

$ docker build services/frontend/. -t jmnnr/soa-k8s-frontend
```

To verify, that your image is visible for minikube, execute the following command:

```shell
$ docker images
```

Your output should look like the following:

| REPOSITORY                              | TAG               | IMAGE ID     | CREATED        | SIZE   |
|-----------------------------------------|-------------------|--------------|----------------|--------|
| jmnnr/soa-k8s-frontend                  | latest            | 5f4fda9a993d | 4 seconds ago  | 378MB  |
| jmnnr/soa-k8s-backend                   | latest            | 689a9862d38a | 16 seconds ago | 399MB  |
| gradle                                  | 5.4.1-jdk8-alpine | 8017d8c2ba74 | 12 days ago    | 204MB  |
| php                                     | 7-apache          | 59d2cf691156 | 2 weeks ago    | 378MB  |
| amazoncorretto                          | 8u212             | 68fe666c02f5 | 4 weeks ago    | 389MB  |
| k8s.gcr.io/kube-proxy                   | v1.14.0           | 5cd54e388aba | 8 weeks ago    | 82.1MB |
| k8s.gcr.io/kube-scheduler               | v1.14.0           | 00638a24688b | 8 weeks ago    | 81.6MB |
| k8s.gcr.io/kube-apiserver               | v1.14.0           | ecf910f40d6e | 8 weeks ago    | 210MB  |
| k8s.gcr.io/kube-controller-manager      | v1.14.0           | b95b1efa0436 | 8 weeks ago    | 158MB  |
| k8s.gcr.io/kube-addon-manager           | v9.0              | 119701e77cbc | 4 months ago   | 83.1MB |
| k8s.gcr.io/coredns                      | 1.3.1             | eb516548c180 | 4 months ago   | 40.3MB |
| k8s.gcr.io/kubernetes-dashboard-amd64   | v1.10.1           | f9aed6605b81 | 5 months ago   | 122MB  |
| k8s.gcr.io/etcd                         | 3.3.10            | 2c4adeb21b4f | 5 months ago   | 258MB  |
| k8s.gcr.io/k8s-dns-sidecar-amd64        | 1.14.13           | 4b2e93f0133d | 8 months ago   | 42.9MB |
| k8s.gcr.io/k8s-dns-kube-dns-amd64       | 1.14.13           | 55a3c5209c5e | 8 months ago   | 51.2MB |
| k8s.gcr.io/k8s-dns-dnsmasq-nanny-amd64  | 1.14.13           | 6dc8ef8287d3 | 8 months ago   | 41.4MB |
| k8s.gcr.io/pause                        | 3.1               | da86e6ba6ca1 | 17 months ago  | 742kB  |
| gcr.io/k8s-minikube/storage-provisioner | v1.8.1            | 4689081edb10 | 18 months ago  | 80.8MB |

Now you are ready to deploy the cats image on K8s!

### Via kubectl commands - starting a single container in a pod

First step is to create a deployment and run the backend image. Since the image is locally available there is an additional option to only pull images if they are not present locally.

After the deployment *catz* is created the master starts to create a pod with the *cats* image and schedules the pod on a node. Hint: Minikube has only a single node where all the pods are running on.

```shell
$ kubectl run catz --image=jmnnr/soa-k8s-backend --image-pull-policy=IfNotPresent --port=12345
```

After the deployment is created, you can view the available pods via the get kubectl command. Minikube adds a unique suffix to the deployment name as a pod name.

```shell
$ kubectl get pods
NAME                    READY   STATUS    RESTARTS   AGE
catz-76d67b6659-2245t   1/1     Running   0          6s
```

```shell
$ kubectl get deployments
NAME   READY   UP-TO-DATE   AVAILABLE   AGE
catz   1/1     1            1           89s
```

Before we expose the service, we can check, if our service works correctly. Therefore we need the internal IP of our created pod.

```shell
$ kubectl describe pods
Name:               catz-76d67b6659-2245t
Namespace:          default
Priority:           0
PriorityClassName:  <none>
Node:               minikube/10.0.2.15
Start Time:         Tue, 21 May 2019 11:22:26 +0200
Labels:             pod-template-hash=76d67b6659
                    run=catz
Annotations:        <none>
Status:             Running
IP:                 172.17.0.5
Controlled By:      ReplicaSet/catz-76d67b6659
Containers:
....
```

Then we can connect to our minikube VM via ssh and execute a curl command to get the cats list of our service.

```
$ minikube ssh
                         _             _
            _         _ ( )           ( )
  ___ ___  (_)  ___  (_)| |/')  _   _ | |_      __
/' _ ` _ `\| |/' _ `\| || , <  ( ) ( )| '_`\  /'__`\
| ( ) ( ) || || ( ) || || |\`\ | (_) || |_) )(  ___/
(_) (_) (_)(_)(_) (_)(_)(_) (_)`\___/'(_,__/'`\____)

$ curl 172.17.0.5:9999/cats
[{"id":1,"name":"Garfield",...}]
```
As you might remember, our implemented service exposes the service on port 9999 and with the path parameter *cats*.

No we can expose a service and use one of the service types. We use a load balancer in this example.

```shell
$ kubectl expose deployments/catz --type="LoadBalancer" --target-port=9999 --port=8989
service/catz exposed

$ kubectl describe services catz
Name:                     catz
Namespace:                default
Labels:                   run=catz
Annotations:              <none>
Selector:                 run=catz
Type:                     LoadBalancer
IP:                       10.99.220.194
Port:                     <unset>  8989/TCP
TargetPort:               9999/TCP
NodePort:                 <unset>  31916/TCP
Endpoints:                172.17.0.5:9999
Session Affinity:         None
External Traffic Policy:  Cluster
Events:                   <none>
```

Since we are using minikube, there is no integrated load balancer, so we use the NodePort mechanism here. If you are in a production environment, there is an additional exposed external LoadBalancer IP. If you want to check your load balancer component, you can ssh your minikube VM use make another curl with the SERVICE-IP:PORT/cats.

To get the service Endpoint, which is externally accessible use the following minikube command:

```shell
$ minikube service catz --url
```

#### Clean-up

To clean up, we can delete the service and the deployment via:
```shell
$ kubectl delete services catz
service "catz" deleted
$ kubectl delete deployments catz
deployment.extensions "catz" deleted
```

### Via declarative configuration files - starting replicated Containers

In the previous example, we used an imperative way to get a single container within a single pod running. Since declarative configuration turned out to have a lot of advantages, we show the same use case with replicated containers running in different pods.

```shell
$ kubectl apply -f cats-rs-backend.yaml
```

We created a replica set with 2 instances. Play a bit around with the kubectl commands *get pods*, *describe pods* etc.

```shell
$ kubectl describe rs cats-rs
Name:         cats-rs
Namespace:    default
Selector:     app=cats,version=1
Labels:       app=cats
              version=1
Replicas:     2 current / 2 desired
Pods Status:  2 Running / 0 Waiting / 0 Succeeded / 0 Failed
Pod Template:
  Labels:  app=cats
           version=1
  Containers:
   cats:
    Image:        cats
    ...
Events:
  Type    Reason            Age   From                   Message
  ----    ------            ----  ----                   -------
  Normal  SuccessfulCreate  9s    replicaset-controller  Created pod: cats-rs-7577v
  Normal  SuccessfulCreate  9s    replicaset-controller  Created pod: cats-rs-cmnwc
```

You can see that the replica set uses *label selectors* to group all the pods, which are part in this replica set.
Via *$ kubectl get pods **pod-name** -o yaml*, you can find the replica set the pod is included in.

When you are aware of the label selectors, you can also use the selectors to get all the pods included in a replica set via *$ kubectl get pods -l **label selectors***

Alter the *cats-rs-backend.yaml* file and change the number of replicas to 3.
Then execute the following command again, which declaratively scales the number of running cats pods to 3.

```shell
$ kubectl apply -f cats-rs-backend.yaml
```

ReplicaSets are a first step to a self-healing system, but there is another option to specify a ReplicaSet implicitly and also add the possiblity to ship new versions of your software with zero downtime: Deployments. Therefore, we delete the ReplicaSet and create the deployment:

```shell
$ kubectl delete -f cats-rs-backend.yaml
replicaset.extensions "cats-rs" deleted

$ kubectl apply -f architectures/backend-simple/cats-deployment-backend.yaml
deployment.apps/cats-d-b created
```

In this configuration, a single pod is created running the cats image. A ReplicaSet is created and a ReplicationController checks the system state and takes action if necessary. What's missing is a service, which makes the deployment accessible via a service name. As we have a replication controller, the underlying pods can crash and K8s restarts it, but the service is always available with the same interface. The matching of the pods and the service is done via a label selector within the service configuration.

```shell
$ kubectl apply -f architectures/backend-simple/cats-service-backend.yaml
service/cat-service created
```

The backend service in only accessible within the minikube cluster.
For accessing the service and creating a simple output of our data, we use another deployment and a second service, which connects to the previously deployed backend.

```shell
$ kubectl apply -f architectures/cats-deployment-frontend.yaml
deployment.apps/cats-d-f created
```

If you used the images from DockerHub (so you build no images on your own), the system downloads the images. This might take a while. So use the *get pods* command to see, if all the pods are **Running**. In the following example the frontend pod is still under creation.

```shell
$ kubectl get pods
NAME                        READY   STATUS              RESTARTS   AGE
cats-d-b-5ffd4b9598-2fwbk   1/1     Running             0          92s
cats-d-f-5d98c794d6-z5mqf   0/1     ContainerCreating   0          3s
```

Finally, expose the backend as a service inside the cluster and the frontend to the user via a *LoadBalancer*. As already mentioned, minikube has no external IP, but you can get a externally (on your host) accessible IP from minikube.

```shell
$ kubectl apply -f architectures/backend-simple/cats-service-backend.yaml
service/cat-service-backend created

$ kubectl apply -f architectures/cats-service-frontend.yaml
service/cat-service-frontend created

$ minikube service cat-service-frontend --url
```

Use the result of the last command and add **/index.php** to see the functionality of your service.
Only the frontend is now accessible from your host via your browser. If you want to delete a cat, use the following commands via your shell or use the delete button in the web browser:

```shell
$ kubectl describe services

HINT: get the service IP of your cat-service-backend

$ minikube ssh
$ curl -X "DELETE" SERVICE-IP:8989/cats/1
$ exit
```

After this refresh your local browser and the first cat *Garfield* was deleted.

As a summary: Your backend is only accessible within your K8s cluster. The frontend only exposes a subset of the functionality of your backend. A replication controller checks the state of your pods and take actions if necessary. The service hides the underlying pods and allows a stable interface to work with. Your frontend is the only component which is accessible on your local machine. There is also the same approach with a replication controller and a service interface.

#### Inconsistent state within backend

The backend contains a *CatService*, which holds all the cats in memory. The backend is therefore a **stateful** component of our application. Since scaling is a major concern in K8s, inconsistency problems arise. To reproduce this problem, alter the number of replicas in the *backend deployment* to 2 and apply the configuration.
Invoke your service and delete 1 or 2 cats and refresh the page as long as you see the inconsistency.

#### Clean-up

To clean up, we can delete the services and the deployments via:
```shell
$ kubectl delete -f architectures/cats-service-frontend.yaml
service "cat-service-frontend" deleted
$ kubectl delete -f architectures/backend-simple/cats-service-backend.yaml
service "cat-service-backend" deleted
$ kubectl delete -f architectures/cats-deployment-frontend.yaml
deployment.apps "cats-d-f" deleted
$ kubectl delete -f architectures/backend-simple/cats-deployment-backend.yaml
deployment.apps "cats-d-b" deleted
```

#### Handling state with mounted volumnes and stateful sets

Due to the inconsistent state problems, there are two options to handle stateful applications. The first and most prominent option is to use third party services like DynamoDb, hosted by a cloud provider. The consistency and scaling aspects are then handled by the provider and easily usable via API calls.

The other option is to create a StatefulSet and mount a volume to the pod, where the database is running. We choose an approach, where only a single replica is running a mongo database handled by a ReplicaSet, which handles failures and restarts for us, but we can't achieve zero downtime in this architecture. For more than one database instance, you need synchronization between the instances to get an overall consistent state. Since this is another layer of complexity we hazard downtime of our system for short timeperiods if failures occur.

Deploying the 3 tier application is quite easy.
```shell
$ kubectl apply -f architectures/backend-db/
$ kubectl apply -f architectures/
```

The first command deploys the backend with the mongo db and the second `kubectl` command deploys the frontend. When getting the dashboard url, you see following interface:
<img src="pics/dashboard.pdf" alt="Minikube Dashboard"/>

If you request for all your pods, you see a single instance for the frontend (cats-d-f-#####), backend (cats-d-b-db-#####) and the database (mongod-0). Now you can scale your backend to 2 or 3 replicas and send a few request via your frontend to the backend.
In the following you can get the logs from your backend pods and see how many requests each backend instance has sent to the database.

```shell
$ kubectl get pods
NAME                           READY   STATUS    RESTARTS   AGE
cats-d-b-db-6c9f5947f9-kg52t   1/1     Running   0          4s
cats-d-b-db-6c9f5947f9-x46g6   1/1     Running   0          3m57s
cats-d-f-d55688787-s84hn       1/1     Running   0          114s
mongod-0                       1/1     Running   0          3m57s
```

To see the self-healing feature of kubernetes you can delete a pod via `$ kubectl delete pods mongod-0`. Check the status via `get pods` and also see if your data has changed?

Due to the volume mounted to the node (and not to the pod directly), the data is not affected of the restart of your database instance.

You can also replicate the database layer via the stateful set. Therefore you have to specify a primary replica (normally pod xy-0) and add all the other database replicas to the primary replica for synchronizing state.

#### Clean-up

To clean up, we can delete the services and the deployments via:
```shell
$ kubectl delete -f architectures/
$ kubectl delete -f architectures/backend-db/
```

#### Useful kubectl commands

- `kubectl describe pods *PODNAME*` Describes all the important information about the pod specified. PODNAME is optional. If you leave it, you get information about all the pods. (Also available for all other concepts in K8s like `describe rs` for ReplicaSets, `describe deployments` for Deployments etc.)
- `kubectl get pods` Gets all the pods. (Also available for all other concepts in K8s like `get rs` for ReplicaSets, `get deployments` for Deployments etc.)
- `kubectl logs PODNAME` Get the logs from the running pod. When thinking about Java applications, this command presents all the information logged to the console.
- `kubectl apply -f FILE or FOLDER` You can apply a config file or a folder (means all configuration files are applied). The CLI of K8s then creates all the objects specified. Infrastructure as Code :)
- `kubectl delete -f FILE or FOLDER` You can delete all objects specified in the file or folder. Infrastructure as Code :)

#### Useful minikube commands
- `$ minikube service SERVICE-NAME --url` Get the url of your service to access it via your local environment.
- `$ minikube dashboard --url` Get a URL for a K8s dashboard to see all the objects deployed in the cluster.
