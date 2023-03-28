import { HttpClient, HttpHeaders } from '@angular/common/http'; 
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class FileIndexerApiserviceService {

  constructor(private _client: HttpClient) { }


  

  getProducts() {
    var httpOpts = {
      headers: new HttpHeaders({
        'Content-Type' : 'application/json',
        'Authorization' : 'Basic ' + btoa('bob:bob')
      })
    }
    return this._client.get("http://localhost:8081/api/scan/list", httpOpts);
  }
}
