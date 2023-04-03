import { HttpClient, HttpHeaders } from '@angular/common/http'; 
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ScanJobStatusDataEntry, EqueueJobReceipt, ScannedDataEntry } from '../models/indexedentry.model';


@Injectable({
  providedIn: 'root'
})
export class DefaultapiserviceService {
  
  scanEnqueue(requestData): Observable<EqueueJobReceipt> {

    var httpOpts = {
      headers: new HttpHeaders({
        'Content-Type' : 'application/json',
        'Authorization' : 'Basic ' + btoa('bob:bob')
      })
    }
    return this._client
    .post<EqueueJobReceipt>("http://localhost:8081/api/scan/enqueue", requestData, httpOpts);

  }

  constructor(private _client: HttpClient) { }


  getQueueJobStatus(): Observable<{data:ScanJobStatusDataEntry[], timestamp: number}> {
    var httpOpts = {
      headers: new HttpHeaders({
        'Content-Type' : 'application/json',
        'Authorization' : 'Basic ' + btoa('bob:bob')
      })
    }
    return this._client
    .get<{data:ScanJobStatusDataEntry[], timestamp: number}>("http://localhost:8081/api/queue/status", httpOpts);
      
  }

  getScanData(): Observable<{names: ScannedDataEntry[]}> {
    var httpOpts = {
      headers: new HttpHeaders({
        'Content-Type' : 'application/json',
        'Authorization' : 'Basic ' + btoa('bob:bob')
      })
    }
    return this._client
    .get<{names: ScannedDataEntry[]}>("http://localhost:8081/api/scan/list", httpOpts);
      
  }

}
