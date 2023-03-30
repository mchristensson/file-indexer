import { Component } from '@angular/core';
import { map, timestamp } from 'rxjs';
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
  
  constructor(private apiService: DefaultapiserviceService) {}
  
  ngOnInit() {
    console.log("ngoninit fetch from service...")
    this.apiService.getQueueJobStatus()
    .subscribe(scanJobsData => {
      this.scanJobStatusData = scanJobsData.result;
      this.scanJobStatusDataTs = scanJobsData.timestamp;
      console.log("QueueJobStatus: ", scanJobsData.result);
      console.log("QueueJobStatus: ", this.scanJobStatusData);
      console.log("QueueJobStatus: ", this.scanJobStatusDataTs);
    });
  }
  
}
