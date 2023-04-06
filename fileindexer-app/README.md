# File-Indexer
The file indexer is a simple test application with the following components
* Cassandra database for data storage in backend
  * Present tables for dedicated usage
  * Remotely connected (i.e. Docker container) through configuration in application.properties.
* Queue engine that performs pre-defined tasks of type (_see `org.se.mac.blorksandbox.jobqueue.job.QueuedJob`_)
  * Scheduled cleanup of completed tasks 
  * Is a _Executor Service_ with single job processing
* REST-api for communcation with the application functions
  * Simple basic-auth authentication with a single named user (`bob`)
* Configurable with spring profiles for different environment
* 

> * There's NO built-in support for embedded launch of Cassandra
> * Security for Cassandra has not been configured

### Rest API Reference
In order to connect to the REST-api you need to apply basic-auth to all requests.

When accessed from a host outside the network the request need to pass a reverse-proxy in order to passthrough CORS.

> **Example:**
> 
> _Browser access localhost-machine to docker-container that hosts this application._

#### Job Queue Engine
| url                  | type  | description                                                               | request-payload | response-payload     |
|----------------------|-------|---------------------------------------------------------------------------|-----------------|----------------------|
| `/api/queue/enqueue` | `GET` | Enqueues a dummy job (does not do anything)                               | n/a             | `ScanEnqueueReceipt` |
| `/api/queue/status`  | `GET` | Returns pending and completed queued jobs (that has not yet been purged)  | n/a             | `QueueJobStatus` |

ScanEnqueueReceipt:
```
{
"id": 9073116747503125808,
"errorMessage": null,
"message": "Scanner job was enqueued"
}
```

#### Directory scanning data
| url                  | type   | description                                              | request-payload      | response-payload           |
|----------------------|--------|----------------------------------------------------------|----------------------|----------------------------|
| `/api/scan/list`     | `POST` | Lists all scanned files present as (type `FileMetaData`) | n/a                  | `LogicalFilesSearchResult` |
| `/api/scan/enqueue`  | `POST` | Enqueues a new directory scanning job                    | `ScanEnqueueRequest` | `ScanEnqueueReceipt`       |


ScanEnqueueRequest:
```
{
  "path": "c:/temp/misc",
  "type": "WIN_DRIVE_LETTER",
  "deviceId" : "7f800e14-47f0-4ca3-8010-499bd70cd569"
}
```

#### Image Processing
| url                         | type   | description                                                                                                                                                                              | request-payload          | response-payload           |
|-----------------------------|--------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------|----------------------------|
| `/api/imgash/enqueue`       | `POST` | Enqueues a pre-defined image analysis job (POST). This produces an entity containing a _image checksum_ (type `FileMetaData`) and also stores a simplified image as type `SmallFileData` | `ScanEnqueueRequest`     | `ScanEnqueueReceipt`       |
| `/api/imgash/list`          | `GET`  | Lists all the Compares two present image entities of type `FileMetaData` on the fly (GET) and returns the result as string.                                                              | n/a                      | `LogicalFilesSearchResult` |
| `/api/imgash/image?id={id}` | `GET`  | Returns a present image by its `ID` stored as type `FileMetaData`.                                                                                                                       | _request-param_ `id`     | `byte[]`                   |
| `/api/imgash/compare`       | `POST` | Compares two present image entities of type `FileMetaData` on the fly (GET) and returns the result as string.                                                                            | `CompareHashPairRequest` | `String`                   |

CompareHashPairRequest:
```
{
  "idA": "d1116925-641f-4454-b1d9-dc9ee71c30ca",
  "idB": "9d00aa50-d8c5-4f47-a141-16ef7d3261ff"
}
```

### Data stored in Cassandra
Stored data can be viewed by connecting to the host machine for cassandra.

Use command `cqlsh` to start the cassandra CLI.

```
cqlsh> use fileindexer_cassandra;
cqlsh:fileindexer_cassandra> desc tables;

filedatarepository  filehashrepository  logicaldevice          logicaluri   
filehashdata        filemetadata        logicalfilerepository  smallfiledata

cqlsh:fileindexer_cassandra> select * from smallfiledata;
...
```