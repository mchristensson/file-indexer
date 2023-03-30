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
    this.apiService.getQueueJobStatus()
    .subscribe(scanJobsData => {
      this.scanJobStatusData = scanJobsData.data;
      var d = new Date(0);
      d.setUTCSeconds(scanJobsData.timestamp);
      this.scanJobStatusDataTs = d;
    });
  }
  
}
