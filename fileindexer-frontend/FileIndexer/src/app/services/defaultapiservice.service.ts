import { HttpClient, HttpHeaders } from '@angular/common/http'; 
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ScanJobStatusData, ScanJobStatusDataEntry } from '../models/indexedentry.model';


@Injectable({
  providedIn: 'root'
})
export class DefaultapiserviceService {

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

}
