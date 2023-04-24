import { Component } from '@angular/core';
import { map, timestamp, Subscription, interval, startWith, switchMap } from 'rxjs';
import { ScanJobStatusDataEntry } from 'src/app/models/indexedentry.model';
import { DefaultapiserviceService } from '../../services/defaultapiservice.service';

@Component({
  selector: 'app-scanjob-list',
  templateUrl: './scanjob-list.component.html',
  styleUrls: ['./scanjob-list.component.css']
})
export class ScanjobListComponent {
  
  scanJobStatusData: ScanJobStatusDataEntry[];
  scanJobStatusDataTs: Date;
  scanJobStatusDataSubscriptionTi: Subscription;

  constructor(private apiService: DefaultapiserviceService) {}
  
 
  ngOnInit() {
    this.scanJobStatusDataSubscriptionTi = interval(5000).pipe(
      startWith(0),
      switchMap(() => this.apiService.getQueueJobStatus())
    ).subscribe( result => {
      console.log("Handling result (getQueueJobStatus)...", result);
      this.scanJobStatusData = result.data;
      var d = new Date(0);
      d.setUTCSeconds(result.timestamp);
      this.scanJobStatusDataTs = d;

    } )

  }

  ngOnDestroy() {
    this.scanJobStatusDataSubscriptionTi.unsubscribe();
  }


  
  private refreshJobData() {
    this.apiService.getQueueJobStatus()
    .subscribe(scanJobsData => {
      this.scanJobStatusData = scanJobsData.data;
      var d = new Date(0);
      d.setUTCSeconds(scanJobsData.timestamp);
      this.scanJobStatusDataTs = d;
    });
  }
}
