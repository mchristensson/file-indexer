import { HttpClient, HttpHeaders } from '@angular/common/http'; 
import { Injectable } from '@angular/core';
import { Observable, map, switchMap, Subscriber } from 'rxjs';
import { ScanJobStatusDataEntry, EqueueJobReceipt, ScannedDataEntry, ImgHashData } from '../models/indexedentry.model';


@Injectable({
  providedIn: 'root'
})
export class DefaultapiserviceService {

  constructor(private _client: HttpClient) { }

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

  getScanData(): Observable<{values: ScannedDataEntry[]}> {
    var httpOpts = {
      headers: new HttpHeaders({
        'Content-Type' : 'application/json',
        'Authorization' : 'Basic ' + btoa('bob:bob')
      })
    }
    return this._client
    .get<{values: ScannedDataEntry[]}>("http://localhost:8081/api/scan/list", httpOpts);
      
  }

  getImgHashDataList(): Observable<{values: ImgHashData[]}> {
    var httpOpts = {
      headers: new HttpHeaders({
        'Content-Type' : 'application/json',
        'Authorization' : 'Basic ' + btoa('bob:bob')
      })
    }
    return this._client
    .get<{values: ImgHashData[]}>("http://localhost:8081/api/imgash/list", httpOpts);
      
  }

  /**
   * 
   * Reference: https://stackblitz.com/edit/angular-download-image-example-xwskyf?file=src%2Fapp%2Fimage.service.ts
   * @param imgId Database id for image
   * @returns 
   */
  getSmallImage(imgId: string): Observable<string> {
    return this._client.get("http://localhost:8081/api/imgash/image", { 
      headers: new HttpHeaders({
        'Content-Type' : 'image/jpeg',
        'Authorization' : 'Basic ' + btoa('bob:bob')
      }),
      params: {
        id: imgId
      },
      responseType: 'blob' 
  })
      .pipe(
        switchMap(responseBlob => {
          return new Observable<string>((obs) => {
            const reader = new FileReader();
            reader.onerror = err => obs.error(err);
            reader.onabort = err => obs.error(err);
            reader.onload = () => obs.next(reader.result as string);
            reader.onloadend = () => obs.complete();
            return reader.readAsDataURL(responseBlob);
          })
        })
      );
  }


  imgAnalysisEnqueue(requestData): Observable<EqueueJobReceipt> {

    var httpOpts = {
      headers: new HttpHeaders({
        'Content-Type' : 'application/json',
        'Authorization' : 'Basic ' + btoa('bob:bob')
      })
    }
    return this._client
    .post<EqueueJobReceipt>("http://localhost:8081/api/imgash/enqueue", requestData, httpOpts);

  }
}
