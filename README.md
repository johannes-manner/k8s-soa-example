# Kubernetes example as part of SOA lecture

## Installing environment

We use [minikube](https://github.com/kubernetes/minikube) as a local K8s environment to get familiar with the K8s concepts. See all the installation details and information on the linked GitHub page.

You need also a running docker environment to do this hands-on. So please install docker as well. We recommend to use the [Docker Toolbox](https://docs.docker.com/toolbox/overview/), since Minikube works best with VirtualBox. Hyper-V causes problems under Windows when using Docker and Minikube in combination.

## Running the cat service

### Starting minikube

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
### Using a simple Linux Distribution for Debugging

We start with the same functionality as in our [docker tutorial](https://github.com/uniba-dsg/docker-tutorial).
We start an interactive container within a pod. We use the alpine image again.
This mechanism can also be used for debugging and some sort of monitoring your system, when you want to perform *curl* or other commands to other pods within the system. Remember: the least deployable unit in K8s is a pod, not a container!!
It is also sometimes beneficial to have a lightweight pod in your cluster for debugging purposes.

```shell
$ kubectl run hello --image=alpine -it -- ash
/ # uptime
/ # apk add curl

$ kubectl delete pod hello
```

With Ctrl+p and Ctrl+q you can leave the container and pod.
There will be a hint within the console and a command to reconnect to the pod again.
With the delete command you can delete your imperatively specified pod.

I highly recommend to use the [Kubernetes Command Docu](https://kubernetes.io/docs/reference/generated/kubectl/kubectl-commands#-strong-getting-started-strong-) for further runtime and configuration options.

Since we know, that imperative configuration is limited, we investigate the declarative specification possibilities.
K8s uses YAML files.
A simple YAML file, where we specify the properties of the pod we created previously looks like the following:

```yml
apiVersion: v1
kind: Pod
metadata:
  name: shell-demo          # name of the pod
spec:
  containers:
  - name: hello-spec        # name of the container within this pod
    image: alpine:latest    # As in our docker tutorial, use this lightweight linux distribution
    stdin: true             # Keep stdin open on the container(s) in the pod, even if nothing is attached.
    tty: true               # Allocated a TTY for each container in the pod.
```

So the first command applies the configuration and Kubernetes handles the creation of resources etc. for us.
With the second command, we can attach to the *shell-demo* pod and within this pod (you know that more than one container can run inside a pod) to the *hello-spec* container.
The last command deletes the created artifacts of the configuration file.
```shell
echo "<config>" >> pod.yaml
$ kubectl apply -f pod.yaml
$ kubectl attach shell-demo -c hello-spec -i -t
$ kubectl delete -f pod.yaml
```

### Simple Storage Example

As told in the lecture, persistent storage subsystem has two components PersistentVolumes (PV) and PersistenVolumeClaims (PVC) to decouple the two concepts.
So you need to specify PV first, then specify a PVC, where the K8s manager dynamically binds a suitable PV.
Then you can mount this PVC to your container and store data persistently.
Play a bit around with the following files and commands.

```shell
$ kubectl apply -f pv-demo.yaml
$ kubectl get pv
NAME                                       CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM                                               STORAGECLASS    REASON   AGE
hello-pv                                   5Gi        RWO            Retain           Bound    default/hello-pvc                                   local-storage            2m31s

$ kubectl apply -f shell-demo.yaml

$ kubectl get pvc
NAME                                        STATUS   VOLUME                                     CAPACITY   ACCESS MODES   STORAGECLASS   AGE
hello-pvc                                   Bound    pvc-e049c1b5-771e-4a96-94a3-cee05ae60617   2Gi        RWO            standard       42s

$ kubectl describe pods
 - you will see the volume claim here

 $ kubectl attach shell-demo -c hello-spec -i -t
   / cd data/hello/container
   /data/hello/container  echo "test" >> minikube.text.txt
 Leave container and look into minikube /custom/data/hello directory
```

Check also the [docs](https://kubernetes.io/docs/concepts/storage/persistent-volumes/
).

### Config Maps and Environment Variables

Next we define environment variables.
Look into *config-map.yaml* and apply the file.
Also apply the pod *shell-demo.yaml* configuration to minikube.
Then check the content of the environment file via *echo* within the container.

```shell
$ kubectl apply -f config-map.yaml
$ kubectl describe configmaps
$ kubectl apply -f shell-demo.yaml
$ kubectl attach shell-demo -c hello-spec -i -t
```

Change the content of config map parameter and exit the *hello-spec* container.
See whats happened :)

### Building *cats* Image

After this introduction and the facility to work with a container pod as in a native docker environment, we can build the *frontend* and *backend* images. Thanks to [Stefan](https://github.com/stefan-kolb/jaxrs-samples) for providing the example.

```shell
$ docker build services/backend/. -t jmnnr/soa-k8s-backend

$ docker build services/frontend/. -t jmnnr/soa-k8s-frontend
```

To verify, that your image is visible for minikube, execute the following command:

```shell
$ docker images
```

Your output should look like the following (your output may be differ, especially the version numbers of the tags):

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

First step is to create a deployment and run the backend image.

After the deployment *catz* is created the master starts to create a pod with the *cats* image and schedules the pod on a node. Hint: Minikube has only a single node where all the pods are running on.

```shell
$ kubectl create deployment catz --image=jmnnr/soa-k8s-backend
deployment.apps/catz created
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
Name:         catz-58dc8dfdd7-7l8qb
Namespace:    default
Priority:     0
Node:         minikube/192.168.99.101
Start Time:   Tue, 07 Jul 2020 16:45:24 +0200
Labels:       app=catz
              pod-template-hash=58dc8dfdd7
Annotations:  <none>
Status:       Running
IP:           172.17.0.5
IPs:
  IP:           172.17.0.5
Controlled By:  ReplicaSet/catz-58dc8dfdd7
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

So now you have different ways to access your REST resource:

```shell
$ http://minikube:31916/cats          DNS resolution, minikube is the node name.
$ http://MACHINE-IP:31916/cats        Via the $ service --url command.
$ minikube ssh
  $ curl 172.17.0.5:9999/cats         Port on the pod.
  $ curl 10.99.220.194:8989/cats      Service IP and mapped port.
  $ curl minikube:31916/cats          DNS resolution, minikube is the node name.
```

#### Clean-up

To clean up, we can delete the service and the deployment via:
```shell
$ kubectl delete services catz
service "catz" deleted
$ kubectl delete deployments catz
deployment.apps "catz" deleted
```

### Via declarative configuration files - starting replicated Containers

In the previous example, we used an imperative way to get a single container within a single pod running. Since declarative configuration turned out to have a lot of advantages, we show the same use case with replicated containers running in different pods.

```shell
$ kubectl apply -f cats-rs-backend.yaml
replicaset.apps/cats-rs created
```

We created a replica set with 2 instances. Play a bit around with the kubectl commands *get pods*, *describe pods* etc.

```shell
$ kubectl describe rs cats-rs
Name:         cats-rs
Namespace:    default
Selector:     animal=cat
Labels:       <none>
Annotations:  Replicas:  2 current / 2 desired
Pods Status:  2 Running / 0 Waiting / 0 Succeeded / 0 Failed
Pod Template:
  Labels:  animal=cat
           app=cats
           version=1
  Containers:
   backend:
    Image:        jmnnr/soa-k8s-backend:latest
    Port:         <none>
    Host Port:    <none>
    Environment:  <none>
    Mounts:       <none>
  Volumes:        <none>
Events:
  Type    Reason            Age   From                   Message
  ----    ------            ----  ----                   -------
  Normal  SuccessfulCreate  90m   replicaset-controller  Created pod: cats-rs-v8mw7
  Normal  SuccessfulCreate  90m   replicaset-controller  Created pod: cats-rs-44xnk
```

You can see that the replica set uses *label selectors* to group all the pods, which are part in this replica set.
Via *$ kubectl get pods **pod-name** -o yaml*, you can find the replica set the pod is included in.

When you are aware of the label selectors, you can also use the selectors to get all the pods included in a replica set via *$ kubectl get pods -l **label selectors***

Alter the *cats-rs-backend.yaml* file and change the number of replicas to 3.
Then execute the following command again, which declaratively scales the number of running cats pods to 3.

```shell
$ kubectl apply -f cats-rs-backend.yaml
```

We also included Liveness and Readiness checks.
To investigate these, use the *kubectl get pods* and *kubectl logs --follow <pod-name>* command.

ReplicaSets are a first step to a self-healing system, but there is another option to specify a ReplicaSet implicitly and also add the possiblity to ship new versions of your software with zero downtime: Deployments. Therefore, we delete the ReplicaSet and create the deployment:

```shell
$ kubectl delete -f cats-rs-backend.yaml
replicaset.apps "cats-rs" deleted

$ kubectl apply -f architectures/backend-simple/cats-deployment-backend.yaml
deployment.apps/cats-d-b created
```

In this configuration, a single pod is created running the cats image. A ReplicaSet is created and a ReplicationController checks the system state and takes action if necessary. What's missing is a service, which makes the deployment accessible via a service name. As we have a replication controller, the underlying pods can crash and K8s restarts it, but the service is always available with the same interface. The matching of the pods and the service is done via a label selector within the service configuration.

```shell
$ kubectl apply -f architectures/backend-simple/cats-service-backend.yaml
service/cat-service-backend created
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

As a summary: Your backend is only accessible within your K8s cluster. The frontend only exposes a subset of the functionality of your backend. A replication controller checks the state of your pods and takes actions if necessary. The service hides the underlying pods and allows a stable interface to work with. Your frontend is the only component which is accessible on your local machine. There is also the same approach with a replication controller and a service interface.

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
