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
    this.refreshJobData();
  }
  
  enqueueScanJob() {
    var requestData = {
      "path": "opt/app/test-filestructure",
      "type": "UNIX",
      "deviceId" : "7f800e14-47f0-4ca3-8010-499bd70cd569"
    }

    console.log("TODO: Implement enqueueScanJob");
    this.apiService.scanEnqueue(requestData)
    .subscribe(scanEnqueueReceipt => {
      console.log("Result: ", scanEnqueueReceipt.id);
      this.refreshJobData();
    });
    
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
