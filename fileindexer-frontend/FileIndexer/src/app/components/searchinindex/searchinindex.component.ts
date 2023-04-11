import { Component } from '@angular/core';
import { interval, startWith, Subscription, switchMap, timeInterval } from 'rxjs';
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
  scannedDataSubscriptionTi: Subscription;
 
  ngOnInit() {
    this.scannedDataSubscriptionTi = interval(5000).pipe(
      startWith(0),
      switchMap(() => this.apiService.getScanData())
    ).subscribe( result => {
      console.log("Handling result...", result);
      this.scannedData = result.values;
    } )

  }

  ngOnDestroy() {
    this.scannedDataSubscriptionTi.unsubscribe();
  }

}
