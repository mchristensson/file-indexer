import { HttpClient, HttpHeaders } from '@angular/common/http'; 
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ScanJobStatusData } from '../models/indexedentry.model';

@Injectable({
  providedIn: 'root'
})
export class DefaultapiserviceService {

  constructor(private _client: HttpClient) { }


  getQueueJobStatus(): Observable<ScanJobStatusData> {
    var httpOpts = {
      headers: new HttpHeaders({
        'Content-Type' : 'application/json',
        'Authorization' : 'Basic ' + btoa('bob:bob')
      })
    }
    return this._client.get<ScanJobStatusData>("http://localhost:8080/api/queue/status", httpOpts)
    
  }

}
