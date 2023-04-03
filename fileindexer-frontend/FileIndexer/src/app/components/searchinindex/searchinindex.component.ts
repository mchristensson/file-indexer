import { Component } from '@angular/core';
import { ScannedDataEntry } from 'src/app/models/indexedentry.model';
import { DefaultapiserviceService } from '../../services/defaultapiservice.service';

@Component({
  selector: 'app-searchinindex',
  templateUrl: './searchinindex.component.html',
  styleUrls: ['./searchinindex.component.css']
})
export class SearchinindexComponent {

  constructor(private apiService: DefaultapiserviceService) {}
  scannedData: ScannedDataEntry[];

  ngOnInit() {
    this.refreshJobData();
  }

  private refreshJobData() {
    console.log("Fetching data from index...");
    this.apiService.getScanData()
    .subscribe(searchResult => {
      this.scannedData = searchResult.names;
    });
  }
}
