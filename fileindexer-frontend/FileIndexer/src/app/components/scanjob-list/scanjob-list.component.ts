import { Component } from '@angular/core';
import { map } from 'rxjs';
import { ScanJobStatusData } from 'src/app/models/indexedentry.model';
import { DefaultapiserviceService } from '../../services/defaultapiservice.service';

@Component({
  selector: 'app-scanjob-list',
  templateUrl: './scanjob-list.component.html',
  styleUrls: ['./scanjob-list.component.css']
})
export class ScanjobListComponent {
  
  scanJobStatusData:ScanJobStatusData
  
  constructor(private apiService: DefaultapiserviceService) {}
  
  ngOnInit() {
    console.log("ngoninit fetch from service...")
    this.apiService.getQueueJobStatus()
    .subscribe(scanJobsData => {
      this.scanJobStatusData = scanJobsData;
      console.log("QueueJobStatus: ", this.scanJobStatusData)
    });
  }
  
}
